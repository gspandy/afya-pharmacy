package com.ndz.zkoss.util;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.zkoss.zul.Messagebox;

import freemarker.template.SimpleList;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * @author: NthDimenzion
 * @since 1.0
 */
public class MailUtil {

    public static Map<String, Object> sendMail(String templateName, String emailAddress, Map dataMap, LocalDispatcher dispatcher) throws GenericServiceException {
        Map<String, Object> mailContentMap = new HashMap<String, Object>();
        mailContentMap.put("sendTo", emailAddress);
        mailContentMap.put("templateName", templateName);
        Properties mailProp = UtilProperties.getProperties("easyHrmsMail.properties");
        mailContentMap.put("sendFrom", mailProp.get("sendFrom"));
        mailContentMap.put("subject", "Welcome To" + " " + mailProp.get("companyName"));
        dataMap.put("teamFrom", mailProp.get("teamFrom"));
        dataMap.put("website", mailProp.get("website"));
        dataMap.put("companyName", mailProp.get("companyName"));
        dataMap.put("companyWebsite", mailProp.get("companyWebsite"));
        mailContentMap.put("templateData", dataMap);
        ((Map) mailContentMap.get("templateData")).putAll(mailProp);
        Map<String, Object> resultMap = dispatcher.runSync("sendGenericNotificationEmail", mailContentMap);
        return resultMap;
    }


    public static boolean sendClaimApprovalMailToCFO(String userLoginPartyId, Delegator delegator, String templateName, String emailAddress, Map dataMap, LocalDispatcher dispatcher) throws GenericServiceException, GenericEntityException, InterruptedException {
        boolean success = false;
        GenericValue personGv = delegator.findOne("Person", UtilMisc.toMap("partyId", userLoginPartyId), false);
        GenericValue claimGv = (GenericValue) dataMap.get("claimGv");
        GenericValue employeeGv = (GenericValue) dataMap.get("personGv");

        List<GenericValue> contactMechPurposes = delegator.findByAnd("PartyContactMechPurpose", UtilMisc.toMap("partyId", userLoginPartyId, "contactMechPurposeTypeId", "PRIMARY_EMAIL"));
        if (UtilValidate.isEmpty(contactMechPurposes)) {
            Messagebox.show("Email cannot be sent;please configure your primary e-mail address", "Error", 1, null);
            return success;
        }
        String contactMechId = contactMechPurposes.get(0).getString("contactMechId");
        for (GenericValue contactMechPurposeGv : contactMechPurposes) {
            if (UtilValidate.isEmpty(contactMechId) && UtilValidate.isNotEmpty(contactMechPurposeGv.getString("contactMechId"))) {
                contactMechId = contactMechPurposeGv.getString("contactMechId");
            }
        }
        List<GenericValue> contactMechGvs = delegator.findByAnd("ContactMech", UtilMisc.toMap("contactMechTypeId", "EMAIL_ADDRESS", "contactMechId", contactMechId));
        GenericValue contactMechGv = contactMechGvs.get(0);
        if(UtilValidate.isEmpty(contactMechGv.getString("infoString"))){
        	Messagebox.show("Email cannot be sent;please configure your primary e-mail address", "Error", 1, null);
            return success;
        }

        List  personList=delegator.findByAnd("Person", UtilMisc.toMap("isCfo","Y"));
        GenericValue cfoGv=(GenericValue)personList.get(0);
        String cfoFirstName = (String) cfoGv.getString("firstName");
        String cfoLastName = (String) cfoGv.getString("lastName");
             	
        Map<String, Object> mailContentMap = new HashMap<String, Object>();
        mailContentMap.put("sendTo", emailAddress);
        mailContentMap.put("templateName", templateName);
        Properties mailProp = UtilProperties.getProperties("easyHrmsMail.properties");
        mailContentMap.put("sendFrom", contactMechGv.getString("infoString"));
        mailContentMap.put("subject", "Claim Reimbursement");
        dataMap.put("teamFrom", personGv.getString("firstName") + " " + personGv.getString("lastName"));
        dataMap.put("website", mailProp.get("website"));
        dataMap.put("companyName", mailProp.get("companyName"));
        dataMap.put("companyWebsite", mailProp.get("companyWebsite"));
        dataMap.put("employeeName", employeeGv.getString("firstName") + " " + employeeGv.getString("lastName"));
        dataMap.put("claimAmount", claimGv.getBigDecimal("acceptedAmount")!=null? claimGv.getBigDecimal("acceptedAmount") : claimGv.getBigDecimal("amount"));
        dataMap.put("currencyUomId", claimGv.getString("currencyUomId"));
        dataMap.put("claimType", delegator.findOne("Enumeration", UtilMisc.toMap("enumId", claimGv.getString("claimType")), false).getString("description"));
        dataMap.put("personName", cfoFirstName + " " + cfoLastName);
        mailContentMap.put("templateData", dataMap);
        ((Map) mailContentMap.get("templateData")).putAll(mailProp);
        Map<String, Object> resultMap = dispatcher.runSync("sendGenericNotificationEmail", mailContentMap);
        success = true;
        return success;
    }

