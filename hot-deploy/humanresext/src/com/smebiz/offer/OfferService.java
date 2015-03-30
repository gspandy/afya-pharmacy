package com.smebiz.offer;

import static org.ofbiz.humanresext.util.HumanResUtil.getEmailAddress;
import static org.ofbiz.humanresext.util.HumanResUtil.getReportingMangerForParty;

import java.sql.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import javolution.util.FastList;
import javolution.util.FastMap;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.EntityUtil;

import com.nthdimenzion.humanres.payroll.SalaryBean;
import com.nthdimenzion.humanres.payroll.SalaryComponentBean;

public class OfferService {

	public static final String module = OfferService.class.getName();
	public static final String C_SAVED = "OF_GENERATED";
	public static final String C_JOINED = "OF_ACCEPT_JOINED";

	public static Map<String, Object> createOffer(DispatchContext dctx, Map<String, Object> context) {
		GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
		String lupdatedBy = (String) ((GenericValue) context.get("userLogin")).get("partyId");
		String lpartyId = (String) context.get("partyId");
		String lapplicationId = (String) context.get("applicationId");
		Date lofferDate = (Date) context.get("offerDate");
		Date ljoiningDate = (Date) context.get("joiningDate");
		String lmgrPositionId = (String) context.get("mgrPositionId");
		Date lactualJoiningDate = (Date) context.get("actualJoiningDate");
		String lcomment = (String) context.get("hr_comment");
		String requisitionId = (String)context.get("requisitionId");
		Map<String, Object> offerVals = FastMap.newInstance();
		offerVals.putAll(UtilMisc.toMap("partyId", lpartyId, "applicationId", lapplicationId, "offerDate", lofferDate, "joiningDate",
				ljoiningDate, "mgrPositionId", lmgrPositionId,"requsitionId",requisitionId));
		offerVals.put("actualJoiningDate", lactualJoiningDate);
		GenericValue ofGV = delegator.makeValue("OfferHead", offerVals);
		String lofferId = delegator.getNextSeqId("OfferHead");
		ofGV.set("offerId", lofferId);
		try {
			ofGV.create();
		} catch (GenericEntityException e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError("An Error occurred creating the Offer record" + e.getMessage());
		}

		/** Create the offer status record **/
		Map<String, Object> result = ServiceUtil.returnSuccess("Offer created");
//		LocalDispatcher dispatcher = dctx.getDispatcher();
//		Map<String, Object> os = FastMap.newInstance();
//		os.putAll(UtilMisc.toMap("offerId", lofferId, "statusId", C_SAVED, "updatedBy", lupdatedBy, "hr_comment", lcomment));
//		try {
//			result = dispatcher.runSync("createOfferStatusService", os);
//		} catch (GenericServiceException e) {
//			Debug.logError(e, module);
//		}
		result.put("offerId", lofferId);
		return result;
	}

	public static Map<String, Object> createOfferStatus(DispatchContext dctx, Map<String, Object> context) {
		GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
		String lstatusId = (String) context.get("statusId");
		String lofferId = (String) context.get("offerId");
		String lupdatedBy = (String) context.get("updatedBy");
		String lcomment = (String) context.get("hr_comment");
		String requisitionId = (String) context.get("requisitionId");

		Map<String, Object> os = FastMap.newInstance();
		os.putAll(UtilMisc.toMap("offerId", lofferId, "statusId", lstatusId, "updatedBy", lupdatedBy, "hr_comment", lcomment,"requisitionId",requisitionId));

		GenericValue osGV = delegator.makeValue("OfferStatus", os);
		String osId = delegator.getNextSeqId("OfferStatus");
		osGV.set("osId", osId);
		try {
			osGV.create();
		} catch (GenericEntityException e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError("An Error occurred creating the Offer Status record" + e.getMessage());
		}
		Map<String, Object> result = ServiceUtil.returnSuccess("Offer Status created");
		// result.put("terminationId", lterminationId);
		result.put("osId", osId);
		return result;
	}

