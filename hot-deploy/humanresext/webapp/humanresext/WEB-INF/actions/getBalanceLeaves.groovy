import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.humanresext.util.HumanResUtil;
import org.ofbiz.base.util.UtilMisc;
import com.nthdimenzion.humanres.payroll.PayrollHelper;

 lpartyId = leaveMap.partyId; /** PartyId which submitted the Leave **/
 fullName = HumanResUtil.getFullName(delegator, lpartyId," ");
 
 /** Get current financial year **/
 fiscalYearGV = PayrollHelper.getCurrentFiscalYear();
 periodFrom = fiscalYearGV.getDate("fromDate");
 periodTo = fiscalYearGV.getDate("thruDate"); 
 
 /** Get the entitled and availed leaves of this party for this financial year **/
 /** Select num_days, availed from empl_leave_limit
   * where partyId = lpartyId
   * and leaveTypeId = lleaveTypeId
   * and begin_year >= periodFrom
   * and end_year <= periodTo
   */
 
	EntityCondition beginCond = EntityConditionFunction.makeCondition(
			"beginYear", EntityComparisonOperator.GREATER_THAN_EQUAL_TO, periodFrom);
	EntityCondition endCond = EntityConditionFunction
			.makeCondition("endYear", EntityComparisonOperator.LESS_THAN_EQUAL_TO, periodTo);
	EntityCondition partyCond = EntityCondition.makeCondition("partyId", lpartyId);
	EntityCondition typeCond = EntityCondition.makeCondition("leaveTypeId", EntityOperator.EQUALS, leaveMap.leaveTypeId);
	EntityCondition conditions = EntityCondition.makeCondition(partyCond, typeCond, beginCond, endCond);
	
	 data = [];
	 fields = [];
	 fields.add("numDays");
	 fields.add("availed");
	 data= delegator.findByCondition("EmplLeaveLimit", conditions, fields, null);

 	 System.out.println("\n\n Hello Data check begins\n");
	if(data.size() >0) {
		System.out.println("\n\n Hello Data > 0 \n");
		leaveLimit = data.get(0).get("numDays");
		leaveAvailed = data.get(0).get("availed");
		context.leaveLimit = leaveLimit;
		context.availed = leaveAvailed;
	 }

	context.partyName=fullName; // Full Name of the Leave Party **/
 