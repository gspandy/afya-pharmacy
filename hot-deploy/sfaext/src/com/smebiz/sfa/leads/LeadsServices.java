package com.smebiz.sfa.leads;

import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.security.Security;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

import com.smebiz.sfa.common.UtilMessage;
import com.smebiz.sfa.party.SfaPartyHelper;
import com.smebiz.sfa.security.CrmsfaSecurity;

public class LeadsServices {

    private static final String module="LeadsServices";
    
    public static Map createLead(DispatchContext dctx, Map context) {
        GenericDelegator delegator = (GenericDelegator) (GenericDelegator) dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Security security = dctx.getSecurity();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Locale locale = (Locale) context.get("locale");

        /*if (!security.hasPermission("CRMSFA_LEAD_CREATE", userLogin)) {
            return ServiceUtil.returnError("No permission");
        }*/

        try {
            if (UtilValidate.isNotEmpty(context.get("parentPartyId")))
                SfaPartyHelper.isActive((String) context.get("parentPartyId"), delegator);
        } catch (GenericEntityException e) {
            return ServiceUtil.returnError("Parent Account is not Active.");
        }

        // the net result of creating an lead is the generation of a Lead partyId
        String leadPartyId = null;
        try {
            // make sure user has the right crmsfa roles defined.  otherwise the lead could be created but then once converted the account will be deactivated.
            if (UtilValidate.isEmpty(SfaPartyHelper.getFirstValidTeamMemberRoleTypeId(userLogin.getString("partyId"), delegator))) {
                return ServiceUtil.returnError("CrmError_NoRoleForCreateParty RequiredRoleTypes are "+ SfaPartyHelper.TEAM_MEMBER_ROLES);
            }

            // set statusId is PTYLEAD_ASSIGNED, because we are assigning to the user down below.
            // perhaps a better alternative is to create the lead as NEW, call the reassignLeadResponsibleParty service below, and have it update it to ASSIGNED if not already so.
            String statusId = "PTYLEAD_ASSIGNED";
            
            // create the Party and Person, which results in a partyId
            Map input = UtilMisc.toMap("firstName", context.get("firstName"), "lastName", context.get("lastName"));
            input.put("firstNameLocal", context.get("firstNameLocal"));
            input.put("lastNameLocal", context.get("lastNameLocal"));
            input.put("personalTitle", context.get("personalTitle"));
            input.put("preferredCurrencyUomId", context.get("currencyUomId"));
            input.put("description", context.get("description"));
            input.put("birthDate", context.get("birthDate"));
            input.put("statusId", statusId); // initial status
            Map serviceResults = dispatcher.runSync("createPerson", input);
            if (ServiceUtil.isError(serviceResults)) {
                return ServiceUtil.returnError("CrmErrorCreateLeadFail");
            }
            leadPartyId = (String) serviceResults.get("partyId");

            // create a PartyRole for the resulting Lead partyId with roleTypeId = PROSPECT
            serviceResults = dispatcher.runSync("createPartyRole", UtilMisc.toMap("partyId", leadPartyId, "roleTypeId", "PROSPECT", "userLogin", userLogin));
            if (ServiceUtil.isError(serviceResults)) {
                return ServiceUtil.returnError("CrmErrorCreateLeadFail");
            }

            // create a party relationship between the userLogin and the Lead with partyRelationshipTypeId RESPONSIBLE_FOR
            SfaPartyHelper.createNewPartyToRelationship(userLogin.getString("partyId"), leadPartyId, "PROSPECT", "RESPONSIBLE_FOR",
                    "LEAD_OWNER", SfaPartyHelper.TEAM_MEMBER_ROLES, true, userLogin, delegator, dispatcher);

            // if initial data source is provided, add it
            String dataSourceId = (String) context.get("dataSourceId");
            if (dataSourceId != null) {
                serviceResults = dispatcher.runSync("sfaext.addLeadDataSource", 
                        UtilMisc.toMap("partyId", leadPartyId, "dataSourceId", dataSourceId, "userLogin", userLogin));
                if (ServiceUtil.isError(serviceResults)) {
                    return ServiceUtil.returnError("CrmErrorCreateLeadFail");
                }
            }

            // if initial marketing campaign is provided, add it
            String marketingCampaignId = (String) context.get("marketingCampaignId");
            if (marketingCampaignId != null) {
                serviceResults = dispatcher.runSync("sfaext.addLeadMarketingCampaign",
                        UtilMisc.toMap("partyId", leadPartyId, "marketingCampaignId", marketingCampaignId, "userLogin", userLogin));
                if (ServiceUtil.isError(serviceResults)) {
                    return ServiceUtil.returnError("CrmErrorCreateLeadFail");
                }
            }

            // create basic contact info
            ModelService service = dctx.getModelService("sfaext.createBasicContactInfoForParty");
            input = service.makeValid(context, "IN");
            input.put("partyId", leadPartyId);
            serviceResults = dispatcher.runSync(service.name, input);
            if (ServiceUtil.isError(serviceResults)) {
                return ServiceUtil.returnError("CrmErrorCreateLeadFail");
            }
            

            // create PartySupplementalData
            GenericValue partyData = delegator.makeValue("PartySupplementalData", UtilMisc.toMap("partyId", leadPartyId));
            partyData.setNonPKFields(context);
            partyData.setString("primaryEmailId",(String)serviceResults.get("primaryEmailContactMechId"));
            partyData.setString("primaryTelecomNumberId",(String)serviceResults.get("primaryPhoneContactMechId"));
            partyData.setString("primaryPostalAddressId",(String)serviceResults.get("generalAddressContactMechId"));
            partyData.create();

            // Send email re: responsible party to all involved parties
            //Map sendEmailResult = dispatcher.runSync("sfaext.sendLeadNotificationEmails", UtilMisc.toMap("newPartyId", userLogin.getString("partyId"), "leadPartyId", leadPartyId, "userLogin", userLogin));

        } catch (GenericServiceException e) {
            return ServiceUtil.returnError("CrmErrorCreateLeadFail ");
        } catch (GenericEntityException e) {
            return ServiceUtil.returnError("CrmErrorCreateLeadFail ");
        }

        // return the partyId of the newly created Lead
        Map results = ServiceUtil.returnSuccess();
        results.put("partyId", leadPartyId);
        return results;
    }
	
