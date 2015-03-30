import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.entity.condition.*;


List entConds = new ArrayList();

timeStampFrom =  UtilDateTime.addDaysToTimestamp(UtilDateTime.nowTimestamp(),-30);
entConds.add(EntityCondition.makeCondition("createdTxStamp", EntityOperator.GREATER_THAN_EQUAL_TO, timeStampFrom));
entConds.add(EntityCondition.makeCondition("createdTxStamp", EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.nowTimestamp()));
//entConds.add(EntityCondition.makeCondition("workEffortTypeId", EntityOperator.EQUALS, "PROD_ORDER_TASK"));
entConds.add(EntityCondition.makeCondition("pdProductTypeId", EntityOperator.EQUALS, "SCRAP_GOOD"));
//entConds.add(EntityCondition.makeCondition("workEffortGoodStdTypeId", EntityOperator.EQUALS, ""));


EntityCondition condition =  EntityCondition.makeCondition(entConds);

scrapProductList = delegator.findList("ItemProducedAndProductInv", condition, null, null, null, false);
context.scrapProductList = scrapProductList;