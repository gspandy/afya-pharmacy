import org.ofbiz.entity.transaction.*;

boolean beganTrans = TransactionUtil.begin();
request.setAttribute("facilityId", parameters.facilityId);

try{
	qtyToBundle  = new BigDecimal(parameters.quantityToBundle);
	inventoryItem = delegator.findOne("InventoryItem",false,"inventoryItemId",parameters.inventoryItemId);
	if(inventoryItem.availableToPromiseTotal.compareTo(qtyToBundle)==-1)
	{
		request.setAttibute("_ERROR_MESSAGE_","ATP of InventoryItem "+inventoryItemId+"has to be greater than or equal to Quantity To Bundle.");
		return "error";
	}
	txnDetail = delegator.makeValue("ProductBundleTxnDetail");
	txnDetail.productBundleTxnId=parameters.productBundleTxnId;
	txnDetail.inventoryItemId = parameters.inventoryItemId;
	txnDetail.baseSkuId= parameters.productIdTo;
	txnDetail.quantityToUse = new BigDecimal(parameters.quantityToBundle);
	
	delegator.create(txnDetail);
	
	//DO Reservation
	dispatcher.runSync("createInventoryItemDetail",
	                   [userLogin:userLogin,
	                   inventoryItemId:parameters.inventoryItemId,
	                   availableToPromiseDiff:qtyToBundle.negate(),
	                   quantityOnHandDiff:BigDecimal.ZERO
	                   ]
	                   );	
	
	request.setAttribute("productBundleTxnId",parameters.productBundleTxnId);
	if(beganTrans)
	  TransactionUtil.commit();

}catch(all){
	if(beganTrans)
		TransactionUtil.rollback();
}
 
return "success";