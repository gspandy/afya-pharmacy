import java.util.*;
import org.ofbiz.entity.GenericValue;
//import static org.ofbiz.issuetracking.events.IssueTrackerEvents.Status;
import org.ofbiz.entity.condition.EntityCondition;

//for status and severity population based  upon permissions
String partyId = userLogin.get("partyId");

GenericValue issue = delegator.findOne("IssueDetailView", false, "issueId", parameters.issueId);

context.isOwner = null;

if(!"edit".equals(parameters.mode) || security.hasPermission("ISSUEMGR_ADMIN", request.getSession())){
	context.statusList = delegator.findList("IssueStatus",null,null,null,null,false);
	context.severityList = delegator.findList("IssueSeverity",null,null,null,null,false);
	context.isOwner = "Y";
	return ;
}

List statusList = new ArrayList();
List severityList = new ArrayList();

if(partyId.equals(issue.ownerId)){
	severityList.addAll(delegator.findList("IssueSeverity",null,null,null,null,false));
	statusList.add( delegator.findOne("IssueStatus", true, "issueStatusId", "2") );
	statusList.add( delegator.findOne("IssueStatus", true, "issueStatusId", "3") );
	statusList.add( delegator.findOne("IssueStatus", true, "issueStatusId", "6") );
	statusList.add( delegator.findOne("IssueStatus", true, "issueStatusId", "7") );
	context.isOwner = "Y";
}
else if(partyId.equals(issue.assignedTo)){
	statusList.add( delegator.findOne("IssueStatus", true, "issueStatusId", "3") );
	statusList.add( delegator.findOne("IssueStatus", true, "issueStatusId", "4") );
	statusList.add( delegator.findOne("IssueStatus", true, "issueStatusId", "5") );
}


// For adding the current state of an issue and putting in the context; The common task

GenericValue value = delegator.findOne("IssueSeverity", true, "issueSeverityId", issue.issueSeverityId);
if(!severityList.contains(value))
	severityList.add(value);
	
value = delegator.findOne("IssueStatus", true, "issueStatusId", issue.issueStatusId);
if(!statusList.contains(value))
	statusList.add(value);
	
context.statusList = statusList;
context.severityList = severityList;