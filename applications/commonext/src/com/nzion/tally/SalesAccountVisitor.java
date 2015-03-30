package com.nzion.tally;

import java.math.BigDecimal;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.LocalDispatcher;

public class SalesAccountVisitor
		extends AbstractVisitor {

	private long salesAccount=3001;
	private final String moduleName="SalesAccountVisitor";

	protected SalesAccountVisitor(LocalDispatcher dispatcher, GenericValue userLogin, String organizationId) {
	super(dispatcher, userLogin, organizationId);
	}

	@Override
	public void visit(Ledger ledger) throws Exception {

	boolean isSales = "Sales Accounts".equals(ledger.getParentLedger()) 
						|| "Export - Sales".equals(ledger.getParentLedger()) 
						|| "Interstate - Sales".equals(ledger.getParentLedger()) 
						|| "C Form".equals(ledger.getParentLedger()) 
						|| "Local - Sales".equals(ledger.getParentLedger());
	
	if(!isSales)return;
	System.out.println(moduleName +":: Creating GL Account Name"+ledger.getLedgerName() +" GlAccount Id "+salesAccount);
	dispatcher.runSync("createGlAccount", UtilMisc.toMap("userLogin", userLogin, "parentGlAccountId","1300000",
			"accountName", ledger.getLedgerName(), "accountCode", ledger.getLedgerName(), "postedBalance",
			BigDecimal.ZERO, "glAccountClassId", "ASSET", "glResourceTypeId", "MONEY", "glAccountId", String
					.valueOf(salesAccount), "glAccountTypeId", "CURRENT_ASSET", "organizationPartyId", organizationId,
			"taxClassification", ledger.getTaxClassification(), "taxType", ledger.getTaxType()));
	salesAccount=salesAccount+1L;
	}
}
