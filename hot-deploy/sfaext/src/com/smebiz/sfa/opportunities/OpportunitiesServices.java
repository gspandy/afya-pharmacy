package com.smebiz.sfa.opportunities;


import java.sql.Timestamp;
import java.text.ParseException;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.party.party.PartyHelper;
import org.ofbiz.security.Security;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import com.smebiz.sfa.common.UtilMessage;
import com.smebiz.sfa.party.SfaPartyHelper;
import com.smebiz.sfa.security.CrmsfaSecurity;

public class OpportunitiesServices {

    public static final String module = OpportunitiesServices.class.getName();

    // TODO: the input for this service should be vastly simplified when AJAX autocomplete is finished: only input should be internalPartyId
    public static Map createOpportunity(DispatchContext dctx, Map context) {
        GenericDelegator delegator = (GenericDelegator) (GenericDelegator) dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Security security = dctx.getSecurity();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Locale locale = (Locale) context.get("locale");
        TimeZone timeZone = (TimeZone) context.get("timeZone");

        String internalPartyId = (String) context.get("internalPartyId");
        String accountPartyId = (String) context.get("accountPartyId");
        String contactPartyId = (String) context.get("contactPartyId");
        String leadPartyId = (String) context.get("leadPartyId");

        // if internal not supplied, then make sure either an account or lead is supplied, but not both
        if (internalPartyId == null && ((accountPartyId == null && leadPartyId == null) || (accountPartyId != null && leadPartyId != null))) {
            return UtilMessage.createAndLogServiceError("Please specify an account or a lead (not both).", "CrmErrorCreateOpportunityFail", locale, module);
        }

        // track which partyId we're using, the account or the lead
        String partyId = null;
        if (accountPartyId != null) partyId = accountPartyId;
        if (leadPartyId != null) partyId = leadPartyId;
        if (internalPartyId != null) partyId = internalPartyId;

        // make sure userLogin has CRMSFA_OPP_CREATE permission for the account or lead
        if (!CrmsfaSecurity.hasPartyRelationSecurity(security, "CRMSFA_OPP", "_CREATE", userLogin, partyId)) {
            return UtilMessage.createAndLogServiceError("CrmErrorPermissionDenied", locale, module);
        }

        try {
            // set the accountPartyId or leadPartyId according to the role of internalPartyId
            if (internalPartyId != null) {
                String roleTypeId = SfaPartyHelper.getFirstValidInternalPartyRoleTypeId(internalPartyId, delegator);
                if ("ACCOUNT".equals(roleTypeId)) accountPartyId = internalPartyId;
                if ("PROSPECT".equals(roleTypeId)) leadPartyId = internalPartyId;
            }

            // make sure the lead is qualified if we're doing initial lead
            if (leadPartyId != null) {
                GenericValue party = delegator.findByPrimaryKey("Party", UtilMisc.toMap("partyId", leadPartyId));
                if (party == null) {
                    return UtilMessage.createAndLogServiceError("CrmErrorLeadNotFound", UtilMisc.toMap("leadPartyId", leadPartyId), locale, module);
                }
                if (!"PTYLEAD_QUALIFIED".equals(party.get("statusId"))) {
                    return UtilMessage.createAndLogServiceError("CrmErrorLeadNotQualified", UtilMisc.toMap("leadPartyId", leadPartyId), locale, module);
                }
            }

            // set estimatedCloseDate to 23:59:59.999 so that it's at the end of the day
            String estimatedCloseDateString = (String) context.get("estimatedCloseDate");
            Timestamp estimatedCloseDate = UtilDateTime.getDayEnd(UtilDateTime.stringToTimeStamp(estimatedCloseDateString, UtilDateTime.DATE_FORMAT, timeZone, locale), timeZone, locale);

            // create the opportunity
            String salesOpportunityId = delegator.getNextSeqId("SalesOpportunity");
            GenericValue opportunity = delegator.makeValue("SalesOpportunity", UtilMisc.toMap("salesOpportunityId", salesOpportunityId));
            opportunity.setNonPKFields(context);
            opportunity.set("estimatedCloseDate", estimatedCloseDate);
            opportunity.set("createdByUserLogin", userLogin.getString("userLoginId"));

            // if an opportunityStageId is present, set the estimated probability to that of the related stage
            String opportunityStageId = (String) context.get("opportunityStageId");
            if (opportunityStageId != null) {
                GenericValue stage = opportunity.getRelatedOne("SalesOpportunityStage");
                opportunity.set("estimatedProbability", stage.getDouble("defaultProbability"));
            }

            // store it
            opportunity.create();

            // copy to history
            UtilOpportunity.createSalesOpportunityHistory(opportunity, delegator, context);

            // assign the initial account
            if (accountPartyId != null) {
                Map serviceResults = dispatcher.runSync("sfaext.assignOpportunityToAccount", 
                        UtilMisc.toMap("salesOpportunityId", salesOpportunityId, "accountPartyId", accountPartyId, "userLogin", userLogin));
                if (ServiceUtil.isError(serviceResults)) {
                    return UtilMessage.createAndLogServiceError(serviceResults, "CrmErrorCreateOpportunityFail", locale, module);
                }
            }

            // assign the initial lead
            if (leadPartyId != null) {
                Map serviceResults = dispatcher.runSync("sfaext.assignOpportunityToLead", 
                        UtilMisc.toMap("salesOpportunityId", salesOpportunityId, "leadPartyId", leadPartyId, "userLogin", userLogin));
                if (ServiceUtil.isError(serviceResults)) {
                    return UtilMessage.createAndLogServiceError(serviceResults, "CrmErrorCreateOpportunityFail", locale, module);
                }
            }

            // assign the initial contact, but only if account was specified
            if (contactPartyId != null && accountPartyId != null) {
                Map serviceResults = dispatcher.runSync("sfaext.addContactToOpportunity", 
                        UtilMisc.toMap("salesOpportunityId", salesOpportunityId, "contactPartyId", contactPartyId, "userLogin", userLogin));
                if (ServiceUtil.isError(serviceResults)) {
                    return UtilMessage.createAndLogServiceError(serviceResults, "CrmErrorCreateOpportunityFail", locale, module);
                }
            }

            // update forecasts as the system user, so we can update all forecasts for all team members that need updating
            GenericValue system = delegator.findByPrimaryKeyCache("UserLogin", UtilMisc.toMap("userLoginId", "system"));
            Map serviceResults = dispatcher.runSync("sfaext.updateForecastsRelatedToOpportunity", 
                    UtilMisc.toMap("salesOpportunityId", salesOpportunityId, "userLogin", system));
            if (ServiceUtil.isError(serviceResults)) {
                return UtilMessage.createAndLogServiceError(serviceResults, "CrmErrorCreateOpportunityFail", locale, module);
            }
			// return the resulting opportunity ID
            Map results = ServiceUtil.returnSuccess();
            results.put("salesOpportunityId", salesOpportunityId);
            return results;
        } catch (GenericServiceException e) {
            return UtilMessage.createAndLogServiceError(e, "CrmErrorCreateOpportunityFail", locale, module);
        } catch (GenericEntityException e) {
            return UtilMessage.createAndLogServiceError(e, "CrmErrorCreateOpportunityFail", locale, module);
        } catch (ParseException pe) {
            return UtilMessage.createAndLogServiceError(pe, locale, module);
        }
    }

