
 import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.entity.condition.*;


List<EntityCondition> allCondition = [] as ArrayList;
EntityCondition condition = null;
facilityId=parameters.facilityId;
	List andExprs = new ArrayList();
	andExprs.add(EntityCondition.makeCondition("datetimeReceived", EntityOperator.GREATER_THAN_EQUAL_TO,
		UtilDateTime.getDayStart(filterDate)));
		andExprs.add(EntityCondition.makeCondition("datetimeReceived", EntityOperator.LESS_THAN_EQUAL_TO,
		UtilDateTime.getDayEnd(filterDate)));
	allCondition.add(EntityCondition.makeCondition(andExprs, EntityOperator.AND));
	
	condition=  EntityCondition.makeCondition("locationTypeEnumId", EntityOperator.EQUALS, "FLT_PICKLOC");
	allCondition.add(condition);
	allCondition.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, facilityId));
EntityCondition whereEntityCondition = EntityCondition.makeCondition(allCondition);

stockMoveToday = delegator.findList("OrderItemShipGrpInvResAndItemLocation", whereEntityCondition, null, null, null, false);

context.stockMoveToday = stockMoveToday;
context.filterDate = filterDate;
 
