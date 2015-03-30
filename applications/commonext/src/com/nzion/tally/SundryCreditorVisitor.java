package com.nzion.tally;

import java.util.List;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.LocalDispatcher;

public class SundryCreditorVisitor
		extends PartyVisitor {

	private final String moduleName = "SundryCreditorVisitor";

	public SundryCreditorVisitor(LocalDispatcher dispatcher, GenericValue userLogin, String organizationId) {
	super(dispatcher, userLogin, organizationId);
	}

	@Override
	public void visit(Ledger ledger) {
	if (ledger.getParentLedger() == null) return;

	if ("Sundry Creditors".equals(ledger.getParentLedger())
			|| "Advance Against Purchases & Expenses".equals(ledger.getParentLedger())
			|| "TDS - Undeducted".equals(ledger.getParentLedger())
			|| "Sundry Creditor for Factory Building".equals(ledger.getParentLedger())
			|| "Sundry Creditors for Expenses".equals(ledger.getParentLedger())
			|| "Expenses Payable".equals(ledger.getParentLedger())
			|| "Sundry Creditors for Freight & Couriers".equals(ledger.getParentLedger())
			|| "Sundry Creditors for Purchases".equals(ledger.getParentLedger())) {

		try {
			super.visit(ledger);
			List l= delegator.findByAnd("PartyGroup",UtilMisc.toMap("groupName",ledger.getLedgerName()));
			GenericValue partyGroupGV = EntityUtil.getFirst(l);
			String partyId = partyGroupGV.getString("partyId");
			System.out.println(moduleName + ":: Assigning Role Supplier to Party id " + partyId);
			dispatcher.runSync("createPartyRole", UtilMisc.toMap("userLogin", userLogin, "partyId", partyId,
					"roleTypeId", "BILL_FROM_VENDOR"));
			
			//As discussed with Prad and Shiva, this is not required for suppliers while Importing Tally Ledgers
			/*GenericValue acctgPref = delegator.makeValidValue("PartyAcctgPreference", UtilMisc.toMap("partyId",
					partyId, "baseCurrencyUomId", "INR"));*/
			dispatcher.runSync("createPartyRelationship", UtilMisc.toMap("userLogin", userLogin, "partyIdFrom",
					organizationId, "partyIdTo", partyId, "roleTypeIdFrom", "INTERNAL_ORGANIZATIO", "roleTypeIdTo",
					"BILL_FROM_VENDOR", "partyRelationshipTypeId", null,"accountName",ledger.getLedgerName()));
			System.out.println(moduleName + ":: Creating PartAcctgPreference for Party id " + partyId);
			//delegator.create(acctgPref);
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
	}
	}
}