    public static Map updateLead(DispatchContext dctx, Map context) {
        GenericDelegator delegator = (GenericDelegator) (GenericDelegator) dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Security security = dctx.getSecurity();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Locale locale = (Locale) context.get("locale");

        String leadPartyId = (String) context.get("partyId");
         
        // make sure userLogin has CRMSFA_LEAD_UPDATE permission for this lead
        if (!CrmsfaSecurity.hasPartyRelationSecurity(security, "CRMSFA_LEAD", "_UPDATE", userLogin, leadPartyId)) {
            return UtilMessage.createAndLogServiceError("CrmErrorPermissionDenied", locale, module);
        }

        try {
            if (UtilValidate.isNotEmpty(context.get("parentPartyId")))
                SfaPartyHelper.isActive((String) context.get("parentPartyId"), delegator);
        } catch (GenericEntityException e) {
            return UtilMessage.createAndLogServiceError("CrmErrorPermissionDenied", locale, module);
        }

        try {
            // get the party
            GenericValue party = delegator.findByPrimaryKey("Party", UtilMisc.toMap("partyId", leadPartyId));
            if (party == null) {
                return UtilMessage.createAndLogServiceError("CrmErrorUpdateLeadFail", locale, module);
            }

            // change status if passed in statusId is different
            String statusId = (String) context.get("statusId");
            if ((statusId != null) && (!statusId.equals(party.getString("statusId")))) {
                Map serviceResults = dispatcher.runSync("setPartyStatus", UtilMisc.toMap("partyId", leadPartyId, "statusId", statusId, "userLogin", userLogin));
                if (ServiceUtil.isError(serviceResults)) {
                    return UtilMessage.createAndLogServiceError(serviceResults, "CrmErrorUpdateLeadFail", locale, module);
                }
            } 

            // update the Party and Person
            Map input = UtilMisc.toMap("partyId", leadPartyId, "firstName", context.get("firstName"), "lastName", context.get("lastName"));
            input.put("firstNameLocal", context.get("firstNameLocal"));
            input.put("lastNameLocal", context.get("lastNameLocal"));
            input.put("personalTitle", context.get("personalTitle"));
            input.put("preferredCurrencyUomId", context.get("currencyUomId"));
            input.put("description", context.get("description"));
            input.put("birthDate", context.get("birthDate"));
            input.put("userLogin", userLogin);
            Map serviceResults = dispatcher.runSync("updatePerson", input);
            if (ServiceUtil.isError(serviceResults)) {
                return UtilMessage.createAndLogServiceError(serviceResults, "CrmErrorUpdateLeadFail", locale, module);
            }

            // update PartySupplementalData
            GenericValue partyData = delegator.findByPrimaryKey("PartySupplementalData", UtilMisc.toMap("partyId", leadPartyId));
            if (partyData == null) {
                // create a new one
                partyData = delegator.makeValue("PartySupplementalData", UtilMisc.toMap("partyId", leadPartyId));
                partyData.create();
            }
            partyData.setNonPKFields(context);
            partyData.store();

        } catch (GenericServiceException e) {
            return UtilMessage.createAndLogServiceError(e, "CrmErrorUpdateLeadFail", locale, module);
        } catch (GenericEntityException e) {
            return UtilMessage.createAndLogServiceError(e, "CrmErrorUpdateLeadFail", locale, module);
        }
        Map<String, Object> map = ServiceUtil.returnSuccess();
        map.put("partyId", leadPartyId);
        return map;
    }