   public static boolean sendRequisitionMailToCEO(String userLoginPartyId, Delegator delegator, String templateName, String emailAddress, Map dataMap,
    		LocalDispatcher dispatcher,String subject) throws GenericServiceException, GenericEntityException, InterruptedException {
        boolean success = false;
        GenericValue personGv = delegator.findOne("Person", UtilMisc.toMap("partyId", userLoginPartyId), false);
        GenericValue requisitionGv = (GenericValue) dataMap.get("requisitionGv");
        GenericValue employeeGv = (GenericValue) dataMap.get("personGv");

        List<GenericValue> contactMechPurposes = delegator.findByAnd("PartyContactMechPurpose", UtilMisc.toMap("partyId", userLoginPartyId, "contactMechPurposeTypeId", "PRIMARY_EMAIL"));
        if (UtilValidate.isEmpty(contactMechPurposes)) {
            Messagebox.show("Email cannot be sent;please configure your primary e-mail address", "Error", 1, null);
            return success;
        }
        String contactMechId = contactMechPurposes.get(0).getString("contactMechId");
        for (GenericValue contactMechPurposeGv : contactMechPurposes) {
            if (UtilValidate.isEmpty(contactMechId) && UtilValidate.isNotEmpty(contactMechPurposeGv.getString("contactMechId"))) {
                contactMechId = contactMechPurposeGv.getString("contactMechId");
            }
        }
        List<GenericValue> contactMechGvs = delegator.findByAnd("ContactMech", UtilMisc.toMap("contactMechTypeId", "EMAIL_ADDRESS", "contactMechId", contactMechId));
        GenericValue contactMechGv = contactMechGvs.get(0);
        if(UtilValidate.isEmpty(contactMechGv.getString("infoString"))){
        	Messagebox.show("Email cannot be sent;please configure your primary e-mail address", "Error", 1, null);
            return success;
        }
        List  personList=delegator.findByAnd("Person", UtilMisc.toMap("isCeo","Y"));
        GenericValue cfoGv=(GenericValue)personList.get(0);
        String cfoFirstName = (String) cfoGv.getString("firstName");
        String cfoLastName = (String) cfoGv.getString("lastName");
        
        
        Map<String, Object> mailContentMap = new HashMap<String, Object>();
        mailContentMap.put("sendTo", emailAddress);
        mailContentMap.put("templateName", templateName);
        Properties mailProp = UtilProperties.getProperties("easyHrmsMail.properties");
        mailContentMap.put("sendFrom", contactMechGv.getString("infoString"));
        mailContentMap.put("subject",subject);
        dataMap.put("teamFrom", personGv.getString("firstName") + " " + personGv.getString("lastName"));
        dataMap.put("website", mailProp.get("website"));
        dataMap.put("companyName", mailProp.get("companyName"));
        dataMap.put("companyWebsite", mailProp.get("companyWebsite"));
        
        dataMap.put("employeeName", employeeGv.getString("firstName") + " " + employeeGv.getString("lastName"));
        dataMap.put("requisitionId",requisitionGv.getString("requisitionId"));
        dataMap.put("positionType", requisitionGv.getString("positionType"));
        dataMap.put("numberOfPosition", requisitionGv.getLong("numberOfPosition"));
        dataMap.put("qualification", requisitionGv.getString("qualification"));
        dataMap.put("minExprience", requisitionGv.getLong("minExprience"));
        dataMap.put("maxExprience", requisitionGv.getLong("maxExprience"));
        dataMap.put("jobTitle", requisitionGv.getString("jobTitle"));
        dataMap.put("jobDescription", requisitionGv.getString("jobDescription"));
        dataMap.put("roleDetails", requisitionGv.getString("roleDetails"));
        dataMap.put("softSkills", requisitionGv.getString("softSkills"));
        dataMap.put("justificationForHiring", requisitionGv.getString("justificationForHiring"));
        dataMap.put("minimumSalary", requisitionGv.getString("minimumSalary"));
        dataMap.put("maximumSalary", requisitionGv.getString("maximumSalary"));
        dataMap.put("personName", cfoFirstName + " " + cfoLastName);
        mailContentMap.put("templateData", dataMap);
        ((Map) mailContentMap.get("templateData")).putAll(mailProp);
        Map<String, Object> resultMap = dispatcher.runSync("sendGenericNotificationEmail", mailContentMap);
        success = true;
        return success;
    }

