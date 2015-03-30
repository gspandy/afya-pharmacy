package com.nzion.tally;

import static com.nzion.tally.TallyUtil.getTextContent;

import java.util.List;
import java.util.Map;

import javolution.util.FastMap;

import org.apache.commons.lang.StringUtils;
import org.apache.xpath.XPathAPI;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.LocalDispatcher;
import org.w3c.dom.Node;
import org.w3c.dom.traversal.NodeIterator;

public abstract class PartyVisitor
		extends AbstractVisitor {

	private final String moduleName="PartyVisitor";

	public PartyVisitor(LocalDispatcher dispatcher, GenericValue userLogin, String organizationId) {
	super(dispatcher, userLogin, organizationId);
	}

	@Override
	public void visit(Ledger ledger) throws Exception {
	String ledgerName = ledger.getLedgerName();
	System.out.println("Creating Party "+ledgerName);
	Node n = ledger.getXmlNode();
	NodeIterator addressIterator = XPathAPI.selectNodeIterator(n, "ADDRESS.LIST/ADDRESS");
	int i = 1;
	Node addressNode = null;
	Map addressMap = FastMap.newInstance();
	while ((addressNode = addressIterator.nextNode()) != null) {
		String s = addressNode.getTextContent();
		addressMap.put("address_" + i, s);
		i++;
	}
	String stateName = getTextContent(n, "STATENAME");
	String zipCode = getTextContent(n, "PINCODE");
	if (StringUtils.isEmpty(zipCode)) zipCode = "000000";
	String emailAddress = getTextContent(n, "EMAIL");
	String vatInNumber = getTextContent(n, "VATTINNUMBER");
	String incomeTaxNumber = getTextContent(n, "INCOMETAXNUMBER");
	String salesTaxNumber = getTextContent(n, "SALESTAXNUMBER");
	String registrationNumber = getTextContent(n, "REGISTRATIONNUMBER");
	String exciseRegistrationNumber = getTextContent(n, "EXCISEREGISTRATIONNUMBER");
	String ledgerPhone = getTextContent(n, "LEDGERPHONE");
	String ledgerFax = getTextContent(n, "LEDGERFAX");
	String ledgerMobile = getTextContent(n, "LEDGERMOBILE");
	String ledgerContact = getTextContent(n, "LEDGERCONTACT");

	if (!StringUtils.isEmpty(stateName)) {
		List l = delegator.findByAnd("Geo", UtilMisc.toMap("geoTypeId", "STATE", "geoName", stateName));
		GenericValue geoGV = EntityUtil.getFirst(l);
		if (geoGV != null)
			stateName = (String) geoGV.get("geoId");
		else
			System.err.println("Warn:  [" + ledgerName + "] Mapping for statename [" + stateName + "] not found.");
	}

	partyId = (String) dispatcher.runSync(
			"createPartyGroup",
			UtilMisc.toMap("userLogin", this.userLogin, "groupName", ledgerName, "description", ledgerName,
					"preferredCurrencyUomId", "INR", "vatTinNumber", vatInNumber, "incomeTaxNumber", incomeTaxNumber,
					"salesTaxNumber", salesTaxNumber, "registrationNumber", registrationNumber,
					"exciseRegistrationNumber", exciseRegistrationNumber, "tallyLedgerName", ledgerName))
			.get("partyId");
	
	System.out.println(moduleName+":: Party created with Party id "+partyId);
	if (addressMap.get("address_1") != null) {
		String contactMechId = (String) dispatcher.runSync(
				"createPartyPostalAddress",
				UtilMisc.toMap("userLogin", userLogin, "partyId", partyId, "address1", addressMap.get("address_1"),
						"address2", addressMap.get("address_2"), "stateProvinceGeoId", stateName, "city", addressMap
								.get("address_3") == null ? "Not Available" : addressMap.get("address_3"),
						"contactMechPurposeTypeId", "GENERAL_LOCATION", "countryGeoId", "IND", "postalCode", zipCode,
						"attnName", ledgerContact)).get("contactMechId");
		
		System.out.println(moduleName+":: Creating Postal Address with Address Map "+addressMap);
		if (contactMechId != null) {
			dispatcher.runSync("createPartyContactMechPurpose", UtilMisc
					.toMap("userLogin", userLogin, "contactMechPurposeTypeId", "SHIPPING_LOCATION", "contactMechId",
							contactMechId, "partyId", partyId));
		} else {
			System.out.println(" Not Populating createPartyContactMechPurpose " + partyId);
		}

	}
	if (!StringUtils.isEmpty(emailAddress)) {
		dispatcher.runSync("createPartyEmailAddress", UtilMisc.toMap("userLogin", userLogin, "partyId", partyId,
				"emailAddress", emailAddress));
	System.out.println(moduleName+":: Creating Email Address  "+emailAddress);
	}
	
	/*dispatcher.runSync("createPartyRole", UtilMisc.toMap("userLogin", userLogin, "partyId", partyId, "roleTypeId",
			"INTERNAL_ORGANIZATIO"));*/
	}

	public String getPartyId() {
	return partyId;
	}

	public void setPartyId(String partyId) {
	this.partyId = partyId;
	}

}
