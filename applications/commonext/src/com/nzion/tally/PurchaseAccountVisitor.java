package com.nzion.tally;

import java.math.BigDecimal;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.LocalDispatcher;

public class PurchaseAccountVisitor
		extends AbstractVisitor {

	private static long purchaseAccount = 4001;
	private final String moduleName = "PurchaseAccountVisitor";

	public PurchaseAccountVisitor(LocalDispatcher dispatcher, GenericValue userLogin, String organizationId) {
	super(dispatcher, userLogin, organizationId);
	}

	@Override
	public void visit(Ledger ledger) throws Exception {

	boolean isPurchase = "Purchase Accounts".equals(ledger.getParentLedger())
							|| "Consumables".equals(ledger.getParentLedger())
							|| "Purchase - Import".equals(ledger.getParentLedger())
							|| "Purchase - Interstate".equals(ledger.getParentLedger())
							|| "Purchase - Local".equals(ledger.getParentLedger())
							|| "Purchase - Local 13.5%".equals(ledger.getParentLedger())
							|| "Purchase - Local - 14%".equals(ledger.getParentLedger())
							|| "Purchase - Local 4%".equals(ledger.getParentLedger())
							|| "Purchase - Local 5%".equals(ledger.getParentLedger())
							|| "Purchase - Prior Period".equals(ledger.getParentLedger());
	if (!isPurchase) return;
	System.out.println(moduleName + ":: Creating GL Account Name " + ledger.getLedgerName() + " GlAccount Id "
			+ purchaseAccount);
	dispatcher.runSync("createGlAccount", UtilMisc.toMap("userLogin", userLogin, "parentGlAccountId", "2300000",
			"accountName", ledger.getLedgerName(), "accountCode", ledger.getLedgerName(), "postedBalance",
			BigDecimal.ZERO, "glAccountClassId", "CURRENT_LIABILITY", "glResourceTypeId", "MONEY", "glAccountId",
			String.valueOf(purchaseAccount), "glAccountTypeId", "ACCOUNTS_PAYABLE", "organizationPartyId",
			organizationId, "taxClassification", ledger.getTaxClassification(), "taxType", ledger.getTaxType()));
	purchaseAccount = purchaseAccount + 1;
	}
}
