import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;



String invReqItemId = request.getParameter("id");

GenericValue inventoryRequisitionItem = delegator.findOne("InventoryRequisitionItem", UtilMisc.toMap("invReqItemId",invReqItemId), false);

/* Below code to get the inventory transfer id */

GenericValue inventoryTransferAndInventoryRequisitionGv = delegator.findByAnd("InventoryTransferAndInventoryRequisition",
		UtilMisc.toMap("invRequisitionId",inventoryRequisitionItem.getString("invRequisitionId"),"productId",inventoryRequisitionItem.getString("productId")),null, false).get(0);

dispatcher.runSync("updateInventoryTransfer",
		[userLogin:userLogin,
		inventoryItemId:inventoryTransferAndInventoryRequisitionGv.getString("inventoryItemId"),
		statusId:"IXF_COMPLETE",
		inventoryTransferId:inventoryTransferAndInventoryRequisitionGv.getString("inventoryTransferId")
		]);
	
GenericValue inventoryRequisitionItemToStore = delegator.makeValue("InventoryRequisitionItem",UtilMisc.toMap("invReqItemId",invReqItemId,"acceptedQuantity",inventoryRequisitionItem.getBigDecimal("quantity")));
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
	GenericValue inventoryRequisitionGvToStore = delegator.makeValue("InventoryRequisition",UtilMisc.toMap("invRequisitionId",inventoryRequisitionItem.getString("invRequisitionId"),"status","Complete"));
	delegator.store(inventoryRequisitionGvToStore);
}

dispatcher.runSync("createPhysicalInventoryAndVariance",
	[userLogin:userLogin,
	inventoryItemId:inventoryTransferAndInventoryRequisitionGv.getString("inventoryItemId"),
	partyId:userLogin.getString("partyId"),
	varianceReasonId:"VAR_DAMAGED",
	quantity:inventoryRequisitionItem.getString("quantity")
	]);
	
return "success";
