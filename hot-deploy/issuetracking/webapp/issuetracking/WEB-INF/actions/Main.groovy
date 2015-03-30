import org.ofbiz.entity.condition.EntityCondition;

String partyId = userLogin.get("partyId");

EntityCondition condition = EntityCondition.makeCondition("createdBy",partyId);
context.myIssuesDB = delegator.findList("IssueStatusDashboardView", condition, null, null, null, false);
context.myIssues = delegator.findList("IssueNormalView", condition, null, null, null, false);

if(security.hasPermission("ISSUEMGR_RESOLVER", request.getSession())){
	condition = EntityCondition.makeCondition("assignedTo",partyId);
	context.assignedIssuesDB = delegator.findList("IssueStatusDashboardView", condition, null, null, null, false);
	context.assignedIssues = delegator.findList("IssueNormalView", condition, null, null, null, false);
}

if(security.hasPermission("ISSUEMGR_OWNER", request.getSession())){
	condition = EntityCondition.makeCondition("categoryOwner",partyId);
	context.ownedIssuesDB = delegator.findList("IssueStatusDashboardView", condition, null, null, null, false);
	context.ownedIssues = delegator.findList("IssueNormalView", condition, null, null, null, false);
}
