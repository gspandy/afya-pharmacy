package com.nzion.tally;

import java.util.List;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.LocalDispatcher;

public class SundryDebtorVisitor
		extends PartyVisitor {

	private final String moduleName = "SundryDebtorVisitor";

	public SundryDebtorVisitor(LocalDispatcher dispatcher, GenericValue userLogin, String organizationId) {
	super(dispatcher, userLogin, organizationId);
	}

	@Override
	public void visit(Ledger ledger) {
	if (ledger.getParentLedger() == null) return;

	if ("Sundry Debtors".equals(ledger.getParentLedger())
			|| "Advance Against Sales".equals(ledger.getParentLedger())) {

		try {
			super.visit(ledger);
			List l= delegator.findByAnd("PartyGroup",UtilMisc.toMap("groupName",ledger.getLedgerName()));
			GenericValue partyGroupGV = EntityUtil.getFirst(l);
			String partyId = partyGroupGV.getString("partyId");
			System.out.println(moduleName + ":: Assigning Role Supplier to Party id " + partyId);
			dispatcher.runSync("createPartyRole", UtilMisc.toMap("userLogin", userLogin, "partyId", partyId,
					"roleTypeId", "BILL_TO_CUSTOMER"));
			System.out.println(moduleName + ":: Creating Party Relationship CUSTOMER_REL for Party id " + partyId);
			dispatcher.runSync("createPartyRelationship", UtilMisc.toMap("userLogin", userLogin, "partyIdFrom",
					organizationId, "partyIdTo", partyId, "roleTypeIdFrom", "INTERNAL_ORGANIZATIO", "roleTypeIdTo",
					"BILL_TO_CUSTOMER", "partyRelationshipTypeId", "CUSTOMER_REL","accountName",ledger.getLedgerName()));
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	}

}
