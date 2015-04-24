import java.util.*;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import javolution.util.FastList;
import javolution.util.FastMap;
import java.sql.Timestamp;
import org.apache.commons.lang.StringUtils;

GenericDelegator delegator = (GenericDelegator)context.get("delegator");
String lpartyIdFrom = (String)context.get("partyIdFrom");
String lpartyIdTo = ((String)context.get("partyIdTo")).trim();		
List<EntityCondition> condList = FastList.newInstance();
EntityCondition roleCond = EntityCondition.makeCondition("roleTypeIdTo", "EMPLOYEE");
condList.add(roleCond);
System.out.println("\n\n condList : \n\n" + condList);	
if (!StringUtils.isBlank(lpartyIdFrom)) {
	EntityCondition partyFromCond = EntityCondition.makeCondition("partyIdFrom", lpartyIdFrom);
	condList.add(partyFromCond);
}
System.out.println("\n\n lpartyIdTo : \n\n" + lpartyIdTo);

if (!StringUtils.isBlank(lpartyIdTo)) {
	System.out.println("\n\n Inside IF : \n\n" + lpartyIdTo);
	EntityCondition partyToCond = EntityCondition.makeCondition("partyIdTo", lpartyIdTo);
	condList.add(partyToCond);
}
System.out.println("\n\n condList : \n\n" + condList);	
EntityCondition conditions = EntityCondition.makeCondition(condList);
Set<String> fields = new HashSet();
fields.add("partyIdTo");
List<String> orderBy = FastList.newInstance();
orderBy.add("partyIdTo");
List<GenericValue> employeeIds = delegator.findList("Employment", conditions, fields, orderBy, null, false);

/** Remove all the employess whose payslips have already been generated **/
Date lperiodFrom = Timestamp.valueOf(parameters.periodFrom + " " + "00:00:00.000"); 
Date lperiodTo = Timestamp.valueOf(parameters.periodTo + " " + "00:00:00.000"); 

EntityCondition fromCond = EntityCondition.makeCondition("periodFrom", EntityOperator.LESS_THAN_EQUAL_TO, lperiodFrom);
EntityCondition toCond = EntityCondition.makeCondition("periodTo", EntityOperator.GREATER_THAN_EQUAL_TO, lperiodTo);
EntityCondition slipCond = EntityCondition.makeCondition(fromCond, toCond);
Set<String> fieldsToSelect = new HashSet();
fieldsToSelect.add("partyId");
List<String> orderByField = FastList.newInstance();
orderByField.add("partyId");
List<GenericValue> slipIds = delegator.findList("EmplPayslip", slipCond, fieldsToSelect, orderByField, null, false);

System.out.println("\n\n employeeIds : \n\n" + employeeIds);
System.out.println("\n\n slipIds : \n\n" + slipIds);


String donePartyId = null;
String checkPartyIdTo = null;
for (GenericValue doneId: slipIds) {
	donePartyId = (String)doneId.get("partyId");
	for (GenericValue allId: employeeIds){
		checkPartyIdTo = (String)allId.get("partyIdTo");
		//System.out.println("\n\n checkPartyIdTo : \n\n" + checkPartyIdTo);
		if (checkPartyIdTo.equals(donePartyId)) {
			//System.out.println("\n\n Equal donePartyId : \n\n" + donePartyId);
			employeeIds.remove(allId);
			break;
		}
	}
}
//System.out.println("\n\n After removal employeeIds : \n\n" + employeeIds);
context.listIt = employeeIds;


