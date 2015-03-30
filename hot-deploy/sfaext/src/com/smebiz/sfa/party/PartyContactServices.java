package com.smebiz.sfa.party;

import java.util.Map;
import java.util.Locale;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.security.Security;

import com.smebiz.sfa.common.UtilMessage;


public class PartyContactServices {

	public static final String module = PartyContactServices.class.getName();

	    public static Map createBasicContactInfoForParty(DispatchContext dctx, Map context) {
	        GenericDelegator delegator = (GenericDelegator) (GenericDelegator) dctx.getDelegator();
	        LocalDispatcher dispatcher = dctx.getDispatcher();
	        Security security = dctx.getSecurity();
	        GenericValue userLogin = (GenericValue) context.get("userLogin");
	        Locale locale = (Locale) context.get("locale");
	        Map serviceResults = null; // for collecting service results
	        Map results = ServiceUtil.returnSuccess();  // for returning the contact mech IDs when finished

	        // security
	        if (!security.hasEntityPermission("PARTYMGR", "_PCM_CREATE", userLogin)) {
	            return UtilMessage.createAndLogServiceError("CrmErrorPermissionDenied", locale, module);
	        }
	        
	        // input
	        String partyId = (String) context.get("partyId");
	        String primaryEmail = (String) context.get("primaryEmail");
	        String primaryWebUrl = (String) context.get("primaryWebUrl");
	        String primaryPhoneCountryCode = (String) context.get("primaryPhoneCountryCode");
	        String primaryPhoneAreaCode = (String) context.get("primaryPhoneAreaCode");
	        String primaryPhoneNumber = (String) context.get("primaryPhoneNumber");
	        String primaryPhoneExtension = (String) context.get("primaryPhoneExtension");
	        String primaryPhoneAskForName = (String) context.get("primaryPhoneAskForName");
	        String generalToName = (String) context.get("generalToName");
	        String generalAttnName = (String) context.get("generalAttnName");
	        String generalAddress1 = (String) context.get("generalAddress1");
	        String generalAddress2 = (String) context.get("generalAddress2");
	        String generalCity = (String) context.get("generalCity");
	        String generalStateProvinceGeoId = (String) context.get("generalStateProvinceGeoId");
	        String generalPostalCode = (String) context.get("generalPostalCode");
	        String generalPostalCodeExt = (String) context.get("generalPostalCodeExt");
	        String generalCountryGeoId = (String) context.get("generalCountryGeoId");

	        try {
	            // create primary email
	            if ((primaryEmail != null) && !primaryEmail.equals("")) {
	                serviceResults = dispatcher.runSync("createPartyEmailAddress", UtilMisc.toMap("partyId", partyId, "userLogin", userLogin,
	                            "contactMechTypeId", "EMAIL_ADDRESS", "contactMechPurposeTypeId", "PRIMARY_EMAIL", "emailAddress", primaryEmail));
	                if (ServiceUtil.isError(serviceResults)) {
	                    return serviceResults;
	                }
	                results.put("primaryEmailContactMechId", serviceResults.get("contactMechId"));
	            }

	            // create primary web url
	            if ((primaryWebUrl != null) && !primaryWebUrl.equals("")) {
	                serviceResults = dispatcher.runSync("createPartyContactMech", UtilMisc.toMap("partyId", partyId, "userLogin", userLogin,
	                            "contactMechTypeId", "WEB_ADDRESS", "contactMechPurposeTypeId", "PRIMARY_WEB_URL", "infoString", primaryWebUrl));
	                if (ServiceUtil.isError(serviceResults)) {
	                    return serviceResults;
	                }
	                results.put("primaryWebUrlContactMechId", serviceResults.get("contactMechId"));
	            }

	            // create primary telecom number
	            if (((primaryPhoneNumber != null) && !primaryPhoneNumber.equals(""))) {
	                Map input = UtilMisc.toMap("partyId", partyId, "userLogin", userLogin, "contactMechPurposeTypeId", "PRIMARY_PHONE");
	                input.put("countryCode", primaryPhoneCountryCode);
	                input.put("areaCode", primaryPhoneAreaCode);
	                input.put("contactNumber", primaryPhoneNumber);
	                input.put("extension", primaryPhoneExtension);
	                input.put("askForName", primaryPhoneAskForName);
	                serviceResults = dispatcher.runSync("createPartyTelecomNumber", input);
	                if (ServiceUtil.isError(serviceResults)) {
	                    return serviceResults;
	                }
	                results.put("primaryPhoneContactMechId", serviceResults.get("contactMechId"));
	            }

	            // create general correspondence postal address
	            if ((generalAddress1 != null) && !generalAddress1.equals("")) {
	                Map input = UtilMisc.toMap("partyId", partyId, "userLogin", userLogin, "contactMechPurposeTypeId", "GENERAL_LOCATION");
	                input.put("toName", generalToName);
	                input.put("attnName", generalAttnName);
	                input.put("address1", generalAddress1);
	                input.put("address2", generalAddress2);
	                input.put("city", generalCity);
	                input.put("stateProvinceGeoId", generalStateProvinceGeoId);
	                input.put("postalCode", generalPostalCode);
	                input.put("postalCodeExt", generalPostalCodeExt);
	                input.put("countryGeoId", generalCountryGeoId);
	                serviceResults = dispatcher.runSync("createPartyPostalAddress", input);
	                if (ServiceUtil.isError(serviceResults)) {
	                    return serviceResults;
	                }
	                String contactMechId = (String) serviceResults.get("contactMechId");
	                results.put("generalAddressContactMechId", contactMechId);

	                // also make this address the SHIPPING_LOCATION
	                input = UtilMisc.toMap("partyId", partyId, "userLogin", userLogin, "contactMechId", contactMechId, "contactMechPurposeTypeId", "SHIPPING_LOCATION");
	                serviceResults = dispatcher.runSync("createPartyContactMechPurpose", input);
	                if (ServiceUtil.isError(serviceResults)) {
	                    return serviceResults;
	                }
	            }

	        } catch (GenericServiceException e) {
	            return UtilMessage.createAndLogServiceError(e, "CrmErrorCreateBasicContactInfoFail", locale, module);
	        }
	        return results;
	    }

	}
