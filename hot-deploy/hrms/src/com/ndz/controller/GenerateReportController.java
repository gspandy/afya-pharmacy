package com.ndz.controller;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.content.report.JREntityListIteratorDataSource;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityComparisonOperator;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionFunction;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.model.DynamicViewEntity;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.humanresext.util.HumanResUtil;
import org.ofbiz.service.ServiceUtil;

import com.smebiz.common.PartyUtil;

public class GenerateReportController {
	@SuppressWarnings({ "unused", "deprecation" })
	public static String pdfFormatLeaveReport(HttpServletRequest request, HttpServletResponse response) throws InterruptedException, GenericEntityException, ParseException {
		GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
		String employeeId = request.getParameter("employeeId");
		String hiddenEmployeeId = request.getParameter("hiddenEmployeeId");
		String hiddenStatusType = request.getParameter("hiddenStatusType");
		String hiddenManagerId = request.getParameter("hiddenManagerId");
		String reportFormat = request.getParameter("reportFormat");
		String fromDate = request.getParameter("fromDate");
		String thruDate = request.getParameter("thruDate");
		String leaveType = request.getParameter("hiddenLeaveType");
		Timestamp fDate = null;
		Timestamp tDate = null;
		
		if (UtilValidate.isEmpty(employeeId) && UtilValidate.isNotEmpty(hiddenEmployeeId))
			employeeId = hiddenEmployeeId;
		
		if (fromDate != "") {
			SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
			Date result = format.parse(fromDate);
			fDate = new Timestamp(result.getTime());
		}
		if (thruDate != "") {
			SimpleDateFormat format1 = new SimpleDateFormat("dd-MM-yyyy");
			Date result1 = format1.parse(thruDate);
			tDate = new Timestamp(result1.getTime());
		}

		EntityCondition condition = null;
		EntityCondition employeeIdCondition = null;
		
		List<EntityCondition> condiList = new ArrayList<EntityCondition>();
		
		if (UtilValidate.isNotEmpty(employeeId))
			condiList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, employeeId));

		if (UtilValidate.isNotEmpty(fDate))
			condiList.add(EntityConditionFunction.makeCondition("fromDate", EntityComparisonOperator.GREATER_THAN_EQUAL_TO, fDate));
		
		
		if (UtilValidate.isNotEmpty(tDate))
			condiList.add(EntityConditionFunction.makeCondition("thruDate", EntityComparisonOperator.LESS_THAN_EQUAL_TO, tDate));

		if (UtilValidate.isNotEmpty(leaveType))
			condiList.add(EntityCondition.makeCondition("leaveTypeId", EntityOperator.EQUALS, leaveType));
		
		if (UtilValidate.isNotEmpty(hiddenStatusType))
			condiList.add(EntityCondition.makeCondition("leaveStatusId", EntityOperator.EQUALS, hiddenStatusType));
		
		if (UtilValidate.isEmpty(employeeId) && UtilValidate.isNotEmpty(hiddenManagerId)) {
			List<GenericValue> listSubOrdinates = GetSubOrdinates(hiddenManagerId);
			if (listSubOrdinates != null) {
				List<String> positions = new ArrayList<String>(listSubOrdinates.size());
				for (GenericValue position : listSubOrdinates) {
					positions.add(position.getString("partyId"));
				}
				positions.add(hiddenManagerId);
				condiList.add(EntityCondition.makeCondition("partyId", EntityComparisonOperator.IN, positions));
			} else {
				condiList.add(EntityCondition.makeCondition("partyId", EntityComparisonOperator.IN, hiddenManagerId));
			}
		}
		condition = EntityCondition.makeCondition(condiList,EntityOperator.AND);
		org.ofbiz.entity.transaction.TransactionUtil.begin();
		DynamicViewEntity oidve = new DynamicViewEntity();
		oidve.setEntityName("EmplLeaveView");
		EntityListIterator leave = delegator.find("EmplLeaveView",condition,null,null,null,null);// delegator.findListIteratorByCondition(oidve, condition, null, null,null,null);//EmplLeaveView
		JREntityListIteratorDataSource jrDataSource = new JREntityListIteratorDataSource(leave);
		
		Map<String,Object> companyHeaderInfoMap = PartyUtil.getCompanyHeaderInfo(delegator);
		String headOfficeImg = "./framework/images/webapp/images/head_office.png";
		String verticalLineImg = "./framework/images/webapp/images/vertical_line_bar.png";
		
		Map<String,Object> jrParameters = new HashMap<String,Object>();
		jrParameters.put("headOfficeImg", headOfficeImg);
		jrParameters.put("verticalLineImg", verticalLineImg);
		jrParameters.put("logoImageUrl", companyHeaderInfoMap.get("logoImageUrl"));
		jrParameters.put("companyName", companyHeaderInfoMap.get("companyName"));
		jrParameters.put("address1", companyHeaderInfoMap.get("address1"));
		jrParameters.put("address2", companyHeaderInfoMap.get("address2"));
		jrParameters.put("companyPostalAddr", companyHeaderInfoMap.get("companyPostalAddr"));
		jrParameters.put("postalCode", companyHeaderInfoMap.get("postalCode"));
		jrParameters.put("city", companyHeaderInfoMap.get("city"));
		jrParameters.put("stateProvinceAbbr", companyHeaderInfoMap.get("stateProvinceAbbr"));
		jrParameters.put("countryName", companyHeaderInfoMap.get("countryName"));
		jrParameters.put("telephoneNumber", companyHeaderInfoMap.get("telephoneNumber"));
		jrParameters.put("faxNumber", companyHeaderInfoMap.get("faxNumber"));
		jrParameters.put("email", companyHeaderInfoMap.get("email"));
		jrParameters.put("companyRegNo", companyHeaderInfoMap.get("companyRegNo"));
		jrParameters.put("vatRegNo", companyHeaderInfoMap.get("vatRegNo"));
		jrParameters.put("tpinNo", companyHeaderInfoMap.get("tpinNo"));
		
		request.setAttribute("jrDataSource", jrDataSource);
		request.setAttribute("jrParameters", jrParameters);
		org.ofbiz.entity.transaction.TransactionUtil.commit();
		return "pdf";
	}

	public static List<GenericValue> GetSubOrdinates(String managerId) throws GenericEntityException {
		GenericDelegator delegator = GenericDelegator.getGenericDelegator("default");
		List<GenericValue> positionValues = HumanResUtil.getSubordinatesForParty(managerId, delegator);
		if (positionValues != null) {
			List<String> positions = new ArrayList<String>(positionValues.size());
			for (GenericValue position : positionValues) {
				positions.add(position.getString("emplPositionIdManagedBy"));
			}
			EntityCondition ec = EntityCondition.makeCondition("emplPositionId", EntityComparisonOperator.IN, positions);
			List<GenericValue> subOrdinates = delegator.findList("EmplPositionFulfilmentAndPerson", ec, null, null, null, false);
			return subOrdinates;
		}
		return null;
	}

	@SuppressWarnings("deprecation")
	public static String genereateClaimReport(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException, ParseException {
		GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
		String hiddenEmployeeId = request.getParameter("hiddenEmployeeId");
		String employeeId = request.getParameter("employeeId");
		String hiddenManagerId = request.getParameter("hiddenManagerId");
		String fromDate = request.getParameter("fromDate");
		String thruDate = request.getParameter("thruDate");
		String claimType = request.getParameter("hiddenClaimType");
		String hiddenStatusType = request.getParameter("hiddenStatusType");
		Timestamp fDate = null;
		Timestamp tDate = null;
		if (UtilValidate.isEmpty(employeeId) && UtilValidate.isNotEmpty(hiddenEmployeeId))
			employeeId = hiddenEmployeeId;
		if (fromDate != "") {
			SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
			fDate = new Timestamp(format.parse(fromDate).getTime());
		}
		if (thruDate != "") {
			SimpleDateFormat format1 = new SimpleDateFormat("dd-MM-yyyy");
			tDate = new Timestamp(format1.parse(thruDate).getTime());
		}

		List<EntityCondition> conditionList = new ArrayList<EntityCondition>();
		conditionList.add(EntityCondition.makeCondition("enumTypeId", EntityOperator.EQUALS, "CLAIM_TYPE"));
		if (UtilValidate.isNotEmpty(employeeId)) {
			conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, employeeId));
		}
		if (UtilValidate.isNotEmpty(fDate)) {
			conditionList.add(EntityConditionFunction.makeCondition("beginDate", EntityComparisonOperator.GREATER_THAN_EQUAL_TO, fDate));
		}
		if (UtilValidate.isNotEmpty(tDate)) {
			conditionList.add(EntityConditionFunction.makeCondition("endDate", EntityComparisonOperator.LESS_THAN_EQUAL_TO, tDate));
		}
		if (UtilValidate.isNotEmpty(claimType)) {
			conditionList.add(EntityCondition.makeCondition("claimType", EntityOperator.EQUALS, claimType));
		}
		if(UtilValidate.isNotEmpty(hiddenStatusType)){
			conditionList.add(EntityCondition.makeCondition("statusId",EntityOperator.EQUALS,hiddenStatusType));
		}
		EntityCondition ec = null;
		if (UtilValidate.isEmpty(employeeId) && UtilValidate.isNotEmpty(hiddenManagerId)) {
			List<GenericValue> listSubOrdinates = GetSubOrdinates(hiddenManagerId);
			if (listSubOrdinates != null) {
				List<String> positions = new ArrayList<String>(listSubOrdinates.size());
				for (GenericValue position : listSubOrdinates) {
					positions.add(position.getString("partyId"));
				}
				positions.add(hiddenManagerId);
				ec = EntityCondition.makeCondition("partyId", EntityComparisonOperator.IN, positions);
			} else {
				ec = EntityCondition.makeCondition("partyId", EntityComparisonOperator.IN, hiddenManagerId);
			}
			conditionList.add(ec);
		}
		EntityCondition condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
		org.ofbiz.entity.transaction.TransactionUtil.begin();
		DynamicViewEntity oidve = new DynamicViewEntity();
		oidve.setEntityName("EmplLeaveView");
		EntityListIterator claim = delegator.find("EmplLeaveView",condition,null,null,null,null);;// delegator.findListIteratorByCondition(oidve, condition, null, null,null,null);
		JREntityListIteratorDataSource jrDataSource = new JREntityListIteratorDataSource(claim);
		
		Map<String,Object> companyHeaderInfoMap = PartyUtil.getCompanyHeaderInfo(delegator);
		String headOfficeImg = "./framework/images/webapp/images/head_office.png";
		String verticalLineImg = "./framework/images/webapp/images/vertical_line_bar.png";
		
		Map<String,Object> jrParameters = new HashMap<String,Object>();
		jrParameters.put("headOfficeImg", headOfficeImg);
		jrParameters.put("verticalLineImg", verticalLineImg);
		jrParameters.put("logoImageUrl", companyHeaderInfoMap.get("logoImageUrl"));
		jrParameters.put("companyName", companyHeaderInfoMap.get("companyName"));
		jrParameters.put("address1", companyHeaderInfoMap.get("address1"));
		jrParameters.put("address2", companyHeaderInfoMap.get("address2"));
		jrParameters.put("companyPostalAddr", companyHeaderInfoMap.get("companyPostalAddr"));
		jrParameters.put("postalCode", companyHeaderInfoMap.get("postalCode"));
		jrParameters.put("city", companyHeaderInfoMap.get("city"));
		jrParameters.put("stateProvinceAbbr", companyHeaderInfoMap.get("stateProvinceAbbr"));
		jrParameters.put("countryName", companyHeaderInfoMap.get("countryName"));
		jrParameters.put("telephoneNumber", companyHeaderInfoMap.get("telephoneNumber"));
		jrParameters.put("faxNumber", companyHeaderInfoMap.get("faxNumber"));
		jrParameters.put("email", companyHeaderInfoMap.get("email"));
		jrParameters.put("companyRegNo", companyHeaderInfoMap.get("companyRegNo"));
		jrParameters.put("vatRegNo", companyHeaderInfoMap.get("vatRegNo"));
		jrParameters.put("tpinNo", companyHeaderInfoMap.get("tpinNo"));
		
		request.setAttribute("jrDataSource", jrDataSource);
		request.setAttribute("jrParameters", jrParameters);

		org.ofbiz.entity.transaction.TransactionUtil.commit();

		return "pdf";

	}

	@SuppressWarnings({ "deprecation", "null", "unused" })
	public static String GenerateLoansReport(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException, ParseException {
		GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
		String employeeId = request.getParameter("employeeId");
		String fromDate = request.getParameter("fromDate");
		String thruDate = request.getParameter("thruDate");
		String loanType = request.getParameter("loanTypeText");
		String hiddenStatusType = request.getParameter("hiddenStatusType");
		String hiddenEmployeeId = request.getParameter("hiddenEmployeeId");
		String hiddenManagerId = request.getParameter("hiddenManagerId");
		if (UtilValidate.isEmpty(employeeId) && UtilValidate.isNotEmpty(hiddenEmployeeId))
			employeeId = hiddenEmployeeId;
		
		java.sql.Date fDate = null;
		java.sql.Date tDate = null;
		if (fromDate != "") {
			SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
			fDate = new java.sql.Date(format.parse(fromDate).getTime());
		}
		if (thruDate != "") {
			SimpleDateFormat format1 = new SimpleDateFormat("dd-MM-yyyy");
			tDate = new java.sql.Date(format1.parse(thruDate).getTime());
		}

		List<EntityCondition> condition = new ArrayList<EntityCondition>();
		if (employeeId != "") {
			condition.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, employeeId));
		}
		if (UtilValidate.isNotEmpty(fDate)) {
			condition.add(EntityConditionFunction.makeCondition("fromDate", EntityComparisonOperator.GREATER_THAN_EQUAL_TO, fDate));
		}
		if (UtilValidate.isNotEmpty(tDate)) {
			condition.add(EntityConditionFunction.makeCondition("thruDate", EntityComparisonOperator.LESS_THAN_EQUAL_TO, tDate));
		}
		if (UtilValidate.isNotEmpty(loanType)) {
			condition.add(EntityCondition.makeCondition("loanType", EntityOperator.EQUALS, loanType));
		}
		if(UtilValidate.isNotEmpty(hiddenStatusType)){
			condition.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, hiddenStatusType));
		}
		
		EntityCondition ec = null;
		if (UtilValidate.isEmpty(employeeId) && UtilValidate.isNotEmpty(hiddenManagerId)) {
			List<GenericValue> listSubOrdinates = GetSubOrdinates(hiddenManagerId);
			if (listSubOrdinates != null) {
				List<String> positions = new ArrayList<String>(listSubOrdinates.size());
				for (GenericValue position : listSubOrdinates) {
					positions.add(position.getString("partyId"));
				}
				positions.add(hiddenManagerId);
				ec = EntityCondition.makeCondition("partyId", EntityComparisonOperator.IN, positions);
			} else {
				ec = EntityCondition.makeCondition("partyId", EntityComparisonOperator.IN, hiddenManagerId);
			}
			condition.add(ec);
		}
		EntityCondition mainCondition = EntityCondition.makeCondition(condition,EntityOperator.AND);
		org.ofbiz.entity.transaction.TransactionUtil.begin();
		DynamicViewEntity oidve = new DynamicViewEntity();
		oidve.setEntityName("EMaxEloanDetail");
		EntityListIterator claim = delegator.find("EMaxEloanDetail",mainCondition,null,null,null,null);// delegator.findListIteratorByCondition(oidve, mainCondition, null, null,null,null);
		JREntityListIteratorDataSource jrDataSource = new JREntityListIteratorDataSource(claim);
		
		Map<String,Object> companyHeaderInfoMap = PartyUtil.getCompanyHeaderInfo(delegator);
		String headOfficeImg = "./framework/images/webapp/images/head_office.png";
		String verticalLineImg = "./framework/images/webapp/images/vertical_line_bar.png";
		
		Map<String,Object> jrParameters = new HashMap<String,Object>();
		jrParameters.put("headOfficeImg", headOfficeImg);
		jrParameters.put("verticalLineImg", verticalLineImg);
		jrParameters.put("logoImageUrl", companyHeaderInfoMap.get("logoImageUrl"));
		jrParameters.put("companyName", companyHeaderInfoMap.get("companyName"));
		jrParameters.put("address1", companyHeaderInfoMap.get("address1"));
		jrParameters.put("address2", companyHeaderInfoMap.get("address2"));
		jrParameters.put("companyPostalAddr", companyHeaderInfoMap.get("companyPostalAddr"));
		jrParameters.put("postalCode", companyHeaderInfoMap.get("postalCode"));
		jrParameters.put("city", companyHeaderInfoMap.get("city"));
		jrParameters.put("stateProvinceAbbr", companyHeaderInfoMap.get("stateProvinceAbbr"));
		jrParameters.put("countryName", companyHeaderInfoMap.get("countryName"));
		jrParameters.put("telephoneNumber", companyHeaderInfoMap.get("telephoneNumber"));
		jrParameters.put("faxNumber", companyHeaderInfoMap.get("faxNumber"));
		jrParameters.put("email", companyHeaderInfoMap.get("email"));
		jrParameters.put("companyRegNo", companyHeaderInfoMap.get("companyRegNo"));
		jrParameters.put("vatRegNo", companyHeaderInfoMap.get("vatRegNo"));
		jrParameters.put("tpinNo", companyHeaderInfoMap.get("tpinNo"));
		
		request.setAttribute("jrDataSource", jrDataSource);
		request.setAttribute("jrParameters", jrParameters);

		org.ofbiz.entity.transaction.TransactionUtil.commit();

		return "pdf";

	}

	@SuppressWarnings({ "deprecation", "null", "unused" })
	public static String GenerateEmployeeReport(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException, ParseException {
		GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");

		String reportFormat = request.getParameter("reportFormat");
		String employeeId = request.getParameter("employeeId");
		String employeeStatus = request.getParameter("employeeStatus");
		String positionType = request.getParameter("positionType");

		EntityCondition condition = null;
		EntityCondition employeeIdCondition = null;
		if (employeeId != "") {
			employeeIdCondition = EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, employeeId);
			if (condition != null) {
				condition = EntityCondition.makeCondition(employeeIdCondition, condition);
			} else {
				condition = employeeIdCondition;
			}
		}
		if (employeeStatus != "") {
			EntityCondition claimTypeCon = EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, employeeStatus);
			if (condition != null) {
				condition = EntityCondition.makeCondition(claimTypeCon, condition);
			} else {
				condition = claimTypeCon;
			}

		}
		if (positionType != "") {
			EntityCondition claimTypeCon = EntityCondition.makeCondition("emplPositionTypeId", EntityOperator.EQUALS, positionType);
			if (condition != null) {
				condition = EntityCondition.makeCondition(claimTypeCon, condition);
			} else {
				condition = claimTypeCon;
			}

		}

		String reportFormatType = null;
		if (reportFormat.equals("PDF"))
			reportFormatType = "pdf";
		else
			reportFormatType = "excel";

		org.ofbiz.entity.transaction.TransactionUtil.begin();
		DynamicViewEntity oidve = new DynamicViewEntity();
		oidve.setEntityName("EmplPositionStatusView");
		EntityListIterator claim = delegator.find("EmplPositionStatusView",condition,null,null,null,null);
		JREntityListIteratorDataSource jrDataSource = new JREntityListIteratorDataSource(claim);
		
		Map<String,Object> companyHeaderInfoMap = PartyUtil.getCompanyHeaderInfo(delegator);
		String headOfficeImg = "./framework/images/webapp/images/head_office.png";
		String verticalLineImg = "./framework/images/webapp/images/vertical_line_bar.png";
		
		Map<String,Object> jrParameters = new HashMap<String,Object>();
		jrParameters.put("headOfficeImg", headOfficeImg);
		jrParameters.put("verticalLineImg", verticalLineImg);
		jrParameters.put("logoImageUrl", companyHeaderInfoMap.get("logoImageUrl"));
		jrParameters.put("companyName", companyHeaderInfoMap.get("companyName"));
		jrParameters.put("address1", companyHeaderInfoMap.get("address1"));
		jrParameters.put("address2", companyHeaderInfoMap.get("address2"));
		jrParameters.put("companyPostalAddr", companyHeaderInfoMap.get("companyPostalAddr"));
		jrParameters.put("postalCode", companyHeaderInfoMap.get("postalCode"));
		jrParameters.put("city", companyHeaderInfoMap.get("city"));
		jrParameters.put("stateProvinceAbbr", companyHeaderInfoMap.get("stateProvinceAbbr"));
		jrParameters.put("countryName", companyHeaderInfoMap.get("countryName"));
		jrParameters.put("telephoneNumber", companyHeaderInfoMap.get("telephoneNumber"));
		jrParameters.put("faxNumber", companyHeaderInfoMap.get("faxNumber"));
		jrParameters.put("email", companyHeaderInfoMap.get("email"));
		jrParameters.put("companyRegNo", companyHeaderInfoMap.get("companyRegNo"));
		jrParameters.put("vatRegNo", companyHeaderInfoMap.get("vatRegNo"));
		jrParameters.put("tpinNo", companyHeaderInfoMap.get("tpinNo"));
		
		request.setAttribute("jrDataSource", jrDataSource);
		request.setAttribute("jrParameters", jrParameters);

		org.ofbiz.entity.transaction.TransactionUtil.commit();

		return reportFormatType;

	}

	@SuppressWarnings({ "deprecation", "null" })
	public static String RequisitionByStatusReport(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException, ParseException {
		GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");

		String reportFormat = request.getParameter("reportFormat");
		EntityCondition condition = null;
		String requisitionPositionType = request.getParameter("requisitionPositionType");
		String requisitionDepartment = request.getParameter("requisitionDepartment");

		if (UtilValidate.isNotEmpty(requisitionPositionType) && UtilValidate.isEmpty(requisitionDepartment)) {
			EntityCondition requisitionPositionTypeCon = EntityCondition.makeCondition("positionType", EntityOperator.EQUALS, requisitionPositionType);
			condition = EntityCondition.makeCondition(requisitionPositionTypeCon);
		}
		if (UtilValidate.isEmpty(requisitionPositionType) && UtilValidate.isNotEmpty(requisitionDepartment)) {
			EntityCondition requisitionDepartmentCon = EntityCondition.makeCondition("requisitionId", EntityOperator.EQUALS, requisitionDepartment);
			condition = EntityCondition.makeCondition(requisitionDepartmentCon);
		}
		if (UtilValidate.isNotEmpty(requisitionPositionType) && UtilValidate.isNotEmpty(requisitionDepartment)) {
			EntityCondition requisitionPositionTypeCon = EntityCondition.makeCondition("positionType", EntityOperator.EQUALS, requisitionPositionType);
			EntityCondition requisitionDepartmentCon = EntityCondition.makeCondition("requisitionId", EntityOperator.EQUALS, requisitionDepartment);
			condition = EntityCondition.makeCondition(requisitionPositionTypeCon, requisitionDepartmentCon);
		}
		String reportFormatType = null;
		if (reportFormat.equals("PDF"))
			reportFormatType = "pdf";
		else
			reportFormatType = "excel";

		org.ofbiz.entity.transaction.TransactionUtil.begin();
		DynamicViewEntity oidve = new DynamicViewEntity();
		oidve.setEntityName("EmployeeRequisition");
		EntityListIterator claim = delegator.find("EmployeeRequisition",condition,null,null,null,null);// delegator.findListIteratorByCondition(oidve, condition, null, null,null,null);
		JREntityListIteratorDataSource jrDataSource = new JREntityListIteratorDataSource(claim);
		
		Map<String,Object> companyHeaderInfoMap = PartyUtil.getCompanyHeaderInfo(delegator);
		String headOfficeImg = "./framework/images/webapp/images/head_office.png";
		String verticalLineImg = "./framework/images/webapp/images/vertical_line_bar.png";
		
		Map<String,Object> jrParameters = new HashMap<String,Object>();
		jrParameters.put("headOfficeImg", headOfficeImg);
		jrParameters.put("verticalLineImg", verticalLineImg);
		jrParameters.put("logoImageUrl", companyHeaderInfoMap.get("logoImageUrl"));
		jrParameters.put("companyName", companyHeaderInfoMap.get("companyName"));
		jrParameters.put("address1", companyHeaderInfoMap.get("address1"));
		jrParameters.put("address2", companyHeaderInfoMap.get("address2"));
		jrParameters.put("companyPostalAddr", companyHeaderInfoMap.get("companyPostalAddr"));
		jrParameters.put("postalCode", companyHeaderInfoMap.get("postalCode"));
		jrParameters.put("city", companyHeaderInfoMap.get("city"));
		jrParameters.put("stateProvinceAbbr", companyHeaderInfoMap.get("stateProvinceAbbr"));
		jrParameters.put("countryName", companyHeaderInfoMap.get("countryName"));
		jrParameters.put("telephoneNumber", companyHeaderInfoMap.get("telephoneNumber"));
		jrParameters.put("faxNumber", companyHeaderInfoMap.get("faxNumber"));
		jrParameters.put("email", companyHeaderInfoMap.get("email"));
		jrParameters.put("companyRegNo", companyHeaderInfoMap.get("companyRegNo"));
		jrParameters.put("vatRegNo", companyHeaderInfoMap.get("vatRegNo"));
		jrParameters.put("tpinNo", companyHeaderInfoMap.get("tpinNo"));
		
		request.setAttribute("jrDataSource", jrDataSource);
		request.setAttribute("jrParameters", jrParameters);

		org.ofbiz.entity.transaction.TransactionUtil.commit();

		return reportFormatType;

	}

	@SuppressWarnings("deprecation")
	public static String requisitionByOffer(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException, ParseException {
		GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
		EntityCondition condition = null;
		String requisitionById = request.getParameter("requisitionById");
		String positionType = request.getParameter("allocatePositionTypeTextBox");
		if (UtilValidate.isNotEmpty(requisitionById) && UtilValidate.isEmpty(positionType)) {
			EntityCondition requisitionPositionTypeCon = EntityCondition.makeCondition("requisitionId", EntityOperator.EQUALS, requisitionById);
			condition = EntityCondition.makeCondition(requisitionPositionTypeCon);
		}
		if (UtilValidate.isEmpty(requisitionById) && UtilValidate.isNotEmpty(positionType)) {
			EntityCondition requisitionDepartmentCon = EntityCondition.makeCondition("requisitionId", EntityOperator.EQUALS, positionType);
			condition = EntityCondition.makeCondition(requisitionDepartmentCon);
		}
		if (UtilValidate.isNotEmpty(requisitionById) && UtilValidate.isNotEmpty(positionType)) {
			EntityCondition requisitionPositionTypeCon = EntityCondition.makeCondition("positionType", EntityOperator.EQUALS, requisitionById);
			EntityCondition requisitionDepartmentCon = EntityCondition.makeCondition("requisitionId", EntityOperator.EQUALS, positionType);
			condition = EntityCondition.makeCondition(requisitionPositionTypeCon, requisitionDepartmentCon);
		}
		@SuppressWarnings("unused")
		Integer noOfCadidatesApplied = 0;
		Integer noOfCanOffered = 0;
		Integer noOfCanJoined = 0;
		String offerId = null;
		String positiontype = new String();
		String requisitionId = new String();

		org.ofbiz.entity.transaction.TransactionUtil.begin();
		DynamicViewEntity oidve = new DynamicViewEntity();
		oidve.setEntityName("RecruitmentOfferStatus");
		EntityListIterator reqOffer = delegator.find("RecruitmentOfferStatus",condition,null,null,null,null);// delegator.findListIteratorByCondition(oidve, condition, null, null,null,null);
		JREntityListIteratorDataSource jrDataSource = new JREntityListIteratorDataSource(reqOffer);
		
		Map<String,Object> companyHeaderInfoMap = PartyUtil.getCompanyHeaderInfo(delegator);
		String headOfficeImg = "./framework/images/webapp/images/head_office.png";
		String verticalLineImg = "./framework/images/webapp/images/vertical_line_bar.png";
		
		Map<String,Object> jrParameters = new HashMap<String,Object>();
		jrParameters.put("headOfficeImg", headOfficeImg);
		jrParameters.put("verticalLineImg", verticalLineImg);
		jrParameters.put("logoImageUrl", companyHeaderInfoMap.get("logoImageUrl"));
		jrParameters.put("companyName", companyHeaderInfoMap.get("companyName"));
		jrParameters.put("address1", companyHeaderInfoMap.get("address1"));
		jrParameters.put("address2", companyHeaderInfoMap.get("address2"));
		jrParameters.put("companyPostalAddr", companyHeaderInfoMap.get("companyPostalAddr"));
		jrParameters.put("postalCode", companyHeaderInfoMap.get("postalCode"));
		jrParameters.put("city", companyHeaderInfoMap.get("city"));
		jrParameters.put("stateProvinceAbbr", companyHeaderInfoMap.get("stateProvinceAbbr"));
		jrParameters.put("countryName", companyHeaderInfoMap.get("countryName"));
		jrParameters.put("telephoneNumber", companyHeaderInfoMap.get("telephoneNumber"));
		jrParameters.put("faxNumber", companyHeaderInfoMap.get("faxNumber"));
		jrParameters.put("email", companyHeaderInfoMap.get("email"));
		jrParameters.put("companyRegNo", companyHeaderInfoMap.get("companyRegNo"));
		jrParameters.put("vatRegNo", companyHeaderInfoMap.get("vatRegNo"));
		jrParameters.put("tpinNo", companyHeaderInfoMap.get("tpinNo"));
		
		jrParameters.put("noOfCadidatesApplied", noOfCadidatesApplied.toString());
		jrParameters.put("noOfCanOffered", noOfCanOffered.toString());
		jrParameters.put("noOfCanJoined", noOfCanJoined.toString());
		jrParameters.put("positiontype", positiontype);
		request.setAttribute("jrDataSource", jrDataSource);
		request.setAttribute("jrParameters", jrParameters);

		org.ofbiz.entity.transaction.TransactionUtil.commit();

		return "pdf";

	}

	@SuppressWarnings("deprecation")
	public static String TdsReport(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException, ParseException {
		GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");

		String reportFormat = request.getParameter("reportFormat");

		EntityCondition condition = null;
		String reportFormatType = null;
		if (reportFormat.equals("PDF"))
			reportFormatType = "pdf";
		else
			reportFormatType = "excel";

		org.ofbiz.entity.transaction.TransactionUtil.begin();
		DynamicViewEntity oidve = new DynamicViewEntity();
		oidve.setEntityName("EmplTaxDeduction");
		EntityListIterator claim = delegator.find("EmplTaxDeduction",condition,null,null,null,null);// delegator.findListIteratorByCondition(oidve, condition, null, null,null,null);
		JREntityListIteratorDataSource jrDataSource = new JREntityListIteratorDataSource(claim);
		
		Map<String,Object> companyHeaderInfoMap = PartyUtil.getCompanyHeaderInfo(delegator);
		String headOfficeImg = "./framework/images/webapp/images/head_office.png";
		String verticalLineImg = "./framework/images/webapp/images/vertical_line_bar.png";
		
		Map<String,Object> jrParameters = new HashMap<String,Object>();
		jrParameters.put("headOfficeImg", headOfficeImg);
		jrParameters.put("verticalLineImg", verticalLineImg);
		jrParameters.put("logoImageUrl", companyHeaderInfoMap.get("logoImageUrl"));
		jrParameters.put("companyName", companyHeaderInfoMap.get("companyName"));
		jrParameters.put("address1", companyHeaderInfoMap.get("address1"));
		jrParameters.put("address2", companyHeaderInfoMap.get("address2"));
		jrParameters.put("companyPostalAddr", companyHeaderInfoMap.get("companyPostalAddr"));
		jrParameters.put("postalCode", companyHeaderInfoMap.get("postalCode"));
		jrParameters.put("city", companyHeaderInfoMap.get("city"));
		jrParameters.put("stateProvinceAbbr", companyHeaderInfoMap.get("stateProvinceAbbr"));
		jrParameters.put("countryName", companyHeaderInfoMap.get("countryName"));
		jrParameters.put("telephoneNumber", companyHeaderInfoMap.get("telephoneNumber"));
		jrParameters.put("faxNumber", companyHeaderInfoMap.get("faxNumber"));
		jrParameters.put("email", companyHeaderInfoMap.get("email"));
		jrParameters.put("companyRegNo", companyHeaderInfoMap.get("companyRegNo"));
		jrParameters.put("vatRegNo", companyHeaderInfoMap.get("vatRegNo"));
		jrParameters.put("tpinNo", companyHeaderInfoMap.get("tpinNo"));
		
		request.setAttribute("jrDataSource", jrDataSource);
		request.setAttribute("jrParameters", jrParameters);

		org.ofbiz.entity.transaction.TransactionUtil.commit();

		return reportFormatType;
	}

	@SuppressWarnings({ "deprecation", "unchecked" })
	public static String MyTotalTdsReport(HttpServletRequest request, HttpServletResponse response) throws InterruptedException, GenericEntityException, ParseException {

		GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");

		String periodFrom = (String) request.getParameter("periodFrom");
		String periodTo = (String) request.getParameter("periodTo");

		DateFormat df = new SimpleDateFormat("dd-MM-yyyy");

		Date periodFm = null;
		Date periodT = null;

		String reportFormat = request.getParameter("reportFormat");
		String partyId = request.getParameter("partyId");
		String taxType = request.getParameter("taxType");

		EntityCondition condition = null;
		EntityCondition partyCond = null;
		EntityCondition fromCond = null;
		EntityCondition toCond = null;
		EntityCondition typeCond = null;
		if (partyId != "") {
			partyCond = EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId);
			condition = partyCond;
		}
		if (taxType != "") {

			typeCond = EntityCondition.makeCondition("taxType", EntityOperator.EQUALS, taxType);
			if (condition != null)
				condition = EntityCondition.makeCondition(condition, typeCond);
			else {
				condition = typeCond;
			}
		}
		if (periodFrom != "") {

			periodFm = df.parse(periodFrom.trim());
			fromCond = EntityCondition.makeCondition("periodFrom", EntityOperator.GREATER_THAN_EQUAL_TO, periodFm);
			if (condition != null)
				condition = EntityCondition.makeCondition(condition, fromCond);
			else {
				condition = fromCond;
			}
		}
		if (periodTo != "") {
			periodT = df.parse(periodTo.trim());

			toCond = EntityCondition.makeCondition("periodTo", EntityOperator.LESS_THAN_EQUAL_TO, periodT);
			if (condition != null)
				condition = EntityCondition.makeCondition(condition, toCond);
			else {
				condition = toCond;
			}
		}
		Set<String> fields = new HashSet<String>();
		fields.add("partyId");
		fields.add("taxType");
		fields.add("amount");
		fields.add("periodTo");
		fields.add("periodFrom");
		List<String> orderBy = new LinkedList<String>();
		orderBy.add("partyId");
		orderBy.add("taxType");
		EntityListIterator tdsList = null;
		try {
			DynamicViewEntity oidve = new DynamicViewEntity();
			oidve.setEntityName("EmplTaxDeduction");
			tdsList = delegator.find("EmplTaxDeduction",condition,null,null,null,null);// delegator.findListIteratorByCondition(oidve, condition,null, fields, orderBy,null);
		} catch (GenericEntityException e) {
			ServiceUtil.returnError("Error finding TDS for : " + partyId + " :" + e.getMessage());
		}

		String reportFormatType = null;
		if (reportFormat.equals("PDF"))
			reportFormatType = "pdf";
		else
			reportFormatType = "excel";
		org.ofbiz.entity.transaction.TransactionUtil.begin();

		JREntityListIteratorDataSource jrDataSource = new JREntityListIteratorDataSource(tdsList);
		
		Map<String,Object> companyHeaderInfoMap = PartyUtil.getCompanyHeaderInfo(delegator);
		String headOfficeImg = "./framework/images/webapp/images/head_office.png";
		String verticalLineImg = "./framework/images/webapp/images/vertical_line_bar.png";
		
		Map<String,Object> jrParameters = new HashMap<String,Object>();
		jrParameters.put("headOfficeImg", headOfficeImg);
		jrParameters.put("verticalLineImg", verticalLineImg);
		jrParameters.put("logoImageUrl", companyHeaderInfoMap.get("logoImageUrl"));
		jrParameters.put("companyName", companyHeaderInfoMap.get("companyName"));
		jrParameters.put("address1", companyHeaderInfoMap.get("address1"));
		jrParameters.put("address2", companyHeaderInfoMap.get("address2"));
		jrParameters.put("companyPostalAddr", companyHeaderInfoMap.get("companyPostalAddr"));
		jrParameters.put("postalCode", companyHeaderInfoMap.get("postalCode"));
		jrParameters.put("city", companyHeaderInfoMap.get("city"));
		jrParameters.put("stateProvinceAbbr", companyHeaderInfoMap.get("stateProvinceAbbr"));
		jrParameters.put("countryName", companyHeaderInfoMap.get("countryName"));
		jrParameters.put("telephoneNumber", companyHeaderInfoMap.get("telephoneNumber"));
		jrParameters.put("faxNumber", companyHeaderInfoMap.get("faxNumber"));
		jrParameters.put("email", companyHeaderInfoMap.get("email"));
		jrParameters.put("companyRegNo", companyHeaderInfoMap.get("companyRegNo"));
		jrParameters.put("vatRegNo", companyHeaderInfoMap.get("vatRegNo"));
		jrParameters.put("tpinNo", companyHeaderInfoMap.get("tpinNo"));
		
		request.setAttribute("jrDataSource", jrDataSource);
		request.setAttribute("jrParameters", jrParameters);

		org.ofbiz.entity.transaction.TransactionUtil.commit();
		return reportFormatType;
	}

	@SuppressWarnings("deprecation")
	public static String MyTotalPfReport(HttpServletRequest request, HttpServletResponse response) throws InterruptedException, GenericEntityException, ParseException {
		GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");

		String periodFrom = (String) request.getParameter("periodFrom");
		String periodTo = (String) request.getParameter("periodTo");

		DateFormat df = new SimpleDateFormat("dd-MM-yyyy");

		Date periodFm = null;
		Date periodT = null;

		String reportFormat = request.getParameter("reportFormat");
		String partyId = request.getParameter("partyId");

		EntityCondition condition = null;
		EntityCondition partyCond = null;
		EntityCondition fromCond = null;
		EntityCondition toCond = null;
		if (partyId != "") {
			partyCond = EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId);
			condition = partyCond;
		}
		if (periodFrom != "") {
			periodFm = df.parse(periodFrom.trim());
			fromCond = EntityCondition.makeCondition("periodFrom", EntityOperator.GREATER_THAN_EQUAL_TO, periodFm);
			if (condition != null)
				condition = EntityCondition.makeCondition(condition, fromCond);
			else {
				condition = fromCond;

			}
		}
		if (periodTo != "") {
			periodT = df.parse(periodTo.trim());
			toCond = EntityCondition.makeCondition("periodTo", EntityOperator.LESS_THAN_EQUAL_TO, periodT);
			if (condition != null)
				condition = EntityCondition.makeCondition(condition, toCond);
			else {
				condition = toCond;
			}
		}
		Set<String> fields = new HashSet<String>();
		fields.add("partyId");
		fields.add("employeeAmount");
		fields.add("epfDiff");
		fields.add("pensionAmount");
		fields.add("periodFrom");
		fields.add("periodTo");

		List<String> orderBy = new LinkedList<String>();
		orderBy.add("partyId");
		EntityListIterator tdsList = null;
		try {
			DynamicViewEntity oidve = new DynamicViewEntity();
			oidve.setEntityName("EmplPf");
			tdsList = delegator.find("EmplPf",condition,null,null,null,null);// delegator.findListIteratorByCondition(oidve, condition,null, fields, orderBy,null);
		} catch (GenericEntityException e) {
			ServiceUtil.returnError("Error finding PF for : " + partyId + " :" + e.getMessage());
		}

		String reportFormatType = null;
		if (reportFormat.equals("PDF"))
			reportFormatType = "pdf";
		else
			reportFormatType = "excel";
		org.ofbiz.entity.transaction.TransactionUtil.begin();

		JREntityListIteratorDataSource jrDataSource = new JREntityListIteratorDataSource(tdsList);
		
		Map<String,Object> companyHeaderInfoMap = PartyUtil.getCompanyHeaderInfo(delegator);
		String headOfficeImg = "./framework/images/webapp/images/head_office.png";
		String verticalLineImg = "./framework/images/webapp/images/vertical_line_bar.png";
		
		Map<String,Object> jrParameters = new HashMap<String,Object>();
		jrParameters.put("headOfficeImg", headOfficeImg);
		jrParameters.put("verticalLineImg", verticalLineImg);
		jrParameters.put("logoImageUrl", companyHeaderInfoMap.get("logoImageUrl"));
		jrParameters.put("companyName", companyHeaderInfoMap.get("companyName"));
		jrParameters.put("address1", companyHeaderInfoMap.get("address1"));
		jrParameters.put("address2", companyHeaderInfoMap.get("address2"));
		jrParameters.put("companyPostalAddr", companyHeaderInfoMap.get("companyPostalAddr"));
		jrParameters.put("postalCode", companyHeaderInfoMap.get("postalCode"));
		jrParameters.put("city", companyHeaderInfoMap.get("city"));
		jrParameters.put("stateProvinceAbbr", companyHeaderInfoMap.get("stateProvinceAbbr"));
		jrParameters.put("countryName", companyHeaderInfoMap.get("countryName"));
		jrParameters.put("telephoneNumber", companyHeaderInfoMap.get("telephoneNumber"));
		jrParameters.put("faxNumber", companyHeaderInfoMap.get("faxNumber"));
		jrParameters.put("email", companyHeaderInfoMap.get("email"));
		jrParameters.put("companyRegNo", companyHeaderInfoMap.get("companyRegNo"));
		jrParameters.put("vatRegNo", companyHeaderInfoMap.get("vatRegNo"));
		jrParameters.put("tpinNo", companyHeaderInfoMap.get("tpinNo"));
		
		request.setAttribute("jrDataSource", jrDataSource);
		request.setAttribute("jrParameters", jrParameters);

		org.ofbiz.entity.transaction.TransactionUtil.commit();
		return reportFormatType;
	}

	@SuppressWarnings({ "deprecation", "null" })
	public static String GenerateAppraisalReportsbyStatus(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException, ParseException {
		GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
		String employeeId = (String) request.getParameter("employeeId");
		String listboxStatusType = (String) request.getParameter("listboxStatusType");
		String reportFormat = request.getParameter("reportFormat");
		
		SimpleDateFormat format = new SimpleDateFormat(UtilDateTime.DATE_FORMAT);
		
		String fromDateStr = request.getParameter("fromDate");
		String thruDateStr = request.getParameter("thruDate");
		Date fromDate = null;
		Date thruDate = null;
		
		if(UtilValidate.isNotEmpty(fromDateStr))
			 fromDate = format.parse(fromDateStr);
		
		if(UtilValidate.isNotEmpty(thruDateStr))
			 thruDate = format.parse(thruDateStr);

		EntityCondition condition = null;
		List<EntityCondition> conList = new ArrayList<EntityCondition>();
		if (UtilValidate.isNotEmpty(employeeId)) {
			conList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, employeeId));
		}
		if (UtilValidate.isNotEmpty(listboxStatusType)) {
			EntityCondition statusItemCondition = EntityCondition.makeCondition(UtilMisc.toMap("description", listboxStatusType, "statusTypeId", "PERF_REVIEW_STATUS"));
			List<GenericValue> statusItemList = delegator.findList("StatusItem", statusItemCondition, null, null, null, false);
			String statusID = (String) statusItemList.get(0).get("statusId");
			conList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, statusID));
		}
		if(fromDate != null){
			conList.add(EntityCondition.makeCondition("periodThruDate",EntityOperator.GREATER_THAN_EQUAL_TO,fromDate));
		}
		if(thruDate != null){
			conList.add(EntityCondition.makeCondition("periodStartDate",EntityOperator.LESS_THAN_EQUAL_TO,thruDate));
		}

		String reportFormatType = null;
		if (reportFormat.equals("PDF"))
			reportFormatType = "pdf";
		else
			reportFormatType = "excel";
		
		condition = EntityCondition.makeCondition(conList,EntityOperator.AND);
		org.ofbiz.entity.transaction.TransactionUtil.begin();
		DynamicViewEntity oidve = new DynamicViewEntity();
		oidve.setEntityName("SearchEmplPerfReviewerView");
		EntityListIterator emplPerfReviewList = delegator.find("SearchEmplPerfReviewerView",condition,null,null,null,null);// delegator.findListIteratorByCondition(oidve, condition, null, null,null,null);
		JREntityListIteratorDataSource jrDataSource = new JREntityListIteratorDataSource(emplPerfReviewList);
		
		Map<String,Object> companyHeaderInfoMap = PartyUtil.getCompanyHeaderInfo(delegator);
		String headOfficeImg = "./framework/images/webapp/images/head_office.png";
		String verticalLineImg = "./framework/images/webapp/images/vertical_line_bar.png";
		
		Map<String,Object> jrParameters = new HashMap<String,Object>();
		jrParameters.put("headOfficeImg", headOfficeImg);
		jrParameters.put("verticalLineImg", verticalLineImg);
		jrParameters.put("logoImageUrl", companyHeaderInfoMap.get("logoImageUrl"));
		jrParameters.put("companyName", companyHeaderInfoMap.get("companyName"));
		jrParameters.put("address1", companyHeaderInfoMap.get("address1"));
		jrParameters.put("address2", companyHeaderInfoMap.get("address2"));
		jrParameters.put("companyPostalAddr", companyHeaderInfoMap.get("companyPostalAddr"));
		jrParameters.put("postalCode", companyHeaderInfoMap.get("postalCode"));
		jrParameters.put("city", companyHeaderInfoMap.get("city"));
		jrParameters.put("stateProvinceAbbr", companyHeaderInfoMap.get("stateProvinceAbbr"));
		jrParameters.put("countryName", companyHeaderInfoMap.get("countryName"));
		jrParameters.put("telephoneNumber", companyHeaderInfoMap.get("telephoneNumber"));
		jrParameters.put("faxNumber", companyHeaderInfoMap.get("faxNumber"));
		jrParameters.put("email", companyHeaderInfoMap.get("email"));
		jrParameters.put("companyRegNo", companyHeaderInfoMap.get("companyRegNo"));
		jrParameters.put("vatRegNo", companyHeaderInfoMap.get("vatRegNo"));
		jrParameters.put("tpinNo", companyHeaderInfoMap.get("tpinNo"));
		
		request.setAttribute("jrDataSource", jrDataSource);
		request.setAttribute("jrParameters", jrParameters);
		org.ofbiz.entity.transaction.TransactionUtil.commit();
		return reportFormatType;

	}

	@SuppressWarnings({ "deprecation", "null" })
	public static String GenerateAppraisalReportsbyDepartment(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException, ParseException {
		GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
		String employeeId = (String) request.getParameter("employeeId");
		String listboxPosition = (String) request.getParameter("listboxPosition");
		String reportFormat = request.getParameter("reportFormat");
		String listboxStatusType = (String) request.getParameter("listboxStatusType");
		
		SimpleDateFormat format = new SimpleDateFormat(UtilDateTime.DATE_FORMAT);
		
		String fromDateStr = request.getParameter("fromDate");
		String thruDateStr = request.getParameter("thruDate");
		Date fromDate = null;
		Date thruDate = null;
		
		if(UtilValidate.isNotEmpty(fromDateStr))
			 fromDate = format.parse(fromDateStr);
		
		if(UtilValidate.isNotEmpty(thruDateStr))
			 thruDate = format.parse(thruDateStr);

		EntityCondition condition = null;
		List<EntityCondition> conList = new ArrayList<EntityCondition>();
		
		if (UtilValidate.isNotEmpty(employeeId)) {
			conList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, employeeId));
		}
		
		if (UtilValidate.isNotEmpty(listboxPosition)) {
			conList.add(EntityCondition.makeCondition("description", EntityOperator.EQUALS, listboxPosition));
		}
		if(fromDate != null){
			conList.add(EntityCondition.makeCondition("periodThruDate",EntityOperator.GREATER_THAN_EQUAL_TO,fromDate));
		}
		if(thruDate != null){
			conList.add(EntityCondition.makeCondition("periodStartDate",EntityOperator.LESS_THAN_EQUAL_TO,thruDate));
		}
		
		if (UtilValidate.isNotEmpty(listboxStatusType)) {
			EntityCondition statusItemCondition = EntityCondition.makeCondition(UtilMisc.toMap("description", listboxStatusType, "statusTypeId", "PERF_REVIEW_STATUS"));
			List<GenericValue> statusItemList = delegator.findList("StatusItem", statusItemCondition, null, null, null, false);
			String statusID = (String) statusItemList.get(0).get("statusId");
			conList.add(EntityCondition.makeCondition("statusType", EntityOperator.EQUALS, statusID));
		}
		
		String reportFormatType = null;
		if (reportFormat.equals("PDF"))
			reportFormatType = "pdf";
		else
			reportFormatType = "excel";
		
		condition = EntityCondition.makeCondition(conList,EntityOperator.AND);
		org.ofbiz.entity.transaction.TransactionUtil.begin();
		DynamicViewEntity oidve = new DynamicViewEntity();
		oidve.setEntityName("EmplPerfRevieweByPosition");
		EntityListIterator emplPerfReviewList = delegator.find("EmplPerfRevieweByPosition",condition,null,null,null,null);// delegator.findListIteratorByCondition(oidve, condition, null, null,null,null);//EmplPerfRevieweByDepartment
		JREntityListIteratorDataSource jrDataSource = new JREntityListIteratorDataSource(emplPerfReviewList);
		
		Map<String,Object> companyHeaderInfoMap = PartyUtil.getCompanyHeaderInfo(delegator);
		String headOfficeImg = "./framework/images/webapp/images/head_office.png";
		String verticalLineImg = "./framework/images/webapp/images/vertical_line_bar.png";
		
		Map<String,Object> jrParameters = new HashMap<String,Object>();
		jrParameters.put("headOfficeImg", headOfficeImg);
		jrParameters.put("verticalLineImg", verticalLineImg);
		jrParameters.put("logoImageUrl", companyHeaderInfoMap.get("logoImageUrl"));
		jrParameters.put("companyName", companyHeaderInfoMap.get("companyName"));
		jrParameters.put("address1", companyHeaderInfoMap.get("address1"));
		jrParameters.put("address2", companyHeaderInfoMap.get("address2"));
		jrParameters.put("companyPostalAddr", companyHeaderInfoMap.get("companyPostalAddr"));
		jrParameters.put("postalCode", companyHeaderInfoMap.get("postalCode"));
		jrParameters.put("city", companyHeaderInfoMap.get("city"));
		jrParameters.put("stateProvinceAbbr", companyHeaderInfoMap.get("stateProvinceAbbr"));
		jrParameters.put("countryName", companyHeaderInfoMap.get("countryName"));
		jrParameters.put("telephoneNumber", companyHeaderInfoMap.get("telephoneNumber"));
		jrParameters.put("faxNumber", companyHeaderInfoMap.get("faxNumber"));
		jrParameters.put("email", companyHeaderInfoMap.get("email"));
		jrParameters.put("companyRegNo", companyHeaderInfoMap.get("companyRegNo"));
		jrParameters.put("vatRegNo", companyHeaderInfoMap.get("vatRegNo"));
		jrParameters.put("tpinNo", companyHeaderInfoMap.get("tpinNo"));
		
		request.setAttribute("jrDataSource", jrDataSource);
		request.setAttribute("jrParameters", jrParameters);
		org.ofbiz.entity.transaction.TransactionUtil.commit();
		return reportFormatType;

	}

	@SuppressWarnings("deprecation")
	public static String printPerformancePlan(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException, ParseException { 
		GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
		org.ofbiz.entity.transaction.TransactionUtil.begin();
		Object reviewId = request.getParameter("reviewId");
		EntityCondition condition = EntityCondition.makeCondition("emplPerfReviewId",EntityOperator.EQUALS,reviewId);
		DynamicViewEntity oidve = new DynamicViewEntity();
		oidve.setEntityName("PerfSectionAndAttributeForReviewer");
		EntityListIterator emplPerfReviewList =  delegator.find("PerfSectionAndAttributeForReviewer",condition,null,null,null,null);// delegator.findListIteratorByCondition(oidve, condition, null, null,null,null);
		JREntityListIteratorDataSource jrDataSource = new JREntityListIteratorDataSource(emplPerfReviewList);
		
		Map<String,Object> companyHeaderInfoMap = PartyUtil.getCompanyHeaderInfo(delegator);
		String headOfficeImg = "./framework/images/webapp/images/head_office.png";
		String verticalLineImg = "./framework/images/webapp/images/vertical_line_bar.png";
		
		Map<String,Object> jrParameters = new HashMap<String,Object>();
		jrParameters.put("headOfficeImg", headOfficeImg);
		jrParameters.put("verticalLineImg", verticalLineImg);
		jrParameters.put("logoImageUrl", companyHeaderInfoMap.get("logoImageUrl"));
		jrParameters.put("companyName", companyHeaderInfoMap.get("companyName"));
		jrParameters.put("address1", companyHeaderInfoMap.get("address1"));
		jrParameters.put("address2", companyHeaderInfoMap.get("address2"));
		jrParameters.put("companyPostalAddr", companyHeaderInfoMap.get("companyPostalAddr"));
		jrParameters.put("postalCode", companyHeaderInfoMap.get("postalCode"));
		jrParameters.put("city", companyHeaderInfoMap.get("city"));
		jrParameters.put("stateProvinceAbbr", companyHeaderInfoMap.get("stateProvinceAbbr"));
		jrParameters.put("countryName", companyHeaderInfoMap.get("countryName"));
		jrParameters.put("telephoneNumber", companyHeaderInfoMap.get("telephoneNumber"));
		jrParameters.put("faxNumber", companyHeaderInfoMap.get("faxNumber"));
		jrParameters.put("email", companyHeaderInfoMap.get("email"));
		jrParameters.put("companyRegNo", companyHeaderInfoMap.get("companyRegNo"));
		jrParameters.put("vatRegNo", companyHeaderInfoMap.get("vatRegNo"));
		jrParameters.put("tpinNo", companyHeaderInfoMap.get("tpinNo"));
		
		request.setAttribute("jrDataSource", jrDataSource);
		request.setAttribute("jrParameters", jrParameters);
		org.ofbiz.entity.transaction.TransactionUtil.commit();
		return "pdf";

	}

}
