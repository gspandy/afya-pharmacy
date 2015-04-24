import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.party.contact.*;
import org.ofbiz.order.order.OrderReadHelper;
import java.sql.Timestamp;
import org.ofbiz.entity.condition.*;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.humanresext.util.HumanResUtil;


/** Employee Details **/
lpartyId = parameters.partyId;
System.out.println("************* lpartyId :" + lpartyId);
empFullName = HumanResUtil.getFullName(delegator, lpartyId, null);
context.empFullName = empFullName
System.out.println("************* empFullName :" + empFullName);

/** Get Authorised Signatory **/
EntityCondition desgCond = EntityCondition.makeCondition("positionDesc",EntityOperator.EQUALS, "AUTHORISED SIGNATORY");
desigList = [];
desigList = delegator.findList("PositionFulfillmentView", null, null, null, null, false);
if (desigList.size() != 0) {
	signatory = desigList.get(0); 
	context.signatory = HumanResUtil.getFullName(delegator, signatory.partyId, null);
} else {
	ServiceUtil.returnError("No Authorised Signaotry Found for positionDesc AUTHORISED SIGNATORY: ");
}

/** Get Employee Bank Details **/
prefHeader = delegator.findOne("PaySlipPrefView", UtilMisc.toMap("partyId", lpartyId), false);
System.out.println("************* prefHeader :" + prefHeader);
context.prefHeader = prefHeader;

// defaults:
logoImageUrl = null; // the default value, "/images/ofbiz_powered.gif", is set in the screen decorators
companyId = "Company";

// if partyId wasn't found use fromPartyId-parameter
// the logo
partyGroup = delegator.findByPrimaryKey("PartyGroup", [partyId : companyId]);
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


//  vikrant 
 person=delegator.findByPrimaryKey("Person",UtilMisc.toMap("partyId",lpartyId));
String location=(String) person.get("locationId");
List locationList=null;
EntityCondition condLocation = EntityCondition.makeCondition(UtilMisc.toMap(
		"locationId", location));
String locationGeo="";
locationList = delegator.findList("Location", condLocation, null,null, null, false);
//for(locate:locationList)
	locate=locationList.get(0);
 locationGeo=locate.getString("contactMechId");

 
 address = delegator.findByPrimaryKey("PostalAddress",UtilMisc.toMap("contactMechId",locationGeo));
 String country=address.getString("countryGeoId");
 String stateProvince=address.getString("stateProvinceGeoId");

 if (stateProvince) {
     context.stateProvinceAbbr = stateProvince;
 }
 context.postalAddress = address;

//vikrant


//addresses = delegator.findByAnd("PartyContactMechPurpose", [partyId : companyId, contactMechPurposeTypeId : "GENERAL_LOCATION"]);
//selAddresses = EntityUtil.filterByDate(addresses, nowTimestamp, "fromDate", "thruDate", true);        
//address = null;
//if (selAddresses) {
//    address = delegator.findByPrimaryKey("PostalAddress", [contactMechId : selAddresses[0].contactMechId]);
//}
//if (address)	{
//   // get the country name and state/province abbreviation
//   country = address.getRelatedOneCache("CountryGeo");
//   if (country) {
//      context.countryName = country.geoName;
//   }
//   stateProvince = address.getRelatedOneCache("StateProvinceGeo");
//   if (stateProvince) {
//       context.stateProvinceAbbr = stateProvince.abbreviation;
//   }
//}
//context.postalAddress = address;

//telephone
phones = delegator.findByAnd("PartyContactMechPurpose", [partyId : companyId, contactMechPurposeTypeId : "PRIMARY_PHONE"]);
selPhones = EntityUtil.filterByDate(phones, nowTimestamp, "fromDate", "thruDate", true);
if (selPhones) {
    context.phone = delegator.findByPrimaryKey("TelecomNumber", [contactMechId : selPhones[0].contactMechId]);
}

//Email
emails = delegator.findByAnd("PartyContactMechPurpose", [partyId : companyId, contactMechPurposeTypeId : "PRIMARY_EMAIL"]);
selEmails = EntityUtil.filterByDate(emails, nowTimestamp, "fromDate", "thruDate", true);
if (selEmails) {
    context.email = delegator.findByPrimaryKey("ContactMech", [contactMechId : selEmails[0].contactMechId]);
} else {    //get email address from party contact mech
    contacts = delegator.findByAnd("PartyContactMech", [partyId : companyId]);
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
contacts = delegator.findByAnd("PartyContactMech", [partyId : companyId]);
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

