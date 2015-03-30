package com.nthdimenzion.humanres.payroll.events;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;

import org.ofbiz.entity.transaction.GenericTransactionException;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.entity.util.EntityUtil;

import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import com.nthdimenzion.humanres.payroll.SalaryBean;
import com.nthdimenzion.humanres.payroll.SalaryComponentBean;
import com.nthdimenzion.humanres.payroll.calc.PayrollContext;
import com.nthdimenzion.humanres.payroll.calc.ResolverManager;
import com.nthdimenzion.humanres.payroll.calc.Rule;
import com.nthdimenzion.humanres.payroll.resolver.RuleResolver;
import com.nthdimenzion.humanres.payroll.PayrollHelper;
import com.smebiz.common.UtilDateTimeSME;

/**
 * @author sandeep
 * 
 */
public class PayrollEvents {

	private static String module = "PayrollEvents";
	private static final String delimiter = "-";

	public static String createEmplSalary(HttpServletRequest request, HttpServletResponse response) throws GenericServiceException,
			GenericEntityException {

		GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
		String partyId = request.getParameter("partyId");
		SalaryBean salary = (SalaryBean) request.getSession().getAttribute("salary");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");

		boolean beganTransaction = false;

		try {

			beganTransaction = TransactionUtil.begin();

			/***
			 * TODO first find record from empl_sal where from date is
			 * lfromDate,if records found delete it. Do the same thing for parTy
			 * salary Structure
			 */

			Date lfromDate = (Date) salary.getFromDate();
			delegator.removeByAnd("EmplSal", "partyId", partyId, "fromDate", lfromDate);
			// Date today = new Date();
			// Date lfromDate = today;
			String salaryStructureId = request.getParameter("salaryStructureId");
			Debug.logInfo(" createEmplSalary : fromDate :" + lfromDate, module);

			List<GenericValue> emplSalaryHeads = FastList.newInstance();

			Map<String, Object> cxt = FastMap.newInstance();
			cxt.put("partyId", partyId);
			cxt.put("fromDate", lfromDate);
			Debug.logInfo("fromDate :" + lfromDate + " partyId : " + partyId, module);
			dispatcher.runSync("retireEmplSalComponent", cxt); // Retire old
			// salary
			// heads

			Debug.logInfo("retireEmplSalComponent has been run ", module);

			for (SalaryComponentBean bean : salary.getAllComponents()) {
				System.out.println("@@@ CREATE @@@@ : salaryHeadId :" + bean.getSalaryHeadId() + "  amount " + bean.getAmount());
				Map<String, Object> argMap = new HashMap<String, Object>();
				argMap.put("partyId", partyId);
				argMap.put("salaryHeadId", bean.getSalaryHeadId());
				argMap.put("amount", bean.getAmount());
				argMap.put("fromDate", lfromDate);
				argMap.put("thruDate", bean.getThruDate());
				argMap.put("salaryStructureId", salaryStructureId);
				emplSalaryHeads.add(delegator.create("EmplSal", argMap));
			}

			Map<String, Object> context = FastMap.newInstance();
			context.put("userLogin", request.getSession().getAttribute("userLogin"));
			context.put("emplSalaryHeads", emplSalaryHeads);

			dispatcher.runSync("createEmplSalComponent", context);

			request.getSession().removeAttribute("salary");
			request.setAttribute("mode", "view");

			// Store info like PartyId - SalaryStructureId
			// PartyId -- PayGradeId - SalarySequenceId
			storePayrollInformationForParty(request);

		} catch (Throwable ge) {
			ge.printStackTrace();
			TransactionUtil.rollback();
			return "error";
		} finally {
			try {
				TransactionUtil.commit(beganTransaction);
			} catch (GenericTransactionException gte) {

			}
		}
		return "success";
	}

	private static void storePayrollInformationForParty(HttpServletRequest request) throws GenericEntityException, ParseException {
		GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");

		String payGradeId = request.getParameter("payGradeId");
		String salaryStepSeqId = request.getParameter("salaryStepSeqId");
		String revisionId = request.getParameter("revisionId");
		String salaryStructureId = request.getParameter("salaryStructureId");
		String partyId = request.getParameter("partyId");
		String fromDateParam = request.getParameter("fromDate");
		java.text.DateFormat df = new java.text.SimpleDateFormat("dd-MM-yyyy");
		Object fromDateObj = null;
		fromDateObj = df.parse(fromDateParam);
		java.util.Date offerFromDate = (java.util.Date) fromDateObj;
		java.sql.Date fromDate = new java.sql.Date(offerFromDate.getTime());

		// If not revision is mentioned then it should start from 0
		if (revisionId == null) {
			revisionId = "0";
		}

		// createPartyGradeRecord(delegator, partyId, payGradeId,
		// salaryStepSeqId,
		// revisionId);
		associateSalaryStructureToParty(delegator, partyId, salaryStructureId, fromDate);

	}

