package com.smebiz.dynamicform.input.renderer;

import java.sql.Date;
import java.util.Calendar;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericValue;

import com.nthdimenzion.humanres.payroll.PayrollHelper;
import com.smebiz.dynamicform.renderer.RendererContext;

public class EmployeePF{

	protected void preRender(RendererContext ctx) {
		String partyId = (String) ctx.get("partyId");
		GenericDelegator delegator = null;
		try {
			GenericValue fiscalYear = PayrollHelper.getFiscalYear(Calendar.getInstance().getTime(),delegator);
			Date fromDate = fiscalYear.getDate("fromDate");
			
			delegator.findOne("EmplPF", UtilMisc.toMap("partyId",partyId,"periodFrom",fromDate), false);
		} catch (Exception e) {

		}
	}

}
