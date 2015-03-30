import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.util.EntityUtil;

import com.smebiz.sfa.security.CrmsfaSecurity;
import com.smebiz.sfa.party.SfaPartyHelper;
import com.smebiz.sfa.party.PartyContactHelper;

/*
 * The purpose of this script is to gather data for the duplicate lead function into one big map.
 */

partyId = parameters.get("partyId");

//make sure that the partyId is actually a PROSPECT (i.e., a lead) before trying to display it as once
delegator = request.getAttribute("delegator");
validRoleTypeId = SfaPartyHelper.getFirstValidRoleTypeId(partyId, UtilMisc.toList("PROSPECT"), delegator);
if ((validRoleTypeId == null) || !validRoleTypeId.equals("PROSPECT")) {
    return;
}

// now get all data necessary for this page
dispatcher = request.getAttribute("dispatcher");

// lead summary data
partySummary = delegator.findByPrimaryKey("PartySummaryCRMView", UtilMisc.toMap("partyId", partyId));

// in case this lead is not found
if (partySummary == null) {
    return;
}

//if the lead has already been converted, then just put this in there and nothing
if (partySummary.getString("statusId").equals("PTYLEAD_CONVERTED")) {
    context.put("hasBeenConverted", true);
    return;
} 

// this is a Map of the current lead's information which is passed into pre-fill the form
currentLead = new HashMap();
currentLead.putAll(partySummary.getAllFields());

// PRIMARY_PHONE telecom number
phone = PartyContactHelper.getTelecomNumberMapByPurpose(partyId, "PRIMARY_PHONE", true, delegator);
System.out.println("************** PHONE "+phone);
if (phone != null) {
    currentLead.put("primaryPhoneAreaCode", phone.get("areaCode"));
    currentLead.put("primaryPhoneNumber", phone.get("contactNumber"));
    currentLead.put("primaryPhoneCountryCode", phone.get("countryCode"));
    currentLead.put("primaryPhoneAskForName", phone.get("askForName"));
    currentLead.put("primaryPhoneExtension", phone.get("extension"));
}

// PRIMARY_EMAIL contactmech
email = PartyContactHelper.getElectronicAddressByPurpose(partyId, "EMAIL_ADDRESS", "PRIMARY_EMAIL", delegator);
currentLead.put("primaryEmail", email);

// PRIMARY_WEB_URL contactmech
url = PartyContactHelper.getElectronicAddressByPurpose(partyId, "WEB_ADDRESS", "PRIMARY_WEB_URL", delegator);
currentLead.put("primaryWebUrl", url);

// GENERAL_LOCATION postal address
address = PartyContactHelper.getPostalAddressValueByPurpose(partyId, "GENERAL_LOCATION", true, delegator);
if (address != null) {
    currentLead.put("generalToName", address.getString("toName"));
    currentLead.put("generalAttnName", address.getString("attnName"));
    currentLead.put("generalAddress1", address.getString("address1"));
    currentLead.put("generalAddress2", address.getString("address2"));
    currentLead.put("generalCity", address.getString("city"));
    currentLead.put("generalStateProvinceGeoId", address.getString("stateProvinceGeoId"));
    currentLead.put("generalPostalCode", address.getString("postalCode"));
    currentLead.put("generalPostalCodeExt", address.getString("postalCodeExt"));
    currentLead.put("generalCountryGeoId", address.getString("countryGeoId"));
}

// make it available for the duplicateLeadForm
context.put("currentLead", currentLead);