    public static Map updateOpportunity(DispatchContext dctx, Map context) {
        GenericDelegator delegator = (GenericDelegator) (GenericDelegator) dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Security security = dctx.getSecurity();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Locale locale = (Locale) context.get("locale");
        TimeZone timeZone = (TimeZone) context.get("timeZone");

        String salesOpportunityId = (String) context.get("salesOpportunityId");

        try {
            GenericValue opportunity = delegator.findByPrimaryKey("SalesOpportunity", UtilMisc.toMap("salesOpportunityId", salesOpportunityId));
            if (opportunity == null) {
                return UtilMessage.createAndLogServiceError("CrmErrorUpdateOpportunityFail", locale, module);
            }

            // for security, we need to get the accountPartyId or leadPartyId for this opportunity
            String partyId = UtilOpportunity.getOpportunityAccountOrLeadPartyId(opportunity);
            
            // make sure userLogin has CRMSFA_OPP_UPDATE permission for this account or lead
            if (!CrmsfaSecurity.hasPartyRelationSecurity(security, "CRMSFA_OPP", "_UPDATE", userLogin, partyId)) {
                return UtilMessage.createAndLogServiceError("CrmErrorPermissionDenied", locale, module);
            }

            // get the new and old stages
            String stageId = opportunity.getString("opportunityStageId");
            String newStageId = (String) context.get("opportunityStageId");
            if (stageId == null) stageId = "";
            if (newStageId == null) newStageId = "";

            // this is needed for updating forecasts
            Timestamp previousEstimatedCloseDate = opportunity.getTimestamp("estimatedCloseDate");
            
            // update the fields
            opportunity.setNonPKFields(context);

            // set estimatedCloseDate to 23:59:59.999 so that it's at the end of the day
            String estimatedCloseDateString = (String) context.get("estimatedCloseDate");
            Timestamp estimatedCloseDate = UtilDateTime.getDayEnd(UtilDateTime.stringToTimeStamp(estimatedCloseDateString, UtilDateTime.DATE_FORMAT, timeZone, locale), timeZone, locale);
            opportunity.set("estimatedCloseDate", estimatedCloseDate);

            // if the stage changed, set the probability to the one of the stage
            if (!stageId.equals(newStageId)) {
                opportunity.set("estimatedProbability", opportunity.getRelatedOne("SalesOpportunityStage").getDouble("defaultProbability"));
            }

            // store
            opportunity.store();

            // copy the _new_ opportunity into history
            UtilOpportunity.createSalesOpportunityHistory(opportunity, delegator, context);

            // update forecasts as the system user, so we can update all forecasts for all team members that need updating
           
            GenericValue system = delegator.findByPrimaryKeyCache("UserLogin", UtilMisc.toMap("userLoginId", "system"));
            Map serviceResults = dispatcher.runSync("sfaext.updateForecastsRelatedToOpportunity", 
                    UtilMisc.toMap("salesOpportunityId", salesOpportunityId, "previousEstimatedCloseDate", previousEstimatedCloseDate, 
                            "changeNote", context.get("changeNote"), "userLogin", system));
            if (ServiceUtil.isError(serviceResults)) {
                return UtilMessage.createAndLogServiceError(serviceResults, "CrmErrorUpdateOpportunityFail", locale, module);
            }
            
            
        }catch(GenericServiceException e){ 
            return UtilMessage.createAndLogServiceError(e, "CrmErrorUpdateOpportunityFail", locale, module);
        }catch (GenericEntityException e) {
            return UtilMessage.createAndLogServiceError(e, "CrmErrorUpdateOpportunityFail", locale, module);
        } catch (ParseException pe) {
            return UtilMessage.createAndLogServiceError(pe, locale, module);
        }
        return ServiceUtil.returnSuccess();
    }

