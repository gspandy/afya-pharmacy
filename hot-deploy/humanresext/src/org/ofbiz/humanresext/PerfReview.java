package org.ofbiz.humanresext;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import javolution.util.FastList;
import javolution.util.FastSet;

import org.apache.commons.lang.StringUtils;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.humanresext.util.HumanResUtil;

public class PerfReview {

	public static final String module = PerfReview.class.getName();

	public static final String PERF_REVIEW_KEY = "PERF_REVIEW_DATAMAP";
	public static final String resource_error = "HumanResErrorUiLabels";

	public static String createPerfReview(HttpServletRequest request,
			HttpServletResponse response) {

		Map parameters = UtilHttp.getParameterMap(request);

		GenericDelegator delegator = (GenericDelegator) request
				.getAttribute("delegator");

		GenericValue userLogin = (GenericValue) request.getSession()
				.getAttribute("userLogin");
		String perfReviewStartDateStr = (String) parameters
				.get("perfReviewStartDate");
		Timestamp perfReviewStartDate = toTimestamp(perfReviewStartDateStr);
		String perfReviewEndDateStr = (String) parameters
				.get("perfReviewEndDate");
		Timestamp perfReviewEndDate = toTimestamp(perfReviewEndDateStr);
		String partyId = (String) userLogin.getString("partyId");

		String periodStartDateStr = request.getParameter("periodStartDate");
		Timestamp periodStartDate = toTimestamp(periodStartDateStr);
		
		String periodThruDateStr = request.getParameter("periodThruDate");
		Timestamp periodThruDate = toTimestamp(periodThruDateStr);
		
		String perfReviewId = delegator.getNextSeqId("PerfReviews");
		
		Map<String, Object> dataMap = UtilMisc.toMap("perfReviewId",perfReviewId, "enabledForAll", parameters
				.get("enabledForAll"), "perfReviewType", parameters
				.get("perfReviewType"), "perfReviewStartDate",
				perfReviewStartDate, "perfReviewEndDate", perfReviewEndDate,
				"initiatedBy", partyId,"periodStartDate",periodStartDate,"periodThruDate",periodThruDate);

		request.getSession().setAttribute(PERF_REVIEW_KEY, dataMap);
		return "success";
	}

	public static String associateTemplates(HttpServletRequest request,
			HttpServletResponse response) {

		Map parameters = UtilHttp.getParameterMap(request);

		Set<Entry<String, String>> entries = parameters.entrySet();

		List<Map> assocTemplateList = FastList.newInstance();
		Map perfReviewDataMap = (Map) request.getSession().getAttribute(
				PERF_REVIEW_KEY);
		String perfReviewId = (String) perfReviewDataMap.get("perfReviewId");

		Set<String> positions = FastSet.newInstance();

		for (Entry<String, String> entry : entries) {
			String key = entry.getKey();
			if (key.startsWith("position_")) {
				String value = entry.getValue();
				key = key.substring(9);
				if (StringUtils.isBlank(value)) {
					positions.add(key);
					continue;
				}
				assocTemplateList.add(UtilMisc.toMap("emplPositionTypeId", key,
						"perfTemplateId", value, "perfReviewId", perfReviewId));
			}
		}

		GenericDelegator delegator = (GenericDelegator) request
				.getAttribute("delegator");

		if (positions.size() > 0) {
			StringBuilder buffer = new StringBuilder();
			int i = 1;
			for (String positionId : positions) {
				buffer.append(i).append(".").append(
						HumanResUtil.getPositionDescription(delegator,
								positionId)).append("<br/>");
				i++;
			}
			request.setAttribute("_ERROR_MESSAGE_",
					"Please Associate Template for Employee Position. <br/>"
							+ buffer.toString());
			// UtilProperties.getMessage(resource_error,"OrderErrorProcessingOfflinePayments",
			// locale));
			return "error";
		} else {
			request.getSession().setAttribute("PERF_ASSOC_DATA",
					assocTemplateList);
		}

		return "success";
	}
	
