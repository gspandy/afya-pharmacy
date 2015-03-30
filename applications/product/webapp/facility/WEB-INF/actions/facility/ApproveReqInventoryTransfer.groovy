import java.text.SimpleDateFormat;

import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;


requestId = request.getParameter("requestId");
senderNote = request.getParameter("senderNote");
transferDateStr = request.getParameter("transferDate");
SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
if(UtilValidate.isEmpty(transferDateStr)){
	request.setAttribute("_ERROR_MESSAGE_", "Transfer Date can't be empty");
	request.setAttribute("invRequisitionId", requestId);
	return "error";
}
Date parsedDate = dateFormat.parse(transferDateStr);
Timestamp transferDate = new java.sql.Timestamp(parsedDate.getTime());

GenericValue requestInvTransferGv = delegator.findOne("InventoryRequisition", ["invRequisitionId" : requestId], false);
List<GenericValue> inventoryRequisitionItemList = delegator.findList("InventoryRequisitionItem",EntityCondition.makeCondition("invRequisitionId",requestId),null,null,null,false);


for(GenericValue inventoryRequisitionItemGv : inventoryRequisitionItemList){
	String productId = inventoryRequisitionItemGv.getString("productId");
	BigDecimal totalQuantity = inventoryRequisitionItemGv.getBigDecimal("quantity");
	
	List<GenericValue> inventoryItemList = delegator.findByAnd("InventoryItem",UtilMisc.toMap("productId",productId),null,false);
	if(UtilValidate.isNotEmpty(inventoryItemList) && "SERIALIZED_INV_ITEM".equals(inventoryItemList.get(0).getString("inventoryItemTypeId")) ){
		
		if(totalQuantity.compareTo(inventoryItemList.size()) == -1 ){
			request.setAttribute("_ERROR_MESSAGE_", "Inventory not available for this serialized product.");
			return "error";
		}
		BigDecimal qty = 1;
		
		if(totalQuantity.compareTo(qty) != -1){
			GenericValue inventoryItem = delegator.findByAnd("InventoryItem",UtilMisc.toMap("productId",productId,"inventoryItemTypeId","SERIALIZED_INV_ITEM","availableToPromiseTotal",qty),null,false).get(0);
			dispatcher.runSync("createInventoryTransfer",
				[userLogin:userLogin,
				inventoryItemId:inventoryItem.getString("inventoryItemId"),
				statusId:"IXF_REQUESTED",
				facilityId:requestInvTransferGv.getString("facilityIdFrom"),
				facilityIdTo:requestInvTransferGv.getString("facilityIdTo"),
				productId:productId,
				xferQty:qty,
				invRequisitionId:requestId]);
			
			totalQuantity = totalQuantity.subtract(qty);
	     }
		
	}else{
	dispatcher.runSync("createInventoryTransfersForProduct",
		[userLogin:userLogin,
		facilityId:requestInvTransferGv.getString("facilityIdFrom"),
		facilityIdTo:requestInvTransferGv.getString("facilityIdTo"),
		productId:productId,
		quantity:totalQuantity,
		invRequisitionId:requestId]);
	}
	
	GenericValue gvToStore = delegator.makeValue("InventoryRequisitionItem", UtilMisc.toMap("invReqItemId",inventoryRequisitionItemGv.getString("invReqItemId"),"outTime",transferDate));
	delegator.store(gvToStore);
}


GenericValue inventoryRequisitionGv = delegator.makeValue("InventoryRequisition",UtilMisc.toMap("invRequisitionId",requestId,
	"status","Approved","senderNote",senderNote,"transferDate",transferDate,"approvedByPartyId",userLogin.getString("partyId")));
delegator.store(inventoryRequisitionGv,false);

return "success";