package org.ofbiz.humanresext.perfReview;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import javolution.util.FastList;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.humanresext.util.HumanResUtil;

public class PerfReviewWorker {

	/**
	 * @param partyId
	 * @param perfReviewId
	 * @param delegator
	 * @return
	 * @throws GenericEntityException
	 * @throws NoPerfReviewException
	 */
	public static GenericValue getEmplPerfReview(String partyId,
			String perfReviewId, GenericDelegator delegator)
			throws GenericEntityException, NoPerfReviewException {

		EntityCondition entityCondition = EntityCondition
				.makeCondition(UtilMisc.toMap("partyId", partyId,
						"perfReviewId", perfReviewId));

		List<GenericValue> perfReviews = delegator.findList("EmplPerfReview",
				entityCondition, null, null, null, false);

		if (perfReviews.size() > 0) {
			GenericValue perfReview = perfReviews.get(0);
			return perfReview;
		}

		throw new NoPerfReviewException(
				" There is no Performance Review For Review ID " + perfReviewId
						+ " For Party " + partyId);
	}

	public static List<String> getManagers(HttpServletRequest request,
			String partyId) throws GenericEntityException,
			ManagerNotFoundException {

		GenericDelegator delegator = (GenericDelegator) request
				.getAttribute("delegator");
		GenericValue positionValue = HumanResUtil.getEmplPositionForParty(
				partyId, delegator);

		String emplPositionId = positionValue.getString("emplPositionId");
		EntityCondition condition = EntityCondition.makeCondition(
				"emplPositionIdManagedBy", emplPositionId);

		GenericValue perfReview = (GenericValue) request.getSession()
				.getAttribute("PERF_REVIEW");

		// get emplPerfReview from database.
		if (perfReview == null) {
			throw new ManagerNotFoundException(
					" There are no managers for User Id " + partyId);
		}

		System.out.println(" PERF REVIEW " + perfReview);

		Timestamp startDate = perfReview.getTimestamp("periodStartDate");
		Timestamp thruDate = perfReview.getTimestamp("periodThruDate");

		EntityCondition timeCondition1 = EntityCondition.makeCondition(
				"fromDate", EntityOperator.LESS_THAN_EQUAL_TO, thruDate);
		EntityCondition timeCondition2 = EntityCondition.makeCondition(
				"fromDate", EntityOperator.GREATER_THAN_EQUAL_TO, startDate);

		EntityCondition entityCondition = EntityCondition
				.makeCondition(UtilMisc.toList(timeCondition1, timeCondition2,
						condition));

		List<GenericValue> managerPositions = delegator.findList(
				"EmplPositionReportingStruct", entityCondition, UtilMisc
						.toSet("emplPositionIdReportingTo"), null, null, false);
		Iterator<GenericValue> iter = managerPositions.iterator();
		List<String> managers = FastList.newInstance();
		while (iter.hasNext()) {
			String positionId = iter.next().getString(
					"emplPositionIdReportingTo");
			GenericValue value = HumanResUtil.getLatestFulfillmentForPosition(
					positionId, delegator);
			managers.add(value.getString("partyId"));
		}
		return managers;
	}

	public static List<String> getReviewers(HttpServletRequest request,
			String reviewId) throws ManagerNotFoundException {

		GenericDelegator delegator = (GenericDelegator) request
				.getAttribute("delegator");
		EntityCondition entityCondition = EntityCondition.makeCondition(
				"emplPerfReviewId", reviewId);

		List<GenericValue> listOfReviewers = new ArrayList<GenericValue>(0);
		try {
			EntityFindOptions findOptions = new EntityFindOptions();
			findOptions.setDistinct(true);
			listOfReviewers = delegator.findList("EmplPerfReviewers",
					entityCondition, null, null, findOptions, false);
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new ManagerNotFoundException(e.getMessage());
		}
		List<String> managers = FastList.newInstance();
		for (GenericValue val : listOfReviewers) {
			String reviewerId = val.getString("reviewerId");
			if (!managers.contains(reviewerId))
				managers.add(reviewerId);
		}
		return managers;
	}

	public static Map<String, Object> getSectionMapForParty(
			HttpServletRequest request) throws GenericEntityException,
			PositionNotFoundException {

		GenericDelegator delegator = (GenericDelegator) request
				.getAttribute("delegator");
		HttpSession session = request.getSession();
		String emplPositionTypeId = null;
		String partyId = ((GenericValue) session.getAttribute("userLogin"))
				.getString("partyId");
		String perfReviewId = (String) session.getAttribute("PERF_REVIEW_ID");
		GenericValue emplPosition = HumanResUtil.getEmplPositionForParty(
				partyId, delegator);
		if (emplPosition == null) {
			throw new PositionNotFoundException(
					" No Position Found for User Id " + partyId);
		}
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
}
