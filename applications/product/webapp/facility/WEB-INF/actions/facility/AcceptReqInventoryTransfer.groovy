import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;


GenericDelegator delegator = delegator;
String requestId = request.getParameter("requestId");
GenericValue inventoryRequisition = delegator.findOne("InventoryRequisition", UtilMisc.toMap("invRequisitionId",requestId), false);

EntityCondition condition = EntityCondition.makeCondition("invRequisitionId",EntityOperator.EQUALS,requestId);
List<GenericValue> inventoryRequisitionItemList = delegator.findList("InventoryRequisitionItem",condition,null,null,null,false);
List<Map<String, Object>> rejectedItemList = new ArrayList<Map<String, Object>>();
for(GenericValue inventoryRequisitionItem : inventoryRequisitionItemList ){
	Map<String, Object> rejectedItemMap = new HashMap<String, Object>();
	BigDecimal orderQty = inventoryRequisitionItem.getBigDecimal("quantity");
	BigDecimal acceptedQuantity = inventoryRequisitionItem.getBigDecimal("acceptedQuantity");
	if(UtilValidate.isEmpty(acceptedQuantity))
		acceptedQuantity = orderQty;
	if(orderQty.compareTo(acceptedQuantity) == 1){
		rejectedItemMap.put("invReqItemId", inventoryRequisitionItem.getString("invReqItemId"));
		rejectedItemMap.put("productId", inventoryRequisitionItem.getString("productId"));
		rejectedItemMap.put("rejectedQty", orderQty.subtract(acceptedQuantity));
		rejectedItemMap.put("rejectionId", inventoryRequisitionItem.getString("rejectionId"));
		rejectedItemList.add(rejectedItemMap);
	}
	GenericValue inventoryRequisitionItemGvToStore = delegator.makeValue("InventoryRequisitionItem",UtilMisc.toMap("invReqItemId",inventoryRequisitionItem.getString("invReqItemId"),"acceptedQuantity",acceptedQuantity));
	delegator.store(inventoryRequisitionItemGvToStore);
	GenericValue productFacilityGv = delegator.makeValue("ProductFacility", UtilMisc.toMap("productId",inventoryRequisitionItem.getString("productId"),"facilityId",inventoryRequisition.getString("facilityIdTo") ));
	delegator.createOrStore(productFacilityGv);
}

List<GenericValue> invTransferList = delegator.findList("InventoryTransfer",condition,null,null,null,false);

for(GenericValue invTransfer : invTransferList){
		dispatcher.runSync("updateInventoryTransfer",
		[userLogin:userLogin,
		inventoryItemId:invTransfer.getString("inventoryItemId"),
		statusId:"IXF_COMPLETE",
		inventoryTransferId:invTransfer.getString("inventoryTransferId")
		]);
}

GenericValue inventoryRequisitionGvToStore = delegator.makeValue("InventoryRequisition",UtilMisc.toMap("invRequisitionId",requestId,"status","Complete"));
delegator.store(inventoryRequisitionGvToStore);

/* Below Code create a new Request for Rejected Qty */

GenericValue userLogin = session.getAttribute("userLogin");
String partyId = userLogin.getString("partyId");

GenericValue facility = delegator.findOne("Facility",UtilMisc.toMap("facilityId",inventoryRequisition.getString("facilityIdTo")),false);

String seqIdRIT = delegator.getNextSeqId("InventoryRequisition");
if(UtilValidate.isNotEmpty(rejectedItemList)){
	GenericValue inventoryRequisitionGv = delegator.create("InventoryRequisition",UtilMisc.toMap("invRequisitionId",seqIdRIT,"requestType","Request",
		"status","Requested","facilityIdFrom",inventoryRequisition.getString("facilityIdFrom"),"facilityIdTo",
		inventoryRequisition.getString("facilityIdTo"),"createdByPartyId",partyId,"approvalPartyId",
		facility.getString("ownerPartyId"),"invReqParentId" ,requestId,"requestDate",UtilDateTime.nowTimestamp() ));
	delegator.createOrStore(inventoryRequisitionGv);
}

for(Map<String, Object> rejectedItem : rejectedItemList){
	BigDecimal rejectedQty = new BigDecimal(rejectedItem.get("rejectedQty"));
	dispatcher.runSync("createInventoryTransfersForProduct",
		[userLogin:userLogin,
		facilityId:inventoryRequisition.getString("facilityIdTo"),
		facilityIdTo:inventoryRequisition.getString("facilityIdFrom"),
		productId:rejectedItem.get("productId"),
		quantity:rejectedQty,
		invRequisitionId:seqIdRIT]);
	
	String seqIdRITP = delegator.getNextSeqId("InventoryRequisitionItem");
	GenericValue inventoryRequisitionItemGv = delegator.create("InventoryRequisitionItem",UtilMisc.toMap("invReqItemId",seqIdRITP,"invRequisitionId",seqIdRIT,
		"productId",rejectedItem.get("productId"),"quantity",rejectedQty,"rejectionId",rejectedItem.get("rejectionId"),"glAccountCategoryId",rejectedItem.get("glAccountCategoryId") ));
	delegator.createOrStore(inventoryRequisitionItemGv);
	
}

/* Below code create a new returnable Requisition */
List conditions = [];
conditions.add(condition);
conditions.add(EntityCondition.makeCondition("acceptedQuantity",EntityOperator.GREATER_THAN,BigDecimal.ZERO));
conditions.add(EntityCondition.makeCondition("returnable",EntityOperator.EQUALS,"Yes"));
EntityCondition mainCondition = EntityCondition.makeCondition(conditions,EntityOperator.AND);
List<GenericValue> invReqItemToReturnList = delegator.findList("InventoryRequisitionItem",mainCondition,null,null,null,false);
String inReqId = delegator.getNextSeqId("InventoryRequisition");
if(UtilValidate.isNotEmpty(invReqItemToReturnList)){
	GenericValue inventoryRequisitionGv = delegator.create("InventoryRequisition",UtilMisc.toMap("invRequisitionId",inReqId,"requestType","Return",
		"status","Saved","facilityIdFrom",inventoryRequisition.getString("facilityIdFrom"),"facilityIdTo",
		inventoryRequisition.getString("facilityIdTo"),"createdByPartyId",partyId,"approvalPartyId",
		facility.getString("ownerPartyId"),"requestDate",UtilDateTime.nowTimestamp() ));
	delegator.createOrStore(inventoryRequisitionGv);
}

for(GenericValue invReqItemToReturnGv : invReqItemToReturnList){
	String seqIdRITP = delegator.getNextSeqId("InventoryRequisitionItem");
	GenericValue inventoryRequisitionItemGv = delegator.create("InventoryRequisitionItem",UtilMisc.toMap("invReqItemId",seqIdRITP,"invRequisitionId",inReqId,
		"productId",invReqItemToReturnGv.get("productId"),"quantity",invReqItemToReturnGv.getBigDecimal("acceptedQuantity"),
		"glAccountCategoryId",invReqItemToReturnGv.get("glAccountCategoryId"),"returnable","Yes","outTime",invReqItemToReturnGv.getTimestamp("outTime") ));
	delegator.createOrStore(inventoryRequisitionItemGv);
}



return "success";
