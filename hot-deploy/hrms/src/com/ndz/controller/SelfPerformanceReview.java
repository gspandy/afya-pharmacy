package com.ndz.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.humanresext.PerfReviewStatus;
import org.ofbiz.humanresext.perfReview.ManagerNotFoundException;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.python.antlr.ast.Str;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

public class SelfPerformanceReview {
	public static String initiateSelfAppraisal(HttpServletRequest request,
			HttpServletResponse response) throws GenericEntityException {
		LocalDispatcher dispatcher = (LocalDispatcher) request
				.getAttribute("dispatcher");
		HttpSession session = request.getSession();
		Map<String, Object> context = FastMap.newInstance();
		GenericValue userLogin = (GenericValue) session
				.getAttribute("userLogin");
		String partyId = userLogin.getString("partyId");
		context.put("partyId", partyId);
		context.put("perfReviewId", session.getAttribute("PERF_REVIEW_ID"));
		context.put("statusType", PerfReviewStatus.PERF_IN_PROGRESS.toString());
		context.put("userLogin", userLogin);
		context.put("request", request);
		Map<String, Object> resultMap = null;
		try {
			resultMap = dispatcher.runSync("createEmplPerfReview", context);
		} catch (Exception e) {

		}
		if ("error".equals(resultMap.get("responseMessage"))) {
			request.setAttribute("_ERROR_MESSAGE_",
					"User does not have a Valid Position.");
			return "error";
		}

		GenericValue emplPerfReview = (GenericValue) resultMap
				.get("emplPerfReview");
		session.setAttribute("emplPerfReview", emplPerfReview);
		request.setAttribute("reviewId", resultMap.get("emplPerfReviewId"));
		populateSelfAppraisal(request, response);
		return "success";
	}

	private static Map createFormData(List<GenericValue> perfValues,
			boolean isReviewData) {
		List<Map<String, Object>> sections = FastList.newInstance();
		Map<String, Object> sectionMap = FastMap.newInstance();
		for (GenericValue entity : perfValues) {

			String sectionId = entity.getString("perfReviewSectionId");
			Map<String, Object> sectionmap = (Map<String, Object>) sectionMap
					.get(sectionId);
			if (sectionmap == null) {
				sectionmap = new HashMap<String, Object>();
				sectionmap.put("sectionId", sectionId);
				sectionmap.put("sectionName", entity.getString("sectionName"));
				sectionmap.put("description", entity.getString("sectionDesc"));
				sectionmap.put("weightage", entity.getString("weightage"));
                Map<String,Object> map = new HashMap<String, Object>();
                map.put("sectionId", sectionId);
                map.put("sectionName", (String) sectionmap.get("sectionName"));
                map.put("description", (String) sectionmap.get("description"));
                map.put("weightage", (String) sectionmap.get("weightage"));
                sections.add(map);
				/*sections.add(UtilMisc.toMap("sectionId", sectionId,
						"sectionName", (String) sectionmap.get("sectionName"),
						"description", (String) sectionmap.get("description"),
						"weightage", (String) sectionmap.get("weightage")));*/
				sectionMap.put(sectionId, sectionmap);
			}

			List<Map<String, String>> attributeList = (List<Map<String, String>>) sectionmap
					.get("attributeList");
			if (attributeList == null) {
				attributeList = FastList.newInstance();
			}

			String attributeId = entity.getString("attributeId");
			if (attributeId != null) {
				if ("_NA_".equals(attributeId)) {
					sectionmap.put("selfComment", entity
							.getString("selfComment"));
					sectionmap
							.put("selfRating", entity.getString("selfRating"));
					sectionmap.put("rating", entity.getString("rating"));
					if (isReviewData) {
						sectionmap.put("reviewerComment", entity
								.getString("reviewerComment"));
					}
					continue;
				}
				Map<String, String> attributeMap = new HashMap<String, String>();
				attributeMap.put("attributeId", attributeId);
				attributeMap.put("attributeName", entity
						.getString("attributeName"));
				attributeMap.put("attributeDesc", entity
						.getString("attributeDesc"));
				attributeMap.put("selfRating", entity.getString("selfRating"));
				attributeMap
						.put("selfComment", entity.getString("selfComment"));
				if (isReviewData) {
					attributeMap.put("rating", entity.getString("rating"));
					attributeMap.put("reviewerComment", entity
							.getString("reviewerComment"));
				}
				attributeList.add(attributeMap);
				sectionmap.put("attributeList", attributeList);
			}
		}
		sectionMap.put("perfSections", sections);
		return sectionMap;
	}

	public static String populateSelfAppraisal(HttpServletRequest request,
			HttpServletResponse response) throws GenericEntityException {

		HttpSession session = request.getSession();

		String reviewId = request.getParameter("reviewId");
		if (reviewId == null) {
			reviewId = (String) request.getAttribute("reviewId");
		}
		GenericDelegator delegator = (GenericDelegator) request
				.getAttribute("delegator");

		GenericValue userLogin = (GenericValue) session
				.getAttribute("userLogin");
		String partyId = userLogin.getString("partyId");

		GenericValue emplPerfReview = (GenericValue) session
				.getAttribute("emplPerfReview");
		if (emplPerfReview == null) {
			EntityCondition condition = EntityCondition.makeCondition(UtilMisc
					.toMap("perfReviewId", reviewId));
			emplPerfReview = delegator.findOne("EmplPerfReview", UtilMisc
					.toMap("emplPerfReviewId", reviewId), false);
			session.setAttribute("emplPerfReview", emplPerfReview);
		}

		if (!partyId.equals(emplPerfReview.getString("partyId"))) {
			partyId = emplPerfReview.getString("partyId");
		}

		Map<String, Object> sectionMap = null;
		String reviewerId = request.getParameter("reviewerId");
		// If the Reivew is reviewed by Manager
		if (PerfReviewStatus.PERF_REVIEWED_BY_MGR.toString().equals(
				emplPerfReview.getString("statusType"))
				|| PerfReviewStatus.PERF_REVIEW_AGREED.toString().equals(
						emplPerfReview.getString("statusType"))
				|| PerfReviewStatus.PERF_REVIEW_COMPLETE.toString().equals(
						emplPerfReview.getString("statusType"))) {
			List<String> reviewers = null;
			try {
				reviewers = org.ofbiz.humanresext.perfReview.PerfReviewWorker
						.getReviewers(request, reviewId);
			} catch (ManagerNotFoundException mnfe) {
				return "error";
			}
			request.setAttribute("reviewers", reviewers);
			if (reviewerId == null && reviewers != null) {
				reviewerId = reviewers.get(0);
				request.setAttribute("reviewerId", reviewerId);
			} else {
				// No Reviewer Found
				request.setAttribute("reviewerId", "_NA_");
			}
			EntityCondition entityCondition = EntityCondition
					.makeCondition(UtilMisc.toMap("emplPerfReviewId", reviewId,
							"reviewerId", reviewerId));
			List<GenericValue> perfValues = delegator.findList(
					"PerfSectionAndAttributeForReviewer", entityCondition,
					null, null, null, false);
			sectionMap = createFormData(perfValues, true);
		} else {
			EntityCondition entityCondition = EntityCondition
					.makeCondition(UtilMisc.toMap("emplPerfReviewId", reviewId));
			List<GenericValue> perfValues = delegator.findList(
					"PerfSectionAndAttribute", entityCondition, null, null,
					null, false);
			sectionMap = createFormData(perfValues, false);
		}
		request.setAttribute("perfSections", sectionMap.get("perfSections"));
		request.getSession().setAttribute("perfGlobalMap", sectionMap);
		return "success";
	}

	
	

}
