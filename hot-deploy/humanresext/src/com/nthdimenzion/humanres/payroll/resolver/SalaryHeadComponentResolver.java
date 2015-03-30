package com.nthdimenzion.humanres.payroll.resolver;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;

import com.nthdimenzion.humanres.payroll.PayrollHelper;
import com.nthdimenzion.humanres.payroll.SalaryBean;
import com.nthdimenzion.humanres.payroll.SalaryComponentBean;
import com.nthdimenzion.humanres.payroll.calc.PayrollContext;
import com.nthdimenzion.humanres.payroll.calc.ResolverManager;
import com.nthdimenzion.humanres.payroll.calc.Rule;
import com.nthdimenzion.humanres.payroll.expr.Field;

/**
 * This class is for returning the Field<Double> against the Salary Head as
 * stored in the EmplSal.
 * 
 * @author pradyumna
 * 
 *         10-Apr-2009: Added fields fromDate and thruDate to EmpSal.
 */
public final class SalaryHeadComponentResolver extends AbstractResolver {

	private static String COMP_NAME = "SALARYHEAD";
	private Map<String, Double> salMap = null;
	private static final String module = "SalaryHeadComponentResolver";

	public Field<Double> resolve(String key) {
		System.out.println("salMap : " + salMap);
		Double amount = salMap.get(key);
		if (amount != null)
			return new Field<Double>(amount);

		PayrollContext workingMemory = PayrollContext.getInstance();
		GenericValue userLogin = (GenericValue) workingMemory.get("userLogin");
		GenericDelegator delegator = (GenericDelegator) workingMemory.get("delegator");
		String partyId = userLogin.getString("partyId");
		if (delegator == null) {
			delegator = (GenericDelegator) userLogin.getDelegator();
		}

		try {
			System.out.println("************ TRYING TO RESOLVE KEY " + key);

			String salaryStructureStr = salMap.get("salaryStructureId").toString();

			Double d = Double.parseDouble(salaryStructureStr);

			String salaryStructureId = String.valueOf(d.intValue());

			System.out.println("SlaaryStructureId ======== " + salaryStructureId);

			List<GenericValue> salStructureHeads = delegator.findByAnd("SalaryStructureHead",
					UtilMisc.toMap("salaryStructureId", salaryStructureId, "salaryHeadId", key));

			GenericValue salStructureHeadGV = null;
			if (salStructureHeads.size() > 0) {
				salStructureHeadGV = salStructureHeads.get(0);
			}

			List<GenericValue> headRules = delegator.findByAnd("PayrollHeadRule",
					UtilMisc.toMap("salaryStructureHeadId", salStructureHeadGV.get("salaryStructureHeadId")));

			GenericValue headRule = null;
			if (headRules.size() > 0) {
				headRule = headRules.get(0);
			}

			ResolverManager resolverMgr = PayrollContext.getResolver();

			Rule rule = (Rule) resolverMgr.resolve(resolverMgr.encode(new String[] { RuleResolver.COMP_NAME, headRule.getString("ruleId") }));
			Number amt = rule.evaluate();
			return new Field<Double>(amt.doubleValue());

		} catch (GenericEntityException ge) {
			ge.printStackTrace();
		}

		List<GenericValue> salHeads = null;
		/**
		 * //List<String> fieldsToSelect = FastList.newInstance();
		 * //fieldsToSelect.add("amount"); try { EntityCondition condition =
		 * EntityCondition.makeCondition(UtilMisc.toMap("partyId", partyId,
		 * "salaryHeadId", key)); salHeads = delegator.findList("EmplSal",
		 * condition, null, null, null, false); salHeads =
		 * EntityUtil.filterByDate(salHeads); if (salHeads.size() == 1) { amount
		 * = salHeads.get(0).getDouble("amount"); } } catch
		 * (GenericEntityException gee) {
		 * Debug.logInfo("Unable to find salaryhead for partyId == " + partyId +
		 * "due to exception : " + gee, module); }
		 **/
		/**
		 * Instead of directly selecting from EmplSal create a SalaryBean with
		 * prorated amounts
		 **/
		SalaryBean salary = new SalaryBean(partyId, null, null,delegator);
		try {
			salary = PayrollHelper.populateEstimatedAnnualSalaryDetail(partyId, null, null,delegator);
		} catch (GenericEntityException e) {
			Debug.logInfo("Unable to find salaryhead for partyId == " + partyId + "due to exception : " + e, module);
			e.printStackTrace();
			return null;
		}
		List<SalaryComponentBean> salcomps = salary.getAllComponents();
		Iterator<SalaryComponentBean> l = salcomps.iterator();
		SalaryComponentBean salc = null;
		while (l.hasNext()) {
			salc = l.next();
			if (salc.getSalaryHeadId().equalsIgnoreCase(key)) {
				amount = salc.getAmount();
				salMap.put(key, amount);
				break;
			}
		}
		return new Field<Double>(amount);

	}

	public String getComponentName() {
		return COMP_NAME;
	}

	@Override
	public void setLookupContext(Map map) {
		this.salMap = map;
	}

	@Override
	public Object resolve(String key, GenericDelegator delegator) {
		// TODO Auto-generated method stub
		return null;
	}
}
