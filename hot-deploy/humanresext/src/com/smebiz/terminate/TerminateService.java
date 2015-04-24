package com.smebiz.terminate;

import static org.ofbiz.humanresext.util.HumanResUtil.getEmailAddress;
import static org.ofbiz.humanresext.util.HumanResUtil.getReportingMangerForParty;

import java.sql.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.List;
import javolution.util.FastMap;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.entity.condition.*;
import org.ofbiz.humanresext.util.HumanResUtil;

public class TerminateService {

	public static final String module = TerminateService.class.getName();
	public static final String C_SAVED = "ET_SAVED";
	public static final String C_SUBMIT = "ET_SUBMITTED";
	public static final String C_ADM_REJECTED = "ET_ADM_REJECTED";

	public static Map<String, Object> createEmplTermination(DispatchContext dctx, Map<String, Object> context) {
		GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
		String lpartyId = (String) context.get("partyId");
		Date lapplicationDate = (Date) context.get("applicationDate");
		Date lterminationDate = (Date) context.get("terminationDate");
		Long lnoticePeriod = (Long) context.get("noticePeriod");
		String lmgrPositionId = (String) context.get("mgrPositionId");
		String lreason = (String) context.get("reason");
		String lcomment = (String) context.get("hr_comment");
		String lExitType = (String) context.get("exitType");
		Map<String, Object> termVals = FastMap.newInstance();
		termVals.putAll(UtilMisc.toMap("partyId", lpartyId, "applicationDate", lapplicationDate, "terminationDate", lterminationDate,
				"noticePeriod", lnoticePeriod, "mgrPositionId", lmgrPositionId, "reason", lreason, "exitType", lExitType,"lastUpdatedDate",UtilDateTime.nowTimestamp()));
		// termVals.put("hr_comment", lcomment);

		GenericValue etGV = delegator.makeValue("EmplTermination", termVals);
		String lterminationId = delegator.getNextSeqId("EmplTermination");
		etGV.set("terminationId", lterminationId);
		try {
			etGV.create();
		} catch (GenericEntityException e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError("An Error occurred creating the EmplTermination record" + e.getMessage());
		}

		/** Create the termination status record **/
		Map<String, Object> result = ServiceUtil.returnSuccess("Employee Termination created");
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Map<String, Object> ets = FastMap.newInstance();
		ets.putAll(UtilMisc.toMap("terminationId", lterminationId, "statusId", C_SAVED, "updatedBy", lpartyId, "hr_comment", lcomment));
		try {
			dispatcher.runSync("createEmplTerminationStatusService", ets);
		} catch (GenericServiceException e) {
			Debug.logError(e, module);
		}
		result.put("terminationId", lterminationId);
		return result;
	}

	public static Map<String, Object> createEmplTerminationStatus(DispatchContext dctx, Map<String, Object> context) {
		GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
		String lstatusId = (String) context.get("statusId");
		String lterminationId = (String) context.get("terminationId");
		String lupdatedBy = (String) context.get("updatedBy");
		String lcomment = (String) context.get("hr_comment");

		Map<String, Object> ets = FastMap.newInstance();
		ets.putAll(UtilMisc.toMap("terminationId", lterminationId, "statusId", lstatusId, "updatedBy", lupdatedBy, "hr_comment", lcomment));

		GenericValue etsGV = delegator.makeValue("EmplTerminationStatus", ets);
		String tsId = delegator.getNextSeqId("EmplTerminationStatus");
		etsGV.set("tsId", tsId);
		try {
			etsGV.create();
		} catch (GenericEntityException e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError("An Error occurred creating the Empl Termination Status record" + e.getMessage());
		}
		Map<String, Object> result = ServiceUtil.returnSuccess("Employee Termination Status created");
		// result.put("terminationId", lterminationId);
		result.put("tsId", tsId);
		return result;
	}

