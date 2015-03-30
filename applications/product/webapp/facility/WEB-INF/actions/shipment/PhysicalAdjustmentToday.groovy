import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.entity.condition.*;


List<EntityCondition> allCondition = [] as ArrayList;
EntityCondition condition = null;

	List andExprs = new ArrayList();
	andExprs.add(EntityCondition.makeCondition("createdStamp", EntityOperator.GREATER_THAN_EQUAL_TO,
		UtilDateTime.getDayStart(filterDate)));
		andExprs.add(EntityCondition.makeCondition("createdStamp", EntityOperator.LESS_THAN_EQUAL_TO,
		UtilDateTime.getDayEnd(filterDate)));
	allCondition.add(EntityCondition.makeCondition(andExprs, EntityOperator.AND));
	allCondition.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, facilityId));	
EntityCondition whereEntityCondition = EntityCondition.makeCondition(allCondition);

physicalVarianceToday = delegator.findList("InventoryItemVarianceInventoryItemView", whereEntityCondition, null, null, null, false);

context.physicalVarianceToday = physicalVarianceToday;
context.filterDate = filterDate;