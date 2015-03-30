package com.smebiz.common;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;

public class PartyUtil {

	public static String getFullName(GenericDelegator delegator,
			String partyId, String separator) throws GenericEntityException {

		Map primaryKey = UtilMisc.toMap("partyId", partyId);
		GenericValue entity = delegator.findOne("PartyAndPerson", primaryKey,
				false);
		String firstName = "N";
		String lastName = "A";
		StringBuilder buffer = new StringBuilder();
		if (entity != null) {
			firstName = entity.getString("firstName");
			lastName = entity.getString("lastName");
			if (separator == null) {
				separator = ", ";
			}
		}
		buffer.append(firstName).append(separator == null ? " " : separator)
				.append(lastName);
		return buffer.toString();
	}

	
	
	public static String getGeoIdForUserLogin(GenericValue userLogin) throws GenericEntityException{
		
		
		GenericDelegator delegator = (GenericDelegator) userLogin.getDelegator();//GenericDelegator.getGenericDelegator("default");
		String partyId=userLogin.getString("partyId");
		GenericValue person=delegator.findByPrimaryKey("Person", UtilMisc.toMap("partyId",partyId));
		String location = (String) person.get("locationId");
		List<GenericValue> locationList = null;
		EntityCondition condLocation = EntityCondition.makeCondition(UtilMisc
				.toMap("locationId", location));
		String locationGeo = "";
		locationList = delegator.findList("Location", condLocation, null, null,
				null, false);
		for (GenericValue locate : locationList)
			locationGeo = locate.getString("contactMechId");

		GenericValue GeoGV = null;
		GeoGV = delegator.findByPrimaryKey("PostalAddress", UtilMisc.toMap(
				"contactMechId", locationGeo));
		if(GeoGV==null){
			EntityCondition entityCondition1 = EntityCondition.makeCondition("partyId", EntityOperator.EQUALS,"Company");
			EntityCondition entityCondition2 = EntityCondition.makeCondition("contactMechPurposeTypeId", EntityOperator.EQUALS,"GENERAL_LOCATION");
			EntityCondition entityCondition3 = EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS,null);
			List<EntityCondition> conditionList = UtilMisc.toList(entityCondition1,entityCondition2,entityCondition3);
			List<GenericValue> companyContactMechs = delegator.findList("PartyContactMechPurpose", EntityCondition.makeCondition(conditionList), null, null, null, true);

			GenericValue anyOneCompanyContactMech = EntityUtil.getFirst(companyContactMechs);

			GeoGV = delegator.findOne("PostalAddress",UtilMisc.toMap("contactMechId",anyOneCompanyContactMech.getString("contactMechId")),true);
		}
		if(GeoGV==null)return null;
		String countryGeoId = GeoGV.getString("countryGeoId");

		
		return countryGeoId;
	
	
	
	}
	
	
	
