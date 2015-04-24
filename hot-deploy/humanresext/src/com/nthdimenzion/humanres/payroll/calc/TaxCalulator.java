package com.nthdimenzion.humanres.payroll.calc;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javolution.util.FastMap;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;

import com.nthdimenzion.humanres.payroll.PayrollHelper;

public class TaxCalulator {

	/**
	 * 
	 * @param context
	 *            The mandatory key value pairs include TAXABLE_INCOME GENDER
	 *            IS_SENIOR_CITIZEN
	 * @param fromDate
	 *            is start of financial Year thruDate is end of financial Year
	 * @return double Tax Amount calculated on annual income.
	 */

	private static final Double professionalTax = 2400D; // Rs 200 p.m.
	private static String module = "TaxCalulator";
	private static String C_IT = "INCOME_TAX_RATE";
	private static String C_PT = "PROFESSIONAL_TAX_RATE";

	public static Map<String, Double> calculateTax(Map<String, Object> context,GenericDelegator delegatorG) {
		PayrollContext workingMemory = PayrollContext.getInstance();
		// GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
		Date vfromDate = (Date) context.get("fromDate");
		Date vthruDate = (Date) context.get("thruDate");

		Map<String, Double> taxMap = FastMap.newInstance();
		GenericValue fiscalYear = null;
		try {
			fiscalYear = PayrollHelper.getFiscalYear(vfromDate,delegatorG);
		} catch (GenericEntityException e) {
			Debug.logError("No records found searching the Financial Year for:"
					+ vfromDate, module);
			return taxMap;
		}
		Date yearBegin = fiscalYear.getDate("fromDate");
		Date yearEnd = fiscalYear.getDate("thruDate");

		double taxableIncomeNonRounded = (Double) workingMemory
				.get("TAXABLE_INCOME");
		long taxableIncome = Math.round(taxableIncomeNonRounded);
		System.out.println("[Tax Calculation]\t Calculating Tax on Income "
				+ taxableIncome);

		boolean isFemale = ((String) workingMemory.get("GENDER"))
				.equalsIgnoreCase("MALE") ? false : true;

		boolean isSeniorCitizen = (Boolean) workingMemory
				.get("IS_SENIOR_CITIZEN");
		double taxAmount = 0D;
		double simpleTax = 0D;

		String lpersonType = "MALE"; // Default value
		if (isFemale) {
			lpersonType = "FEMALE";
		}
		if (isSeniorCitizen) {
			lpersonType = "SENIOR";
		}
		System.out.println("[Tax Calculation]\t Slab Type Tax on Income "
				+ lpersonType);
		taxableIncome = Math.round(taxableIncome);
		/**
		 * Select * from tax_slab where geoId = "IND" and personType IN ("ALL",
		 * lpersonType) and fromDate <= yearBegin and (thruDate >= yearEnd or
		 * thruDate is null) order by person_Type, tax_Type, slab_min For
		 * highest slab_Max has been assumed to be 999999999999 = million
		 * million not possible in SME ;)
		 */

		EntityCondition fromCond = EntityCondition.makeCondition(("fromDate"),
				EntityOperator.EQUALS, yearBegin);
		EntityCondition personCond = EntityCondition.makeCondition(
				("personType"), EntityOperator.IN,
				UtilMisc.toList("ALL", lpersonType));
		// EntityCondition taxTypeCond =
		// EntityCondition.makeCondition("taxType", C_IT);
		EntityCondition andCond = EntityCondition.makeCondition(fromCond,
				personCond);

		EntityCondition thruCond = EntityCondition.makeCondition(("thruDate"),
				EntityOperator.LESS_THAN_EQUAL_TO, yearEnd);
		EntityCondition nullCond = EntityCondition.makeCondition(("thruDate"),
				EntityOperator.EQUALS, null);
		EntityCondition orCond = EntityCondition.makeCondition(thruCond,
				EntityOperator.OR, nullCond);

		EntityCondition geoCond = EntityCondition.makeCondition(("geoId"),
				EntityOperator.EQUALS, UtilProperties.getPropertyValue(
						"general.properties", "country.geo.id.default"));
		EntityCondition cond = EntityCondition.makeCondition(andCond, orCond,
				geoCond);

		List<String> orderBy = new LinkedList<String>();
		orderBy.add("personType");
		orderBy.add("taxType");
		orderBy.add("slabMin");

		List<GenericValue> data = null;
		try {
			data = delegatorG.findList("TaxSlab", cond, null, orderBy, null,
					false);
		} catch (GenericEntityException e) {
			Debug.logError("No records found searching the TaxSlab for:"
					+ vfromDate + " personType " + lpersonType, module);
			return taxMap;
		}

		if (data.size() == 0) {
			Debug.logError("No records found searching the TaxSlab for:"
					+ vfromDate + " personType " + lpersonType, module);
			return taxMap;
		}
		String taxType = null;
		Double ltaxRate = 0.0;

		Double lHigherEduCessRate = 0.0;
		Double lPrimEduCessRate = 0.0;
		Double lSurchargeRate = 0.0;
		GenericValue surchargeGV = null;

		EntityCondition notPTCond = EntityCondition.makeCondition("taxType",
				EntityOperator.NOT_EQUAL, C_PT);
		EntityCondition allCond = EntityCondition.makeCondition("personType",
				"ALL");
		andCond = EntityCondition.makeCondition(notPTCond, allCond);
		List<GenericValue> cessData = EntityUtil.filterByCondition(data,
				andCond); // Get
		// the
		// common
		// rates
		// here
		for (GenericValue slabGV : cessData) {
			taxType = (String) slabGV.get("taxType");
			ltaxRate = (Double) slabGV.get("taxRate") / 100; // Divide by 100 as
			// it s
			// percentage
			if (taxType.indexOf("HIGHER_EDU") >= 0) {
				lHigherEduCessRate = ltaxRate;
				continue;
			} else if (taxType.indexOf("PRIM_EDU") >= 0) {
				lPrimEduCessRate = ltaxRate;
				continue;
			} else if (taxType.indexOf("SURCHARGE") >= 0) {
				lSurchargeRate = ltaxRate;
				surchargeGV = (GenericValue) slabGV.clone();
				continue;
			} else {
				System.out.println("Unknown Tax Slab");
			}
		}

		final String cIT = "INCOME_TAX%";
		EntityCondition persCond = EntityCondition.makeCondition("personType",
				EntityOperator.EQUALS, lpersonType);
		EntityCondition typeCond = EntityCondition.makeCondition("taxType",
				EntityOperator.LIKE, cIT);
		EntityCondition slabCond = EntityCondition.makeCondition("slabMin",
				EntityOperator.LESS_THAN_EQUAL_TO, new Double(taxableIncome));

		EntityCondition itCond = EntityCondition.makeCondition(persCond,
				typeCond, slabCond);

		List<GenericValue> slabData = EntityUtil
				.filterByCondition(data, itCond); // Get
		// the
		// common
		// rates
		// here
		Double lslabMin = 0.0;
		Double lslabMax = 0.0;
		/** slabData is already ordered by slabs going from low to high **/
		for (GenericValue slabGV : slabData) {
			ltaxRate = (Double) slabGV.get("taxRate") / 100;
			lslabMin = (Double) slabGV.get("slabMin");
			lslabMax = (Double) slabGV.get("slabMax");
			System.out.print("\n Computing Tax per Slab " + lslabMin + " -- "
					+ lslabMax);
			if (taxableIncome > lslabMax) {
				simpleTax = simpleTax + (lslabMax - lslabMin + 1) * ltaxRate;
				System.out.println(" Amount " + (lslabMax - lslabMin + 1)
						* ltaxRate);
				continue;
			} else { // (taxableIncome < lslabMax)
				simpleTax = simpleTax + (taxableIncome - lslabMin + 1)
						* ltaxRate;
			}
			System.out.println(" Amount " + (taxableIncome - lslabMin + 1)
					+ " * " + ltaxRate + " = " + (taxableIncome - lslabMin + 1)
					* ltaxRate);
		}

		double surcharge = 0D;
		if (surchargeGV != null
				&& taxableIncome > (Double) surchargeGV.getDouble("slabMin")) {
			surcharge = simpleTax * lSurchargeRate;
		}

		System.out.println(" Surcharge " + surcharge);
		double educationCess = (simpleTax + surcharge) * lPrimEduCessRate;

		System.out.println(" Education Cess " + educationCess);

		double higherEduCess = (simpleTax + surcharge) * lHigherEduCessRate;
		System.out.println(" Secondary and Higher Education Cess "
				+ higherEduCess);
		taxMap.put("simpleTax", simpleTax);
		taxMap.put("surcharge", surcharge);
		taxMap.put("educationCess", educationCess);
		taxMap.put("higherEduCess", higherEduCess);
		taxAmount = (simpleTax + surcharge + educationCess + higherEduCess);
		taxAmount = Math.max(0.0D, taxAmount);
		taxMap.put("taxAmount", taxAmount);
		System.out.println("[Tax Calculation]\t Total Tax Amount " + taxAmount);
		return taxMap;
	}

