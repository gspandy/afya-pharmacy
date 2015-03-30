package com.nthdimenzion.humanres.payroll.resolver;

import java.util.List;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;

import com.nthdimenzion.humanres.payroll.UtilTax;
import com.nthdimenzion.humanres.payroll.calc.PayrollContext;
import com.nthdimenzion.humanres.payroll.expr.Field;

public class TaxDeclarationComponentResolver extends AbstractResolver {

	private static final String COMP_NAME = "TAXDECL";

	public Field<Double> resolve(String key) {

		Double amount = new Double(0);
		PayrollContext workingMemory = PayrollContext.getInstance();
		GenericValue userLogin = (GenericValue) workingMemory.get("userLogin");
		GenericDelegator delegator = (GenericDelegator) workingMemory.get("delegator");
		String partyId = userLogin.getString("partyId");

		if (delegator == null) {
			delegator = (GenericDelegator) userLogin.getDelegator();
		}

		try {
			String validTaxDeclId = UtilTax.getValidTaxDeclId(delegator);
			EntityCondition ec1 = EntityCondition.makeCondition("numValue", EntityOperator.NOT_EQUAL, null);
			EntityCondition ec2 = EntityCondition.makeCondition(UtilMisc.toMap("partyId", partyId, "itemId", key,"validTaxDeclId",validTaxDeclId));
			EntityCondition whereEntityCondition = EntityCondition.makeCondition(ec1, ec2);
			List<GenericValue> taxDecls = delegator.findList("EmplTaxDecl", whereEntityCondition, null, null, null, false);
			if (taxDecls != null && taxDecls.size() == 1) {
				amount = taxDecls.get(0).getDouble("acceptedValue");
			}
		} catch (GenericEntityException gee) {

		}
		if (amount == null) {
			amount = new Double(0);
		}

		return new Field<Double>(amount);
	}

	public String getComponentName() {
		return COMP_NAME;
	}

	@Override
	public Object resolve(String key, GenericDelegator delegator) {
		// TODO Auto-generated method stub
		return null;
	}
}
