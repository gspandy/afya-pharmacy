import java.math.BigDecimal;

import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;

request.setAttribute("facilityId", parameters.facilityId);

param = UtilHttp.getParameterMap(request);

BigDecimal quantity = BigDecimal.ZERO;

if (UtilValidate.isEmpty(param.partyIdTo)) {
    request.setAttribute("_ERROR_MESSAGE_", "Please provide employee id.");
    return "error";
}


if (UtilValidate.isNotEmpty(param.quantity)) {
    quantity = new BigDecimal(param.quantity);
} else {
    request.setAttribute("_ERROR_MESSAGE_", "Quantity cannot be empty.");
    return "error";
}

Map physicalInvMap = dispatcher.runSync("createPhysicalInventory", [userLogin: userLogin,
        physicalInventoryDate: UtilDateTime.nowTimestamp(), partyId: userLogin.getString("partyId")]);

String physicalInventoryId = physicalInvMap.get("physicalInventoryId");


BigDecimal availableToPromiseTotal = new BigDecimal(param.availableToPromiseTotal.replaceAll(",", ""));


if (availableToPromiseTotal.compareTo(quantity) < 0) {
    request.setAttribute("_ERROR_MESSAGE_", "Quantity cannot be greater than Item ATP.");
    return "error";
}

GenericValue inventoryItemGv = delegator.findOne("InventoryItem", UtilMisc.toMap("inventoryItemId", param.inventoryItemId), false);
GenericValue productGv = delegator.findOne("Product", UtilMisc.toMap("productId", inventoryItemGv.getString("productId")), false);
dispatcher.runSync("createEmployeeItemIssuance" ,UtilMisc.toMap("inventoryItemId", param.inventoryItemId,"employeeId",param.partyIdTo,"quantity",quantity,"userLogin",userLogin));
if (!"Y".equals(productGv.getString("returnable"))) {
    GenericValue inventoryGv = delegator.findOne("InventoryItem", UtilMisc.toMap("inventoryItemId", param.inventoryItemId), false);
    BigDecimal unitCost = inventoryGv.getBigDecimal("unitCost");
    if (unitCost.compareTo(BigDecimal.ZERO) > 0) {
        BigDecimal totalAmount = quantity.multiply(unitCost);
        dispatcher.runSync("requisitionIssueTransactionEntry", [userLogin: userLogin, facilityId: parameters.facilityId, 
			amount: totalAmount.doubleValue(),"productId":inventoryItemGv.getString("productId"),"groupId":param.glAccountCategoryId]);
    }
}
return "success";