	public static Map<String, Object> getOfferComment(DispatchContext dctx, Map<String, Object> context) {
		GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
		String l_offerId = (String) context.get("offerId");
		String l_statusId = (String) context.get("statusId");

		if (l_offerId == null) {
			return ServiceUtil.returnSuccess("Employee Termination Status searched. Its a new record so no comments yet.");
		}

		if (l_statusId == null) {
			return ServiceUtil.returnSuccess("Employee Termination Status searched. Its a new record so no comments yet.");
		}

		EntityCondition termCond = EntityCondition.makeCondition("offerId", l_offerId);
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
			return ServiceUtil.returnError("More/Less than 1 records found for same status and offerId combination." + l_offerId + " "
					+ l_statusId);
		}

		GenericValue osGV = data.get(0);

		String l_comment = (String) osGV.get("hr_comment");
		;
		String l_updatedBy = (String) osGV.get("updatedBy");
		String l_tsId = (String) osGV.get("tsId");

		Map<String, Object> result = ServiceUtil.returnSuccess("Employee Termination Status searched");
		result.putAll(UtilMisc.toMap("hr_comment", l_comment, "updatedBy", l_updatedBy, "tsId", l_tsId, "statusId", l_statusId));
		return result;
	}

	public static Map<String, Object> updateOffer(DispatchContext dctx, Map<String, Object> context) {
		GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
		String lupdatedBy = (String) ((GenericValue) context.get("userLogin")).get("partyId");
		String lofferId = (String) context.get("offerId");
		Map<String, Object> oId = FastMap.newInstance();
		oId.put("offerId", lofferId);
		String lpartyId = (String) context.get("partyId");

		GenericValue ofGV = null;
		// GenericValue tsGV = null;
		try {
			ofGV = delegator.findByPrimaryKey("OfferHead", oId);
		} catch (GenericEntityException e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError("Problems finding the offer record [" + lofferId + "]");
		}

		String lapplicationId = (String) context.get("applicationId");
		Date lofferDate = (Date) context.get("offerDate");
		Date ljoiningDate = (Date) context.get("joiningDate");
		String lmgrPositionId = (String) context.get("mgrPositionId");
		Date lactualJoiningDate = (Date) context.get("actualJoiningDate");
		String lcomment = (String) context.get("hr_comment");
		String lstatusId = (String) context.get("statusId");
		Map<String, Object> offerVals = FastMap.newInstance();
		offerVals.putAll(UtilMisc.toMap("partyId", lpartyId, "applicationId", lapplicationId, "offerDate", lofferDate, "joiningDate",
				ljoiningDate, "mgrPositionId", lmgrPositionId, "hr_comment", lcomment));
		offerVals.put("actualJoiningDate", lactualJoiningDate);

		try {
			ofGV.store();
		} catch (GenericEntityException e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError("Problems updating the offer record [" + lofferId + "]" + e.getMessage());
		}

		/** Create the offer status record **/
		Map<String, Object> result = ServiceUtil.returnSuccess("Offer created");
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Map<String, Object> os = FastMap.newInstance();
		os.putAll(UtilMisc.toMap("offerId", lofferId, "statusId", lstatusId, "updatedBy", lupdatedBy, "hr_comment", lcomment));

		try {
			result = dispatcher.runSync("createOfferStatusService", os);
		} catch (GenericServiceException e) {
			Debug.logError(e, module);
		}

		result = ServiceUtil.returnSuccess("Offer updated");
		result.put("offerId", lofferId);
		return result;
	}

	public static Map<String, Object> processOffer(DispatchContext dctx, Map<String, Object> context) throws GenericEntityException {
		GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
		String lofferId = (String) context.get("offerId");
		String lstatusId = (String) context.get("statusId");
		String lcomment = (String) context.get("hr_comment");

		java.util.Date ljoiningDate = (java.util.Date) context.get("joiningDate");
		Date joiningDate = null; 
		if(ljoiningDate != null)
		joiningDate = new Date(ljoiningDate.getTime());
		

		
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String l_updatedBy = (String) userLogin.get("partyId");
		GenericValue ofGV = null;
		if (lofferId == null) {
			return ServiceUtil.returnError("Offer Id is null, cannot process.");
		}

		/** Check if the Offer exists **/
		Map<String, Object> oId = FastMap.newInstance();
		oId.put("offerId", lofferId);
		try {
			ofGV = delegator.findByPrimaryKey("OfferHead", oId);
			GenericValue offH = delegator.makeValue("OfferHead",UtilMisc.toMap("offerId", ofGV.getString("offerId"),"joiningDate",joiningDate));
			delegator.store(offH);
		} catch (GenericEntityException e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError("Problems finding the offer record [" + lofferId + "].");
		}

		if (lstatusId == null) {
			return ServiceUtil.returnError("statusId is null, cannot process.");
		}
		String reqId = new String();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		EntityCondition cn1 = EntityCondition.makeCondition("offerId",EntityOperator.EQUALS,lofferId);
		EntityCondition cn2 = EntityCondition.makeCondition("requisitionId",EntityOperator.NOT_EQUAL,null);
		List<GenericValue> offerStatusList = delegator.findList("OfferStatus", EntityCondition.makeCondition(cn1,cn2), null, null, null, false);
		GenericValue offerStatusGv = EntityUtil.getFirst(offerStatusList);
		Map<String, Object> os = FastMap.newInstance();
		os.putAll(UtilMisc.toMap("offerId", lofferId, "statusId", lstatusId, "updatedBy", l_updatedBy));
		os.put("hr_comment", lcomment);
		if(offerStatusGv != null){
			reqId = offerStatusGv.getString("requisitionId");
			os.put("requisitionId", reqId);
		}
		//os.put("joiningDate",joiningDate);
		Map<String, Object> result = ServiceUtil.returnError("Before calling create Status service");
		try {
			result = dispatcher.runSync("createOfferStatusService", os);
		} catch (GenericServiceException e) {
			Debug.logError(e, module);
		}
		return result;
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
		Date lofferDate = (Date) context.get("offerDate");
		GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
		String emailOfApplicant = null, emailOfApprover = null;
		try {
			GenericValue party = delegator.findOne("Party", false, "partyId", lpartyId);
			emailOfApplicant = getEmailAddress(party);
			emailOfApprover = getEmailAddress(getReportingMangerForParty(lpartyId, delegator));
		} catch (GenericEntityException e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError("An Error occurred notifiying the offer application" + e.getMessage());
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
				+ ".<br> The offer date applied is :" + lofferDate;
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

	public static Map<String, Object> createOfferCTC(DispatchContext dctx, Map<String, Object> context) throws GenericEntityException {
		GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
		String partyId = (String) context.get("partyId");
		String offerId = (String) context.get("offerId");
		SalaryBean salary = (SalaryBean) context.get("salary");
		java.util.Date tfromDate = salary.getFromDate();
		Date lfromDate = null;
		if (tfromDate != null) {
			lfromDate = (Date) (new java.sql.Date(tfromDate.getTime()));
		}
		Debug.logInfo(" createOfferCTC : fromDate :" + lfromDate, module);

		List<GenericValue> offerSalHeads = FastList.newInstance();

		for (SalaryComponentBean bean : salary.getAllComponents()) {
			Map<String, Object> argMap = new HashMap<String, Object>();
			argMap.put("partyId", partyId);
			argMap.put("offerId", offerId);
			argMap.put("salaryHeadId", bean.getSalaryHeadId());
			argMap.put("amount", bean.getAmount());
			argMap.put("fromDate", bean.getFromDate());
			System.out.println("@@@ CREATE @@@@ : salaryHeadId :" + bean.getSalaryHeadId() + "  amount " + bean.getAmount());
			GenericValue gv = delegator.makeValue("OfferSal", argMap);
			offerSalHeads.add(delegator.createOrStore(gv));
		}

		Map<String, Object> result = ServiceUtil.returnSuccess("Offer CTC created for offer: " + offerId);

		return result;
	}

}
