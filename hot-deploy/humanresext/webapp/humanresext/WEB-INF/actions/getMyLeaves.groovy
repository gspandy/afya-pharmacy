import org.ofbiz.base.util.UtilDateTime;
import com.smebiz.eloan.*; 
import org.ofbiz.entity.condition.*;
import com.nthdimenzion.humanres.payroll.*;
import java.sql.Timestamp;
import java.text.*;
 lpartyId = userLogin.get("partyId");; /** PartyId which submitted the Leave **/
 /** Get current financial year **/
 fiscalYearGV = PayrollHelper.getCurrentFiscalYear();
 Date periodFrom = fiscalYearGV.getDate("fromDate");
Date periodTo = fiscalYearGV.getDate("thruDate"); 
 /** Get the entitled and availed leaves of this party for this financial year **/
 /** select * from empl_leave 
	* where partyId = lpartyId
	* and fromDate >= periodFrom
	* and thruDate <= periodTo
   */
DateFormat df   =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

fromDate = new Timestamp(periodFrom.getTime());
toDate   =  new Timestamp(periodTo.getTime());

	EntityCondition fromCond = EntityConditionFunction.makeCondition(
			"fromDate", EntityComparisonOperator.GREATER_THAN_EQUAL_TO,fromDate);
	EntityCondition thruCond = EntityConditionFunction.makeCondition("thruDate", EntityComparisonOperator.LESS_THAN_EQUAL_TO,toDate);
	EntityCondition partyCond = EntityCondition.makeCondition("partyId", lpartyId);
	EntityCondition conditions = EntityCondition.makeCondition(partyCond, fromCond, thruCond);
	
	 listIt = []; 
	 listIt= delegator.findByCondition("EmplLeave", conditions, null, null);

 	 System.out.println("\n\n Hello My Leave listIt\n" + listIt);


	context.listIt=listIt; // List of leaves applied by party **/