    /**
     * Delete a "new" lead. A new lead has status PTYLEAD_NEW, PTYLEAD_ASSIGNED or PTYLEAD_QUALIFIED.
     * This will physically remove the lead from the Party entity and related entities.
     * If the party was successfully deleted, the method will return a service success, otherwise it
     * will return a service error with the reason.
     */
    public static Map deleteLead(DispatchContext dctx, Map context) {
        GenericDelegator delegator = (GenericDelegator) (GenericDelegator) dctx.getDelegator();
        Security security = dctx.getSecurity();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Locale locale = (Locale) context.get("locale");

        String leadPartyId = (String) context.get("leadPartyId");

        // ensure delete permission on this lead
        if (!CrmsfaSecurity.hasPartyRelationSecurity(security, "CRMSFA_LEAD", "_DELETE", userLogin, leadPartyId)) {
            return UtilMessage.createAndLogServiceError("CrmErrorPermissionDenied", locale, module);
        }

        try {
            // first ensure the lead is "new" (note that there's no need to check for role because only leads can have these statuses)
            GenericValue lead = delegator.findByPrimaryKey("Party", UtilMisc.toMap("partyId", leadPartyId));
            if (lead == null) {
                return UtilMessage.createAndLogServiceError("Lead [" + leadPartyId + "] not found.",
                        locale, module);
            }
            String statusId = lead.getString("statusId");
            if (statusId == null || !(statusId.equals("PTYLEAD_NEW") || statusId.equals("PTYLEAD_ASSIGNED") || statusId.equals("PTYLEAD_QUALIFIED"))) {
                return UtilMessage.createAndLogServiceError("Lead [" + leadPartyId + "] cannot be deleted. Only new, assigned or qualified leads may be deleted.", 
                         locale, module);
            }

            // record deletion (note this entity has no primary key on partyId)
            delegator.create("PartyDeactivation", UtilMisc.toMap("partyId", leadPartyId, "deactivationTimestamp", UtilDateTime.nowTimestamp()));

            // delete!
            SfaPartyHelper.deleteCrmParty(leadPartyId, delegator);

        } catch (GenericEntityException e) {
            return UtilMessage.createAndLogServiceError(e, "CrmErrorDeleteLeadFail", locale, module);
        }
        return ServiceUtil.returnSuccess();
    }
    
