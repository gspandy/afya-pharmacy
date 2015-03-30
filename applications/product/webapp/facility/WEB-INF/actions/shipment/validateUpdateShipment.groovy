import org.ofbiz.entity.condition.*
import org.ofbiz.widget.html.HtmlFormWrapper
import org.ofbiz.entity.*
import org.ofbiz.entity.condition.*
import org.ofbiz.entity.util.*
import org.ofbiz.entity.*

shipmentId = request.getParameter("shipmentId");
statusId = request.getParameter("statusId");
shipmentTypeId = request.getParameter("shipmentTypeId");
shipment = delegator.findOne("Shipment", [shipmentId : shipmentId], false);
if("OUTGOING_SHIPMENT".equals(shipment.getString("shipmentTypeId")) && "SALES_SHIPMENT".equals(shipmentTypeId)){
	if( "SHIPMENT_PICKED".equals(statusId)){
	request.setAttribute("_ERROR_MESSAGE_", "Outgoing Shipment cannot be updated to Sales Shipment, with status as PICKED");
	return "error";
	}
	if( "SHIPMENT_CANCELLED".equals(statusId)){
	request.setAttribute("_ERROR_MESSAGE_", "Outgoing Shipment cannot be updated to Sales Shipment, with status as CANCELLED");
	return "error";
	}
	
}

if("INCOMING_SHIPMENT".equals(shipment.getString("shipmentTypeId")) && "PURCHASE_SHIPMENT".equals(shipmentTypeId)){

	if( "PURCH_SHIP_SHIPPED".equals(statusId)){
	    request.setAttribute("_ERROR_MESSAGE_", "Incoming Shipment cannot be updated to Purchase Shipment, with status as SHIPPED");
	    return "error";
	}

	if( "PURCH_SHIP_RECEIVED".equals(statusId)){
	    request.setAttribute("_ERROR_MESSAGE_", "Incoming Shipment cannot be updated to Purchase Shipment, with status as RECEIVED");
	    return "error";
	}
}
	whereCondition = EntityCondition.makeCondition("shipmentId", EntityOperator.EQUALS, shipmentId);
	shipmentItem = delegator.findList("ShipmentItem", whereCondition, null, null, null, false);

	if("SALES_SHIPMENT".equals(shipment.getString("shipmentTypeId"))){
        if(!shipmentItem && "SHIPMENT_PICKED".equals(statusId)){
	        request.setAttribute("_ERROR_MESSAGE_", "Orders are not available in Shipment Plan");
	        return "error";
	    }
	}
		
	if("PURCHASE_SHIPMENT".equals(shipment.getString("shipmentTypeId"))){
	if(!shipmentItem && "PURCH_SHIP_SHIPPED".equals(statusId)){
	request.setAttribute("_ERROR_MESSAGE_", " Orders are not available in Shipment Plan");
	return "error";
	}
	if(!shipmentItem && "PURCH_SHIP_RECEIVED".equals(statusId)){
	request.setAttribute("_ERROR_MESSAGE_", " Orders are not available in Shipment Plan");
	return "error";
	}
	}
	orderShip = delegator.findList("OrderShipment", whereCondition, null, null, null, false);
	if(orderShip.size() >0){
	for(GenericValue genValue : orderShip ){
	shipmentReceipt = delegator.findList("ShipmentReceipt", EntityCondition.makeCondition([orderId : genValue.orderId,"shipmentId": shipmentId]), null, null, null, false);
	if(!shipmentReceipt && "PURCH_SHIP_RECEIVED".equals(statusId)){
		request.setAttribute("_ERROR_MESSAGE_", "Orders in shipment are not yet Received. Cannot update status to Received");
		return "error";
	}
	}
	}
	orderShipment = delegator.findList("OrderShipment", whereCondition, null, null, null, false);
	if(orderShipment.size() >0){
    GenericValue valu = EntityUtil.getFirst(orderShipment);
	orderId = valu.orderId;
	orderHeader = delegator.findOne("OrderHeader", [orderId : orderId], false);

    /*if ("SALES_ORDER".equals(orderHeader.orderTypeId)) {
    for(GenericValue value :orderShipment ){
		primaryOrderIdCondition = EntityCondition.makeCondition("primaryOrderId", EntityOperator.EQUALS, value.orderId);
		picklisBin = delegator.findList("PicklistBin", primaryOrderIdCondition, null, null, null, false);
		if(picklisBin.isEmpty()){
	  	if(!picklisBin && "SHIPMENT_PICKED".equals(statusId)){		
		request.setAttribute("_ERROR_MESSAGE_", "Orders in shipment are not yet Picked. Cannot update status  to Picked");
		return "error";
		}
		}
		if(picklisBin.size()>0){
		GenericValue picklisBinValue = EntityUtil.getFirst(picklisBin);
		Picklis = delegator.findOne("Picklist", [picklistId : picklisBinValue.picklistId], false);
	    if(!"PICKLIST_PICKED".equals(Picklis.getString("statusId"))){
		 request.setAttribute("_ERROR_MESSAGE_", "Orders in shipment are not yet Picked. Cannot update status  to Picked");
		 return "error";
    	}
		}
		}
	}*/
	}


	orderIssue = delegator.findList("OrderShipment", whereCondition, null, null, null, false);
	if(orderIssue.size() >0){
	for(GenericValue generic : orderIssue ){
	itemIssuance = delegator.findList("ItemIssuance", EntityCondition.makeCondition([orderId : generic.orderId]), null, null, null, false);
	if(!itemIssuance && "SHIPMENT_PACKED".equals(statusId)){
		request.setAttribute("_ERROR_MESSAGE_", "Orders in shipment are not yet Issued. Cannot update status to Packed");
		return "error";
	}
	if(!itemIssuance && "SHIPMENT_SHIPPED".equals(statusId)){
		request.setAttribute("_ERROR_MESSAGE_", "Orders in shipment are not yet Issued. Cannot update status to Shipped");
		return "error";
	}
	}
	}
	
	

request.setAttribute("shipmentId", shipmentId);
request.setAttribute("statusId", statusId);
return "success";
