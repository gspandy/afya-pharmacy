package com.smebiz.sfa.content;

import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.security.Security;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

import com.smebiz.sfa.common.UtilMessage;
import com.smebiz.sfa.party.SfaPartyHelper;
import com.smebiz.sfa.security.CrmsfaSecurity;


public class ContentServices {

	public static final String module = ContentServices.class.getName();

	public static Map createContentForParty(DispatchContext dctx, Map context) {
		Security security = dctx.getSecurity();
		Locale locale = (Locale) context.get("locale");
		GenericValue userLogin = (GenericValue) context.get("userLogin");

		String partyId = (String) context.get("partyId");
		String roleTypeId = (String) context.get("roleTypeId");
		String contentPurposeEnumId = (String) context
				.get("contentPurposeEnumId");
		if (contentPurposeEnumId == null)
			contentPurposeEnumId = "PTYCNT_CRMSFA";

		// figure out the CRMSFA role of the party if not specified
		if (roleTypeId == null) {
			try {
				roleTypeId = SfaPartyHelper.getFirstValidCrmsfaPartyRoleTypeId(
						partyId, (GenericDelegator) (GenericDelegator) dctx.getDelegator());
			} catch (GenericEntityException e) {
				return UtilMessage.createAndLogServiceError(e,
						"CrmErrorCreateContentFail", locale, module);
			}
		}
		if (roleTypeId == null
				|| !CrmsfaSecurity.hasPartyRelationSecurity(security,
						CrmsfaSecurity.getSecurityModuleForRole(roleTypeId),
						"_UPDATE", userLogin, partyId)) {
			return UtilMessage.createAndLogServiceError(
					"CrmErrorPermissionDenied", locale, module);
		}
		context.put("roleTypeId", roleTypeId);
		context.put("contentPurposeEnumId", contentPurposeEnumId);
		Map results = createContent(dctx, context, "sfaext.createPartyContent");
		if (ServiceUtil.isError(results))
			return results;
		results.put("partyId", partyId);
		return results;
	}

	/*
	 * public static Map createContentForCase(DispatchContext dctx, Map context)
	 * { String custRequestId = (String) context.get("custRequestId"); if
	 * (!CrmsfaSecurity.hasCasePermission(dctx.getSecurity(), "_UPDATE",
	 * (GenericValue) context.get("userLogin"), custRequestId)) { return
	 * UtilMessage.createAndLogServiceError("CrmErrorPermissionDenied", (Locale)
	 * context.get("locale"), module); } Map results = createContent(dctx,
	 * context, "sfaext.createCustRequestContent"); if
	 * (ServiceUtil.isError(results)) return results;
	 * results.put("custRequestId", custRequestId); return results;
	 * 
	 * }
	 */

	public static Map createContentForOpportunity(DispatchContext dctx,
			Map context) {
		String salesOpportunityId = (String) context.get("salesOpportunityId");
		if (!CrmsfaSecurity.hasOpportunityPermission(dctx.getSecurity(),
				"_UPDATE", (GenericValue) context.get("userLogin"),
				salesOpportunityId)) {
			return UtilMessage.createAndLogServiceError(
					"CrmErrorPermissionDenied", (Locale) context.get("locale"),
					module);
		}
		Map results = createContent(dctx, context,
				"sfaext.createSalesOpportunityContent");
		if (ServiceUtil.isError(results))
			return results;
		results.put("salesOpportunityId", salesOpportunityId);
		return results;
	}

	/*
	 * public static Map createContentForActivity(DispatchContext dctx, Map
	 * context) { String workEffortId = (String) context.get("workEffortId"); if
	 * (!CrmsfaSecurity.hasActivityPermission(dctx.getSecurity(), "_UPDATE",
	 * (GenericValue) context.get("userLogin"), workEffortId)) { return
	 * UtilMessage.createAndLogServiceError("CrmErrorPermissionDenied", (Locale)
	 * context.get("locale"), module); } context.put("workEffortContentTypeId",
	 * "CREATED_MEDIA"); Map results = createContent(dctx, context,
	 * "createWorkEffortContent"); if (ServiceUtil.isError(results)) return
	 * results; results.put("workEffortId", workEffortId); return results; }
	 */

