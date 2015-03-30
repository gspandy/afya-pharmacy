package org.ofbiz.humanresext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import javolution.util.FastList;
import javolution.util.FastMap;
import javolution.util.FastSet;

import org.apache.commons.lang.StringUtils;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.humanresext.util.HumanResUtil;
import org.ofbiz.security.OFBizSecurity;

public class SelfPerfReview {

	public static final String SUCCESS = "success".intern();
	public static final String module = "SelfPerfReview";

	public static String savePerfReview(HttpServletRequest request,
			HttpServletResponse response) throws GenericEntityException {

		Map<String, Object> parameters = UtilHttp.getParameterMap(request);
		Collection<Map<String, String>> dataList = convertToDataMap(parameters);

		GenericValue userLogin = (GenericValue) request.getSession()
				.getAttribute("userLogin");

		String partyId = userLogin.getString("partyId");

		GenericDelegator delegator = (GenericDelegator) request
				.getAttribute("delegator");
		String selfPrefReviewId = request.getParameter("reviewId");

		boolean isReviewView = parameters.get("isReviewed") != null ? true
				: false;
		if (isReviewView) {
			request.setAttribute("managerView", "enabled");
			String reviewId = request.getParameter("reviewId");
			updatePerfReviewStatus(request, reviewId,
					PerfReviewStatus.PERF_REVIEW_IN_PROGRESS);
		} else {
			updatePerfReviewStatus(request, selfPrefReviewId,
					PerfReviewStatus.PERF_IN_PROGRESS);
		}
		if (selfPrefReviewId == null) {
			request.setAttribute("_ERROR_MESSAGE_",
					"Cannot save data as EmplPerfReview ID is missing.");
			return "error";
		}

		String sectionId = (String) parameters.get("selectedSectionId");
		List<GenericValue> GVList = new FastList<GenericValue>();
		for (Map<String, String> data : dataList) {
			if (isReviewView) {
				data.put("emplPerfReviewId", request.getParameter("reviewId"));
			} else {
				data.put("emplPerfReviewId", selfPrefReviewId);
				// data.put("partyId", partyId);
			}
			data.put("perfReviewSectionId", sectionId);
			GenericValue value = delegator.makeValue("EmplPerfReviewAttribute",
					data);
			GVList.add(value);
		}

		boolean beganTransaction = false;
		try {
			beganTransaction = TransactionUtil.begin();
			delegator.storeAll(GVList);
		} catch (GenericEntityException e) {
			try {
				// only roll back the transaction if we started one...
				TransactionUtil.rollback(beganTransaction,
						"Error Saving Section Attribute Values", e);
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
								"Could not commit transaction for entity"
										+ " engine error occurred while saving abandoned cart information",
								module);
			}
		}
		return SUCCESS;
	}

	/**
	 * @param partyId
	 * @param delegator
	 * @param perfReviewId
	 * @param appraiserPartyId
	 * @return
	 * @throws GenericEntityException
	 */
	private static String createEmplPerfRecord(String partyId,
			GenericDelegator delegator, String perfReviewId,
			String appraiserPartyId) throws GenericEntityException {
		String selfPrefReviewId;

		EntityCondition entityCondition = EntityCondition
				.makeCondition(UtilMisc.toMap("partyId", partyId,
						"perfReviewId", perfReviewId, "appraiserPartyId",
						appraiserPartyId));
		List<GenericValue> perfReviews = delegator.findList("EmplPerfReview",
				entityCondition, null, null, null, false);

		if (perfReviews.size() > 0) {
			GenericValue perfReview = perfReviews.get(0);
			selfPrefReviewId = perfReview.getString("emplPerfReviewId");
			return selfPrefReviewId;
		}

		selfPrefReviewId = delegator.getNextSeqId("EmplPerfReview");

		Map emplPerfReviewData = UtilMisc.toMap("emplPerfReviewId",
				selfPrefReviewId, "partyId", partyId, "perfReviewId",
				perfReviewId, "appraiserPartyId", appraiserPartyId,
				"statusType", PerfReviewStatus.PERF_IN_PROGRESS.toString());
		try {
			GenericValue value = delegator.create("EmplPerfReview",
					emplPerfReviewData);
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return selfPrefReviewId;
	}

	/**
	 * Return a Map<Section ID, Section Data + AtrributeList< Attribute Date >>
	 * also it has a key "sections" which holds list of sectionData only.
	 * 
	 * @param parameters
	 * @return
	 */
	private static Collection<Map<String, String>> convertToDataMap(
			Map<String, Object> parameters) {

		Set<Map.Entry<String, Object>> entries = parameters.entrySet();

		Collection<Map<String, String>> dataList = FastSet.newInstance();

		Map<String, Map<String, String>> dataMap = FastMap.newInstance();

		boolean isReviewed = parameters.get("isReviewed") != null ? true
				: false;

		for (Map.Entry<String, Object> parameter : entries) {
			String value = (String) parameter.getValue();

			// If blank or NONE do no process it.
			if (StringUtils.isBlank(value) && "NONE".equals(value)) {
				continue;
			}

			String key = parameter.getKey();
			// If its an parameter name starts with perf_attr it means
			// self rating and comments are for that attribute.
			if (key.startsWith("perf_attr_")) {
				String attributeId = null;
				attributeId = key.substring(10);
				if (key.endsWith("_comments"))
					attributeId = attributeId.substring(0, attributeId
							.indexOf("_comments"));

				Map<String, String> map = dataMap.get(attributeId);
				if (map == null) {
					map = FastMap.newInstance();
					dataMap.put(attributeId, map);
				}
				if (key.endsWith("_comments")) {

					map.put(isReviewed ? "appraiserComment" : "selfComment",
							StringUtils.trim(value));
				} else
					map.put(isReviewed ? "rating" : "selfRating", value);
				map.put("attributeId", attributeId);

			} else if (key.startsWith("perf_sec_") && !key.endsWith("comments")) {
				key = key.substring(9);
				Map<String, String> map = dataMap.get(key);
				if (map == null) {
					map = FastMap.newInstance();
					dataMap.put(key, map);
				}
				map.put("attributeId", "_NA_");
				map.put(isReviewed ? "rating" : "selfRating", StringUtils
						.trim(value));
				map.put(isReviewed ? "appraiserComment" : "selfComment",
						(String) parameters
								.get("perf_sec_" + key + "_comments"));
			}

		}
		dataList = dataMap.values();

		return dataList;
	}

	private static Map<String, Object> createSectionMapForParty(
			String perfReviewId, String partyId, GenericDelegator delegator)
			throws GenericEntityException {

		String emplPositionTypeId = null;
		GenericValue emplPosition = HumanResUtil.getEmplPositionForParty(
				partyId, delegator);
		emplPositionTypeId = emplPosition.getString("emplPositionTypeId");

		System.out.println(" EMPL POSITION TYPE ID " + emplPositionTypeId);

		EntityCondition condition1 = EntityCondition.makeCondition(
				"emplPositionTypeId", emplPositionTypeId);
		EntityCondition condition2 = EntityCondition.makeCondition(
				"perfReviewId", perfReviewId);

		List<EntityCondition> conditionList = new ArrayList<EntityCondition>(2);
		conditionList.add(condition1);
		conditionList.add(condition2);

		EntityCondition condition = EntityCondition
				.makeCondition(conditionList);
		List<GenericValue> assocTemplateEntities = null;
		try {
			List<GenericValue> findByCondition = delegator.findList(
					"PerfReviewAssocTemplates", condition, null, null,null,false);
			assocTemplateEntities = findByCondition;
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (assocTemplateEntities.size() > 1) {
			throw new GenericEntityException(
					"More than 1 Templates found for Employee Position "
							+ emplPosition.getString("description"));
		}

		GenericValue assocTemplate = assocTemplateEntities.get(0);
		GenericValue template = assocTemplate
				.getRelatedOne("PerfReviewTemplate");

		String perfTemplateId = template.getString("perfTemplateId");
		List<GenericValue> sectionEntities = template
				.getRelated("PerfTemplateSection");

		List<Map<String, Object>> sections = new ArrayList<Map<String, Object>>(
				sectionEntities.size());
		Map<String, Object> sectionMap = new HashMap<String, Object>();

		for (GenericValue entity : sectionEntities) {
			GenericValue sessionEntity = entity
					.getRelatedOne("PerfReviewSection");
			Map<String, Object> sectionmap = new HashMap<String, Object>();
			String sectionId = sessionEntity.getString("perfReviewSectionId");
			sectionmap.put("id", sectionId);
			sectionmap.put("sectionName", sessionEntity
					.getString("sectionName"));
			sectionmap.put("description", sessionEntity
					.getString("description"));
			sectionmap.put("weightage", entity.getString("weightage"));
			sectionmap.put("templateDescription", entity.getString("comments"));
			sections.add(sectionmap);

			EntityCondition attCondition = EntityCondition.makeCondition(
					"perfReviewSectionId", sectionId);
			List<GenericValue> attributes = delegator.findList(
					"PerfReviewSectionAttribute", attCondition, null, null,null,false);

			List<Map<String, String>> attributeList = new ArrayList<Map<String, String>>(
					attributes.size());
			for (GenericValue attribute : attributes) {
				Map<String, String> attributeMap = new HashMap<String, String>();
				attributeMap.put("attributeId", attribute
						.getString("attributeId"));
				attributeMap.put("attributeName", attribute
						.getString("attributeName"));
				attributeMap.put("description", attribute
						.getString("description"));

				attributeList.add(attributeMap);
			}
			sectionmap.put("attributeList", attributeList);
			sectionMap.put(sectionId, sectionmap);
		}
		sectionMap.put("sections", sections);
		sectionMap.put("perfTemplateId", perfTemplateId);
		return sectionMap;
	}

	public static String createBlankSelfReview(HttpServletRequest request,
			HttpServletResponse response) throws GenericEntityException {

		HttpSession session = request.getSession();
		String emplPerfReviewId = request.getParameter("reviewId");
		String perfReviewId = (String) session.getAttribute("PERF_REVIEW_ID");

		System.out.println("EMPL PERF REVIEW ID " + emplPerfReviewId);
		if (emplPerfReviewId != null) {
			populateFormForAppraisal(request);
			return "success";
		}

		GenericValue userLogin = (GenericValue) request.getSession()
				.getAttribute("userLogin");
		GenericDelegator delegator = (GenericDelegator) request
				.getAttribute("delegator");

		String partyId = userLogin.getString("partyId");

		Map<String, Object> sectionMap = createSectionMapForParty(perfReviewId,
				partyId, delegator);

		System.out.println(" SECTION MAP ---------->\n " + sectionMap);

		List<Map<String, String>> sections = (List<Map<String, String>>) sectionMap
				.remove("sections");
		request.setAttribute("perfSections", sections);

		String perfTemplateId = (String) sectionMap.remove("perfTemplateId");
		request.setAttribute("perfGlobalMap", sectionMap);

		GenericValue managerPosition = HumanResUtil.getManagerPositionForParty(
				partyId, delegator);

		// Its possible if some one does not have a reporting head.In such
		// cases
		// the appraiserPartyId is _NA_
		GenericValue fullfillment = null;
		if (managerPosition != null) {
			fullfillment = HumanResUtil.getLatestFulfillmentForPosition(
					managerPosition.getString("emplPositionId"), delegator);
		}

		String appraiserPartyId = fullfillment == null ? "_NA_" : fullfillment
				.getString("partyId");

		String selfPrefReviewId = null;
		boolean beganTransaction = false;
		try {
			beganTransaction = TransactionUtil.begin();
			selfPrefReviewId = createEmplPerfRecord(partyId, delegator,
					perfReviewId, appraiserPartyId);

			Set<Map.Entry<String, Object>> sectionEntries = sectionMap
					.entrySet();
			for (Map.Entry sectionEntry : sectionEntries) {

				Map<String, String> commonMap = UtilMisc.toMap(
						"emplPerfReviewId", selfPrefReviewId, "partyId",
						partyId, "perfReviewSectionId", (String) sectionEntry
								.getKey(), "perfTemplateId", perfTemplateId);

				Map sectionmap = (Map) sectionEntry.getValue();
				List<Map<String, String>> attributeList = (List<Map<String, String>>) sectionmap
						.get("attributeList");

				for (Iterator<Map<String, String>> iter = attributeList
						.iterator(); iter.hasNext();) {

					Map<String, String> attributemap = iter.next();
					Map<String, String> dataMap = new HashMap<String, String>();
					dataMap.put("attributeId", attributemap.get("attributeId"));
					dataMap.putAll(commonMap);
					delegator.create("EmplPerfReviewAttribute", dataMap);
				}
				// Section Having no Attribute
				if (attributeList.size() == 0) {
					Map<String, String> dataMap = new HashMap<String, String>();
					dataMap.putAll(commonMap);
					dataMap.put("attributeId", "_NA_");
					delegator.create("EmplPerfReviewAttribute", dataMap);
				}
			}
			// If Everythings goes fine assign the perf id in session
			request.setAttribute("SELF_PREF_REVIEW_ID", selfPrefReviewId);

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

		return "success";
	}

	public static String populateFormForAppraisal(HttpServletRequest request,
			HttpServletResponse response) throws GenericEntityException {

		return populateFormForAppraisal(request);
	}

	private static String populateFormForAppraisal(HttpServletRequest request)
			throws GenericEntityException {

		populateForm(request, false);
		return "success";
	}

	public static String populateFormForReview(HttpServletRequest request,
			HttpServletResponse response) throws GenericEntityException {
		populateForm(request, true);
		request.setAttribute("managerView", "enabled");
		return "success";
	}

	private static void populateForm(HttpServletRequest request,
			boolean reviewFlag) throws GenericEntityException {

		HttpSession session = request.getSession();
		String reviewId = request.getParameter("reviewId");
		GenericDelegator delegator = (GenericDelegator) request
				.getAttribute("delegator");

		GenericValue userLogin = (GenericValue) session
				.getAttribute("userLogin");
		String partyId = userLogin.getString("partyId");

		OFBizSecurity security = (OFBizSecurity) request
				.getAttribute("security");
		boolean isAdmin = security.hasEntityPermission("HUMANRES", "_ADMIN",
				session);
		/*
		 * if (!(checkPerfReviewAccess(id, partyId, delegator) || isAdmin)) {
		 * request.setAttribute("_ERROR_MESSAGE_", "Do not have Access to view
		 * the Performance Review " + id); }
		 */
		EntityCondition entityCondition = EntityCondition
				.makeCondition(UtilMisc.toMap("emplPerfReviewId", reviewId,
						"partyId", partyId));

		List<GenericValue> perfValues = null;
		perfValues = delegator.findList(
				"EmplPerfWithSectionAndAttributeDetails", entityCondition,
				null, null,null,false);

		List sections = FastList.newInstance();
		Map sectionMap = new HashMap();
		for (GenericValue entity : perfValues) {

			String sectionId = entity.getString("perfReviewSectionId");

			Map<String, Object> sectionmap = (Map) sectionMap.get(sectionId);
			if (sectionmap == null) {
				entityCondition = EntityCondition.makeCondition();
				GenericValue sessionEntity = null;
				try {
					sessionEntity = delegator.findOne("PerfReviewSection",
							UtilMisc.toMap("perfReviewSectionId", sectionId),
							true);
				} catch (GenericEntityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				sectionmap = new HashMap<String, Object>();
				sectionmap.put("id", sectionId);
				sectionmap.put("sectionName", sessionEntity
						.getString("sectionName"));
				sectionmap.put("description", sessionEntity
						.getString("description"));

				GenericValue templateEntity = null;
				try {
					templateEntity = delegator.findOne("PerfTemplateSection",
							UtilMisc.toMap("perfReviewSectionId", sectionId,
									"perfTemplateId", ""), true);
				} catch (GenericEntityException e) {
					e.printStackTrace();
				}
				sectionmap.put("weightage", templateEntity
						.getString("weightage"));
				sectionmap.put("templateDescription", templateEntity
						.getString("comments"));
				sections.add(sectionmap);
				sectionMap.put(sectionId, sectionmap);

			}

			List attributeList = (List) sectionmap.get("attributeList");
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
					continue;
				}

				GenericValue attribute = null;
				try {
					attribute = delegator.findOne("PerfReviewSectionAttribute",
							UtilMisc.toMap("attributeId", attributeId,
									"perfReviewSectionId", sectionId), true);
				} catch (GenericEntityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Map<String, String> attributeMap = new HashMap<String, String>();
				attributeMap.put("attributeId", attributeId);
				attributeMap.put("attributeName", attribute
						.getString("attributeName"));
				attributeMap.put("description", attribute
						.getString("description"));
				attributeMap.put("selfRating", entity.getString("selfRating"));
				attributeMap
						.put("selfComment", entity.getString("selfComment"));
				attributeList.add(attributeMap);
				sectionmap.put("attributeList", attributeList);
			}

		}
		request.setAttribute("perfSections", sections);
		request.setAttribute("perfGlobalMap", sectionMap);

		System.out.println(" POPULATING FROM DB ----> \n" + sectionMap);

	}

	private static boolean checkPerfReviewAccess(String emplPerfReviewId,
			String partyId, GenericDelegator delegator)
			throws GenericEntityException {

		EntityCondition condition1 = EntityCondition.makeCondition(
				"emplPerfReviewId", emplPerfReviewId);

		EntityCondition condition2 = EntityCondition.makeCondition("partyId",
				partyId);

		List<EntityCondition> conditionList = new ArrayList<EntityCondition>();
		conditionList.add(condition1);
		conditionList.add(condition2);
		EntityCondition entityCondition = EntityCondition
				.makeCondition(conditionList);

		List<GenericValue> list = delegator.findList("EmplPerfReview",
				entityCondition, null, null, null, true);

		if (list.size() == 0)
			return false;
		else
			return true;
	}

	// TODO Check all the sections are complete.
	public static String markAsSubmitted(HttpServletRequest request,
			HttpServletResponse response) throws GenericEntityException {

		String id = request.getParameter("reviewId");
		try {
			updatePerfReviewStatus(request, id,
					PerfReviewStatus.PERF_REVIEW_PENDING);
		} catch (GenericEntityException ge) {
			request.setAttribute("_ERROR_MESSAGE_",
					"Could not Update the Performance Review");
			return "error";
		}
		return "success";
	}

	public static String markAsReviewed(HttpServletRequest request,
			HttpServletResponse response) throws GenericEntityException {

		String reviewId = request.getParameter("reviewId");
		try {
			updatePerfReviewStatus(request, reviewId,
					PerfReviewStatus.PERF_REVIEWED_BY_MGR);
		} catch (GenericEntityException ge) {
			request.setAttribute("_ERROR_MESSAGE_",
					"Could not Update the Performance Review");
			request.setAttribute("managerView", "enabled");
			return "error";
		}
		return "success";
	}

	public static String markAgreed(HttpServletRequest request,
			HttpServletResponse response) throws GenericEntityException {

		String reviewId = request.getParameter("reviewId");

		try {
			updatePerfReviewStatus(request, reviewId,
					PerfReviewStatus.PERF_REVIEW_AGREED);
		} catch (GenericEntityException ge) {
			request.setAttribute("_ERROR_MESSAGE_",
					"Could not Update the Performance Review");
			request.setAttribute("managerView", "enabled");
			return "error";
		}
		return "success";
	}

	public static String markDisAgreed(HttpServletRequest request,
			HttpServletResponse response) throws GenericEntityException {

		String disagreedComments = request.getParameter("disagreedComments");
		if (StringUtils.isBlank(disagreedComments)) {
			request.setAttribute("_ERROR_MESSAGE_", "Please provide a reason.");
			return "error";
		}
		String id = request.getParameter("reviewId");
		try {
			GenericDelegator delegator = (GenericDelegator) request
					.getAttribute("delegator");
			GenericValue template = delegator.findOne("EmplPerfReview",
					UtilMisc.toMap("emplPerfReviewId", id), false);
			template.set("statusType", PerfReviewStatus.PERF_REVIEW_DISAGREE
					.toString());
			template.set("disAgreedComments", disagreedComments);
			delegator.store(template);
		} catch (GenericEntityException ge) {
			request.setAttribute("_ERROR_MESSAGE_",
					"Could not Update the Performance Review");
			request.setAttribute("managerView", "enabled");
			return "error";
		}
		return "success";
	}

	private static void updatePerfReviewStatus(HttpServletRequest request,
			String id, PerfReviewStatus status) throws GenericEntityException {

		HttpSession session = request.getSession();

		GenericValue userLogin = (GenericValue) session
				.getAttribute("userLogin");

		String partyId = userLogin.getString("partyId");

		GenericDelegator delegator = (GenericDelegator) request
				.getAttribute("delegator");
		GenericValue template = delegator.findOne("EmplPerfReview", UtilMisc
				.toMap("emplPerfReviewId", id), false);
		template.set("statusType", status.toString());
		delegator.store(template);
	}

}
