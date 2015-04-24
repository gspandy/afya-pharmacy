import java.util.*;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;

String partyId = userLogin.get("partyId");

List conditions = new ArrayList();
if(parameters.issueSeverityId)
	conditions.add(EntityCondition.makeCondition("issueSeverityId", parameters.issueSeverityIds));

if(parameters.fromDate)
	conditions.add(EntityCondition.makeCondition("createdStamp", EntityOperator.GREATER_THAN_EQUAL_TO, parameters.fromDate));

if(parameters.toDate)
	conditions.add(EntityCondition.makeCondition("createdStamp", EntityOperator.LESS_THAN_EQUAL_TO, parameters.toDate));

EntityCondition condition = null;
if(conditions.size()>0)
	condition = EntityCondition.makeCondition(conditions); 

context.comments = delegator.findList("IssueNormalView", condition, null, null, null, false);

