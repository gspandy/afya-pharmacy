import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilValidate;
import java.util.*;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericValue;

param = UtilHttp.getParameterMap(request);


GenericValue inventoryItemGv = delegator.findOne("InventoryItem",UtilMisc.toMap("inventoryItemId",param.inventoryItemId),false);
GenericValue productGv = delegator.findOne("Product",UtilMisc.toMap("productId",inventoryItemGv.getString("productId")),false);
if(!"Y".equals(productGv.getString("returnable")) ){
	request.setAttribute("_ERROR_MESSAGE_", "Product is not a returnable product.");
	return "error";
}

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


BigDecimal issueToEmployeeQty = BigDecimal.ZERO;
List invItemVarianceIssEmp = delegator.findByAnd("InventoryItemVariance",UtilMisc.toMap("inventoryItemId",param.inventoryItemId,"varianceReasonId","ISSUE_EMPLOYEE"));
for(GenericValue gv : invItemVarianceIssEmp){
	issueToEmployeeQty = issueToEmployeeQty.add(gv.getBigDecimal("availableToPromiseVar").negate());
}

BigDecimal receiveEmpQty = BigDecimal.ZERO;
List invItemVarianceRcvEmp = delegator.findByAnd("InventoryItemVariance",UtilMisc.toMap("inventoryItemId",param.inventoryItemId,
	"varianceReasonId","RCV_EMPLOYEE"));
for(GenericValue gv : invItemVarianceRcvEmp){
	receiveEmpQty = receiveEmpQty.add(gv.getBigDecimal("availableToPromiseVar"));
}

BigDecimal remainingQty = issueToEmployeeQty.minus(receiveEmpQty);

if(quantity.compareTo(issueToEmployeeQty) > 0 || quantity.compareTo(remainingQty) > 0){
	request.setAttribute("_ERROR_MESSAGE_", "Quantity cannot be greater than issue quantity.");
	return "error";
}

Map physicalInvMap = dispatcher.runSync("createPhysicalInventory",[userLogin:userLogin,
	physicalInventoryDate:UtilDateTime.nowTimestamp(),partyId:userLogin.getString("partyId")]);

String physicalInventoryId = physicalInvMap.get("physicalInventoryId");

if("Y".equals(productGv.getString("returnable"))){
	dispatcher.runSync("createInventoryItemVariance",[userLogin:userLogin,
		inventoryItemId:param.inventoryItemId,
		physicalInventoryId:physicalInventoryId,
		varianceReasonId:"RCV_EMPLOYEE",
		availableToPromiseVar:quantity,
		quantityOnHandVar:BigDecimal.ZERO,
		partyIdTo:param.partyIdTo,
		comments:param.comments,
		]);
}else{
	dispatcher.runSync("createInventoryItemVariance",[userLogin:userLogin,
		inventoryItemId:param.inventoryItemId,
		physicalInventoryId:physicalInventoryId,
		varianceReasonId:"RCV_EMPLOYEE",
		availableToPromiseVar:quantity,
		quantityOnHandVar:quantity,
		partyIdTo:param.partyIdTo,
		comments:param.comments,
		]);
}


	
return "success";
