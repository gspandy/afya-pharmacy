import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.EntityUtil;

import java.util.*;

import javolution.util.FastMap;


String acctgTemplateId = parameters.acctgTemplateId;
String organizationPartyId = parameters.organizationPartyId;
GenericValue userLogin = session.getAttribute("userLogin");

if(UtilValidate.isEmpty(acctgTemplateId)){
	request.setAttribute("_ERROR_MESSAGE_", "Template Id missing.");
	return "error";
}
EntityCondition condition = EntityCondition.makeCondition("acctgTemplateId",EntityOperator.EQUALS,acctgTemplateId);

List<GenericValue> acctgTemplateEntriesView = delegator.findList("AcctgTemplateEntriesView",condition,null,null,null,true);
BigDecimal credit = BigDecimal.ZERO;
BigDecimal debit = BigDecimal.ZERO;
for(GenericValue acctgTemplateEntriesGv : acctgTemplateEntriesView){
	if("C".equalsIgnoreCase(acctgTemplateEntriesGv.getString("debitCreditFlag"))){
		credit = credit.add(acctgTemplateEntriesGv.getBigDecimal("amount"));
	}else{
		debit = debit.add(acctgTemplateEntriesGv.getBigDecimal("amount"));
	}
}

if(credit.compareTo(BigDecimal.ZERO) != BigDecimal.ZERO && credit.compareTo(debit) == BigDecimal.ZERO){
	Map<String, Object> serviceContext = FastMap.newInstance();
	serviceContext.put("acctgTemplateId", acctgTemplateId);
	serviceContext.put("userLogin", userLogin);
	serviceContext.put("organizationPartyId",organizationPartyId);
	GenericValue acctgTemplateEntriesViewGv = EntityUtil.getFirst(acctgTemplateEntriesView);
	if(UtilValidate.isNotEmpty(acctgTemplateEntriesViewGv)){
		dispatcher.schedule("scheduleAcctgTransaction", serviceContext, acctgTemplateEntriesViewGv.getTimestamp("startTime").getTime(), 
			UtilMisc.toInteger(acctgTemplateEntriesViewGv.getString("frequency")), UtilMisc.toInteger(1), UtilMisc.toInteger(-1));
		//schedule(String serviceName, Map<String, ? extends Object> context, long startTime, int frequency, int interval, int count) 
			
	}
	GenericValue acctgTemplate = delegator.makeValue("AcctgTemplate",UtilMisc.toMap("acctgTemplateId",acctgTemplateId,"isSchedule","Y"));
	delegator.store(acctgTemplate);
	request.setAttribute("_EVENT_MESSAGE_", "Job scheduled successfully.");
	return "success";
}else{
	request.setAttribute("acctgTemplateId",acctgTemplateId);
	request.setAttribute("organizationPartyId",organizationPartyId);
	request.setAttribute("_ERROR_MESSAGE_", "Credit Debit must be equal.");
	return "error";
}