	public static String createAnnouncement(HttpServletRequest request,
			HttpServletResponse response) {
		
		String announcement = request.getParameter("announcement");
		if(StringUtils.isNotBlank(announcement)){
			request.getSession().setAttribute("PERF_ANNOUNCEMENT",announcement);
		}else{
			request.setAttribute("_ERROR_MESSAGE_",
					" Announcement cannot be blank.");
			return "error";
		}
		return "success";
	}

	public static String initiatePerfReview(HttpServletRequest request,
			HttpServletResponse response) {

		HttpSession session = request.getSession();

		GenericDelegator delegator = (GenericDelegator) request
				.getAttribute("delegator");

		Map perfDataMap = (Map) session.getAttribute(PERF_REVIEW_KEY);
		
		List<Map> assocTemplateList = (List)session.getAttribute("PERF_ASSOC_DATA");
		
		String announcement = (String)request.getSession().getAttribute("PERF_ANNOUNCEMENT");
		
		boolean beganTransaction = false;
		try {
			beganTransaction = TransactionUtil.begin();
			
			delegator.create("PerfReviews", perfDataMap);
			
			for(Map templateMap: assocTemplateList){
				delegator.create("PerfReviewAssocTemplates",templateMap);
			}
			
			
			GenericValue userLogin = (GenericValue) request.getSession()
			.getAttribute("userLogin");
			String partyId = (String) userLogin.getString("partyId");
			String announcementId = delegator.getNextSeqId("Announcement");
			Map announcementMap = UtilMisc.toMap("announcementId",announcementId,"announcement",announcement,"fromPartyId",partyId,"toPartyId","_NA_");
			delegator.create("Announcement",announcementMap);
			
		} catch (GenericEntityException e) {
			try {
				// only rollback the transaction if we started one...
				TransactionUtil.rollback(beganTransaction,
						"Error saving abandoned cart info", e);
			} catch (GenericEntityException e2) {
				Debug.logError(e2, "Could not rollback transaction: "
						+ e2.toString(), module);
			}
		} finally {
			try {
				TransactionUtil.commit(beganTransaction);
			} catch (GenericEntityException e) {
				Debug
						.logError(
								e,
								"Could not commit transaction for entity engine error occurred while saving abandoned cart information",
								module);
			}
		}
		request.setAttribute("_SUCCESS_MESSAGE_", "Successfully initiated.");
		return "success";
	}

	public static Timestamp toTimestamp(String dateTimeStr) {
		Timestamp timestamp = null;
		if (dateTimeStr != null && dateTimeStr.length() > 0) {
			if (dateTimeStr.length() == 10)
				dateTimeStr += " 00:00:00.000";
			try {
				timestamp = java.sql.Timestamp.valueOf(dateTimeStr);
			} catch (IllegalArgumentException e) {
				Debug.logWarning(e, "Bad StartDate input: " + e.getMessage(),
						module);
				timestamp = null;
			}
		}
		return timestamp;
	}

	public static String setPerfReviewStatus(HttpServletRequest request,
			HttpServletResponse response) {
		Timestamp startDate = new Timestamp(Calendar.getInstance()
				.getTimeInMillis());

		EntityExpr condition1 = EntityCondition.makeCondition("perfReviewStartDate",
				EntityOperator.LESS_THAN, startDate);

		EntityExpr condition2 = EntityCondition.makeCondition("perfReviewEndDate",
				EntityOperator.GREATER_THAN, startDate);

		List<EntityExpr> conditionList = new ArrayList<EntityExpr>(2);
		conditionList.add(condition1);
		conditionList.add(condition2);

		EntityCondition entityCondition = EntityCondition
				.makeCondition(conditionList);

		GenericDelegator delegator = (GenericDelegator) request
				.getAttribute("delegator");
		List<GenericValue> reviews = null;
		try {
			reviews = delegator.findList("PerfReviews", entityCondition, null,
					UtilMisc.toList("-perfReviewStartDate"), null, false);
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (reviews != null && reviews.size() == 0) {
			request.getSession().setAttribute("PERF_REVIEW_ENABLED", false);
		} else {
			request.getSession().setAttribute("PERF_REVIEW_ENABLED", true);
			request.getSession().setAttribute("PERF_REVIEW",reviews.get(0));
				request.getSession().setAttribute("PERF_REVIEW_ID",
					reviews.get(0).getString("perfReviewId"));
		}
		return "success";
	}
}