    public static Map assignOpportunityToAccount(DispatchContext dctx, Map context) {
        return assignOpportunityToPartyHelper(dctx, context, (String) context.get("accountPartyId"), "ACCOUNT", "CRMSFA_ACCOUNT");
    }

    public static Map assignOpportunityToLead(DispatchContext dctx, Map context) {
        return assignOpportunityToPartyHelper(dctx, context, (String) context.get("leadPartyId"), "PROSPECT", "CRMSFA_LEAD");
    }
    
    /** Helper method to assign an opportunity to an account/lead party */
    private static Map assignOpportunityToPartyHelper(DispatchContext dctx, Map context, String partyId, String roleTypeId, String permissionId) {
        GenericDelegator delegator = (GenericDelegator) (GenericDelegator) dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Security security = dctx.getSecurity();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Locale locale = (Locale) context.get("locale");

        String salesOpportunityId = (String) context.get("salesOpportunityId");

        // check if userLogin has update permission for this party 
        if (!CrmsfaSecurity.hasPartyRelationSecurity(security, permissionId, "_UPDATE", userLogin, partyId)) {
            return UtilMessage.createAndLogServiceError("CrmErrorPermissionDenied", locale, module);
        }
        try {
            // create a SalesOpportunityRole with salesOpportunityId, partyId and roleTypeId
            GenericValue role = delegator.makeValue("SalesOpportunityRole", UtilMisc.toMap("salesOpportunityId", salesOpportunityId, 
                        "partyId", partyId, "roleTypeId", roleTypeId));
            role.create();
        } catch (GenericEntityException e) {
            return UtilMessage.createAndLogServiceError(e, "CrmErrorAssignFail", locale, module);
        }
        return ServiceUtil.returnSuccess();
    }

