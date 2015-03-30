import org.ofbiz.accounting.util.UtilAccounting;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.base.util.UtilMisc;

String acctgTemplateId = parameters.acctgTemplateId;
String organizationPartyId = parameters.organizationPartyId;

UtilAccounting.cancelScheduleJob(delegator, acctgTemplateId, organizationPartyId);

GenericValue acctgTemplate = delegator.makeValue("AcctgTemplate",UtilMisc.toMap("acctgTemplateId",acctgTemplateId,"isSchedule","N"));
delegator.store(acctgTemplate);

request.setAttribute("acctgTemplateId",acctgTemplateId);
request.setAttribute("organizationPartyId",organizationPartyId);
request.setAttribute("_EVENT_MESSAGE_", "Job unscheduled successfully.");
return "success";
