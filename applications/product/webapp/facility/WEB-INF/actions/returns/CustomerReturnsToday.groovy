import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.entity.condition.*;

List<EntityCondition> allCondition = [] as ArrayList;
EntityCondition condition = null;
facilityId=parameters.facilityId;
List andExprs = new ArrayList();

andExprs.add(EntityCondition.makeCondition("lastUpdatedStamp", EntityOperator.GREATER_THAN_EQUAL_TO,UtilDateTime.getDayStart(filterDate)));
andExprs.add(EntityCondition.makeCondition("returnHeaderTypeId", EntityOperator.EQUALS,"CUSTOMER_RETURN"));

allCondition.add(EntityCondition.makeCondition(andExprs, EntityOperator.AND));
allCondition.add(EntityCondition.makeCondition("destinationFacilityId", EntityOperator.EQUALS, facilityId));
EntityCondition whereEntityCondition = EntityCondition.makeCondition(allCondition);

retunHeaderList = delegator.findList("ReturnHeader", whereEntityCondition, null, null, null, false);

context.retunHeaderList = retunHeaderList;