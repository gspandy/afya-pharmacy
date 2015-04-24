package com.smebiz.sfa.marketing;

import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

import com.smebiz.sfa.common.UtilMessage;
import com.smebiz.sfa.party.SfaPartyHelper;
import com.smebiz.sfa.security.CrmsfaSecurity;

public class MarketingCampaignServices {

	private static final String module=MarketingCampaignServices.class.getName();
	
	public static Map addLeadMarketingCampaign(DispatchContext dctx, Map context) {
        return addMarketingCampaignWithPermission(dctx, context, "CRMSFA_LEAD", "_UPDATE", "PROSPECT");
    }

   
    public static Map addAccountMarketingCampaign(DispatchContext dctx, Map context) {
        return addMarketingCampaignWithPermission(dctx, context, "CRMSFA_ACCOUNT", "_UPDATE", "ACCOUNT");
    }

    public static Map addContactMarketingCampaign(DispatchContext dctx, Map context) {
        return addMarketingCampaignWithPermission(dctx, context, "CRMSFA_CONTACT", "_UPDATE", "CONTACT");
    }

    /**
     * Parametrized service to add a marketing campaign to a party. Pass in the security to check.
     */
    private static Map addMarketingCampaignWithPermission(DispatchContext dctx, Map context, String module, String operation, String roleTypeId) {
        GenericDelegator delegator = (GenericDelegator) (GenericDelegator) dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Security security = dctx.getSecurity();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Locale locale = (Locale) context.get("locale");

        String partyId = (String) context.get("partyId");
        String marketingCampaignId = (String) context.get("marketingCampaignId");

        // check parametrized security
        if (!CrmsfaSecurity.hasPartyRelationSecurity(security, module, operation, userLogin, partyId)) {
            return UtilMessage.createAndLogServiceError("CrmErrorPermissionDenied", locale, module);
        }
        try {
            // create the MarketingCampaignRole to relate the optional marketing campaign to this party as the system user
            GenericValue system = delegator.findByPrimaryKeyCache("UserLogin", UtilMisc.toMap("userLoginId", "system"));
            Map serviceResults = dispatcher.runSync("createMarketingCampaignRole", 
                    UtilMisc.toMap("partyId", partyId , "roleTypeId", roleTypeId, "marketingCampaignId", marketingCampaignId, "userLogin", system));
            if (ServiceUtil.isError(serviceResults)) {
                return UtilMessage.createAndLogServiceError(serviceResults, "CrmErrorAddMarketingCampaign", locale, module);
            }
        } catch (GenericEntityException e) {
            return UtilMessage.createAndLogServiceError(e, "CrmErrorAddMarketingCampaign", locale, module);
        } catch (GenericServiceException e) {
            return UtilMessage.createAndLogServiceError(e, "CrmErrorAddMarketingCampaign", locale, module);
        }
        return ServiceUtil.returnSuccess();
    }

    public static Map removeAccountMarketingCampaign(DispatchContext dctx, Map context) {
        return removeMarketingCampaignWithPermission(dctx, context, "CRMSFA_ACCOUNT", "_UPDATE", "ACCOUNT");
    }

    public static Map removeContactMarketingCampaign(DispatchContext dctx, Map context) {
        return removeMarketingCampaignWithPermission(dctx, context, "CRMSFA_CONTACT", "_UPDATE", "CONTACT");
    }

    public static Map removeLeadMarketingCampaign(DispatchContext dctx, Map context) {
        return removeMarketingCampaignWithPermission(dctx, context, "CRMSFA_LEAD", "_UPDATE", "PROSPECT");
    }

