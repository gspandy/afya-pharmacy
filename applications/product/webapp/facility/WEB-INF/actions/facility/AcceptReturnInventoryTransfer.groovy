import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;

GenericDelegator delegator = delegator;
String invReqItemId = request.getParameter("invReqItemId");
String seqId = request.getParameter("seqId");
String locationSeqId = null;
String locationSeqIdParam =  request.getParameter( "locationSeqId_o_" + seqId);
if(locationSeqIdParam)
	locationSeqId = locationSeqIdParam;
	
GenericValue inventoryRequisitionItem = delegator.findOne("InventoryRequisitionItem", UtilMisc.toMap("invReqItemId",invReqItemId), false);

GenericValue inventoryRequisition = delegator.findOne("InventoryRequisition", false, UtilMisc.toMap("invRequisitionId",inventoryRequisitionItem.getString("invRequisitionId")));

GenericValue inventoryTransferAndInventoryRequisitionGv = delegator.findByAnd("InventoryTransferAndInventoryRequisition",
	UtilMisc.toMap("invRequisitionId",inventoryRequisitionItem.getString("invRequisitionId"),"productId",inventoryRequisitionItem.getString("productId")),null, false).get(0);
 GenericValue product = delegator.findOne("Product", false, UtilMisc.toMap("productId",inventoryRequisitionItem.getString("productId") ) );
 if("Y".equals(product.getString("returnable"))){
	delegator.removeByAnd("ProductFacility", UtilMisc.toMap("productId",inventoryRequisitionItem.getString("productId"),"facilityId",inventoryRequisition.getString("facilityIdTo") ) ); 
 }

if("SERIALIZED_INV_ITEM".equals(inventoryTransferAndInventoryRequisitionGv.getString("InventoryItemTypeId"))){
	if(UtilValidate.isEmpty(locationSeqId)){
		 request.setAttribute("invRequisitionId", inventoryRequisitionItem.getString("invRequisitionId"));
		 request.setAttribute("_ERROR_MESSAGE_","Please select Location to complete.");
          return "error";
	}
}

dispatcher.runSync("updateInventoryTransfer",
		[userLogin:userLogin,
			inventoryItemId:inventoryTransferAndInventoryRequisitionGv.getString("inventoryItemId"),
			statusId:"IXF_COMPLETE",
			inventoryTransferId:inventoryTransferAndInventoryRequisitionGv.getString("inventoryTransferId"),
			locationSeqIdTo:locationSeqId
		]);
	
GenericValue inventoryRequisitionItemToStore = delegator.makeValue("InventoryRequisitionItem",UtilMisc.toMap("invReqItemId",invReqItemId,"acceptedQuantity",inventoryRequisitionItem.getBigDecimal("quantity"),"inTime",UtilDateTime.nowTimestamp()));
delegator.store(inventoryRequisitionItemToStore);

/*Below code for Complete Request Inventory Transfer */

EntityCondition inventoryRequisitionItemCondition = EntityCondition.makeCondition("invRequisitionId",EntityOperator.EQUALS,inventoryRequisitionItem.getString("invRequisitionId"));
List<GenericValue> inventoryRequisitionItemList = delegator.findList("InventoryRequisitionItem",inventoryRequisitionItemCondition,null,null,null,false);

int count = 0;
for(GenericValue gv: inventoryRequisitionItemList){
	if(gv.getBigDecimal("acceptedQuantity") != null && ( gv.getBigDecimal("quantity").compareTo(gv.getBigDecimal("acceptedQuantity")) == 0 ) ){
		count++;
	}
}

if(count == inventoryRequisitionItemList.size()){
	GenericValue inventoryRequisitionGvToStore = delegator.makeValue("InventoryRequisition",UtilMisc.toMap("invRequisitionId",inventoryRequisitionItem.getString("invRequisitionId"),"status","Complete","approvedByPartyId",userLogin.getString("partyId") ));
	delegator.store(inventoryRequisitionGvToStore);
}

request.setAttribute("invRequisitionId", inventoryRequisitionItem.getString("invRequisitionId"));
	
return "success";