	/*
	 * Attach the SalaryStructureId from which the Employee Salary was created
	 * from. This is stored against the PartyId.
	 * 
	 * In case of re-association of Party to a new SalaryStructure the previous
	 * association is still maintained.
	 */
	private static void associateSalaryStructureToParty(GenericDelegator delegator, String partyId, String salaryStructureId,
			java.sql.Date fromDate) throws GenericEntityException {

		List<GenericValue> prevList = delegator.findByAnd("PartySalaryStructure", UtilMisc.toMap("partyId", partyId, "salaryStructureId",
				salaryStructureId, "inUse", "Y"));

		// If nothing is found, it implies this is the first association for the
		// Party.

		GenericValue prevGV = EntityUtil.getFirst(prevList);
		if (prevGV != null) {
			delegator.removeAll(prevList);
		}
		createPartySalaryAssociation(delegator, partyId, salaryStructureId, fromDate);
	}

	public static void createPartySalaryAssociation(GenericDelegator delegator, String partyId, String salaryStructureId,
			java.sql.Date fromDate) throws GenericEntityException {
		delegator.create("PartySalaryStructure", UtilMisc.toMap("partyId", partyId, "salaryStructureId", salaryStructureId, "inUse", "Y",
				"fromDate", fromDate));
	}

	/*
	 * Attach the PaygradeId - SalaryStepSeqId - Revision to a Party The old and
	 * previous association is still maintained.
	 */
	private static void createPartyGradeRecord(GenericDelegator delegator, String partyId, String payGradeId, String salaryStepSeqId,
			String revisionId) throws GenericEntityException {

		if (payGradeId != null && salaryStepSeqId != null && revisionId != null) {

			delegator.create("PartyPayGrade", UtilMisc.toMap("partyId", partyId, "payGradeId", payGradeId, "salaryStepSeqId",
					salaryStepSeqId, "revision", revisionId, "isCurrent", "Y"));
		}

	}