    public static Map convertLead(DispatchContext dctx, Map context) {
    	
        GenericDelegator delegator = (GenericDelegator) (GenericDelegator) dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Security security = dctx.getSecurity();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Locale locale = (Locale) context.get("locale");

        String leadPartyId = (String) context.get("leadPartyId");
        String accountPartyId = (String) context.get("accountPartyId");

        // make sure userLogin has CRMSFA_LEAD_UPDATE permission for this lead
        if (!CrmsfaSecurity.hasPartyRelationSecurity(security, "CRMSFA_LEAD", "_UPDATE", userLogin, leadPartyId)) {
            return UtilMessage.createAndLogServiceError("CrmErrorPermissionDenied", locale, module);
        }
        
        Map input = null;  // used later for service inputs
        try {
            GenericValue lead = delegator.findByPrimaryKey("PartySummaryCRMView", UtilMisc.toMap("partyId", leadPartyId));

            // create a PartyRole of type CONTACT for the lead
            Map serviceResults = dispatcher.runSync("createPartyRole", UtilMisc.toMap("partyId", leadPartyId, "roleTypeId", "CONTACT", "userLogin", userLogin));
            if (ServiceUtil.isError(serviceResults)) {
                return UtilMessage.createAndLogServiceError(serviceResults, "CrmErrorConvertLeadFail", locale, module);
            }

            // if no account was given, then create an account based on the PartySupplementalData of the lead
            if (accountPartyId == null) {
                input = UtilMisc.toMap("accountName", lead.getString("companyName"), "description", lead.getString("description"), "userLogin", userLogin);
                input.put("parentPartyId", lead.getString("parentPartyId"));
                input.put("annualRevenue", lead.getDouble("annualRevenue"));
                input.put("currencyUomId", lead.getString("currencyUomId"));
                input.put("numberEmployees", lead.getLong("numberEmployees"));
                input.put("industryEnumId", lead.getString("industryEnumId"));
                input.put("ownershipEnumId", lead.getString("ownershipEnumId"));
                input.put("importantNote", lead.getString("importantNote")); // The important note will be stored for account and contact
                input.put("sicCode", lead.getString("sicCode"));
                input.put("tickerSymbol", lead.getString("tickerSymbol"));
                serviceResults = dispatcher.runSync("sfaext.createAccount", input);
                if (ServiceUtil.isError(serviceResults)) {
                    return serviceResults;
                }  
                accountPartyId = (String) serviceResults.get("partyId");
                
                // copy all the datasources over to the new account
                List dataSources = delegator.findByAnd("PartyDataSource", UtilMisc.toMap("partyId", leadPartyId));
                for (Iterator iter = dataSources.iterator(); iter.hasNext(); ) {
                    GenericValue dataSource = (GenericValue) iter.next();
                    serviceResults = dispatcher.runSync("sfaext.addAccountDataSource", UtilMisc.toMap("partyId", accountPartyId, 
                                "dataSourceId", dataSource.getString("dataSourceId"), "userLogin", userLogin));
                    if (ServiceUtil.isError(serviceResults)) {
                        return UtilMessage.createAndLogServiceError(serviceResults, "CrmErrorConvertLeadFail", locale, module);
                    }
                }

                // copy all the marketing campaigns over to the new account
                List marketingCampaigns = delegator.findByAnd("MarketingCampaignRole", UtilMisc.toMap("partyId", leadPartyId, "roleTypeId", "PROSPECT"));
                for (Iterator iter = marketingCampaigns.iterator(); iter.hasNext(); ) {
                    GenericValue marketingCampaign = (GenericValue) iter.next();
                    serviceResults = dispatcher.runSync("sfaext.addAccountMarketingCampaign", UtilMisc.toMap("partyId", accountPartyId,
                                "marketingCampaignId", marketingCampaign.getString("marketingCampaignId"), "userLogin", userLogin));
                    if (ServiceUtil.isError(serviceResults)) {
                        return UtilMessage.createAndLogServiceError(serviceResults, "CrmErrorConvertLeadFail", locale, module);
                    }
                }

                // copy all the contact mechs to the account
                serviceResults = dispatcher.runSync("copyPartyContactMechs", UtilMisc.toMap("partyIdFrom", leadPartyId, "partyIdTo", accountPartyId, "userLogin", userLogin));
                if (ServiceUtil.isError(serviceResults)) {
                    return UtilMessage.createAndLogServiceError(serviceResults, "CrmErrorConvertLeadFail", locale, module);
                }
            }
            
            // erase (null out) the PartySupplementalData fields from the lead
            GenericValue leadSupplementalData = delegator.findByPrimaryKey("PartySupplementalData", UtilMisc.toMap("partyId", leadPartyId));
            leadSupplementalData.set("parentPartyId", null);
            leadSupplementalData.set("annualRevenue", null);
            leadSupplementalData.set("currencyUomId", null);
            leadSupplementalData.set("numberEmployees", null);
            leadSupplementalData.set("industryEnumId", null);
            leadSupplementalData.set("ownershipEnumId", null);
            leadSupplementalData.set("sicCode", null);
            leadSupplementalData.set("tickerSymbol", null);
            leadSupplementalData.store();

            // assign the lead, who is now a contact, to the account
            input = UtilMisc.toMap("contactPartyId", leadPartyId, "accountPartyId", accountPartyId, "userLogin", userLogin);
            serviceResults = dispatcher.runSync("sfaext.assignContactToAccount", input);
            if (ServiceUtil.isError(serviceResults)) {
                return serviceResults;
            }

            // expire all lead party relationships (roleTypeIdFrom = PROSPECT)
            List partyRelationships = delegator.findByAnd("PartyRelationship", UtilMisc.toMap("partyIdFrom", leadPartyId, "roleTypeIdFrom", "PROSPECT"));
            SfaPartyHelper.expirePartyRelationships(partyRelationships, UtilDateTime.nowTimestamp(), dispatcher, userLogin);

            // make the userLogin a RESPONSIBLE_FOR CONTACT_OWNER of the CONTACT
            SfaPartyHelper.createNewPartyToRelationship(userLogin.getString("partyId"), leadPartyId, "CONTACT", "RESPONSIBLE_FOR", "CONTACT_OWNER", 
                    SfaPartyHelper.TEAM_MEMBER_ROLES, true, userLogin, delegator, dispatcher);

            // opportunities assigned to the lead have to be updated to refer to both contact and account
            List oppRoles = delegator.findByAnd("SalesOpportunityRole", UtilMisc.toMap("partyId", leadPartyId, "roleTypeId", "PROSPECT")); 
            for (Iterator iter = oppRoles.iterator(); iter.hasNext(); ) {
                GenericValue oppRole = (GenericValue) iter.next();

                // create a CONTACT role using the leadPartyId
                input = UtilMisc.toMap("partyId", leadPartyId, "salesOpportunityId", oppRole.get("salesOpportunityId"), "roleTypeId", "CONTACT");
                GenericValue contactOppRole = delegator.makeValue("SalesOpportunityRole", input);
                contactOppRole.create();

                // create an ACCOUNT role for the new accountPartyId
                input = UtilMisc.toMap("partyId", accountPartyId, "salesOpportunityId", oppRole.get("salesOpportunityId"), "roleTypeId", "ACCOUNT");
                GenericValue accountOppRole = delegator.makeValue("SalesOpportunityRole", input);
                accountOppRole.create();

                // delete the PROSPECT role
                oppRole.remove();
            }

            // associate any lead files and bookmarks with both account and contact
            List conditions = UtilMisc.toList(
            		EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, leadPartyId),
            		EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "PROSPECT"),
                    EntityUtil.getFilterByDateExpr()
                    );
            List contentRoles = delegator.findList("ContentRole", EntityCondition.makeCondition(conditions),null,null,null,false);
            for (Iterator iter = contentRoles.iterator(); iter.hasNext(); ) {
                GenericValue contentRole = (GenericValue) iter.next();
                contentRole.set("thruDate", UtilDateTime.nowTimestamp());
                contentRole.store();

                GenericValue contactContentRole = delegator.makeValue("ContentRole");
                contactContentRole.set("partyId", leadPartyId);
                contactContentRole.set("contentId", contentRole.get("contentId"));
                contactContentRole.set("roleTypeId", "CONTACT");
                contactContentRole.set("fromDate", UtilDateTime.nowTimestamp());
                contactContentRole.create();

                GenericValue accountContent = delegator.makeValue("PartyContent");
                accountContent.set("partyId", accountPartyId);
                accountContent.set("contentId", contentRole.get("contentId"));
                accountContent.set("contentPurposeEnumId", "PTYCNT_CRMSFA");
                accountContent.create();

                GenericValue accountContentRole = delegator.makeValue("ContentRole");
                accountContentRole.set("partyId", accountPartyId);
                accountContentRole.set("contentId", contentRole.get("contentId"));
                accountContentRole.set("roleTypeId", "ACCOUNT");
                accountContentRole.set("fromDate", UtilDateTime.nowTimestamp());
                accountContentRole.create();
            }

