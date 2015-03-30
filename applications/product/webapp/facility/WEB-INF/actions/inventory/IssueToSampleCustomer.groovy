import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilValidate;

request.setAttribute("facilityId",parameters.facilityId );

param = UtilHttp.getParameterMap(request);

BigDecimal quantity = BigDecimal.ZERO;

 if(!UtilValidate.isIntegers(param.quantity)){
request.setAttribute("_ERROR_MESSAGE_", "Quantity can only be numbers.");
	return "error";
}

if(UtilValidate.isNotEmpty(param.quantity)){
 quantity = new BigDecimal(param.quantity);
}


else
{
	request.setAttribute("_ERROR_MESSAGE_", "Quantity cannot be empty.");
	return "error";
}
if(UtilValidate.isNotEmpty(param.quantity)){
 quantity = new BigDecimal(param.quantity);
}

Map physicalInvMap = dispatcher.runSync("createPhysicalInventory",[userLogin:userLogin,
	physicalInventoryDate:UtilDateTime.nowTimestamp(),partyId:userLogin.getString("partyId")]);

String physicalInventoryId = physicalInvMap.get("physicalInventoryId");

 BigDecimal availableToPromiseTotal = new BigDecimal(param.availableToPromiseTotal);
 if(availableToPromiseTotal.compareTo(quantity) < 0){
	 request.setAttribute("_ERROR_MESSAGE_", "Quantity cannot be greater than Item QOH.");
	 return "error";
 }
 
	dispatcher.runSync("createInventoryItemVariance",[userLogin:userLogin,
		inventoryItemId:param.inventoryItemId,
		physicalInventoryId:physicalInventoryId,
		varianceReasonId:param.varianceReasonId,
		availableToPromiseVar:quantity.negate(),
		quantityOnHandVar:quantity.negate(),
		partyIdTo:param.partyIdTo,
		comments:param.comments,
		]);
	
return "success";