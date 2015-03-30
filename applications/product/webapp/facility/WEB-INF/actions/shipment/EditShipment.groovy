import org.ofbiz.entity.condition.*
import org.ofbiz.widget.html.HtmlFormWrapper
import org.ofbiz.entity.*
import org.ofbiz.entity.condition.*

shipmentId = request.getParameter("shipmentId");
statusId = request.getParameter("statusId");
if (!shipmentId) {
   shipmentId = request.getAttribute("shipmentId");
}
shipment = delegator.findOne("Shipment", [shipmentId : shipmentId], false);

// orderHeader is needed here to determine type of order and hence types of shipment status
if (!shipment) {
    primaryOrderId = request.getParameter("primaryOrderId");
} else {
    primaryOrderId = shipment.primaryOrderId;
}
orderHeader = delegator.findOne("OrderHeader", [orderId : primaryOrderId], false);

HtmlFormWrapper editShipmentWrapper = new HtmlFormWrapper("component://product/widget/facility/ShipmentForms.xml", "EditShipment", request, response);
editShipmentWrapper.putInContext("shipmentId", shipmentId);
editShipmentWrapper.putInContext("shipment", shipment);
editShipmentWrapper.putInContext("productStoreId", null); // seems to be needed not exist != null

if (!shipment) {
    editShipmentWrapper.setUseRequestParameters(true);
}

// the kind of StatusItem to use is based on the type of order


if (orderHeader && "PURCHASE_ORDER".equals(orderHeader.orderTypeId)) {
    statusItemType = "PURCH_SHIP_STATUS";
} else {
    statusItemType = "SHIPMENT_STATUS";
}

editShipmentWrapper.putInContext("statusItemType", statusItemType);

context.shipmentId = shipmentId;
context.shipment = shipment;
context.editShipmentWrapper = editShipmentWrapper;

if (shipment) {
    currentStatus = shipment.getRelatedOne("StatusItem");
    currentShipStatus = shipment.getRelatedOne("ShipmentType");
    originPostalAddress = shipment.getRelatedOne("OriginPostalAddress");
    destinationPostalAddress = shipment.getRelatedOne("DestinationPostalAddress");
    originTelecomNumber = shipment.getRelatedOne("OriginTelecomNumber");
    destinationTelecomNumber = shipment.getRelatedOne("DestinationTelecomNumber");
    toPerson = shipment.getRelatedOne("ToPerson");
    toPartyGroup = shipment.getRelatedOne("ToPartyGroup");
    fromPerson = shipment.getRelatedOne("FromPerson");
    fromPartyGroup = shipment.getRelatedOne("FromPartyGroup");
    primaryOrderId = shipment.getString("primaryOrderId");

    statusDescription = currentShipStatus.description;

    editShipmentWrapper.putInContext("currentStatus", currentStatus);
    editShipmentWrapper.putInContext("currentShipStatus", statusDescription);
    editShipmentWrapper.putInContext("originPostalAddress", originPostalAddress);
    editShipmentWrapper.putInContext("destinationPostalAddress", destinationPostalAddress);
    editShipmentWrapper.putInContext("originTelecomNumber", originTelecomNumber);
    editShipmentWrapper.putInContext("destinationTelecomNumber", destinationTelecomNumber);
    editShipmentWrapper.putInContext("toPerson", toPerson);
    editShipmentWrapper.putInContext("toPartyGroup", toPartyGroup);
    editShipmentWrapper.putInContext("fromPerson", fromPerson);
    editShipmentWrapper.putInContext("fromPartyGroup", fromPartyGroup);
    editShipmentWrapper.putInContext("orderHeader", orderHeader);
    if (orderHeader) {
        editShipmentWrapper.putInContext("productStoreId", orderHeader.get("productStoreId"));
    }

    context.currentStatus = currentStatus;
    context.currentShipStatus = currentShipStatus;
    context.originPostalAddress = originPostalAddress;
    context.destinationPostalAddress = destinationPostalAddress;
    context.originTelecomNumber = originTelecomNumber;
    context.destinationTelecomNumber = destinationTelecomNumber;
    context.toPerson = toPerson;
    context.toPartyGroup = toPartyGroup;
    context.fromPerson = fromPerson;
    context.fromPartyGroup = fromPartyGroup;

    if (primaryOrderId) {
        ord = delegator.findOne("OrderHeader", [orderId : primaryOrderId], false);
        pfc = delegator.findList("ProductStoreFacility", null, null, null, null, false);
        fac = delegator.findList("ProductStoreFacilityByOrder", EntityCondition.makeCondition([orderId : primaryOrderId]), null, null, null, false);
    }
}

context.shipmentTypes=delegator.findList("ShipmentType",EntityCondition.makeCondition("parentTypeId",null),null,null,null,true);