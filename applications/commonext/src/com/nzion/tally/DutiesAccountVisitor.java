package com.nzion.tally;

import java.math.BigDecimal;
import java.util.List;

import org.apache.axis.utils.StringUtils;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.party.contact.ContactMechWorker;
import org.ofbiz.service.LocalDispatcher;

public class DutiesAccountVisitor
		extends AbstractVisitor {

	private static int dutiesAccount = 2301;

	public DutiesAccountVisitor(LocalDispatcher dispatcher, GenericValue userLogin, String organizationId) {
	super(dispatcher, userLogin, organizationId);
	}

	@Override
	public void visit(Ledger ledger) throws Exception {
	String taxClassification = ledger.getTaxClassification();
	if (StringUtils.isEmpty(ledger.getParentLedger()) || (!ledger.getParentLedger().startsWith("Duties"))
			|| StringUtils.isEmpty(taxClassification)) return;
	List taxAuthorities = delegator.findByAnd("TaxAuthority",
			UtilMisc.toMap("taxClassificationName", taxClassification));
	if (taxAuthorities.size() > 0) {
		System.out.println("Tax Authority already exists with Tax Classification " + taxClassification);
		return;
	}

	String ledgerName = ledger.getLedgerName();
	boolean cFormRequired = taxClassification.contains("Form");
	String taxAuthorityRateTypeId = null;
	GenericValue postalAddress = (GenericValue) ContactMechWorker
			.getPostalAddressForParty(delegator, organizationId, null).get(0).get("postalAddress");
	String organizationGeoId = (String) postalAddress.get("stateProvinceGeoId");

	if ("VAT".equals(taxAuthorityRateTypeId)) {
		taxAuthorityRateTypeId = "VAT_TAX";
	}
	System.out.println(" Creating Party Group for [" + ledgerName + "]");
	String partyId = (String) dispatcher.runSync(
			"createPartyGroup",
			UtilMisc.toMap("userLogin", userLogin, "groupName", ledgerName, "description", ledgerName,
					"preferredCurrencyUomId", "INR", "tallyLedgerName", ledgerName)).get("partyId");
	dispatcher.runSync("createPartyRole",
			UtilMisc.toMap("userLogin", userLogin, "partyId", partyId, "roleTypeId", "TAX_AUTHORITY"));
	System.out.println(" Creating Tax Authority [" + partyId + "-IND]");
	String geoId = "VAT".equals(ledger.getTaxType()) ? organizationGeoId : "IND";

	dispatcher.runSync("createTaxAuthority", UtilMisc.toMap("userLogin", userLogin, "taxAuthPartyId", partyId,
			"taxAuthGeoId", geoId, "includeTaxInPrice", "N", "requireTaxIdForExemption", "N", "taxAuthorityType",
			ledger.getTaxSubType(), "taxClassificationName", ledger.getTaxClassification(), "requireFormC",
			cFormRequired ? "Y" : "N"));
	if (ledger.getTaxClassification().contains("Exempt")) {
		dispatcher.runSync("createTaxAuthority", UtilMisc.toMap("userLogin", userLogin, "taxAuthPartyId", partyId,
				"taxAuthGeoId", organizationGeoId, "includeTaxInPrice", "N", "requireTaxIdForExemption", "N",
				"taxAuthorityType", ledger.getTaxSubType(), "taxClassificationName", ledger.getTaxClassification(),
				"requireFormC", cFormRequired ? "Y" : "N"));
	}

	String productCategoryId = null;
	List<GenericValue> productCategories = delegator.findList(
			"ProductCategory",
			EntityCondition.makeCondition(UtilMisc.toMap("productCategoryTypeId", "TAX_CATEGORY", "rateOfTax",
					ledger.getTaxRate())), null, null, null, false);
	if (productCategories.size() == 0) {
		productCategoryId = delegator.getNextSeqId("ProductCategory");
		GenericValue prodCatGV = delegator.makeValidValue("ProductCategory", UtilMisc.toMap("productCategoryId",
				productCategoryId, "productCategoryTypeId", "TAX_CATEGORY", "categoryName",
				"For products with Rate of Tax " + ledger.getTaxRate(), "rateOfTax", ledger.getTaxRate()));
		delegator.create(prodCatGV);
	} else {
		productCategoryId = (String) EntityUtil.getFirst(productCategories).get("productCategoryId");
	}

	dispatcher.runSync("createTaxAuthorityCategory", UtilMisc.toMap("userLogin", userLogin, "taxAuthPartyId", partyId,
			"taxAuthGeoId", geoId, "productCategoryId", productCategoryId));
	if (ledger.getTaxClassification().contains("Exempt")) {
		dispatcher.runSync("createTaxAuthorityCategory", UtilMisc.toMap("userLogin", userLogin, "taxAuthPartyId",
				partyId, "taxAuthGeoId", organizationGeoId, "productCategoryId", productCategoryId));
	}
	System.out.println(" Creating GlAccount under Duties And Taxes [3100000] :::: [" + ledgerName + "]");
	String glAccountId = String.valueOf(dutiesAccount);
	dispatcher.runSync("createGlAccount", UtilMisc.toMap("userLogin", userLogin, "parentGlAccountId", "3100000",
			"accountName", ledgerName, "accountCode", "Duties-" + ledgerName, "postedBalance", BigDecimal.ZERO,
			"glAccountTypeId", "CURRENT_LIABILITY", "glAccountClassId", "CURRENT_LIABILITY", "glResourceTypeId",
			"MONEY", "glAccountId", glAccountId, "taxClassification", ledger.getTaxClassification(), "taxType",
			ledger.getTaxType(), "sortOrder", "4"));

	System.out.println(" Creating RateProduct for Tax Authority  [" + partyId + "-IND]");

	dispatcher
			.runSync("createTaxAuthorityRateProduct", UtilMisc.toMap("userLogin", userLogin, "taxAuthPartyId", partyId,
					"taxAuthGeoId", geoId, "productCategoryId", productCategoryId, "taxAuthorityRateTypeId",
					taxAuthorityRateTypeId, "description", ledger.getTaxClassification(), "taxPercentage",
					ledger.getTaxRate()));

	if (ledger.getTaxClassification().contains("Exempt")) {
		dispatcher.runSync("createTaxAuthorityRateProduct", UtilMisc.toMap("userLogin", userLogin, "taxAuthPartyId",
				partyId, "taxAuthGeoId", organizationGeoId, "productCategoryId", productCategoryId,
				"taxAuthorityRateTypeId", taxAuthorityRateTypeId, "description", ledger.getTaxClassification(),
				"taxPercentage", ledger.getTaxRate()));
	}
	// taxAuthLedgers.add(UtilMisc.toMap("taxAuthPartyId", partyId, "glAccountId", String.valueOf(dutiesAccount),
	// "geoId",
	// geoId));
	dutiesAccount++;
	}

}