    public static Map addContactToOpportunity(DispatchContext dctx, Map context) {
        GenericDelegator delegator = (GenericDelegator) (GenericDelegator) dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Security security = dctx.getSecurity();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Locale locale = (Locale) context.get("locale");

        String salesOpportunityId = (String) context.get("salesOpportunityId");
        String contactPartyId = (String) context.get("contactPartyId");

        try {
            GenericValue opportunity = delegator.findByPrimaryKey("SalesOpportunity", UtilMisc.toMap("salesOpportunityId", salesOpportunityId));
            if (opportunity == null) {
                return UtilMessage.createAndLogServiceError("CrmErrorAddContactToOpportunity", locale, module);
            }

            // for security, we need to get the accountPartyId for this opportunity
            String accountPartyId = UtilOpportunity.getOpportunityAccountPartyId(opportunity);

            // if no account exists, don't add contact to opportunity (rationale: this is a weird case and we don't know if convert lead will copy these)
            if (accountPartyId == null) {
                String leadPartyId = UtilOpportunity.getOpportunityLeadPartyId(opportunity);
                if (leadPartyId != null) {
                    return UtilMessage.createAndLogServiceError("Cannot add contact to a lead opportunity.", "CrmErrorAddContactToOpportunity", locale, module);
                } else {
                    return UtilMessage.createAndLogServiceError("Cound not find account for opportunity ["+salesOpportunityId+"].", "CrmErrorAddContactToOpportunity", locale, module);
                }
            }

            // check if userLogin has CRMSFA_OPP_UPDATE permission for this contact
            if (!CrmsfaSecurity.hasPartyRelationSecurity(security, "CRMSFA_OPP", "_UPDATE", userLogin, accountPartyId)) {
                return UtilMessage.createAndLogServiceError("CrmErrorPermissionDenied", locale, module);
            }

            // check first that this contact is associated with the account
            List candidates = EntityUtil.filterByDate(delegator.findByAnd("PartyRelationship", UtilMisc.toMap("partyIdFrom", contactPartyId,
                            "partyIdTo", accountPartyId, "partyRelationshipTypeId", "CONTACT_REL_INV"), UtilMisc.toList("fromDate DESC")));
            if (candidates.size() == 0) {
                return UtilMessage.createAndLogServiceError("Contact with ID [" + contactPartyId + "] is not associated with Account with ID [" +
                        accountPartyId + "]", "CrmErrorAddContactToOpportunity", locale, module);
            }

            // avoid duplicates
            Map keys = UtilMisc.toMap("salesOpportunityId", salesOpportunityId, "partyId", contactPartyId, "roleTypeId", "CONTACT");
            GenericValue role = delegator.findByPrimaryKey("SalesOpportunityRole", keys);
            if (role != null) {
                return UtilMessage.createAndLogServiceError("Contact is already associated with this Opportunity.", "CrmErrorAddContactToOpportunity", locale, module);
            }

            // create a SalesOpportunityRole with salesOpportunityId and partyId=contactPartyId and roleTypeId=CONTACT
            role = delegator.makeValue("SalesOpportunityRole", keys);
            role.create();
        } catch (GenericEntityException e) {
            return UtilMessage.createAndLogServiceError(e, "CrmErrorAddContactToOpportunity", locale, module);
        }
        return ServiceUtil.returnSuccess();
    }
 
    public static Map removeContactFromOpportunity(DispatchContext dctx, Map context) {
        GenericDelegator delegator = (GenericDelegator) (GenericDelegator) dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Security security = dctx.getSecurity();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Locale locale = (Locale) context.get("locale");

        String salesOpportunityId = (String) context.get("salesOpportunityId");
        String contactPartyId = (String) context.get("contactPartyId");

        try {
            GenericValue opportunity = delegator.findByPrimaryKey("SalesOpportunity", UtilMisc.toMap("salesOpportunityId", salesOpportunityId));
            if (opportunity == null) {
                return UtilMessage.createAndLogServiceError("No opportunity with ID [" + salesOpportunityId + "] found.", 
                        "CrmErrorRemoveContactFromOpportunity", locale, module);
            }

            // for security, we need to get the accountPartyId for this opportunity
            String accountPartyId = UtilOpportunity.getOpportunityAccountPartyId(opportunity);

            // check if userLogin has CRMSFA_OPP_UPDATE permission for this contact
            if (!CrmsfaSecurity.hasPartyRelationSecurity(security, "CRMSFA_OPP", "_UPDATE", userLogin, accountPartyId)) {
                return UtilMessage.createAndLogServiceError("CrmErrorPermissionDenied", locale, module);
            }

            // delete the SalesOpportunityRole with salesOpportunityId and partyId=contactPartyId and roleTypeId=CONTACT
            GenericValue role = delegator.findByPrimaryKey("SalesOpportunityRole", UtilMisc.toMap("salesOpportunityId", salesOpportunityId, 
                        "partyId", contactPartyId, "roleTypeId", "CONTACT"));
            if (role == null) {
                return UtilMessage.createAndLogServiceError("Could not find contact with ID [" + 
                        contactPartyId + "] for the opportunity with ID [" + salesOpportunityId + "].", "CrmErrorRemoveContactFromOpportunity", locale, module);
            }
            role.remove();
        } catch (GenericEntityException e) {
            return UtilMessage.createAndLogServiceError(e, "CrmErrorRemoveContactFromOpportunity", locale, module);
        }
        return ServiceUtil.returnSuccess();
    }

