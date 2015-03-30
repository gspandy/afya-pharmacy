import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.entity.condition.*;


List<EntityCondition> allCondition = [] as ArrayList;
EntityCondition condition = null;
facilityId=parameters.facilityId;
	List andExprs = new ArrayList();
	andExprs.add(EntityCondition.makeCondition("estimatedShipDate", EntityOperator.LESS_THAN,
		UtilDateTime.getDayStart(filterDate)));
	allCondition.add(EntityCondition.makeCondition(andExprs, EntityOperator.AND));
	condition2 = [EntityCondition.makeCondition("shipmentTypeId", EntityOperator.EQUALS, "SALES_SHIPMENT"),
             EntityCondition.makeCondition("shipmentTypeId", EntityOperator.EQUALS, "PURCHASE_RETURN")];
	condition = EntityCondition.makeCondition(condition2, EntityOperator.OR);
	allCondition.add(condition);
	allCondition.add(EntityCondition.makeCondition("originFacilityId", EntityOperator.EQUALS, facilityId));
	condition1 = [EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "SHIPMENT_INPUT"),
             EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "SHIPMENT_PICKED"),
             EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "SHIPMENT_PACKED")];
	condition = EntityCondition.makeCondition(condition1, EntityOperator.OR);
	allCondition.add(condition);
	
EntityCondition whereEntityCondition = EntityCondition.makeCondition(allCondition);

outgoingShipmentList = delegator.findList("Shipment", whereEntityCondition, null, null, null, false);

context.outgoingShipmentList = outgoingShipmentList;
context.filterDate = filterDate;