	public static void main(String[] args) {
		TaxCalulator calc = new TaxCalulator();

		Map<String, Object> context = new HashMap<String, Object>();
		context.put("GENDER", "FEMALE");
		context.put("IS_SENIOR_CITIZEN", true);
		context.put("TAXABLE_INCOME", 1055000D);
		double tax = 0; // calc.calculateTax();
		System.out.println("TAX AMOUNT " + tax);

	}

	/**
	 * 
	 * Picking only those tax where the tax_type is PROFESSIONAL_TAX and the
	 * from date is in between the Fiscal Year which is again determined by the
	 * vthrudate.
	 * 
	 * @param vfromDate
	 * @param vthruDate
	 * @param vtaxableIncome
	 * @param remainingMonth
	 * @return
	 */
	public static Double getProfessionalTax(Date vfromDate, Date vthruDate,
			BigDecimal monthlyIncome,GenericDelegator delegatorG) {

		List<GenericValue> data = null;
		try {
			GenericValue fiscalYear = PayrollHelper.getFiscalYear(vthruDate,delegatorG);
			EntityCondition fromCond = EntityCondition.makeCondition(
					"fromDate", EntityOperator.GREATER_THAN_EQUAL_TO,
					fiscalYear.getDate("fromDate"));
			EntityCondition taxTypeCond = EntityCondition.makeCondition(
					"taxType", C_PT);

			EntityCondition thruCond = EntityCondition.makeCondition(
					("fromDate"), EntityOperator.LESS_THAN_EQUAL_TO,
					fiscalYear.getDate("thruDate"));

			/** KA has slabs determined by Monthly Income **/
			EntityCondition minSlabCond = EntityCondition.makeCondition(
					("slabMin"), EntityOperator.LESS_THAN_EQUAL_TO,
					monthlyIncome.doubleValue());
			EntityCondition maxSlabCond = EntityCondition.makeCondition(
					("slabMax"), EntityOperator.GREATER_THAN_EQUAL_TO,
					monthlyIncome.doubleValue());

			EntityCondition cond = EntityCondition.makeCondition(minSlabCond,
					maxSlabCond, taxTypeCond, fromCond, thruCond);

			System.out.println(" Professional Tax Condition " + cond);

			Set<String> fields = new HashSet<String>();
			fields.add("taxRate");
			// fields.add("fromDate");

			System.out.println(" Professional Tax Condition " + cond);
			data = delegatorG
					.findList("TaxSlab", cond, null, null, null, false);

			System.out.println(" getProfessionalTax Rec " + data);
			// data = EntityUtil.filterByCondition(data, fromCond);
			System.out.println(" getProfessionalTax  After filter Recs size "
					+ data.size());

		} catch (GenericEntityException e) {
			Debug.logError(
					"No professional tax records found searching the TaxSlab for:"
							+ vfromDate, module);
			return 0D;
		}

		if (data.size() >= 0 && data.size() != 1) {
			Debug.logError("More/Less records found searching the TaxSlab for:"
					+ vfromDate + " taxableIncome :" + monthlyIncome, module);
			return 0D;
		}
		Double ltaxRate = data.get(0).getDouble("taxRate");
		return ltaxRate;
		// professionalTax;//Assuming employee has worked for whole year in
		// company
	}

	public static BigDecimal calculateMonthlyProfessionalTax(
			BigDecimal monthlyIncome,GenericDelegator delegatorG) throws GenericEntityException {
		GenericValue fiscalYear = PayrollHelper.getCurrentFiscalYear(delegatorG);
        if(fiscalYear ==null){
            return BigDecimal.ZERO;
        }
		return new BigDecimal(getProfessionalTax(
				fiscalYear.getDate("fromDate"), fiscalYear.getDate("thruDate"),
				monthlyIncome,delegatorG));
	}
}
