
import org.ofbiz.entity.condition.*
import org.ofbiz.widget.html.HtmlFormWrapper
import org.ofbiz.entity.*
import org.ofbiz.entity.condition.*
import org.ofbiz.entity.util.*
import org.ofbiz.entity.*


shipmentId = request.getParameter("shipmentId");
statusId = request.getParameter("statusId");
shipment = delegator.findOne("Shipment", [shipmentId : shipmentId], false);
whereCondition = EntityCondition.makeCondition("shipmentId", EntityOperator.EQUALS, shipmentId);
    orderShip = delegator.findList("OrderShipment", whereCondition, null, null, null, false);
	if(orderShip.size() >0){	
     for(GenericValue value : orderShip ){
    ordInv = delegator.findList("OrderItemBilling",  EntityCondition.makeCondition([orderId : value.orderId]), null, null, null, false);
    if(ordInv){
		request.setAttribute("_ERROR_MESSAGE_", "Invoice is already Generated");
		return "error";
	}
	}
	}
	
	
     shipmentItem = delegator.findList("ShipmentItem", whereCondition, null, null, null, false);   
    if(!shipmentItem){
		request.setAttribute("_ERROR_MESSAGE_", "Orders are not available in Shipment Plan");
		return "error";
		}
		
	orderShip = delegator.findList("OrderShipment", whereCondition, null, null, null, false);
	if(orderShip.size() >0){
	if("PURCHASE_SHIPMENT".equals(shipment.getString("shipmentTypeId"))){
	for(GenericValue genValue : orderShip ){
	shipmentReceipt = delegator.findList("ShipmentReceipt", EntityCondition.makeCondition([orderId : genValue.orderId]), null, null, null, false);	
	if(!shipmentReceipt){
		request.setAttribute("_ERROR_MESSAGE_", "Orders in shipment are not yet Received. Cannot generate Invoice");
		return "error";
	}
	}
	}
	}
	orderShip = delegator.findList("OrderShipment", whereCondition, null, null, null, false);
	if(orderShip.size() >0){
	if("SALES_SHIPMENT".equals(shipment.getString("shipmentTypeId"))){
	for(GenericValue value : orderShip ){
	itemIssuances = delegator.findList("ItemIssuance", EntityCondition.makeCondition([orderId : value.orderId]), null, null, null, false);	
	if(!itemIssuances){
		request.setAttribute("_ERROR_MESSAGE_", "Orders in shipment are not yet Issued. Cannot generate Invoice");
		return "error";
	}
	}
	}
	}
		
result = dispatcher.runSync("createInvoicesFromShipment",["shipmentId":shipmentId,"userLogin":userLogin]);
request.setAttribute("invoiceId", result.invoiceId);
itemInvoices = delegator.findList("ShipmentItemBilling", whereCondition, null, null, null, false);
if(itemInvoices.size() >0){
String s2 = null;
for(GenericValue value : itemInvoices ){
String s3 = null;
s3 = value.invoiceId;
if(s2 == null){
s2 =s3;
}
else{
s2 = s3 + ", "+s2;
}
}
request.setAttribute("_EVENT_MESSAGE_", "Invoice created successfully");
}
shipmentReceipt = delegator.findList("ShipmentReceipt", whereCondition, null, null, null, false);
if(shipmentReceipt.size() >0){
String s1 = null;
for(GenericValue genValue : shipmentReceipt ){
orderInvoices = delegator.findList("OrderItemBilling",  EntityCondition.makeCondition([orderId : genValue.orderId]), null, null, null, false);
String s = null;
s = orderInvoices.invoiceId;
if(s1 == null){
s1 =s;
}
else{
s1 = s1 + ", "+s;
}
}
request.setAttribute("_EVENT_MESSAGE_", "Invoice created successfully");
}


return "success";