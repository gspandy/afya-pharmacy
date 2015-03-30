import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;





workEffortTypeId = parameters.workEffortTypeId;
productionRunId = parameters.productionRunId;
fixedAssetId = parameters.fixedAssetId;


// search by productionRunId is mandatory
conditions = [EntityCondition.makeCondition("productionRunId", EntityOperator.EQUALS, productionRunId)];

if (fromDate) {
	conditions.add(EntityCondition.makeCondition("", EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.getDayStart(fromDate)));
}
if (thruDate) {
	conditions.add(EntityCondition.makeCondition("", EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.getDayEnd(thruDate)));
}



context.fromDate = fromDate;
context.thruDate = thruDate;
context.productionRunId = productionRunId;