   public static boolean sendResignationMailToCEO(String userLoginPartyId, Delegator delegator, String templateName, String emailAddress, Map dataMap, LocalDispatcher dispatcher) throws GenericServiceException, GenericEntityException, InterruptedException {
       boolean success = false;
       GenericValue personGv = delegator.findOne("Person", UtilMisc.toMap("partyId", userLoginPartyId), false);
       GenericValue terminationGv = (GenericValue) dataMap.get("terminationGv");
       GenericValue employeeGv = (GenericValue) dataMap.get("personGv");

       List<GenericValue> contactMechPurposes = delegator.findByAnd("PartyContactMechPurpose", UtilMisc.toMap("partyId", userLoginPartyId, "contactMechPurposeTypeId", "PRIMARY_EMAIL"));
       if (UtilValidate.isEmpty(contactMechPurposes)) {
           Messagebox.show("Email cannot be sent;please configure your primary e-mail address", "Error", 1, null);
           return success;
       }
       String contactMechId = contactMechPurposes.get(0).getString("contactMechId");
       for (GenericValue contactMechPurposeGv : contactMechPurposes) {
           if (UtilValidate.isEmpty(contactMechId) && UtilValidate.isNotEmpty(contactMechPurposeGv.getString("contactMechId"))) {
               contactMechId = contactMechPurposeGv.getString("contactMechId");
           }
       }
       
       List<GenericValue> contactMechGvs = delegator.findByAnd("ContactMech", UtilMisc.toMap("contactMechTypeId", "EMAIL_ADDRESS", "contactMechId", contactMechId));
       GenericValue contactMechGv = contactMechGvs.get(0);
       if(UtilValidate.isEmpty(contactMechGv.getString("infoString"))){
       	Messagebox.show("Email cannot be sent;please configure your primary e-mail address", "Error", 1, null);
           return success;
       }

       
       List  personList=delegator.findByAnd("Person", UtilMisc.toMap("isCeo","Y"));
       GenericValue ceoGv=(GenericValue)personList.get(0);
       String ceoFirstName = (String) ceoGv.getString("firstName");
       String ceoLastName = (String) ceoGv.getString("lastName");
       
       
       Map<String, Object> mailContentMap = new HashMap<String, Object>();
       mailContentMap.put("sendTo", emailAddress);
       mailContentMap.put("templateName", templateName);
       Properties mailProp = UtilProperties.getProperties("easyHrmsMail.properties");
       mailContentMap.put("sendFrom", contactMechGv.getString("infoString"));
       mailContentMap.put("subject", "Resignation Approval");
       dataMap.put("teamFrom", personGv.getString("firstName") + " " + personGv.getString("lastName"));
       dataMap.put("website", mailProp.get("website"));
       dataMap.put("companyName", mailProp.get("companyName"));
       dataMap.put("companyWebsite", mailProp.get("companyWebsite"));
       dataMap.put("employeeName", employeeGv.getString("firstName") + " " + employeeGv.getString("lastName"));
       dataMap.put("reason", terminationGv.getString("reason").toLowerCase());
       dataMap.put("noticePeriod", terminationGv.getLong("noticePeriod"));
       dataMap.put("personName", ceoFirstName + " " + ceoLastName);
       mailContentMap.put("templateData", dataMap);
       ((Map) mailContentMap.get("templateData")).putAll(mailProp);
       Map<String, Object> resultMap = dispatcher.runSync("sendGenericNotificationEmail", mailContentMap);
       success = true;
       return success;
   }
   public static boolean sendInitiateOfferMailToCEO(String userLoginPartyId, Delegator delegator, String templateName, String emailAddress, Map dataMap,
   		LocalDispatcher dispatcher,String subject) throws GenericServiceException, GenericEntityException, InterruptedException {
       boolean success = false;
       GenericValue personGv = delegator.findOne("Person", UtilMisc.toMap("partyId", userLoginPartyId), false);
       GenericValue requisitionGv = (GenericValue) dataMap.get("requisitionGv");
       GenericValue employeeGv = (GenericValue) dataMap.get("performanceNoteGv");

       	GenericValue person = null;
       	person = delegator.findByPrimaryKey("Person", UtilMisc.toMap("partyId", employeeGv.getString("partyId")));
       	String firstName = (String) person.getString("firstName");
       	String lastName = (String) person.getString("lastName");
   	
             
       List<GenericValue> contactMechPurposes = delegator.findByAnd("PartyContactMechPurpose", UtilMisc.toMap("partyId", userLoginPartyId, "contactMechPurposeTypeId", "PRIMARY_EMAIL"));
       if (UtilValidate.isEmpty(contactMechPurposes)) {
           Messagebox.show("Email cannot be sent;please configure your primary e-mail address", "Error", 1, null);
           return success;
       }
       String contactMechId = contactMechPurposes.get(0).getString("contactMechId");
       for (GenericValue contactMechPurposeGv : contactMechPurposes) {
           if (UtilValidate.isEmpty(contactMechId) && UtilValidate.isNotEmpty(contactMechPurposeGv.getString("contactMechId"))) {
               contactMechId = contactMechPurposeGv.getString("contactMechId");
           }
       }
       List<GenericValue> contactMechGvs = delegator.findByAnd("ContactMech", UtilMisc.toMap("contactMechTypeId", "EMAIL_ADDRESS", "contactMechId", contactMechId));
       GenericValue contactMechGv = contactMechGvs.get(0);
       if(UtilValidate.isEmpty(contactMechGv.getString("infoString"))){
       	Messagebox.show("Email cannot be sent;please configure your primary e-mail address", "Error", 1, null);
           return success;
       }
       List  personList=delegator.findByAnd("Person", UtilMisc.toMap("isCeo","Y"));
       GenericValue ceoGv=(GenericValue)personList.get(0);
       String ceoFirstName = (String) ceoGv.getString("firstName");
       String ceoLastName = (String) ceoGv.getString("lastName");
           
       Map<String, Object> mailContentMap = new HashMap<String, Object>();
       mailContentMap.put("sendTo", emailAddress);
       mailContentMap.put("templateName", templateName);
       Properties mailProp = UtilProperties.getProperties("easyHrmsMail.properties");
       mailContentMap.put("sendFrom", contactMechGv.getString("infoString"));
       mailContentMap.put("subject",subject);
       dataMap.put("teamFrom", personGv.getString("firstName") + " " + personGv.getString("lastName"));
       dataMap.put("website", mailProp.get("website"));
       dataMap.put("companyName", mailProp.get("companyName"));
       dataMap.put("companyWebsite", mailProp.get("companyWebsite"));
       dataMap.put("employeeName", firstName + " " + lastName);
       dataMap.put("personName", ceoFirstName + " " + ceoLastName);
       dataMap.put("requisitionId",requisitionGv.getString("requisitionId"));
       List<GenericValue> performanceNoteList = (List<GenericValue>) dataMap.get("performanceList");
       dataMap.put("performanceNoteList",new SimpleList(performanceNoteList));
       mailContentMap.put("templateData", dataMap);
       ((Map) mailContentMap.get("templateData")).putAll(mailProp);
       Map<String, Object> resultMap = dispatcher.runSync("sendGenericNotificationEmail", mailContentMap);
       success = true;
       return success;
   }


   
}