//	public static String getGeoIdForUserLogin(GenericValue userLogin) throws GenericEntityException {
//		//First Try the Primary Address of the User Login
//		GenericDelegator delegator = (GenericDelegator) userLogin.getDelegator();
//		EntityCondition cond1 = EntityCondition.makeCondition(UtilMisc.toMap("partyId", userLogin.getString("partyId"), "contactMechPurposeTypeId",
//				"PRIMARY_LOCATION"));
//		List<GenericValue> contactMechValues = delegator.findList("PartyContactMechPurpose", cond1, null, null, null, false);
//		String countryGeoId = "";
//		if (contactMechValues.size() > 0) {
//			GenericValue contactMechValue = contactMechValues.get(0);
//			Map<String, String> params = UtilMisc
//					.toMap("contactMechId", contactMechValue.getString("contactMechId"), "partyId", userLogin.getString("partyId"));
//
//			EntityCondition cond = EntityCondition.makeCondition(params);
//
//			List<GenericValue> values = delegator.findList("PartyAndPostalAddress", cond, null, null, null, false);
//
//			if (values.size() > 0) {
//				countryGeoId = values.get(0).getString("countryGeoId");
//			}
//		}
//		return countryGeoId;
//	}
//}

	public static Map<String,Object> getCompanyHeaderInfo(GenericDelegator delegator) throws GenericEntityException{
		
		Map<String,Object> companyHeaderInfoMap = new HashMap<String,Object>();
		
		// defaults:
		String partyId = UtilProperties.getPropertyValue("general.properties", "ORGANIZATION_PARTY");
		
		String logoImageUrl = "";
		String companyName = "";
		String companyRegNo = "Company Reg No:";
		String vatRegNo = "VAT Reg No:";
		String tpinNo = "TPIN No:";

		// Company logo
		GenericValue partyGroup = delegator.findByPrimaryKey("PartyGroup", UtilMisc.toMap("partyId",partyId));
		if (UtilValidate.isNotEmpty(partyGroup.get("logoImageUrl"))) {
			logoImageUrl = "./framework/images/webapp/images/" + partyGroup.get("logoImageUrl");
		}
		companyHeaderInfoMap.put("logoImageUrl", logoImageUrl);

		// Company info
		GenericValue company = delegator.findOne("PartyGroup", UtilMisc.toMap("partyId","Company"),true);
		if (UtilValidate.isNotEmpty(company.get("registrationNumber"))) {
			companyRegNo = "Company Reg No: " + company.get("registrationNumber");
		}
		companyHeaderInfoMap.put("companyRegNo", companyRegNo);
		
		if (UtilValidate.isNotEmpty(company.get("vatTinNumber"))) {
			vatRegNo = "VAT Reg No: " + company.get("vatTinNumber");
		}
		companyHeaderInfoMap.put("vatRegNo", vatRegNo);
		
		if (UtilValidate.isNotEmpty(company.get("cstNumber"))) {
			tpinNo = "TPIN No: " + company.get("cstNumber");
		}
		companyHeaderInfoMap.put("tpinNo", tpinNo);

		//Company name
		if (UtilValidate.isNotEmpty(partyGroup.getString("groupName"))) {
			companyName = partyGroup.getString("groupName");
		}
		companyHeaderInfoMap.put("companyName", companyName);

		// Company address
		List<GenericValue> addresses = delegator.findByAnd("PartyContactMechPurpose", UtilMisc.toMap("partyId", partyId, "contactMechPurposeTypeId", "GENERAL_LOCATION"));
		List<GenericValue> selAddresses = EntityUtil.filterByDate(addresses, UtilDateTime.nowTimestamp(), "fromDate", "thruDate", true);
		
		GenericValue address = null;
		if (UtilValidate.isNotEmpty(selAddresses)) {
			address = delegator.findByPrimaryKey("PostalAddress", UtilMisc.toMap("contactMechId", selAddresses.get(0).get("contactMechId")));
			GenericValue stateProvince = address.getRelatedOneCache("StateProvinceGeo");
			GenericValue country = address.getRelatedOneCache("CountryGeo");
			String companyPostalAddr = "";
			
			if (UtilValidate.isNotEmpty(address) && UtilValidate.isNotEmpty(address.get("postalCode")) && UtilValidate.isNotEmpty(address.get("city")) && 
				UtilValidate.isNotEmpty(stateProvince) && UtilValidate.isNotEmpty(stateProvince.get("abbreviation")) && 
				UtilValidate.isNotEmpty(country) && UtilValidate.isNotEmpty(country.get("geoName"))) {
				companyPostalAddr = "P.O. Box " + address.get("postalCode") + " - " + address.get("city") + ", " + country.get("geoName");
			}
			
			companyHeaderInfoMap.put("companyPostalAddr", companyPostalAddr);
		}
		
		if (UtilValidate.isNotEmpty(address)) {
			// get the address1, address2, postal code, city name, country name and state/province abbreviation
			String address1 = "";
			String address2 = "";
			String postalCode = "";
			String city = "";
			String stateProvinceAbbr = "";
			String countryName = "";
			
			if (UtilValidate.isNotEmpty(address.get("address1"))) {
				address1 = address.getString("address1");
			}
			companyHeaderInfoMap.put("address1", address1);
			
			if (UtilValidate.isNotEmpty(address.get("address2"))) {
				address2 = address.getString("address2");
			}
			companyHeaderInfoMap.put("address2", address2);
			
			if (UtilValidate.isNotEmpty(address.get("postalCode"))) {
				postalCode = address.getString("postalCode");
			}
			companyHeaderInfoMap.put("postalCode", postalCode);
			
			if (UtilValidate.isNotEmpty(address.get("city"))) {
				city = address.getString("city");
			}
			companyHeaderInfoMap.put("city", city);
			
			GenericValue stateProvince = address.getRelatedOneCache("StateProvinceGeo");
			if (UtilValidate.isNotEmpty(stateProvince)) {
				stateProvinceAbbr = stateProvince.getString("abbreviation");
			}
			companyHeaderInfoMap.put("stateProvinceAbbr", stateProvinceAbbr);
			
			GenericValue country = address.getRelatedOneCache("CountryGeo");
			if (UtilValidate.isNotEmpty(country)) {
				countryName = country.getString("geoName");
			}
			companyHeaderInfoMap.put("countryName", countryName);
		}
		
		// Telephone
		List<GenericValue> phones = delegator.findByAnd("PartyContactMechPurpose", UtilMisc.toMap("partyId", partyId, "contactMechPurposeTypeId", "PRIMARY_PHONE"));
		List<GenericValue> selPhones = EntityUtil.filterByDate(phones, UtilDateTime.nowTimestamp(), "fromDate", "thruDate", true);
		
		GenericValue phone = null;
		String telephoneNumber = "tel.";
		if (UtilValidate.isNotEmpty(selPhones)) {
		    phone = delegator.findByPrimaryKey("TelecomNumber", UtilMisc.toMap("contactMechId", selPhones.get(0).get("contactMechId")));
			if (UtilValidate.isNotEmpty(phone)) {
				if (UtilValidate.isEmpty(phone.get("countryCode")) && UtilValidate.isEmpty(phone.get("areaCode")) && UtilValidate.isEmpty(phone.get("contactNumber"))) {
					telephoneNumber = "tel.";
				}
				if (UtilValidate.isNotEmpty(phone.get("countryCode")) && UtilValidate.isNotEmpty(phone.get("areaCode")) && UtilValidate.isNotEmpty(phone.get("contactNumber"))) {
					telephoneNumber = "tel. +" + phone.get("countryCode") + " " + phone.get("areaCode") + " " + phone.get("contactNumber");
				}
				if (UtilValidate.isEmpty(phone.get("countryCode")) && UtilValidate.isNotEmpty(phone.get("areaCode")) && UtilValidate.isNotEmpty(phone.get("contactNumber"))) {
					telephoneNumber = "tel. " + phone.get("areaCode") + " " + phone.get("contactNumber");
				}
				if (UtilValidate.isNotEmpty(phone.get("countryCode")) && UtilValidate.isEmpty(phone.get("areaCode")) && UtilValidate.isNotEmpty(phone.get("contactNumber"))) {
					telephoneNumber = "tel. +" + phone.get("countryCode") + " " + phone.get("contactNumber");
				}
				if (UtilValidate.isNotEmpty(phone.get("countryCode")) && UtilValidate.isNotEmpty(phone.get("areaCode")) && UtilValidate.isEmpty(phone.get("contactNumber"))) {
					telephoneNumber = "tel.";
				}
				if (UtilValidate.isEmpty(phone.get("countryCode")) && UtilValidate.isEmpty(phone.get("areaCode")) && UtilValidate.isNotEmpty(phone.get("contactNumber"))) {
					telephoneNumber = "tel. " + phone.get("contactNumber");
				}
			}
		}
		companyHeaderInfoMap.put("telephoneNumber", telephoneNumber);

		// Fax 
		List<GenericValue> faxNumbers = delegator.findByAnd("PartyContactMechPurpose", UtilMisc.toMap("partyId", partyId, "contactMechPurposeTypeId", "FAX_NUMBER"));
		faxNumbers = EntityUtil.filterByDate(faxNumbers, UtilDateTime.nowTimestamp(), null, null, true);
		
		GenericValue fax = null;
		String faxNumber = "fax";
		if (UtilValidate.isNotEmpty(faxNumbers)) {  
			fax = delegator.findOne("TelecomNumber", UtilMisc.toMap("contactMechId", faxNumbers.get(0).get("contactMechId")), false);
			if (UtilValidate.isNotEmpty(fax)) {
				if (UtilValidate.isEmpty(fax.get("countryCode")) && UtilValidate.isEmpty(fax.get("areaCode")) && UtilValidate.isEmpty(fax.get("contactNumber"))) {
					faxNumber = "fax";
				}
				if (UtilValidate.isNotEmpty(fax.get("countryCode")) && UtilValidate.isNotEmpty(fax.get("areaCode")) && UtilValidate.isNotEmpty(fax.get("contactNumber"))) {
					faxNumber = "fax +" + fax.get("countryCode") + " " + fax.get("areaCode") + " " + fax.get("contactNumber");
				}
				if (UtilValidate.isEmpty(fax.get("countryCode")) && UtilValidate.isNotEmpty(fax.get("areaCode")) && UtilValidate.isNotEmpty(fax.get("contactNumber"))) {
					faxNumber = "fax " + fax.get("areaCode") + " " + fax.get("contactNumber");
				}
				if (UtilValidate.isNotEmpty(fax.get("countryCode")) && UtilValidate.isEmpty(fax.get("areaCode")) && UtilValidate.isNotEmpty(fax.get("contactNumber"))) {
					faxNumber = "fax +" + fax.get("countryCode") + " " + fax.get("contactNumber");
				}
				if (UtilValidate.isNotEmpty(fax.get("countryCode")) && UtilValidate.isNotEmpty(fax.get("areaCode")) && UtilValidate.isEmpty(fax.get("contactNumber"))) {
					faxNumber = "fax";
				}
				if (UtilValidate.isEmpty(fax.get("countryCode")) && UtilValidate.isEmpty(fax.get("areaCode")) && UtilValidate.isNotEmpty(fax.get("contactNumber"))) {
					faxNumber = "fax " + fax.get("contactNumber");
				}
			}
		}
		companyHeaderInfoMap.put("faxNumber", faxNumber);

		// Email
		List<GenericValue> emails = delegator.findByAnd("PartyContactMechPurpose", UtilMisc.toMap("partyId", partyId, "contactMechPurposeTypeId", "PRIMARY_EMAIL"));
		List<GenericValue> selEmails = EntityUtil.filterByDate(emails, UtilDateTime.nowTimestamp(), "fromDate", "thruDate", true);
		
		GenericValue email = null;
		String emailId = "e-mail";
		if (UtilValidate.isNotEmpty(selEmails)) {
			email = delegator.findByPrimaryKey("ContactMech", UtilMisc.toMap("contactMechId", selEmails.get(0).get("contactMechId")));
			if (UtilValidate.isNotEmpty(email)) {
				if (UtilValidate.isNotEmpty(email.get("infoString"))) {
					emailId = "e-mail: " + email.get("infoString");
				} else {
					emailId = "e-mail:";
				}
			}
			companyHeaderInfoMap.put("email", emailId);
		} else {    //get email address from party contact mech
			List<GenericValue> contacts = delegator.findByAnd("PartyContactMech", UtilMisc.toMap("partyId", partyId));
			List<GenericValue> selContacts = EntityUtil.filterByDate(contacts, UtilDateTime.nowTimestamp(), "fromDate", "thruDate", true);
			if (UtilValidate.isNotEmpty(selContacts)) {
				Iterator i = selContacts.iterator();
				while (i.hasNext())    {
					email = ((GenericValue) i.next()).getRelatedOne("ContactMech");
					if ("ELECTRONIC_ADDRESS".equals(email.get("contactMechTypeId")))    {
						if (UtilValidate.isNotEmpty(email)) {
							if (UtilValidate.isNotEmpty(email.get("infoString"))) {
								emailId = "e-mail: " + email.get("infoString");
							} else {
								emailId = "e-mail:";
							}
						}
						companyHeaderInfoMap.put("email", emailId);
						break;
					}
				}
			}
		}
		return companyHeaderInfoMap;
	}

}