import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;

SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
if(UtilValidate.isEmpty(parameters.effectiveDate)){
	request.setAttribute("_ERROR_MESSAGE_", "Date can't be empty");
	return "error";
}
Date parsedDate = dateFormat.parse(parameters.effectiveDate+" "+"00:00:00");
Timestamp effectiveDate = new java.sql.Timestamp(parsedDate.getTime());

String inventoryItemId = request.getParameter("inventoryItemId");

GenericValue inventoryItem = delegator.findOne("InventoryItem",UtilMisc.toMap("inventoryItemId",inventoryItemId),false);

List<GenericValue> productAttributes = delegator.findByAnd("ProductAttribute",UtilMisc.toMap("productId",inventoryItem.getString("productId")),null,false);
for(GenericValue productAttribute : productAttributes){
	GenericValue inventoryItemAttributes = delegator.makeValue("InventoryItemAttribute",UtilMisc.toMap("inventoryItemId",inventoryItemId,"attrName",productAttribute.getString("attrName"),"attrValue",request.getParameter(productAttribute.getString("attrName")),"effectiveDate",effectiveDate ));
	delegator.createOrStore(inventoryItemAttributes);
}

return "success";