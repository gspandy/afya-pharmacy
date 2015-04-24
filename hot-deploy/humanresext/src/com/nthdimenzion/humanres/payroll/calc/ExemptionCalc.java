package com.nthdimenzion.humanres.payroll.calc;

import java.util.List;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.util.EntityUtil;

import com.nthdimenzion.humanres.payroll.resolver.RuleResolver;

public class ExemptionCalc {

	/**
	 * Only 1 exemption Rule can be associated with a Salary Head.
	 * 
	 * 
	 * @param delegator
	 * @param salaryHeadId
	 * @return
	 * @throws GenericEntityException
	 */
	public static Number calculateExemption(GenericValue userLogin, String salaryHeadId) throws GenericEntityException {
		GenericDelegator delegator = (GenericDelegator) userLogin.getDelegator();
		EntityCondition entityCondition = EntityCondition.makeCondition(UtilMisc.toMap("salaryHeadId", salaryHeadId));
		List<GenericValue> dataValues = delegator.findList("SalaryHeadRule", entityCondition, null, null, null, false);
		if (dataValues.size() == 0) {
			throw new GenericEntityException("Salary Head ID " + salaryHeadId + " does not have any Exemption Rule.");
		}
		String ruleId = dataValues.get(0).getString("ruleId");
		PayrollContext context = PayrollContext.getInstance(userLogin);
		ResolverManager resolver = context.getResolver();
		Object obj = resolver.resolve(resolver.encode(new String[] { RuleResolver.COMP_NAME, ruleId }));
		System.out.println(obj);
		Number result = null;
		if (obj instanceof Rule) {
			Rule rule = (Rule) obj;
			result = rule.evaluate();
		}
		System.out.println(" Exemption of Salary head "+salaryHeadId +" amount == "+result);
		return result;
	}

	public static Number calculateExemption(GenericValue userLogin) throws GenericEntityException {
		String partyId = userLogin.getString("partyId");
		EntityCondition entityCondition = EntityCondition.makeCondition(UtilMisc.toMap("partyId", partyId));
		GenericDelegator delegator = (GenericDelegator) userLogin.getDelegator();
		List<GenericValue> data = delegator.findList("EmplSalDetail", entityCondition, null, null, null, false);
		data = EntityUtil.filterByDate(data);// Get the latest salary heads from
		// EmplSal
		double totalExemptionAmt = 0d;
		for (GenericValue gv : data) {
			try {
				totalExemptionAmt += calculateExemption(userLogin, gv.getString("salaryHeadId")).doubleValue();
			} catch (GenericEntityException ge) {

			}
		}
		return totalExemptionAmt;
	}

}