	// Parametrized create content service.
	private static Map createContent(DispatchContext dctx, Map context,
			String createContentAssocService) {
		GenericDelegator delegator = (GenericDelegator) (GenericDelegator) dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Locale locale = (Locale) context.get("locale");

		String contentTypeId = (String) context.get("contentTypeId");
		try {
			// what we do depends on if we're uploading a file or a URL
			String uploadServiceName = null;
			if ("FILE".equals(contentTypeId)) {
				uploadServiceName = "createContentFromUploadedFile";
			} else if ("HYPERLINK".equals(contentTypeId)) {
				uploadServiceName = "uploadUrl";
			} else {
				return ServiceUtil.returnSuccess();
			}

			ModelService service = dctx.getModelService(uploadServiceName);
			Map input = service.makeValid(context, "IN");
			Map servResults = dispatcher.runSync(uploadServiceName, input);
			if (ServiceUtil.isError(servResults)) {
				return UtilMessage.createAndLogServiceError(servResults,
						"CrmErrorCreateContentFail", locale, module);
			}
			String contentId = (String) servResults.get("contentId");

			// create the association between the content and the object
			service = dctx.getModelService(createContentAssocService);
			input = service.makeValid(context, "IN");
			input.put("contentId", contentId);
			servResults = dispatcher.runSync(createContentAssocService, input);
			return servResults;
		} catch (GenericServiceException e) {
			return UtilMessage.createAndLogServiceError(e,
					"CrmErrorCreateContentFail", locale, module);
		}
	}

	public static Map createPartyContent(DispatchContext dctx, Map context) {
		GenericDelegator delegator = (GenericDelegator) (GenericDelegator) dctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		try {
			GenericValue value = delegator.makeValue("PartyContent");
			value.setPKFields(context);
			value.setNonPKFields(context);
			value.create();
/*
			value = delegator.makeValue("ContentRole");
			value.setPKFields(context);
			value.setNonPKFields(context);
			value.set("fromDate",
					context.get("fromDate") == null ? UtilDateTime
							.nowTimestamp() : context.get("fromDate"));
			value.create();
*/	
		} catch (GenericEntityException e) {
			return UtilMessage.createAndLogServiceError(e,
					"CrmErrorCreateContentFail", locale, module);
		}
		return ServiceUtil.returnSuccess();
	}

	/*
	 * public static Map createCustRequestContent(DispatchContext dctx, Map
	 * context) { GenericDelegator delegator = (GenericDelegator) dctx.getDelegator(); Locale
	 * locale = (Locale) context.get("locale"); try { GenericValue value =
	 * delegator.makeValue("CustRequestContent", null);
	 * value.setPKFields(context); value.setNonPKFields(context); if
	 * (context.get("fromDate") == null) { value.set("fromDate",
	 * UtilDateTime.nowTimestamp()); } value.create(); } catch
	 * (GenericEntityException e) { return
	 * UtilMessage.createAndLogServiceError(e, "CrmErrorCreateContentFail",
	 * locale, module); } return ServiceUtil.returnSuccess(); }
	 */

	public static Map createSalesOpportunityContent(DispatchContext dctx,
			Map context) {
		GenericDelegator delegator = (GenericDelegator) (GenericDelegator) dctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		try {
			GenericValue value = delegator.makeValue("SalesOpportunityContent");
			value.setPKFields(context);
			value.setNonPKFields(context);
			if (context.get("fromDate") == null) {
				value.set("fromDate", UtilDateTime.nowTimestamp());
			}
			value.create();
		} catch (GenericEntityException e) {
			return UtilMessage.createAndLogServiceError(e,
					"CrmErrorCreateContentFail", locale, module);
		}
		return ServiceUtil.returnSuccess();
	}

