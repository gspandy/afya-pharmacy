import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.party.contact.*;
import org.ofbiz.order.order.OrderReadHelper;
import java.sql.Timestamp;


// defaults:
logoImageUrl = null; // the default value, "/images/ofbiz_powered.gif", is set in the screen decorators
partyId = "Company";

// if partyId wasn't found use fromPartyId-parameter
// the logo
partyGroup = delegator.findByPrimaryKey("PartyGroup", [partyId : partyId]);
if (partyGroup?.logoImageUrl) {
    logoImageUrl = partyGroup.logoImageUrl;
}
context.logoImageUrl = logoImageUrl;

// the company name
companyName = "not found";
if (partyGroup?.groupName) {
    companyName = partyGroup.groupName;
}
context.companyName = companyName;

// the address
addresses = delegator.findByAnd("PartyContactMechPurpose", [partyId : partyId, contactMechPurposeTypeId : "GENERAL_LOCATION"]);
selAddresses = EntityUtil.filterByDate(addresses, nowTimestamp, "fromDate", "thruDate", true);        
address = null;
if (selAddresses) {
    address = delegator.findByPrimaryKey("PostalAddress", [contactMechId : selAddresses[0].contactMechId]);
}
if (address)	{
   // get the country name and state/province abbreviation
   country = address.getRelatedOneCache("CountryGeo");
   if (country) {
      context.countryName = country.geoName;
   }
   stateProvince = address.getRelatedOneCache("StateProvinceGeo");
   if (stateProvince) {
       context.stateProvinceAbbr = stateProvince.abbreviation;
   }
}
context.postalAddress = address;

//telephone
phones = delegator.findByAnd("PartyContactMechPurpose", [partyId : partyId, contactMechPurposeTypeId : "PRIMARY_PHONE"]);
selPhones = EntityUtil.filterByDate(phones, nowTimestamp, "fromDate", "thruDate", true);
if (selPhones) {
    context.phone = delegator.findByPrimaryKey("TelecomNumber", [contactMechId : selPhones[0].contactMechId]);
}

//Email
emails = delegator.findByAnd("PartyContactMechPurpose", [partyId : partyId, contactMechPurposeTypeId : "PRIMARY_EMAIL"]);
selEmails = EntityUtil.filterByDate(emails, nowTimestamp, "fromDate", "thruDate", true);
if (selEmails) {
    context.email = delegator.findByPrimaryKey("ContactMech", [contactMechId : selEmails[0].contactMechId]);
} else {    //get email address from party contact mech
    contacts = delegator.findByAnd("PartyContactMech", [partyId : partyId]);
    selContacts = EntityUtil.filterByDate(contacts, nowTimestamp, "fromDate", "thruDate", true);
    if (selContacts) {
        i = selContacts.iterator();
        while (i.hasNext())	{
            email = i.next().getRelatedOne("ContactMech");
            if ("ELECTRONIC_ADDRESS".equals(email.contactMechTypeId))	{
                context.email = email;
                break;
            }
        }
    }
}

// website
contacts = delegator.findByAnd("PartyContactMech", [partyId : partyId]);
selContacts = EntityUtil.filterByDate(contacts, nowTimestamp, "fromDate", "thruDate", true);        
if (selContacts) {
    Iterator i = selContacts.iterator();
    while (i.hasNext())	{
        website = i.next().getRelatedOne("ContactMech");
        if ("WEB_ADDRESS".equals(website.contactMechTypeId)) {
            context.website = website;
            break;
        }
    }
}