import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilValidate;
import java.util.*;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericValue;

param = UtilHttp.getParameterMap(request);

String paramQty = param.quantity;
if(UtilValidate.isEmpty(paramQty)){
	request.setAttribute("_ERROR_MESSAGE_", "Quantity cannot be empty.");
	return "error";
}
request.setAttribute("facilityId",param.facilityId );

BigDecimal quantity = BigDecimal.ZERO;
if(paramQty != null){
 quantity = new BigDecimal(paramQty);
}


BigDecimal issueToSubContractQty = BigDecimal.ZERO;
List invItemVarianceIssSubContact = delegator.findByAnd("InventoryItemVariance",UtilMisc.toMap("inventoryItemId",param.inventoryItemId,"varianceReasonId","ISSUE_SUB_CONTRACT"));
for(GenericValue gv : invItemVarianceIssSubContact){
	issueToSubContractQty = issueToSubContractQty.add(gv.getBigDecimal("availableToPromiseVar").negate());
}

BigDecimal receiveSubContractQty = BigDecimal.ZERO;
List invItemVarianceRcvSubContact = delegator.findByAnd("InventoryItemVariance",UtilMisc.toMap("inventoryItemId",param.inventoryItemId,
	"varianceReasonId","RCV_SUB_CONTRACT"));
for(GenericValue gv : invItemVarianceRcvSubContact){
	receiveSubContractQty = receiveSubContractQty.add(gv.getBigDecimal("availableToPromiseVar"));
}

BigDecimal remainingQty = issueToSubContractQty.minus(receiveSubContractQty);

if(quantity.compareTo(issueToSubContractQty) > 0 || quantity.compareTo(remainingQty) > 0){
	request.setAttribute("_ERROR_MESSAGE_", "Quantity cannot be greater than issue quantity.");
	return "error";
}

Map physicalInvMap = dispatcher.runSync("createPhysicalInventory",[userLogin:userLogin,
	physicalInventoryDate:UtilDateTime.nowTimestamp(),partyId:userLogin.getString("partyId")]);

String physicalInventoryId = physicalInvMap.get("physicalInventoryId");


dispatcher.runSync("createInventoryItemVariance",[userLogin:userLogin,
		inventoryItemId:param.inventoryItemId,
		physicalInventoryId:physicalInventoryId,
		varianceReasonId:"RCV_SUB_CONTRACT",
		availableToPromiseVar:quantity,
		quantityOnHandVar:BigDecimal.ZERO,
		partyIdTo:param.partyIdTo,
		comments:param.comments,
		]);
	
return "success";
