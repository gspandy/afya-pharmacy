import org.ofbiz.entity.condition.EntityCondition;

String partyId = userLogin.get("partyId");


EntityCondition statusCondition = EntityCondition.makeCondition("issueStatusId", parameters.issueStatusId);
EntityCondition fieldCondition = EntityCondition.makeCondition(parameters.field, partyId);

EntityCondition condition = EntityCondition.makeCondition(statusCondition, fieldCondition);
context.issueList = delegator.findList("IssueNormalView", condition, null, null, null, false);
