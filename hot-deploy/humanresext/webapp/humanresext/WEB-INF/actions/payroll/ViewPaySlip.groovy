import java.util.*;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.humanresext.util.HumanResUtil;
import com.ndz.zkoss.HrmsUtil;

userLoginId = userLogin.partyId;
boolean isAdmin = security.hasPermission("HUMANRES_ADMIN", userLogin);
boolean isManager = security.hasPermission("HUMANRES_MGR", userLogin);
payslipHeader =null;
payslipHeaderList=null;
boolean size=false;
paiddays=0.0;
unpaiddays=0.0;
if(isAdmin){
payslipHeader = delegator.findOne("EmplPayslip", UtilMisc.toMap("payslipId", parameters.payslipId),false);
userLoginId=payslipHeader.getString("partyId");

context.paiddays=paiddays;
context.unpaiddays=unpaiddays;
}
if(!(isAdmin||isManager)){
payslipHeaderList = delegator.findByAnd("EmplPayslip", UtilMisc.toMap("payslipId", parameters.payslipId,"partyId",userLoginId));
context.paiddays=paiddays;
context.unpaiddays=unpaiddays;
if(payslipHeaderList.size()>0){
payslipHeader = payslipHeaderList.get(0);
context.loanAmount  =payslipHeader.get("monthlyLoanAmount");
}
else{
return "payslipheadernull";
}
}
if(isManager){
	subOrdList= HumanResUtil.getSubordinatesForParty(userLoginId,delegator);
	payslipGv = delegator.findOne("EmplPayslip", UtilMisc.toMap("payslipId", parameters.payslipId),false);
	employeeId =payslipGv.getString("partyId");
	context.paiddays=paiddays;
	context.unpaiddays=unpaiddays;
	if(subOrdList){
	for(int i=0;i<subOrdList.size();i++){
	employeeposGv = subOrdList.get(i);
	employeePosFulfillment = delegator.findByAnd("EmplPositionFulfillment",UtilMisc.toMap("emplPositionId",employeeposGv.getString("emplPositionIdManagedBy")));
		if(employeePosFulfillment.size() > 0 ){
			employeePosFulfillmentGv = employeePosFulfillment.get(0);
			subOrdId = employeePosFulfillmentGv.getString("partyId");
				if(employeeId.equals(subOrdId)){
					payslipHeaderList = delegator.findByAnd("EmplPayslip", UtilMisc.toMap("payslipId", parameters.payslipId,"partyId",subOrdId));
					payslipHeader = payslipHeaderList.get(0);
			}
		}
    } 
}else{
	payslipHeaderList = delegator.findByAnd("EmplPayslip", UtilMisc.toMap("payslipId", parameters.payslipId,"partyId",employeeId));
	payslipHeader = payslipHeaderList.get(0);
	}
}
context.payslipHeader=payslipHeader;
if(payslipHeader){
	userLoginId=payslipHeader.getString("partyId");
	java.util.Date periodFrom = payslipHeader.getDate("periodFrom");
	java.util.Date periodTo = payslipHeader.getDate("periodTo");
	Map map = [:];
	map.put("partyId",userLoginId);
	map.put("periodFrom",periodFrom);
	map.put("periodTo",periodTo);
	result = HrmsUtil.getPaidUnpaidDays(map);
	context.paiddays = result.get("PaidDays");
	context.unpaiddays = result.get("UnPaidDays");
}
GenericValue emplValue = payslipHeader.getRelatedOne("Party");
System.out.println("\n\n\n\n\n\nPAYSLIP GENERICVALUE ***********"+emplValue);
lpartyId = emplValue.partyId;

EntityCondition paySlipCond = EntityCondition.makeCondition(UtilMisc.toMap("payslipId", parameters.payslipId));
orderBy = [];
orderBy.add("hrName");
payslipItems= delegator.findList("PaySlipSalHeadView", paySlipCond, null, orderBy, null, false);
context.payslipItems = payslipItems;

 partyGroupInfo=delegator.findOne("PartyGroup", UtilMisc.toMap("partyId", "Company"),false);
 context.defCurrencyType = partyGroupInfo.getString("currencyType");
 System.out.println("@@@@@@@@@@@@@@@@@@@@ : Curr " + context.defCurrencyType);
GenericValue designationGV=null;
designationGV=org.ofbiz.humanresext.util.HumanResUtil.getEmplPositionForParty(emplValue,delegator);
String designation= com.ndz.zkoss.HrmsUtil.getPositionTypeDescription(designationGV.getString("emplPositionTypeId"));
context.designation=designation;
context.taxDeductions = delegator.findList("EmplMonthlyTaxdeduction", paySlipCond, null, null, null, false);
if(payslipItems.size()>0){
size=true;
}
context.size = size;
/** Find total tax paid in payslip **/
EntityCondition itCond = EntityCondition.makeCondition("taxType","INCOMETAX");
EntityCondition totCond = EntityCondition.makeCondition(paySlipCond, itCond);
taxes = []; //List of taxes
taxes = delegator.findList("EmplMonthlyTaxdeduction", totCond, null, null, null, false);
if (taxes.size() > 0) {
	incomeTax = taxes.get(0);
	context.incomeTax = incomeTax.get("amount");
	System.out.println("@@@@@@@@@@@@@@@@@@@@ incomeTax : " + incomeTax);
}


/** Find professional Tax paid in payslip **/
EntityCondition professionalCond = EntityCondition.makeCondition("taxType","PROFESSIONALTAX");
EntityCondition proCond = EntityCondition.makeCondition(paySlipCond, professionalCond);
taxes = delegator.findList("EmplMonthlyTaxdeduction", proCond, null, null, null, false);
if (taxes.size() > 0) {
	professionalTax = taxes.get(0);
	context.professionalTax = professionalTax.get("amount");
	System.out.println("@@@@@@@@@@@@@@@@@@@@ professionalTax : " + professionalTax);
}


/** Get Employee Bank Details **/
context.prefHeader = delegator.findOne("PaySlipPrefView", UtilMisc.toMap("partyId", lpartyId), false);