	public static Map updateContentForParty(DispatchContext dctx, Map context) {
		Security security = dctx.getSecurity();
		Locale locale = (Locale) context.get("locale");
		GenericValue userLogin = (GenericValue) context.get("userLogin");

		String partyId = (String) context.get("partyId");
		String roleTypeId = (String) context.get("roleTypeId");

		// figure out the CRMSFA role of the party if not specified
		if (roleTypeId == null) {
			try {
				roleTypeId = SfaPartyHelper.getFirstValidCrmsfaPartyRoleTypeId(
						partyId, (GenericDelegator) (GenericDelegator) dctx.getDelegator());
			} catch (GenericEntityException e) {
				return UtilMessage.createAndLogServiceError(e,
						"CrmErrorUpdateContentFail", locale, module);
			}
		}
		if (roleTypeId == null
				|| !CrmsfaSecurity.hasPartyRelationSecurity(security,
						CrmsfaSecurity.getSecurityModuleForRole(roleTypeId),
						"_UPDATE", userLogin, partyId)) {
			return UtilMessage.createAndLogServiceError(
					"CrmErrorPermissionDenied", locale, module);
		}
		Map results = updateContent(dctx, context);
		if (ServiceUtil.isError(results))
			return results;
		results.put("partyId", partyId);
		return results;
	}

	/*
	 * public static Map updateContentForCase(DispatchContext dctx, Map context)
	 * { String custRequestId = (String) context.get("custRequestId"); if
	 * (!CrmsfaSecurity.hasCasePermission(dctx.getSecurity(), "_UPDATE",
	 * (GenericValue) context.get("userLogin"), custRequestId)) { return
	 * UtilMessage.createAndLogServiceError("CrmErrorPermissionDenied", (Locale)
	 * context.get("locale"), module); } Map results = updateContent(dctx,
	 * context); if (ServiceUtil.isError(results)) return results;
	 * results.put("custRequestId", custRequestId); return results;
	 * 
	 * }
	 */

	public static Map updateContentForOpportunity(DispatchContext dctx,
			Map context) {
		String salesOpportunityId = (String) context.get("salesOpportunityId");
		if (!CrmsfaSecurity.hasOpportunityPermission(dctx.getSecurity(),
				"_UPDATE", (GenericValue) context.get("userLogin"),
				salesOpportunityId)) {
			return UtilMessage.createAndLogServiceError(
					"CrmErrorPermissionDenied", (Locale) context.get("locale"),
					module);
		}
		Map results = updateContent(dctx, context);
		if (ServiceUtil.isError(results))
			return results;
		results.put("salesOpportunityId", salesOpportunityId);
		return results;
	}

	/*
	 * public static Map updateContentForActivity(DispatchContext dctx, Map
	 * context) { String workEffortId = (String) context.get("workEffortId"); if
	 * (!CrmsfaSecurity.hasActivityPermission(dctx.getSecurity(), "_UPDATE",
	 * (GenericValue) context.get("userLogin"), workEffortId)) { return
	 * UtilMessage.createAndLogServiceError("CrmErrorPermissionDenied", (Locale)
	 * context.get("locale"), module); } Map results = updateContent(dctx,
	 * context); if (ServiceUtil.isError(results)) return results;
	 * results.put("workEffortId", workEffortId); return results; }
	 */

	private static Map updateContent(DispatchContext dctx, Map context) {
		GenericDelegator delegator = (GenericDelegator) (GenericDelegator) dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Locale locale = (Locale) context.get("locale");
		GenericValue userLogin = (GenericValue) context.get("userLogin");

		// the forms control what gets updated an how, this service simply
		// updates the contentName, description and url fields
		String contentId = (String) context.get("contentId");
		String contentName = (String) context.get("contentName");
		String description = (String) context.get("description");
		String url = (String) context.get("url");
		try {
			// first update content
			Map input = UtilMisc.toMap("userLogin", userLogin, "contentId",
					contentId, "contentName", contentName, "description",
					description);
			Map results = dispatcher.runSync("updateContent", input);
			if (ServiceUtil.isError(results)) {
				return UtilMessage.createAndLogServiceError(results,
						"CrmErrorUpdateContentFail", locale, module);
			}

			// if url is supplied, then we update the related DataResource
			if (UtilValidate.isNotEmpty(url)) {
				GenericValue content = delegator.findByPrimaryKey("Content",
						UtilMisc.toMap("contentId", contentId));
				GenericValue dataResource = content
						.getRelatedOne("DataResource");
				if (dataResource != null) {
					input = UtilMisc.toMap("userLogin", userLogin,
							"dataResourceId", dataResource
									.get("dataResourceId"), "objectInfo", url);
					results = dispatcher.runSync("updateDataResource", input);
					if (ServiceUtil.isError(results)) {
						return UtilMessage.createAndLogServiceError(results,
								"CrmErrorUpdateContentFail", locale, module);
					}
				}
			}

			return ServiceUtil.returnSuccess();
		} catch (GenericServiceException e) {
			return UtilMessage.createAndLogServiceError(e,
					"CrmErrorUpdateContentFail", locale, module);
		} catch (GenericEntityException e) {
			return UtilMessage.createAndLogServiceError(e,
					"CrmErrorUpdateContentFail", locale, module);
		}
	}

