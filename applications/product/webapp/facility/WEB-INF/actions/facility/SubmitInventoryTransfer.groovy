import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;



Map<String,String> headerMap = session.getAttribute("headerMap");
List<Map<String,String>> requestItemList = session.getAttribute("requestItemList");

GenericValue userLogin = session.getAttribute("userLogin");
String partyId = userLogin.getString("partyId");

	GenericValue facility = delegator.findOne("Facility",UtilMisc.toMap("facilityId",headerMap.get("facilityIdFrom")),false);
	String seqIdRIT = null;
	if(headerMap.get("invRequisitionId"))
		seqIdRIT = headerMap.get("invRequisitionId");
	else
		seqIdRIT = delegator.getNextSeqId("InventoryRequisition");
		
    Map invReqMap = UtilMisc.toMap("invRequisitionId",seqIdRIT,"requestType",headerMap.get("requestType"),
		"status","Requested","facilityIdFrom",headerMap.get("facilityIdFrom"),"facilityIdTo",headerMap.get("facilityIdTo"),
		"createdByPartyId",partyId,"approvalPartyId",facility.getString("ownerPartyId"));
    
   if(UtilValidate.isEmpty(headerMap.get("invRequisitionId")))
   		invReqMap.put("requestDate", UtilDateTime.dateStringToTimestampParser(headerMap.get("requestDate")));
	
	GenericValue inventoryRequisitionGv = delegator.makeValue("InventoryRequisition", invReqMap);
	delegator.createOrStore(inventoryRequisitionGv);
	
	for(Map<String,String> requestItem : requestItemList){
		String seqIdRITP = null; 
		if(requestItem.get("invReqItemId"))
			seqIdRITP = requestItem.get("invReqItemId");
		else
			seqIdRITP = delegator.getNextSeqId("InventoryRequisitionItem");
			
		GenericValue inventoryRequisitionItemGv = delegator.makeValue("InventoryRequisitionItem",UtilMisc.toMap("invReqItemId",seqIdRITP,"invRequisitionId",seqIdRIT,
			"productId",requestItem.get("productId"),"quantity",new BigDecimal(requestItem.get("quantity")),"note",requestItem.get("note"),
			"glAccountCategoryId",requestItem.get("glAccountCategoryId"),"returnable",requestItem.get("returnable") ));
		delegator.createOrStore(inventoryRequisitionItemGv);
	}
if("Return".equals(headerMap.get("requestType"))){
	for(Map<String,String> requestItem : requestItemList){
		
		String productId = requestItem.get("productId");
		BigDecimal totalQuantity = new BigDecimal(requestItem.get("quantity"));
		
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
					facilityId:headerMap.get("facilityIdTo"),
					facilityIdTo:headerMap.get("facilityIdFrom"),
					productId:productId,
					xferQty:qty,
					invRequisitionId:seqIdRIT]);
				
				totalQuantity = totalQuantity.subtract(qty);
			 }
			
		}else{
		
		dispatcher.runSync("createInventoryTransfersForProduct",
			[userLogin:userLogin,
			facilityId:headerMap.get("facilityIdTo"),
			facilityIdTo:headerMap.get("facilityIdFrom"),productId:requestItem.get("productId"),invRequisitionId:seqIdRIT,quantity:requestItem.get("quantity")]);
		}
	}
	
}

return "success";