	public static Map<String, Object> getEmplTerminationComment(DispatchContext dctx, Map<String, Object> context) {
		GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
		String l_terminationId = (String) context.get("terminationId");
		String l_statusId = (String) context.get("statusId");

		if (l_terminationId == null) {
			return ServiceUtil.returnSuccess("Employee Termination Status searched. Its a new record so no comments yet.");
		}

		if (l_statusId == null) {
			return ServiceUtil.returnSuccess("Employee Termination Status searched. Its a new record so no comments yet.");
		}

		EntityCondition termCond = EntityCondition.makeCondition("terminationId", l_terminationId);
		EntityCondition statusCond = EntityCondition.makeCondition("statusId", l_statusId);
		EntityCondition andCond = EntityCondition.makeCondition(termCond, statusCond);

		List<GenericValue> data = null;

		try {
			data = delegator.findList("ELoanStatus", andCond, null, null, null, false);
		} catch (GenericEntityException e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError("An Error occurred searching the Termination Status record" + e.getMessage());
		}

		if (data.size() != 1) {
			return ServiceUtil.returnError("More/Less than 1 records found for same status and terminationId combination."
					+ l_terminationId + " " + l_statusId);
		}

		GenericValue etsGV = data.get(0);

		String l_comment = (String) etsGV.get("hr_comment");
		;
		String l_updatedBy = (String) etsGV.get("updatedBy");
		String l_tsId = (String) etsGV.get("tsId");

		Map<String, Object> result = ServiceUtil.returnSuccess("Employee Termination Status searched");
		result.putAll(UtilMisc.toMap("hr_comment", l_comment, "updatedBy", l_updatedBy, "tsId", l_tsId, "statusId", l_statusId));
		return result;
	}

	public static Map<String, Object> updateEmplTermination(DispatchContext dctx, Map<String, Object> context) {
		Map<String, Object> result = null;
		GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();

		String lterminationId = (String) context.get("terminationId");
		Map<String, Object> tId = FastMap.newInstance();
		tId.put("terminationId", lterminationId);
		String lpartyId = (String) context.get("partyId");

		GenericValue termGV = null;
		// GenericValue tsGV = null;
		try {
			termGV = delegator.findByPrimaryKey("EmplTermination", tId);
		} catch (GenericEntityException e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError("Problems finding the loan record [" + lterminationId + "]");
		}

		/** Check if the loan can be updated **/
		if (checkDuplicateTermination(dctx, lterminationId, C_SUBMIT)) {
			return ServiceUtil.returnError("Empl Termination : " + lterminationId + " has already been submitted.");
		}

		Date lapplicationDate = (Date) context.get("applicationDate");
		Date lterminationDate = (Date) context.get("terminationDate");
		Long lnoticePeriod = (Long) context.get("noticePeriod");
		String lmgrPositionId = (String) context.get("mgrPositionId");
		String lreason = (String) context.get("reason");
		// String lcomment = (String)context.get("hr_comment");
		Map<String, Object> termVals = FastMap.newInstance();
		termVals.putAll(UtilMisc.toMap("partyId", lpartyId, "applicationDate", lapplicationDate, "terminationDate", lterminationDate,
				"noticePeriod", lnoticePeriod, "mgrPositionId", lmgrPositionId, "reason", lreason));

		termGV.putAll(termVals);

		try {
			termGV.store();
		} catch (GenericEntityException e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError("Problems updating the termination record [" + lterminationId + "]" + e.getMessage());
		}
		result = ServiceUtil.returnSuccess("Employee Termination updated");
		result.put("terminationId", lterminationId);
		return result;
	}