    public static Map addQuoteToOpportunity(DispatchContext dctx, Map context) {
        GenericDelegator delegator = (GenericDelegator) (GenericDelegator) dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Security security = dctx.getSecurity();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Locale locale = (Locale) context.get("locale");

        String salesOpportunityId = (String) context.get("salesOpportunityId");
        String quoteId = (String) context.get("quoteId");

        try {
            // for security, we need to get the account or lead partyId for this opportunity
            String partyId = UtilOpportunity.getOpportunityAccountOrLeadPartyId(delegator.findByPrimaryKey("SalesOpportunity", 
                        UtilMisc.toMap("salesOpportunityId", salesOpportunityId)));

            // make sure userLogin has CRMSFA_OPP_UPDATE permission for this account
            if (!CrmsfaSecurity.hasPartyRelationSecurity(security, "CRMSFA_OPP", "_UPDATE", userLogin, partyId)) {
                return UtilMessage.createAndLogServiceError("CrmErrorPermissionDenied", locale, module);
            }

            // There's no service in ofbiz to create SalesOpportunityQuote entries, so we do it by hand
            Map input = UtilMisc.toMap("quoteId", quoteId);
            GenericValue relation = delegator.findByPrimaryKeyCache("Quote", input);
            if (relation == null) {
                return UtilMessage.createAndLogServiceError("CrmErrorQuoteNotFound", UtilMisc.toMap("quoteId", quoteId), locale, module);
            }
        
            // see if the relation already exists and if not, create it
            input.put("salesOpportunityId", salesOpportunityId);
            relation = delegator.findByPrimaryKeyCache("SalesOpportunityQuote", input);
            if (relation == null) { 
                relation = delegator.makeValue("SalesOpportunityQuote", input);
                relation.create();
            }
        } catch (GenericEntityException e) {
            return UtilMessage.createAndLogServiceError(e, "CrmErrorAddQuoteFail", locale, module);
        }
        return ServiceUtil.returnSuccess();
    }

    public static Map removeQuoteFromOpportunity(DispatchContext dctx, Map context) {
        GenericDelegator delegator = (GenericDelegator) (GenericDelegator) dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Security security = dctx.getSecurity();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Locale locale = (Locale) context.get("locale");

        String salesOpportunityId = (String) context.get("salesOpportunityId");
        String quoteId = (String) context.get("quoteId");

        try {
            // for security, we need to get the account or lead partyId for this opportunity
            String partyId = UtilOpportunity.getOpportunityAccountOrLeadPartyId(delegator.findByPrimaryKey("SalesOpportunity", 
                        UtilMisc.toMap("salesOpportunityId", salesOpportunityId)));

            // make sure userLogin has CRMSFA_OPP_UPDATE permission for this account
            if (!CrmsfaSecurity.hasPartyRelationSecurity(security, "CRMSFA_OPP", "_UPDATE", userLogin, partyId)) {
                return UtilMessage.createAndLogServiceError("CrmErrorPermissionDenied", locale, module);
            }

            // There's no service in ofbiz to remove SalesOpportunityQuote entries, so we do it by hand
        
            // see if the relation already exists and if so, remove it
            Map input = UtilMisc.toMap("salesOpportunityId", salesOpportunityId, "quoteId", quoteId);
            GenericValue relation = delegator.findByPrimaryKey("SalesOpportunityQuote", input);
            if (relation != null) { 
                relation.remove();
            }
        } catch (GenericEntityException e) {
            return UtilMessage.createAndLogServiceError(e, "CrmErrorRemoveQuoteFail", locale, module);
        }
        return ServiceUtil.returnSuccess();
    }

}
