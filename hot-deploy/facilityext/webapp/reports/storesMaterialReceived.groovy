import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;


productId = parameters.productId;
supplierId = parameters.partyId;

conditions = [];
if (fromDate) {
	conditions.add(EntityCondition.makeCondition("", EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.getDayStart(fromDate)));
}
if (thruDate) {
	conditions.add(EntityCondition.makeCondition("", EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.getDayEnd(thruDate)));
}



context.fromDate = fromDate;
context.thruDate = thruDate;
