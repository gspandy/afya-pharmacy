import java.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.base.util.*;

String partyId = userLogin.get("partyId");
println " partyId "+partyId;

List conditions = new ArrayList();
if(UtilValidate.isNotEmpty(parameters.fromDate)){
	println " fromDate "+parameters.fromDate;
	conditions.add(EntityCondition.makeCondition("createdOn", EntityOperator.GREATER_THAN_EQUAL_TO, new Timestamp(UtilDateTime.SIMPLE_DATE_FORMAT.parse(request.getParameter("fromDate")).getTime())));
}

if(UtilValidate.isNotEmpty(parameters.toDate)){
	println " toDate "+ UtilDateTime.toSqlDate(parameters.toDate);
	conditions.add(EntityCondition.makeCondition("createdOn", EntityOperator.LESS_THAN_EQUAL_TO,  UtilDateTime.getDayEnd(new Timestamp(UtilDateTime.SIMPLE_DATE_FORMAT.parse(request.getParameter("toDate")).getTime()))));
	println " conditions "+conditions;
}

if(!"Y".equals(parameters.all)){
/*	
	String responseViolated = "Y".equals(parameters.responseViolated) ? "Y" : "N"; 
	String resolutionViolated = "Y".equals(parameters.resolutionViolated) ? "Y" : "N"; 
	conditions.add(EntityCondition.makeCondition("responseViolated", responseViolated));
	conditions.add(EntityCondition.makeCondition("resolutionViolated", resolutionViolated));
*/

	EntityOperator responseOp = "Y".equals(parameters.responseViolated) ? EntityOperator.GREATER_THAN : EntityOperator.EQUALS; 
	EntityOperator resolutionOp = "Y".equals(parameters.responseViolated) ? EntityOperator.GREATER_THAN : EntityOperator.EQUALS; 
	conditions.add(EntityCondition.makeCondition("responseViolated", responseOp, 0));
	conditions.add(EntityCondition.makeCondition("resolutionViolated", resolutionOp, 0));
}	

def EntityConditionList condition = null;
if(conditions.size()>0)
	condition = EntityCondition.makeCondition(conditions); 

	println " condition "+condition;

def issueReportList = [];

issueReportList = delegator.findList("IssueSlaReportView", condition, new java.util.HashSet(),new java.util.ArrayList(), new org.ofbiz.entity.util.EntityFindOptions(), false);

context.issueReportList=issueReportList;

println " issueReportList "+context.issueReportList;
