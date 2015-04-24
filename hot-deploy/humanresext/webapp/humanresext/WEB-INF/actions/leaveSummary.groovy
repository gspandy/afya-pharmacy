import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.humanresext.util.HumanResUtil;
import org.ofbiz.entity.GenericValue;

String partyId = userLogin.get("partyId");
System.out.println("\n\n ** My Party Id : " + partyId + "\n\n");
EntityCondition condition = EntityCondition.makeCondition("partyId",partyId);
context.myLeaves = delegator.findList("EmplLeave", condition, null, null, null, false);

/**
GenericValue position = HumanResUtil.getEmplPositionForParty(partyId, delegator);

if(!position) 
	return;

EntityCondition positionCondition = EntityCondition.makeCondition("mgrPositionId",position.get("emplPositionId"));
EntityCondition statusCondition = EntityCondition.makeCondition("leaveStatusId","PENDING-APPROVAL");
condition = EntityCondition.makeCondition(positionCondition, statusCondition);

context.assignedLeaves = delegator.findList("EmplLeave", condition, null, null, null, false); **/