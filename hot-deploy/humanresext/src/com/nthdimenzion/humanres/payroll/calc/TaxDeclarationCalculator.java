package com.nthdimenzion.humanres.payroll.calc;

/**
 * @author Prad , Pankaj
 * @date 22 Apr 2009: 
 * acceptedValue used instead of numValue
 */
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;

import com.nthdimenzion.humanres.payroll.UtilTax;

public class TaxDeclarationCalculator {

	public static final NumberFormat DEFAULT_DECIMAL_FORMAT = new DecimalFormat("#.0#################");

	public static BigDecimal calculate(GenericValue userLogin) throws GenericEntityException {
		org.ofbiz.base.util.Debug.logInfo("Inside Tax Calculator ", "TaxCalculator");
		GenericDelegator delegator = (GenericDelegator) userLogin.getDelegator();
		String partyId = userLogin.getString("partyId");
		return calculate(partyId,delegator);
	}
	
	public static BigDecimal calculate(String partyId,GenericDelegator delegator) throws GenericEntityException {

		String validTaxDeclId = UtilTax.getValidTaxDeclId(delegator);
		EntityCondition ec3 = EntityCondition.makeCondition("itemGroupId", EntityOperator.EQUALS, "DEDUCTIONS");
		EntityCondition ec2 = EntityCondition.makeCondition(UtilMisc.toMap("partyId", partyId, "validTaxDeclId", validTaxDeclId));

		EntityCondition whereEntityCondition = EntityCondition.makeCondition(ec2, ec3);
		List<GenericValue> taxDeclValues = delegator.findList("EmplTaxItemView", whereEntityCondition, null, null, null, false);

		BigDecimal declAmount = BigDecimal.ZERO;
		Map<String, BigDecimal> amountByTaxDeclSection = new HashMap<String, BigDecimal>();

		if (taxDeclValues != null) {
			for (GenericValue taxDecl : taxDeclValues) {
				Double approvedAmt = taxDecl.getDouble("acceptedValue");
				if (approvedAmt == null) {
					approvedAmt = 0.000000;
				}
				double a = approvedAmt != null ? approvedAmt.doubleValue() : 0d; 
				BigDecimal bd = new BigDecimal(a);
				String categoryId = taxDecl.getString("categoryId");
				BigDecimal amount = amountByTaxDeclSection.get(categoryId);
				if (amount == null) {
					amount = BigDecimal.ZERO;
				}
				amount = amount.add(bd);
				amountByTaxDeclSection.put(categoryId, amount);
			}

			for (String categoryId : amountByTaxDeclSection.keySet()) {
				GenericValue gv = delegator.findOne("TaxCategory", UtilMisc.toMap("categoryId", categoryId), true);
				BigDecimal declaredSumAmount = amountByTaxDeclSection.get(categoryId);
				BigDecimal categoryMaxLimit = gv.getBigDecimal("maxAmount");
				//Take the minumum of Category Max Amount or Declared Sum of Values of Tax Items
				declAmount = declAmount.add(new BigDecimal(Math.min(declaredSumAmount.doubleValue(), categoryMaxLimit.doubleValue())));
			}

		}
		return declAmount;
	}
}
