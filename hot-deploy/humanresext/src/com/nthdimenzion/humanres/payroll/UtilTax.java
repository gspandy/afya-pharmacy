package com.nthdimenzion.humanres.payroll;

import java.sql.ResultSet;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityComparisonOperator;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionFunction;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;

import com.nthdimenzion.humanres.payroll.calc.ExemptionCalc;
import com.smebiz.common.UtilDateTimeSME;

public class UtilTax {

	public static List<GenericValue> getTaxItemsForCategory(GenericDelegator delegator, String categoryId) throws GenericEntityException {
		EntityCondition entityCondition = EntityCondition.makeCondition(UtilMisc.toMap("categoryId", categoryId));
		EntityFindOptions options = new EntityFindOptions();
		options.setDistinct(true);
		options.setResultSetConcurrency(ResultSet.CONCUR_READ_ONLY);
		return delegator.findList("TaxItem", entityCondition, null, null, options, false);
	}

	public static String getValidTaxDeclId(GenericDelegator delegator) throws GenericEntityException {
		EntityCondition ec1 = EntityConditionFunction.makeCondition("startDate", EntityComparisonOperator.LESS_THAN_EQUAL_TO,
				new java.sql.Date(new Date().getTime()));
		EntityCondition ec2 = EntityConditionFunction.makeCondition("endDate", EntityComparisonOperator.GREATER_THAN_EQUAL_TO,
				new java.sql.Date(new Date().getTime()));
		EntityCondition geoCond = EntityConditionFunction.makeCondition(("geoId"), EntityOperator.EQUALS, UtilProperties.getPropertyValue("general.properties", "country.geo.id.default"));
		
		EntityCondition whereEntityCondition = EntityConditionFunction.makeCondition(UtilMisc.toList(ec1, ec2, geoCond));

		List<String> orderBy = FastList.newInstance();
		orderBy.add("createdStamp DESC");

		List<GenericValue> validTaxDecls = delegator.findList("ValidTaxDecl", whereEntityCondition, null, null, null, false);

		System.out.println("VALID TAX DECLS " + validTaxDecls);

		String validTaxDeclId = null;
		if (validTaxDecls.size() > 0) {
			validTaxDeclId = validTaxDecls.get(0).getString("validTaxDeclId");
		}
		return validTaxDeclId;
	}

	public static Map<String, Object> getTaxContext(String partyId, double taxableAmount,GenericDelegator delegator) throws GenericEntityException {
		Map<String, Object> taxContext = FastMap.newInstance();
		taxContext.put("TAXABLE_INCOME", taxableAmount);

		GenericValue person = delegator.findOne("Person", false, "partyId", partyId);
		String gender = person.getString("gender");
		taxContext.put("GENDER", gender != null ? gender.equals("M") ? "male" : "female" : "male");
		Date birthDate = person.getDate("birthDate");
		taxContext.put("IS_SENIOR_CITIZEN", false);
		
		if (birthDate != null) {
			GenericValue fiscalYear = PayrollHelper.getCurrentFiscalYear(delegator);
			Date beginDate = fiscalYear!=null? fiscalYear.getDate("fromDate"): new Date();
			GregorianCalendar cal = new GregorianCalendar ();
			cal.setTime(beginDate);
			cal.add(Calendar.YEAR, -60);
			if (cal.getTime().compareTo(birthDate)==1)
				taxContext.put("IS_SENIOR_CITIZEN", true);
		}
		return taxContext;
	}

	public static double getTaxableSalaryAfterExemption(GenericValue userLogin, double grossSalary) {
		Debug.logInfo("@@@@@@@@@@@ GrossSalary Amount " + grossSalary, "UtilTax");
		double exemptedAmount = 0d, deductionAmount = 0d;
		try {
			exemptedAmount = ExemptionCalc.calculateExemption(userLogin).doubleValue();
			Debug.logInfo("@@@@@@@@@@@ Exempted Amount " + exemptedAmount, "UtilTax");
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return grossSalary - exemptedAmount;
	}
}