	public static String calculateEmplSalaryDetail(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException,
			ParseException {
		GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
		Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
		String salaryStructureId = (String) paramMap.get("salaryStructureId");
		String infromDate = (String) paramMap.get("fromDate");
		// infromDate = infromDate + " 00:00:00";
		// Debug.logInfo("infromDate :" + infromDate, module);
		// Date fromDate = UtilDateTimeSME.toDateYMD(infromDate.trim(),
		// delimiter);

		java.text.DateFormat df = new java.text.SimpleDateFormat("dd-MM-yyyy");
		Object fromDateObj = null;
		fromDateObj = df.parse(infromDate);
		java.util.Date offerFromDate = (java.util.Date) fromDateObj;
		java.sql.Date fromDate = new java.sql.Date(offerFromDate.getTime());

		Debug.logInfo("fromDate :" + fromDate, module);
		Date thruDate = null;
		SalaryBean salary = new SalaryBean((String) paramMap.get("partyId"), fromDate, thruDate,delegator);

		SalaryComponentBean bean = null;
		Map<String, Double> salMap = FastMap.newInstance();

		salMap.put("salaryStructureId", new Double(salaryStructureId));

		// Introducing the Slab logic to get the basic value.


		String salaryStructureHeadId = (String) paramMap.get("salaryStructureHeadId");
		String payGradeId = (String) paramMap.get("payGradeId");
		String revisionId = (String) paramMap.get("revisionId");
		if (revisionId == null) {
			revisionId = "0";
		}

		String salaryStepSeqId = (String) paramMap.get("salaryStepSeqId");
		if (payGradeId != null) {
			Double basicValue = PayrollHelper.getValueFromGrade(delegator, payGradeId, salaryStepSeqId, revisionId);
			paramMap.put("head_" + salaryStructureHeadId, basicValue.toString());
		}

		String partyId = request.getParameter("partyId");
		GenericValue emplValue = delegator.findOne("Party", false, "partyId", partyId);

		for (Map.Entry<String, Object> entry : paramMap.entrySet()) {
			salaryStructureHeadId = entry.getKey();
			String amountString = (String) entry.getValue();
			if (!salaryStructureHeadId.startsWith("head_"))
				continue;
			salaryStructureHeadId = salaryStructureHeadId.substring(5) == null ? salaryStructureHeadId : salaryStructureHeadId.substring(5);
			Double amount = Double.parseDouble(amountString);
			bean = new SalaryComponentBean(salaryStructureHeadId, amount, fromDate, thruDate,delegator);
			salary.addSalaryComponent(bean,delegator);
			salMap.put(bean.getSalaryHeadId(), amount);
		}

		PayrollContext.getInstance(emplValue, true);
		ResolverManager resolverMgr = PayrollContext.getResolver();
		resolverMgr.getResolver("SALARYHEAD").setLookupContext(salMap);

		List<GenericValue> formulaHeads = PayrollHelper.getAllCalculatedSalaryHeads(salaryStructureId,delegator);
		for (GenericValue head : formulaHeads) {
			List<GenericValue> headRules = PayrollHelper.getAllRulesFor(head,delegator);
			Number amount = 0;
			for (GenericValue headRule : headRules) {
				Rule rule = (Rule) resolverMgr.resolve(resolverMgr.encode(new String[] { RuleResolver.COMP_NAME,
						headRule.getString("ruleId") }));
				amount = rule.evaluate();
				if (amount.doubleValue() != 0) // why?? JUST calculating the
					// first rule for this head
					break;
			}
			bean = new SalaryComponentBean(head.getString("salaryStructureHeadId"), amount.doubleValue(), fromDate, thruDate,delegator);
			System.out.println("Calcualte :@@@@@ salaryHeadId :" + bean.getSalaryHeadId() + "  amount " + bean.getAmount());
			salary.addSalaryComponent(bean,delegator);
		}
		// For Calculating the exemption and the deductions.
		salary.populateDerivedValues(delegator);
		request.getSession().setAttribute("salary", salary);
		request.setAttribute("salary", salary);
		request.setAttribute("mode", "editable");
		PayrollContext.clear();
		return "success";
	}

	public static String populateSalaryDetail(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
		GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
		String partyId = (String) paramMap.get("partyId");
		if (partyId == null) {
			request.setAttribute("errorMessage", "partyId is null");
			return "error";
		}
		SalaryBean salary = populateSalaryStructureForParty(partyId, delegator);
		request.setAttribute("salary", salary);
		return "success";
	}

	public static SalaryBean populateSalaryStructureForParty(String partyId, GenericDelegator delegator) throws GenericEntityException {
		Date fromDate = UtilDateTime.nowDate();
		Date thruDate = null;

		SalaryBean salary = new SalaryBean(partyId, fromDate, thruDate,delegator);

		EntityCondition entityCondition = EntityCondition.makeCondition(UtilMisc.toMap("partyId", partyId));
		EntityCondition timeCond = EntityCondition.makeCondition(UtilMisc.toMap("thruDate", thruDate));

		EntityCondition newCondition = EntityCondition.makeCondition(entityCondition, timeCond);

		List<GenericValue> salaryHeads = delegator.findList("EmplSalDetail", newCondition, null, null, null, false);

		// salaryHeads = EntityUtil.filterByDate(salaryHeads); // Only get the
		// current records
		// from salary
		SalaryComponentBean bean = null;
		for (GenericValue salHead : salaryHeads) {
			bean = new SalaryComponentBean(salHead);
			salary.addSalaryComponent(bean,delegator);
		}
		salary.populateDerivedValues(delegator);
		return salary;
	}

	public static String populateProRatedSalaryDetail(HttpServletRequest request, HttpServletResponse response)
			throws GenericEntityException, ParseException {
		Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
		GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
		String partyId = (String) paramMap.get("partyId");
		Debug.logInfo("partyId :" + partyId, module);
		GenericValue fiscalYear = null;
		Date periodFrom = null;
		Date periodTo = null;
		String infromDate = (String) paramMap.get("fromDate");
		String inToDate = (String) paramMap.get("thruDate");
		
		SimpleDateFormat format = new SimpleDateFormat(UtilDateTime.DATE_FORMAT);

		/** If fromDate and thruDate are not passed then get current fiscal year **/
		if (infromDate == null || inToDate == null) {
			fiscalYear = PayrollHelper.getCurrentFiscalYear(delegator);
			periodFrom = (Date) fiscalYear.get("fromDate");
			periodTo = fiscalYear.getDate("thruDate");
		} else {
			//infromDate = infromDate + " 00:00:00";
			//Debug.logInfo("infromDate :" + infromDate, module);
			periodFrom = format.parse(infromDate); //UtilDateTimeSME.toDateYMD(infromDate.trim(), delimiter); // Start
			// Date
			// of
			// Financial
			// Year
			
			//inToDate = inToDate + " 00:00:00";
			//Debug.logInfo("inToDate :" + inToDate, module);
			periodTo = format.parse(inToDate);//UtilDateTimeSME.toDateYMD(inToDate.trim(), delimiter); // End
			// Date
			// of
			// Financial
			// Year
		}
		Debug.logInfo("fromDate :" + periodFrom, module);
		Debug.logInfo("periodTo :" + periodTo, module);
		if (partyId == null) {
			request.setAttribute("errorMessage", "partyId is null");
			return "error";
		}
		SalaryBean salary = PayrollHelper.populateEstimatedAnnualSalaryDetail(partyId, periodFrom, periodTo,delegator);
		if (salary != null) { // Employee has some CTC
			salary.populateDerivedValues(delegator);
			request.setAttribute("salary", salary);
			return "success";
		} else {
			return "error";
		}
	}

}