            // set the status of the lead to PTYLEAD_CONVERTED
            serviceResults = dispatcher.runSync("setPartyStatus", UtilMisc.toMap("partyId", leadPartyId, "statusId", "PTYLEAD_CONVERTED", "userLogin", userLogin));
            if (ServiceUtil.isError(serviceResults)) {
                return UtilMessage.createAndLogServiceError(serviceResults, "CrmErrorConvertLeadFail", locale, module);
            }
        } catch (GenericServiceException e) {
            return UtilMessage.createAndLogServiceError(e, "CrmErrorUpdateLeadFail", locale, module);
        } catch (GenericEntityException e) {
            return UtilMessage.createAndLogServiceError(e, "CrmErrorUpdateLeadFail", locale, module);
        }
        // put leadPartyId as partyId
        Map results = ServiceUtil.returnSuccess();
        results.put("partyId", leadPartyId);
        return results;
    }


    public static Map reassignLeadResponsibleParty(DispatchContext dctx, Map context) {
        GenericDelegator delegator = (GenericDelegator) (GenericDelegator) dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Security security = dctx.getSecurity();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Locale locale = (Locale) context.get("locale");

        String leadPartyId = (String) context.get("leadPartyId");
        String newPartyId = (String) context.get("newPartyId");

        // ensure reassign permission on this lead
        if (!CrmsfaSecurity.hasPartyRelationSecurity(security, "CRMSFA_LEAD", "_REASSIGN", userLogin, leadPartyId)) {
            return UtilMessage.createAndLogServiceError("CrmErrorPermissionDenied", locale, module);
        }

        try {
            if (UtilValidate.isNotEmpty(newPartyId))
                SfaPartyHelper.isActive(newPartyId, delegator);
        } catch (GenericEntityException e) {
            return UtilMessage.createAndLogServiceError("CrmErrorLeadNotFound", UtilMisc.toMap("leadPartyId", newPartyId), locale, module);
        }

        try {
            // reassign relationship with this helper method, which expires previous ones
            boolean result = SfaPartyHelper.createNewPartyToRelationship(newPartyId, leadPartyId, "PROSPECT", "RESPONSIBLE_FOR",
                    "LEAD_OWNER", SfaPartyHelper.TEAM_MEMBER_ROLES, true, userLogin, delegator, dispatcher);
            if (result == false) {
                return UtilMessage.createAndLogServiceError("CrmErrorReassignFail", locale, module);
            }
        } catch (GenericServiceException e) {
            return UtilMessage.createAndLogServiceError(e, "CrmErrorReassignFail", locale, module);
        } catch (GenericEntityException e) {
            return UtilMessage.createAndLogServiceError(e, "CrmErrorReassignFail", locale, module);
        }
        return ServiceUtil.returnSuccess();
    }

    

}
