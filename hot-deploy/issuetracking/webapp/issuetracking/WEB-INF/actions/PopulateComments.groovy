import org.ofbiz.entity.condition.EntityCondition;

if(!parameters.issueId)
	return;
String partyId = userLogin.get("partyId");

EntityCondition condition = EntityCondition.makeCondition("issueId",parameters.issueId);
context.comments = delegator.findList("IssueHistory", condition, null, null, null, false);