	public static Map<String, Object> submitEmplTermination(DispatchContext dctx, Map<String, Object> context) throws GenericEntityException {
		GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
		String lterminationId = (String) context.get("terminationId");
		Map<String, Object> tId = FastMap.newInstance();
		tId.put("terminationId", lterminationId);
		String lpartyId = (String) context.get("partyId");
		
		String statusId=C_SUBMIT;
        String mgrPosId=(String) context.get("mgrPositionId");
        String employeeId=(String) context.get("partyId");
        GenericValue empPos=HumanResUtil.getEmplPositionForParty(employeeId, delegator);
    	String empPosId=empPos==null?null:empPos.getString("emplPositionId");
        if(mgrPosId.equals(empPosId))
        	statusId="ET_MGR_APPROVED";
        

		// GenericValue termGV = null;
		if (lterminationId == null) {
			return ServiceUtil.returnError("Termination Id is null, cannot submit.");
		}

		/** Check if the Termination exists **/
		GenericValue termGV = null;
		try {
			termGV = delegator.findByPrimaryKey("EmplTermination", tId);
			termGV.putAll(UtilMisc.toMap("terminationId", lterminationId, "submitStatus", context.get("submitStatus"),"managerStatusType",context.get("managerStatusType")));
			if(mgrPosId.equals(empPosId))
				termGV.put("statusType", "1");
		    delegator.store(termGV);
		} catch (GenericEntityException e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError("Problems finding the termination record [" + lterminationId
					+ "]. Please save the termination first if not already saved.");
		}

		/** Check if the termination has already been submitted **/
		if (checkDuplicateTermination(dctx, lterminationId, statusId)) {
			return ServiceUtil.returnError("Termination : " + lterminationId + " has already been submitted.");
		}

		LocalDispatcher dispatcher = dctx.getDispatcher();
		Map<String, Object> ets = FastMap.newInstance();
		ets.putAll(UtilMisc.toMap("terminationId", lterminationId, "statusId", statusId, "updatedBy", lpartyId));
		ets.put("hr_comment", context.get("hr_comment"));
		Map<String, Object> result = null;
		try {
			result = dispatcher.runSync("createEmplTerminationStatusService", ets);
		} catch (GenericServiceException e) {
			Debug.logError(e, module);
		}

		/** Send email notification to manager **/
		Map<String, Object> emailCtx = FastMap.newInstance();
		Date lapplicationDate = (Date) termGV.getDate("applicationDate");
		Date lterminationDate = (Date) termGV.getDate("terminationDate");
		emailCtx.putAll(UtilMisc.toMap("partyId", lpartyId, "applicationDate", lapplicationDate, "terminationDate", lterminationDate));
		try {
			dispatcher.runAsync("notifyTerminationServcie", emailCtx);
		} catch (GenericServiceException e) {
			e.printStackTrace();
		}

		result = ServiceUtil.returnSuccess("Employee Termination submitted");
		return result;
	}

	public static Map<String, Object> processEmplTermination(DispatchContext dctx, Map<String, Object> context) {
		GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
		String lterminationId = (String) context.get("terminationId");
		String lpartyId = (String) context.get("updatedBy");
		String l_updatedBy = lpartyId;
		GenericValue termGV = null;
		if (lterminationId == null) {
			return ServiceUtil.returnError("Termination Id is null, cannot process.");
		}

		/** Check if the Termination exists **/
		Map<String, Object> tId = FastMap.newInstance();
		tId.put("terminationId", lterminationId);
		try {
			termGV = delegator.findByPrimaryKey("EmplTermination", tId);
		} catch (GenericEntityException e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError("Problems finding the termination record [" + lterminationId + "].");
		}

		String l_statusId = (String) context.get("statusId");
		/** Check duplicate processing **/
		if (checkDuplicateTermination(dctx, lterminationId, l_statusId)) {
			return ServiceUtil.returnError("Termination : " + lterminationId + " has already been processed with status." + l_statusId);
		}
		// Date lapplicationDate = (Date)context.get("applicationDate");
		Date lterminationDate = (Date) context.get("terminationDate");
		Long lnoticePeriod = (Long) context.get("noticePeriod");
		Double lunusedLeaves = (Double) context.get("unusedLeaves");
		Double lencashLeaves = (Double) context.get("encashLeaves");
		String l_comment = ((String) context.get("hr_comment"));
		String lsettlementType = (String) context.get("settlementType");
		String lstatusType = (String) context.get("statusType");
		String ladminComment = (String) context.get("adminComment");
		String lsubmitStatus = (String) context.get("submitStatus");
		String lmanagerComment = (String) context.get("managerComment");
		String lmanagerStatusType = (String) context.get("managerStatusType");
		if (l_statusId == null) {
			return ServiceUtil.returnError("l_statusId is null, cannot process.");
		}

		Map<String, Object> termVals = FastMap.newInstance();
		termVals.putAll(UtilMisc.toMap("terminationDate", lterminationDate, "noticePeriod", lnoticePeriod, "unusedLeaves", lunusedLeaves,
				"encashLeaves", lencashLeaves, "settlementType", lsettlementType, "statusType", lstatusType, "adminComment", ladminComment,
				"submitStatus", lsubmitStatus, "managerComment", lmanagerComment,"managerStatusType",lmanagerStatusType));

		termGV.putAll(termVals);

		try {
			termGV.store();
		} catch (GenericEntityException e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError("Problems processing the termination record [" + lterminationId + "]" + e.getMessage());
		}

		LocalDispatcher dispatcher = dctx.getDispatcher();
		Map<String, Object> ets = FastMap.newInstance();
		ets.putAll(UtilMisc.toMap("terminationId", lterminationId, "statusId", l_statusId, "updatedBy", l_updatedBy));
		ets.put("hr_comment", l_comment);
		Map<String, Object> result = null;
		try {
			dispatcher.runSync("createEmplTerminationStatusService", ets);
		} catch (GenericServiceException e) {
			Debug.logError(e, module);
		}
		result = ServiceUtil.returnSuccess("Employee Termination processed");
		result.put("terminationId", lterminationId);
		return result;
	}

