package org.ofbiz.issuetracking.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.service.GenericDispatcher;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.ServiceValidationException;
import org.ofbiz.webapp.control.Infrastructure;

public class IssuetrackingUtil {
	public static String getFullName(GenericDelegator delegator, String partyId, String separator)
			throws GenericEntityException {

	Map primaryKey = UtilMisc.toMap("partyId", partyId);
	GenericValue entity = delegator.findOne("PartyAndPerson", primaryKey, false);
	String firstName = "N";
	String lastName = "A";
	StringBuilder buffer = new StringBuilder();
	if (entity != null) {
		firstName = entity.getString("firstName");
		lastName = entity.getString("lastName");
		if (separator == null) {
			separator = ",";
		}
	}
	buffer.append(firstName).append(separator == null ? " " : separator).append(lastName);
	return buffer.toString();
	}

	public static String getEmailAdress(GenericDelegator delegator, String partyId) throws GenericEntityException {

	EntityCondition cn1 = EntityCondition.makeCondition("partyId", partyId);
	EntityCondition cn2 = EntityCondition.makeCondition("contactMechPurposeTypeId", "PRIMARY_EMAIL");
	EntityCondition makeCondition = EntityCondition.makeCondition(cn1, cn2);
	List orderBy = new ArrayList();
	orderBy.add("-fromDate");
	List<GenericValue> partyContactMechPurpose = delegator.findList("PartyContactMechPurpose", makeCondition, null,
			orderBy, null, false);
	if (UtilValidate.isNotEmpty(partyContactMechPurpose)) {
		String contactMechId = partyContactMechPurpose.get(0).getString("contactMechId");
		GenericValue contactMech = delegator.findOne("ContactMech", UtilMisc.toMap("contactMechId", contactMechId),
				false);
		return contactMech.getString("infoString");
	}
	return "";
	}

	public static void sendMailToCustomer(GenericDispatcher dispatcher, GenericDelegator delegator,Map<String, Object> issueHeaderParamMap,String cutomerPartyId,
			String subject) throws ServiceValidationException, GenericServiceException, GenericEntityException {

	GenericValue person = delegator.findOne("Person", UtilMisc.toMap("partyId", cutomerPartyId), false);

	Map<String, Object> mailContentMap = new HashMap<String, Object>();

	mailContentMap.put("sendTo", IssuetrackingUtil.getEmailAdress(delegator, cutomerPartyId));
	mailContentMap.put("templateName", "sendNotificationEmailToCustomer.ftl");
	//mailContentMap.put("partyId", cutomerPartyId);
	//mailContentMap.put("userLogin", Infrastructure.getRequest().getSession().getAttribute("userLogin"));
	Properties mailProp = UtilProperties.getProperties("issueTrackingMail.properties");
	mailContentMap.put("sendFrom", mailProp.get("sendFrom"));
	mailContentMap.put("subject", subject);
	mailContentMap.put("templateData", UtilMisc.toMap("firstName", person.getString("firstName"), "lastName",
			person.getString("lastName"), "teamFrom", mailProp.get("teamFrom"), "website", mailProp.get("website"),
			"companyName", mailProp.get("companyName"), "companyWebsite", mailProp.get("companyWebsite")));
	((Map<Object, Object>) mailContentMap.get("templateData")).putAll(mailProp);
	dispatcher.runSync("sendGenericNotificationEmail", mailContentMap);
	}

	public static void sendMailToEmployee(GenericDispatcher dispatcher, GenericDelegator delegator,Map<String, Object> issueHeaderParamMap,String ownerId,
			String issueSummary,String issueDescription,String issueAdditionInfo) throws GenericEntityException, ServiceValidationException, GenericServiceException {

	issueHeaderParamMap.put("assignedTo", ownerId);

	GenericValue person = delegator.findOne("Person", UtilMisc.toMap("partyId", ownerId), false);

	Map<String, Object> mailContentMap = new HashMap<String, Object>();
	if(UtilValidate.isNotEmpty(IssuetrackingUtil.getEmailAdress(delegator, ownerId))){
		mailContentMap.put("sendTo", IssuetrackingUtil.getEmailAdress(delegator, ownerId));
		mailContentMap.put("templateName", "sendNotificationEmailToEmployee.ftl");
		Properties mailProp = UtilProperties.getProperties("issueTrackingMail.properties");
		mailContentMap.put("sendFrom", mailProp.get("sendFrom"));
		mailContentMap.put("subject", issueSummary);
		String body = issueDescription;
		if(UtilValidate.isNotEmpty(issueAdditionInfo))
		 body = issueDescription + "<br/>" + "<br/>" + issueAdditionInfo;
		mailContentMap.put("body", body);
		mailContentMap.put("templateData", UtilMisc.toMap("firstName", person.getString("firstName"), "lastName",
				person.getString("lastName"), "teamFrom", mailProp.get("teamFrom"), "website", mailProp.get("website"),
				"companyName", mailProp.get("companyName"), "companyWebsite", mailProp.get("companyWebsite")));
		((Map<Object, Object>) mailContentMap.get("templateData")).putAll(mailProp);
		dispatcher.runSync("sendGenericNotificationEmail", mailContentMap);
		}
	}

}
