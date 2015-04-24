package com.nthdimenzion.humanres.payroll;

import java.util.List;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;

import com.nthdimenzion.humanres.payroll.calc.PayrollContext;

public class ActualRentPaidEval {

	public static Object execute() throws GenericEntityException {
		PayrollContext ctx = PayrollContext.getInstance();
		GenericDelegator delegator = null;//GenericDelegator.getGenericDelegator("default");
		String partyId = (String) ctx.get("emplId");
		String validTaxDeclId = UtilTax.getValidTaxDeclId(delegator);
		//TODO its now hardcoded has to move to file or db.
		String itemId = "10050";

		EntityCondition entityCondition = EntityCondition.makeCondition(UtilMisc.toMap("partyId", partyId, "validTaxDeclId", validTaxDeclId, "itemId", itemId));
		List<GenericValue> values = delegator.findList("EmplTaxDecl", entityCondition, null, null, null, false);
		System.out.println("ActualRentPaidEval entityCondition " + entityCondition);

		if (values.size() > 1) {
			throw new IllegalStateException();
		}

		Number value = values.get(0).getBigDecimal("numValue");

		System.out.println("ACTUAL RENT PAID EVAL " + value);
		return value;
	}
}
