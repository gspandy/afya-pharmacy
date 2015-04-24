package com.nthdimenzion.humanres.payroll.webapp;

import java.sql.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javolution.util.FastList;
import javolution.util.FastMap;
import org.apache.commons.lang.StringUtils;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;

import com.smebiz.common.UtilDateTimeSME;

public class PayrollInitiationEvent {
	private static final String delimiter = "-";
	public static String initiatePayrollGeneration(HttpServletRequest request, HttpServletResponse response) throws GenericServiceException {

		String infromDate = (String) request.getParameter("periodFrom");
		infromDate = infromDate + " 00:00:00";
		Date lperiodFrom = new Date(UtilDateTimeSME.toDateYMD(infromDate.trim(), delimiter).getTime());  //Start Date of Financial Year
		String inToDate = (String) request.getParameter("periodTo");
		inToDate = inToDate + " 00:00:00";
		Date lperiodTo = new Date(UtilDateTimeSME.toDateYMD(inToDate.trim(), delimiter).getTime()); //End Date of Financial Year
		
		String linkToLMS = (String)request.getAttribute("linkToLMS");
		
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		Map<String, Object> ctx = FastMap.newInstance();
		ctx.put("periodFrom", lperiodFrom);
		ctx.put("periodTo", lperiodTo);		
		ctx.put("linkToLMS", linkToLMS);
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		String lpartyId = request.getParameter("partyId");
		String lpartyIdFrom = request.getParameter("partyIdFrom");
		if (!StringUtils.isBlank(lpartyId)) {
			ctx.put("partyId", lpartyId);
			dispatcher.runAsync("generatePayslip", ctx);
		} else {
			List<String> employeeIds = FastList.newInstance();
			try {
				Map<String, Object> argMap = FastMap.newInstance();
				argMap.put("partyIdFrom", lpartyIdFrom);
				GenericDelegator del = (GenericDelegator) userLogin.getDelegator();
				argMap.put("delegator", del);
				argMap.put("periodFrom", lperiodFrom);
				argMap.put("periodTo", lperiodTo);	
				employeeIds = getAllEmployeeIds(argMap);
			} catch (GenericEntityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			for (String emplId : employeeIds) {
				ctx.put("partyId", emplId);
				dispatcher.runAsync("generatePayslip", ctx);
			}
		}
		return "success";
	}
	
	/** Select all employeeIds from Employee table whose payslip has not been already generated **/
	private static List<String> getAllEmployeeIds(Map<String, Object> ctx) throws GenericEntityException {
		GenericDelegator delegator = (GenericDelegator)ctx.get("delegator");
		String lpartyIdFrom = (String)ctx.get("partyIdFrom");
		
		List<EntityCondition> condList = FastList.newInstance();
		EntityCondition roleCond = EntityCondition.makeCondition("roleTypeIdTo", "EMPLOYEE");
		condList.add(roleCond);
		if (!StringUtils.isBlank(lpartyIdFrom)) {
			EntityCondition partyFromCond = EntityCondition.makeCondition("partyIdFrom", lpartyIdFrom);
			condList.add(partyFromCond);
		}
		EntityCondition conditions = EntityCondition.makeCondition(condList);
		List<GenericValue> employees = delegator.findList("Employment", conditions, null, null, null, false);
	
		//List<GenericValue> newIds = EntityUtil.filterByCondition(employees, dateCond);
		List<String> employeeIds = FastList.newInstance();
		for (GenericValue employee : employees) {
			employeeIds.add(employee.getString("partyIdTo"));
		}
		/** Remove all the employess whose payslips have already been generated **/
		Date lperiodFrom = (Date)ctx.get("periodFrom");
		Date lperiodTo = (Date)ctx.get("periodTo");
		EntityCondition fromCond = EntityCondition.makeCondition("periodFrom", EntityOperator.LESS_THAN_EQUAL_TO, lperiodFrom);
		EntityCondition toCond = EntityCondition.makeCondition("periodTo", EntityOperator.GREATER_THAN_EQUAL_TO, lperiodTo);
		EntityCondition slipCond = EntityCondition.makeCondition(fromCond, toCond);
		Set<String> fieldsToSelect = new HashSet<String>();
		fieldsToSelect.add("partyId");
		List<GenericValue> slipIds = delegator.findList("EmplPayslip", slipCond, fieldsToSelect, null, null, false);
		employeeIds.removeAll(slipIds);
		return employeeIds;
	}
}