	private static boolean checkDuplicateTermination(DispatchContext dctx, String v_terminationId, String v_statusId) {
		GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
		/* Check if the loan has not been already submitted * */
		EntityCondition termCond = EntityCondition.makeCondition("terminationId", v_terminationId);
		EntityCondition statusCond = EntityCondition.makeCondition("statusId", v_statusId);
		EntityCondition andCond = EntityCondition.makeCondition(termCond, statusCond);
		Iterator<GenericValue> listTermStatus = null;
		try {
			listTermStatus = delegator.find("EmplTerminationStatus", andCond, null, null, null, null);
		} catch (GenericEntityException e) {
			Debug.logError(e, module);
			return true; // By default duplicates exist
		}

		if (listTermStatus.next() != null)
			return true;

		return false;
	}

	/**
	 * Notify the manager and HR about Initiation of Termination
	 * 
	 * @author pankaj
	 * @date Jul-20-2009
	 * @param dctx
	 * @param context
	 * @return
	 */
	public static Map<String, Object> notifyTermination(DispatchContext dctx, Map<String, ? extends Object> context) {
		String lpartyId = (String) context.get("partyId");
		Date lapplicationDate = (Date) context.get("applicationDate");
		Date lterminationDate = (Date) context.get("terminationDate");
		GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
		String emailOfApplicant = null, emailOfApprover = null;
		try {
			GenericValue party = delegator.findOne("Party", false, "partyId", lpartyId);
			emailOfApplicant = getEmailAddress(party);
			emailOfApprover = getEmailAddress(getReportingMangerForParty(lpartyId, delegator));
		} catch (GenericEntityException e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError("An Error occurred notifiying the termination application" + e.getMessage());
		}

		Map<String, Object> emailContext = new HashMap<String, Object>();

		emailContext.put("sendFrom", emailOfApplicant);
		emailContext.put("sendCc", emailOfApplicant);
		/* Need to change it * */
		// emailContext.put("sendVia",
		// UtilProperties.getPropertyValue("LeaveManagement.proprties",
		// "sendVia"));
		emailContext.put("sendTo", emailOfApprover);
		emailContext.put("subject", "Resignation application for " + lpartyId);

		String body = "Initiation of Service Termination done by employee :" + lpartyId + "<br> on date : " + lapplicationDate
				+ ".<br> The termination date applied is :" + lterminationDate;
		emailContext.put("body", body);
		emailContext.put("userLogin", context.get("userLogin"));

		LocalDispatcher dispatcher = dctx.getDispatcher();
		try {
			dispatcher.runAsync("sendMail", emailContext);
		} catch (GenericServiceException e) {
			e.printStackTrace();
		}
		return ServiceUtil.returnSuccess();
	}

}
