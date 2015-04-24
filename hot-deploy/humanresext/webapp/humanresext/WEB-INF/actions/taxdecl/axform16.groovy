import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.party.contact.*;
import java.sql.Timestamp;
import org.ofbiz.entity.condition.*;

EntityCondition formCondition = EntityCondition.makeCondition(UtilMisc.toMap("formId", "IND_IT_FORM16AX"));

List<GenericValue> formFields = delegator.findList("FormField", formCondition, null, UtilMisc.toList("sequenceId"), null, false);
context.formFields = formFields; //List of fields in the form 16 AX

/** Find fields greater than 05.1 **/
EntityCondition seqCondition = EntityCondition.makeCondition("sequenceId", EntityOperator.GREATER_THAN_EQUAL_TO, "05.1");
cl = [];
cl.add(formCondition);
cl.add(seqCondition);
EntityConditionList conditionList = new EntityConditionList(cl, EntityOperator.AND); 
List<GenericValue> innerFields = delegator.findList("FormField", conditionList, null, UtilMisc.toList("sequenceId"), null, false);
System.out.println("innerFields ******************" + innerFields.sequenceId);
context.innerFields= innerFields;

/**
payslipId = 1; //default value
payslipId = (parameters.payslipId != null) ? parameters.payslipId : 1;
EntityCondition condition = EntityCondition.makeCondition(UtilMisc.toMap("payslipId", payslipId));
context.payslipItems = delegator.findList("PaySlipSalHeadView", condition, null, null, null, false);

context.taxDeductions = delegator.findList("EmplMonthlyTaxdeduction", condition, null, null, null, false);
**/

/** Get Employee Bank Details **/
lpartyId = context.partyId;
context.prefHeader = delegator.findOne("PaySlipPrefView", UtilMisc.toMap("partyId", lpartyId), false);

