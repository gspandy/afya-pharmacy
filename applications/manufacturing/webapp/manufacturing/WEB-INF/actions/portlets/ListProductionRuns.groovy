import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.base.util.UtilMisc;
import java.util.*;

List conds = new ArrayList();
conds.clear();
toTimestamp = UtilDateTime.addDaysToTimestamp(UtilDateTime.nowTimestamp(),2);
conds.add(EntityCondition.makeCondition("workEffortTypeId",EntityOperator.EQUALS,"PROD_ORDER_HEADER"));


if(portletProdRunType.equals("PRODRUNSINPROCESS")){
	List statuses = new ArrayList();
	statuses.add("PRUN_RUNNING");
	conds.add(EntityCondition.makeCondition("currentStatusId", EntityOperator.EQUALS,"PRUN_RUNNING"));
}
if(portletProdRunType.equals("PRODRUNSSCEDULED")){
	conds.add(EntityCondition.makeCondition("estimatedStartDate",EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.nowTimestamp()));
	conds.add(EntityCondition.makeCondition("estimatedStartDate",EntityOperator.LESS_THAN_EQUAL_TO, toTimestamp));
	conds.add(EntityCondition.makeCondition("currentStatusId", EntityOperator.EQUALS,"PRUN_SCHEDULED"));
}

EntityCondition condition = EntityCondition.makeCondition(conds);

productionRunList = delegator.findList("WorkEffortProductGoodsFacUom", condition, null, ["facilityName ASC"], null, false);

context.productionRunList = productionRunList;