    /**
     * Parametrized method to remove a marketing campaign from a party. Pass in the security to check.
     */
    private static Map removeMarketingCampaignWithPermission(DispatchContext dctx, Map context, String module, String operation, String roleTypeId) {
        GenericDelegator delegator = (GenericDelegator) (GenericDelegator) dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Security security = dctx.getSecurity();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Locale locale = (Locale) context.get("locale");

        String partyId = (String) context.get("partyId");
        String marketingCampaignId = (String) context.get("marketingCampaignId");

        // check parametrized security
        if (!CrmsfaSecurity.hasPartyRelationSecurity(security, module, operation, userLogin, partyId)) {
            return UtilMessage.createAndLogServiceError("CrmErrorPermissionDenied", locale, module);
        }
        try {
            // just remove the MarketingCampaignRole as the system user
            GenericValue system = delegator.findByPrimaryKeyCache("UserLogin", UtilMisc.toMap("userLoginId", "system"));
            Map serviceResults = dispatcher.runSync("deleteMarketingCampaignRole",
                    UtilMisc.toMap("partyId", partyId, "marketingCampaignId", marketingCampaignId, "roleTypeId", roleTypeId, "userLogin", system));
            if (ServiceUtil.isError(serviceResults)) {
                return UtilMessage.createAndLogServiceError(serviceResults, "CrmErrorRemoveMarketingCampaign", locale, module);
            }
        } catch (GenericEntityException e) {
            return UtilMessage.createAndLogServiceError(e, "CrmErrorRemoveMarketingCampaign", locale, module);
        } catch (GenericServiceException e) {
            return UtilMessage.createAndLogServiceError(e, "CrmErrorRemoveMarketingCampaign", locale, module);
        }

        return ServiceUtil.returnSuccess();
    }

    public static Map sendMarketingCampaignEmail(DispatchContext dctx, Map context) {
        GenericDelegator delegator = (GenericDelegator) (GenericDelegator) dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Locale locale = (Locale) context.get("locale");
        String marketingCampaignId = (String) context.get("marketingCampaignId");

        // TODO: security
        try {
            // create a communicaiton event for each contact list and invoke sendCommEventAsEmail
            List contactLists = delegator.findByAnd("ContactList", UtilMisc.toMap("marketingCampaignId", marketingCampaignId));
            contactLists = EntityUtil.filterByAnd(contactLists, UtilMisc.toMap("contactMechTypeId", "EMAIL_ADDRESS"));
            for (Iterator iter = contactLists.iterator(); iter.hasNext(); ) {
                GenericValue contactList = (GenericValue) iter.next();

                ModelService service = dctx.getModelService("createCommunicationEvent");
                Map input = service.makeValid(context, "IN");
                input.put("contactListId", contactList.get("contactListId"));
                input.put("entryDate", UtilDateTime.nowTimestamp());
                input.put("communicationEventTypeId", "EMAIL_COMMUNICATION");
                input.put("partyIdFrom", userLogin.getString("partyId"));
                input.put("roleTypeIdFrom", SfaPartyHelper.getFirstValidRoleTypeId(userLogin.getString("partyId"), SfaPartyHelper.TEAM_MEMBER_ROLES, delegator));
                Map serviceResults = dispatcher.runSync("createCommunicationEvent", input);
                if (ServiceUtil.isError(serviceResults)) {
                    return UtilMessage.createAndLogServiceError(serviceResults, "CrmErrorSendEmailToMarketingCampaignFail", locale, module);
                }
                String communicationEventId = (String) serviceResults.get("communicationEventId");

                serviceResults = dispatcher.runSync("sendCommEventAsEmail", UtilMisc.toMap("communicationEventId", communicationEventId, "userLogin", userLogin));
                if (ServiceUtil.isError(serviceResults)) {
                    return UtilMessage.createAndLogServiceError(serviceResults, "CrmErrorSendEmailToMarketingCampaignFail", locale, module);
                }
            }
        } catch (GenericEntityException e) {
            return UtilMessage.createAndLogServiceError(e, "CrmErrorSendEmailToMarketingCampaignFail", locale, module);
        } catch (GenericServiceException e) {
            return UtilMessage.createAndLogServiceError(e, "CrmErrorSendEmailToMarketingCampaignFail", locale, module);
        }

        return ServiceUtil.returnSuccess();
    }

}
