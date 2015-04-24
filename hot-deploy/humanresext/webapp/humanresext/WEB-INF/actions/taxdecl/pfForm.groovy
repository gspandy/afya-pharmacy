import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.party.contact.*;
import java.sql.Timestamp;
import org.ofbiz.entity.condition.*;
import com.smebiz.common.UtilDateTimeSME;
import com.nthdimenzion.humanres.payroll.PayrollHelper;
import javolution.util.FastMap;
import org.ofbiz.service.ServiceUtil;

/** Get Employee Designation Details **/
lpartyId = parameters.partyId;
EntityCondition partyCond = EntityCondition.makeCondition("partyId",EntityOperator.EQUALS, lpartyId);
desigList = [];
desigList = delegator.findList("PositionFulfillmentView", partyCond, null, null, null, false);
if (desigList.size() != 0) {
	empDesignation = desigList.get(0); 
	context.empDesignation = empDesignation.positionDesc;
} else {
	ServiceUtil.returnError("No Employee Positions Found for party : " + lpartyId);
}

java.text.DateFormat df = new java.text.SimpleDateFormat("dd-MM-yyyy");

/** Find PF Payments **/
infromDate = parameters.fromDate;
java.sql.Date lfromDate = new java.sql.Date(df.parse(infromDate).getTime());


inthruDate = parameters.thruDate;
java.sql.Date lthruDate = new java.sql.Date(df.parse(inthruDate).getTime());

System.out.println("********* lfromDate : " + lfromDate + " ******* lthruDate ***: " + lthruDate);

fromCond = EntityCondition.makeCondition("periodFrom", EntityOperator.GREATER_THAN_EQUAL_TO, lfromDate); 
EntityCondition toCond = EntityCondition.makeCondition("periodTo", EntityOperator.LESS_THAN_EQUAL_TO, lthruDate);   
EntityCondition pfCond = EntityCondition.makeCondition(partyCond, fromCond, toCond); //Party Condition reused
orderBy = [];
orderBy.add("periodFrom");
pfs = delegator.findList("EmplPf", pfCond, null, orderBy, null, false);
context.PfPayments=pfs;
System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@pfs : " + pfs);

/** Find Pfs for all parties in the financial Year 
  select * from empl_pf where period_from >= fromDate
  and period_to <= thruDate
  order by party_id, period_from
**/  
EntityCondition allPfCond = EntityCondition.makeCondition( fromCond, toCond); //Only Period Condition
orderBy = [];
orderBy.add("partyId");
orderBy.add("periodFrom");
partyPeriodpfs = delegator.findList("EmplPf", allPfCond, null, orderBy, null, false);
context.partyPeriodpfs=partyPeriodpfs;

/** Find Pfs for all parties in the financial Year 
  select partyId, sum(pensionAmount),.....
  from empl_pf 
  where period_from >= fromDate
  and period_to <= thruDate
  group by partyId
  order by party_id, period_from
**/ 
serviceCtx = [periodFrom : lfromDate, periodTo : lthruDate,partyId:lpartyId];
result = dispatcher.runSync("getConsolidatedPFService", serviceCtx);
context.consolidatedPF = result.get("listIt");

result = dispatcher.runSync("getMonthWiseConsolidatedPFService", serviceCtx);
context.monthWisePF = result.get("listIt");

