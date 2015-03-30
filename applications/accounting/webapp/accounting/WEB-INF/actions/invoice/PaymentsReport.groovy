import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.accounting.util.UtilAccounting;
import org.ofbiz.base.util.*;
import javolution.util.FastList;

GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
GenericDelegator delegator = (GenericDelegator) userLogin.getDelegator();

List paymentList = FastList.newInstance();
if(UtilValidate.isNotEmpty(request.getParameter("paymentTypeId")))
	paymentList.add(EntityCondition.makeCondition("paymentTypeId", EntityOperator.EQUALS, request.getParameter("paymentTypeId")));
if(UtilValidate.isNotEmpty(request.getParameter("statusId")))
	paymentList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, request.getParameter("statusId")));

if(UtilValidate.isNotEmpty(request.getParameter("fromDate"))){
	paymentList.add(EntityCondition.makeCondition("effectiveDate", EntityOperator.GREATER_THAN_EQUAL_TO, new Timestamp(UtilDateTime.SIMPLE_DATE_FORMAT.parse(request.getParameter("fromDate")).getTime())));
}
if(UtilValidate.isNotEmpty(request.getParameter("thruDate"))){
	paymentList.add(EntityCondition.makeCondition("effectiveDate", EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.getDayEnd(new Timestamp(UtilDateTime.SIMPLE_DATE_FORMAT.parse(request.getParameter("thruDate")).getTime()))));
}
if(UtilValidate.isNotEmpty(request.getParameter("organizationPartyId"))){
	List paymentList1 = FastList.newInstance();
	paymentList1.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, request.getParameter("organizationPartyId")));
	paymentList1.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, request.getParameter("organizationPartyId")));
	paymentList.add(EntityCondition.makeCondition(paymentList1, EntityOperator.OR));
}

List<String> orderByList = new ArrayList<String>();
orderByList.add("effectiveDate");
List paymentLists = delegator.findList("PaymentAndTypeAndCreditCard", EntityCondition.makeCondition(paymentList, EntityOperator.AND), null, orderByList, null, false);


List<Map<String, Object>> listOfPayments = new ArrayList<Map<String, Object>>();
   
for(GenericValue payGv : paymentLists){
     Map<String, Object> payMap = new HashMap<String, Object>();
     if ("CUSTOMER_PAYMENT".equals(payGv.paymentTypeId )||"CUSTOMER_DEPOSIT".equals(payGv.paymentTypeId )||"INTEREST_RECEIPT".equals(payGv.paymentTypeId )||"POS_PAID_IN".equals(payGv.paymentTypeId ))  {
      party = delegator.findOne("PartyNameView", [partyId : payGv.partyIdFrom], false);
     payMap.put("groupName",party.groupName);
     }
   else{
        party = delegator.findOne("PartyNameView", [partyId : payGv.partyIdTo], false);
     payMap.put("groupName",party.groupName);
     }
      statusItemsGv = delegator.findByPrimaryKey("StatusItem", UtilMisc.toMap("statusId", payGv.statusId));
      paymentTypeGv = delegator.findByPrimaryKey("PaymentType", UtilMisc.toMap("paymentTypeId", payGv.paymentTypeId));
     
     
    payMap.put("paymentTypeId",paymentTypeGv.description);  
    payMap.put("effectiveDate",UtilDateTime.format(payGv.effectiveDate));
    payMap.put("amount",payGv.amount);
    payMap.put("currencyUomId",payGv.currencyUomId);
    payMap.put("statusId",statusItemsGv.description);
   
    listOfPayments.add(payMap);
}
 
context.resultList = paymentLists;
context.listOfPayments = listOfPayments;

return "success"
