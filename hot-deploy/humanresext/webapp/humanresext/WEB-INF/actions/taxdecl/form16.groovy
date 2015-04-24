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
EntityCondition desgCond = EntityCondition.makeCondition("partyId",EntityOperator.EQUALS, lpartyId);
desigList = [];
desigList = delegator.findList("PositionFulfillmentView", desgCond, null, null, null, false);
if (desigList.size() != 0) {
	empDesignation = desigList.get(0); 
	context.empDesignation = empDesignation.positionDesc;
} else {
	ServiceUtil.returnError("No Employee Positions Found for party : " + lpartyId);
}


/** Get Employee declarations of Investments for Deductions **/
 /**
 select * from EmplTaxDeclView
 where categoryName = "Section80C"
 and partyId = lpartyId
 and startDate = fromDate
 and endDate = thruDate
 and itemGroupId = "DEDUCTIONS";
 **/
 
conditionL = [];
infromDate = parameters.fromDate;
infromDate = infromDate + " 00:00:00";
java.sql.Date lfromDate = new java.sql.Date(UtilDateTimeSME.toDateYMD(infromDate.trim(), "-").getTime());
inthruDate = parameters.thruDate;
inthruDate = inthruDate + " 00:00:00";
java.sql.Date lthruDate = new java.sql.Date(UtilDateTimeSME.toDateYMD(inthruDate.trim(), "-").getTime());

System.out.println("********* lfromDate : " + lfromDate + " ******* lthruDate ***: " + lthruDate);
		
EntityCondition partyCond = EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, lpartyId);
EntityCondition fromCond = EntityCondition.makeCondition("startDate", EntityOperator.EQUALS, lfromDate); 
EntityCondition thruCond = EntityCondition.makeCondition("endDate", EntityOperator.EQUALS, lthruDate);   
EntityCondition dedCond = EntityCondition.makeCondition("itemGroupId", EntityOperator.EQUALS, "DEDUCTIONS");
orderBy = [];
orderBy.add("categoryName");
//EntityCondition categoryCond = EntityCondition.makeCondition(("categoryName"), EntityOperator.EQUALS, "Section80C");
EntityCondition andCond = EntityCondition.makeCondition(partyCond, fromCond, thruCond, dedCond);
decls = delegator.findList("EmplTaxDeclView", andCond, null, orderBy, null, false);
context.decls = decls;

/** Find Tax Deducted from Payslips **/
totTaxPaid = PayrollHelper.getTotalTaxPaid(lpartyId, lfromDate, lthruDate);
context.totTaxPaid = totTaxPaid;

/** Find Actual TDS Paid **/
Map ctx = FastMap.newInstance();
ctx.put("partyId", lpartyId);
ctx.put("periodFrom", lfromDate);
ctx.put("periodTo", lthruDate);
result = dispatcher.runSync("getTdsPartyService",ctx);
context.tdsList = result.listIt;

/** Find TDS Acknowledgments **/
fromCond = EntityCondition.makeCondition("periodFrom", EntityOperator.EQUALS, lfromDate); 
EntityCondition toCond = EntityCondition.makeCondition("periodTo", EntityOperator.EQUALS, lthruDate);   
EntityCondition ackCond = EntityCondition.makeCondition(fromCond, toCond);
orderBy = [];
orderBy.add("quarter");
acks = delegator.findList("TdsAcks", ackCond, null, orderBy, null, false);
context.acks=acks;
System.out.println("acks : " + acks);
