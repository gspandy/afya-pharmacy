package com.nthdimenzion.humanres.payroll;

import java.sql.Date;

import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;

import com.nthdimenzion.humanres.payroll.calc.PayrollContext;

public class CostToCompanyEval {

	public static Object execute() {
		PayrollContext ctx = PayrollContext.getInstance();
		GenericValue userLogin = (GenericValue) ctx
				.get(PayrollContext.USER_LOGIN);
		String partyId = userLogin.getString("partyId");

		GenericValue fiscalYear = null;
		try {
			fiscalYear = PayrollHelper.getFiscalYear(UtilDateTime.nowDate(),(GenericDelegator)userLogin.getDelegator());
		} catch (GenericEntityException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Date fromDate = fiscalYear.getDate("fromDate");
		Date thruDate = fiscalYear.getDate("thruDate");

		SalaryBean salaryBean = null;
		try {
			salaryBean = PayrollHelper.populateEstimatedAnnualSalaryDetail(
					partyId, fromDate, thruDate,(GenericDelegator)userLogin.getDelegator());
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return salaryBean == null ? new Double(0.0d) : salaryBean
				.getGrossAmount();
	}
}