	// Instead of the approach used above, we'll accept remove request for all
	// possible objects
	public static Map removeContent(DispatchContext dctx, Map context) {
		GenericDelegator delegator = (GenericDelegator) (GenericDelegator) dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Security security = dctx.getSecurity();
		Locale locale = (Locale) context.get("locale");
		GenericValue userLogin = (GenericValue) context.get("userLogin");

		String contentId = (String) context.get("contentId");
		String partyId = (String) context.get("partyId");
		String workEffortId = (String) context.get("workEffortId");
		String custRequestId = (String) context.get("custRequestId");
		String salesOpportunityId = (String) context.get("salesOpportunityId");

		if (partyId == null && workEffortId == null && custRequestId == null
				&& salesOpportunityId == null) {
			return UtilMessage.createAndLogServiceError(
					"CrmErrorPermissionDenied", locale, module);
		}

		try {
			boolean hasPermission = false;
			if (partyId != null) {
				String roleTypeId = SfaPartyHelper
						.getFirstValidCrmsfaPartyRoleTypeId(partyId, delegator);
				hasPermission = (roleTypeId != null && CrmsfaSecurity
						.hasPartyRelationSecurity(security, CrmsfaSecurity
								.getSecurityModuleForRole(roleTypeId),
								"_UPDATE", userLogin, partyId));
			}
			/*
			 * else if (workEffortId != null) { hasPermission =
			 * CrmsfaSecurity.hasActivityPermission(dctx.getSecurity(),
			 * "_UPDATE", (GenericValue) context.get("userLogin"),
			 * workEffortId); } else if (custRequestId != null) { hasPermission
			 * = CrmsfaSecurity.hasCasePermission(dctx.getSecurity(), "_UPDATE",
			 * (GenericValue) context.get("userLogin"), custRequestId); }
			 */
			else if (salesOpportunityId != null) {
				hasPermission = CrmsfaSecurity.hasOpportunityPermission(dctx
						.getSecurity(), "_UPDATE", (GenericValue) context
						.get("userLogin"), salesOpportunityId);
			}
			if (!hasPermission) {
				return UtilMessage.createAndLogServiceError(
						"CrmErrorPermissionDenied", locale, module);
			}

			// We're going to expire the content relationship first
			String entityName = null;
			List conditions = UtilMisc.toList(EntityCondition.makeCondition("contentId",
					EntityOperator.EQUALS, contentId), EntityUtil
					.getFilterByDateExpr());
			if (partyId != null) {
				entityName = "ContentRole";
				conditions.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS,
						partyId));
			} else if (workEffortId != null) {
				entityName = "WorkEffortContent";
				conditions.add(EntityCondition.makeCondition("workEffortId",
						EntityOperator.EQUALS, workEffortId));
			} else if (custRequestId != null) {
				entityName = "CustRequestContent";
				conditions.add(EntityCondition.makeCondition("custRequestId",
						EntityOperator.EQUALS, custRequestId));
			} else if (salesOpportunityId != null) {
				entityName = "SalesOpportunityContent";
				conditions.add(EntityCondition.makeCondition("salesOpportunityId",
						EntityOperator.EQUALS, salesOpportunityId));
			}
			List relationships = delegator.findByAnd(entityName, conditions);
			if (relationships.size() == 0) {
				return UtilMessage.createAndLogServiceError(
						"CrmErrorPermissionDenied", locale, module);
			}
			for (Iterator iter = relationships.iterator(); iter.hasNext();) {
				GenericValue relationship = (GenericValue) iter.next();
				relationship.set("thruDate", UtilDateTime.nowTimestamp());
				relationship.store();
			}

			return ServiceUtil.returnSuccess();
		} catch (GenericEntityException e) {
			return UtilMessage.createAndLogServiceError(e,
					"CrmErrorRemoveContentFail", locale, module);
		}
	}
}
