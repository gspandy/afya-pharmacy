import org.ofbiz.humanresext.util.HumanResUtil;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.humanresext.util.HumanResUtil;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityOperator;
import java.sql.Timestamp;

String partyId = userLogin.get("partyId");
String leaveId = parameters.leaveId;
GenericValue emplLeave = delegator.findOne("EmplLeave", false, "leaveId", leaveId); //"partyId", parameters.partyId, "fromDate", Timestamp.valueOf(parameters.fromDate));
context.leave = emplLeave;
Object fromDate = emplLeave.get("fromDate");
Object thruDate = emplLeave.get("thruDate");

GenericValue position = HumanResUtil.getEmplPositionForParty(partyId, delegator);
if(!position) 
	return;

// Stuff to get related employees on leave during the same period

EntityCondition positionCondition = EntityCondition.makeCondition("mgrPositionId",position.get("emplPositionId"));

EntityCondition fromCondition = EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, thruDate);
EntityCondition toCondition = EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, fromDate);
EntityCondition dateCondition = EntityCondition.makeCondition(fromCondition, toCondition);

EntityCondition condition = EntityCondition.makeCondition(positionCondition, dateCondition);

impactingLeaves = delegator.findList("EmplLeave", condition, null, null, null, false);
impactingLeaves.remove(emplLeave);

context.impactingLeaves = impactingLeaves;