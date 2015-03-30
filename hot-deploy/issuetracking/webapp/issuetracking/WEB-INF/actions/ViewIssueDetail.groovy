import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.base.util.UtilProperties;

String partyId = userLogin.get("partyId");

String issueId = parameters.issueId;
context.showEdit = parameters.showEdit;

if(!issueId)
	issueId = request.getAttribute("issueId");

context.issue = delegator.findOne("IssueDetailView", false, "issueId", issueId);

EntityCondition condition = EntityCondition.makeCondition("issueId",parameters.issueId);
context.attachments = delegator.findList("IssueAttachment", condition, null, null, null, false);
