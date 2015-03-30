import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.entity.condition.*;


List<EntityCondition> allCondition = [] as ArrayList;
EntityCondition condition = null;

	List andExprs = new ArrayList();
	andExprs.add(EntityCondition.makeCondition("estimatedArrivalDate", EntityOperator.LESS_THAN,
		UtilDateTime.getDayStart(filterDate)));
	allCondition.add(EntityCondition.makeCondition(andExprs, EntityOperator.AND));
		allCondition.add(EntityCondition.makeCondition("destinationFacilityId", EntityOperator.EQUALS, facilityId));
		
	condition2 = [EntityCondition.makeCondition("shipmentTypeId", EntityOperator.EQUALS, "PURCHASE_SHIPMENT"),
             EntityCondition.makeCondition("shipmentTypeId", EntityOperator.EQUALS, "SALES_RETURN")];
	condition = EntityCondition.makeCondition(condition2, EntityOperator.OR);
	allCondition.add(condition);
	
	condition1 = [EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "PURCH_SHIP_CREATED"),
             EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "PURCH_SHIP_SHIPPED")];
	condition = EntityCondition.makeCondition(condition1, EntityOperator.OR);
	allCondition.add(condition);
	
EntityCondition whereEntityCondition = EntityCondition.makeCondition(allCondition);

incomingShipmentList = delegator.findList("Shipment", whereEntityCondition, null, null, null, false);

context.incomingShipmentList = incomingShipmentList;
context.filterDate = filterDate;
session.setAttribute("facilityId",parameters.facilityId);