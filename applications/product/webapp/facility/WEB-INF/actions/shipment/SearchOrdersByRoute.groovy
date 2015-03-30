import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.model.DynamicViewEntity;
import org.ofbiz.entity.model.ModelKeyMap;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.widget.html.*;
import org.ofbiz.base.util.UtilMisc;
// **************************************
// Orders are searched  by routeId 
// **************************************

shipmentId = request.getParameter("shipmentId");
if (!shipmentId) {
    shipmentId = context.shipmentId;
	context.shipmentId = shipmentId;
}
action = request.getParameter("action");
shipment = null;
if (shipmentId) {
    shipment = delegator.findOne("Shipment", [shipmentId : shipmentId], false);
}
routeId = request.getParameter("routeId");
routeCust = null;
    if (routeId) {
        routeCust = delegator.findList("PartyShipmentRouteAssoc", EntityCondition.makeCondition([routeId : routeId]), null, null, null, false);
}
// **************************************
// CustomerPlan add form
// **************************************
 ordersFromRoute = [] as ArrayList;
 for(GenericValue routeItem : routeCust){
 ordersFromRoute.addAll(delegator.findList("OrderRole", EntityCondition.makeCondition([partyId : routeItem.partyId, roleTypeId : "SHIP_TO_CUSTOMER" ]),UtilMisc.toSet("orderId","partyId"), null, null, false));
 }
 context.ordersFromRoute = ordersFromRoute;
 request.setAttribute("routeId", routeId);
 
 
