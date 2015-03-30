package org.ofbiz.issuetracking.events;

import static org.ofbiz.issuetracking.util.ErrorUtil.addDBError;
import static org.ofbiz.issuetracking.util.ErrorUtil.addError;
import static org.ofbiz.issuetracking.util.ErrorUtil.checkNotEmpty;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.ws.ServiceMode;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.apache.commons.fileupload.FileItem;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.issuetracking.util.IssuetrackingUtil;
import org.ofbiz.issuetracking.util.TimeUtil;
import org.ofbiz.service.GenericDispatcher;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceValidationException;

/**
 * @author sandeep
 * 
 */
@SuppressWarnings("unchecked")
public class IssueTrackerEvents {

	public static enum Status {
		NEW("1"), ASSIGNED("2"), REQUESTEDINFO("3"), INPROGRESS("4"), RESOLVED("5"), CLOSED("6"), REOPENED("7");
		public String value;

		Status(String value) {
			this.value = value;
		}
	}

	public static String createNewIssue(HttpServletRequest request, HttpServletResponse response) throws GenericServiceException {
		Map<String, Object> issueHeaderParamMap = null;
		GenericDispatcher dispatcher = (GenericDispatcher) request.getAttribute("dispatcher");
		try {
			issueHeaderParamMap = UtilAttachment.parseRequest(request);
		} catch (UploadException e) {
			String errMsg = UtilProperties.getMessage("IssueErrorLabels", "UploadError", request.getLocale());
			addError(request, errMsg, e);
			return "error";
		}
		if (UtilValidate.isEmpty(issueHeaderParamMap.get("issueCategoryId"))) {
			request.setAttribute("_ERROR_MESSAGE_", "  Select Category to create an issue ");
			return "error";
		}
		List<FileItem> contentToUpload = (List<FileItem>) issueHeaderParamMap.remove(UtilAttachment.UPLOADCONTENTKEY);
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		issueHeaderParamMap.put("createdBy", userLogin.get("partyId"));
		issueHeaderParamMap.put("lastUpdatedBy", userLogin.get("partyId"));
		issueHeaderParamMap.put("issueStatusId", Status.NEW.value);
		issueHeaderParamMap.put("responseViolated", 0.0);
		issueHeaderParamMap.put("resolutionViolated", 0.0);
	
		Map<String, Object> issueNoteParamMap = FastMap.newInstance();
		String issueSummary = (String) issueHeaderParamMap.remove("issueSummary");
		issueNoteParamMap.put("issueSummary", issueSummary);
		String issueDescription = (String) issueHeaderParamMap.remove("issueDescription");
		issueNoteParamMap.put("issueDescription", issueDescription);
		String issueAdditionInfo = (String) issueHeaderParamMap.remove("issueAdditionalInfo");
		issueNoteParamMap.put("issueAdditionalInfo", issueAdditionInfo);
		Map<String, Object> result = callService("createIssueNote", issueNoteParamMap, request);
	
		issueHeaderParamMap.put("issueNoteId", result.get("issueNoteId"));
	
		GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
	
		try {
			GenericValue category = delegator.findOne("IssueCategory", false, UtilMisc.toMap("issueCategoryId", issueHeaderParamMap.get("issueCategoryId")));
			if (category != null){
				String ownerId = category.getString("ownerId");
//				IssuetrackingUtil.sendMailToCustomer(dispatcher, delegator, issueHeaderParamMap, userLogin.getString("partyId"), "Acknowledgment Mail");
//				IssuetrackingUtil.sendMailToEmployee(dispatcher, delegator, issueHeaderParamMap, ownerId, issueSummary, issueDescription,issueAdditionInfo);
				
		}
		}catch (GenericEntityException e) {
			addError(request, "Issue remained unassigned as category owner couldn't be fetched", e);
		}
	
		result = callService("createIssueHeader", issueHeaderParamMap, request);
	
		String issueId = (String) result.get("issueId");
	
		request.setAttribute("_EVENT_MESSAGE_", "New Issue logged : Issue Id = " + issueId);
		uploadAttachments(issueId, contentToUpload, request);
		request.setAttribute("issueId", issueId);
		return "success";
	}

	private static void uploadAttachments(String issueId, List<FileItem> contentToUpload, HttpServletRequest request) {
		String uploadLocation = UtilProperties.getPropertyValue("IssueTracker.proprties", "uploadLocation");
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("issueId", issueId);
		String fileName = "";
		try {
			for (FileItem fileItem : contentToUpload) {
				fileName = UtilAttachment.getFileName(fileItem);
				if (fileName == null || fileName.length() == 0)
					continue;
				paramMap.put("fileName", fileName);
				paramMap.put("uploadLocation", uploadLocation);
				Map<String, Object> result = callService("createIssueAttachment", paramMap, request);
				String issueAttachmentId = (String) result.get("issueAttachmentId");
				UtilAttachment.uploadAttachment(uploadLocation, issueAttachmentId, fileItem);
			}
		} catch (Exception e) {
			addError(request, fileName + "could not be successfully uploaded", e);
		}
	}

	public static String downloadAttachment(HttpServletRequest request, HttpServletResponse response) {
		String issueAttachmentId = request.getQueryString();
		GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
		GenericValue attachment = null;
		try {
			attachment = delegator.findOne("IssueAttachment", false, "issueAttachmentId", issueAttachmentId);
		} catch (GenericEntityException e) {
			addError(request, "Attachment corrupted and could not be loaded", e);
			return "success";
		}
		String apparentName = (String) attachment.get("fileName");
		String actualName = issueAttachmentId + apparentName.substring(apparentName.lastIndexOf('.'));
		String path = (String) attachment.get("uploadLocation");
		try {
			UtilAttachment.downloadAttachment(response,request,path, actualName);
		} catch (IOException e) {
			addError(request, apparentName + "could not restored probably corrupted or relocated", e);
		}
		return "success";
	}

	public static String updateIssue(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException, ServiceValidationException, GenericServiceException {
		Map<String, Object> issueHeaderParamMap = null;
		HttpSession session = request.getSession();
		GenericDispatcher dispatcher = (GenericDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		try {
			issueHeaderParamMap = UtilAttachment.parseRequest(request);
		} catch (UploadException e) {
			String errMsg = UtilProperties.getMessage("IssueErrorLabels", "UploadError", request.getLocale());
			addError(request, errMsg, e);
			return "error";
		}

		String comm = (String) issueHeaderParamMap.get("comment");
		if(UtilValidate.isEmpty(comm)){
			request.setAttribute("_ERROR_MESSAGE_", " Response is empty, Add Response to Submit ");
			return "error";
		}
		List<FileItem> contentToUpload = (List<FileItem>) issueHeaderParamMap.remove(UtilAttachment.UPLOADCONTENTKEY);
		GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
		String assigned = (String)issueHeaderParamMap.get("assignedTo") ;
		if(UtilValidate.isEmpty(assigned)){
			assigned = userLogin.getString("partyId");
		}else{
			IssuetrackingUtil.sendMailToEmployee(dispatcher, delegator, issueHeaderParamMap, assigned, (String)issueHeaderParamMap.get("issueSummary"), 
					(String)issueHeaderParamMap.get("issueDescription"),(String)issueHeaderParamMap.get("issueAdditionInfo"));
		}
		List partyList = delegator.findList("UserLogin", EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, assigned), null, null, null, false);
		GenericValue party = EntityUtil.getFirst(partyList);
		if(UtilValidate.isEmpty(party)){
			request.setAttribute("_ERROR_MESSAGE_", "Userlogin is not available for this party.");
			return "error";
		}
		String partyLogin = party.getString("userLoginId");
		
		EntityCondition cn1 = EntityCondition.makeCondition("groupId",EntityOperator.IN,UtilMisc.toList("ISSUEMGR_ADMIN","ISSUEMGR_OWNER","ISSUEMGR_VIEW","ISSUEMGR_RESOLVER","FULLADMIN"));
		EntityCondition cn2 = EntityCondition.makeCondition(EntityCondition.makeCondition("userLoginId", EntityOperator.EQUALS, partyLogin),EntityOperator.AND,cn1);
		List usersList = delegator.findList("UserLoginSecurityGroup", cn2, null, null, null, false);
		if (UtilValidate.isEmpty(usersList)) {
			request.setAttribute("_ERROR_MESSAGE_", "Cannot assign the Issue to the selected Party. Party selected does not have Issue Resolver permission.");
			return "error";
		}
		if (usersList.size()!= 0){ 
		GenericValue issueHeader = null, issueNote = null;
		try {
			issueHeader = delegator.findOne("IssueHeader", false, UtilMisc.toMap("issueId", issueHeaderParamMap.remove("issueId")));
			issueNote = delegator.findOne("IssueNote", false, UtilMisc.toMap("issueNoteId", issueHeader.get("issueNoteId")));
		} catch (GenericEntityException e) {
			addDBError(request, e);
			return "success";
		}
		
		/*if(UtilValidate.isNotEmpty(issueHeader)){
			if(assigned.equals(issueHeader.getString("createdBy")) && !("1".equals(issueHeader.getString("issueStatusId"))) ){
				request.setAttribute("_ERROR_MESSAGE_", "You cannot update as the Issue has already been assigned to Resolver.");
				return "error";
			}
		}*/
		
		GenericValue user = (GenericValue) request.getSession().getAttribute("userLogin");
		changeStatus(issueHeader, (String) issueHeaderParamMap.get("issueStatusId"), user);
		issueHeader.set("issueStatusId", issueHeaderParamMap.get("issueStatusId"));
		issueHeader.set("issueSeverityId", issueHeaderParamMap.get("issueSeverityId"));
		issueHeader.set("issueCategoryId", issueHeaderParamMap.get("issueCategoryId"));
		// !StringUtils.isNumeric((String)
		// issueHeaderParamMap.get("issueSubCategoryId"))
		issueHeader.set("issueSubCategoryId", issueHeaderParamMap.get("issueSubCategoryId"));

		if (UtilValidate.isEmpty(issueHeaderParamMap.get("issueSubCategoryId"))) {
			issueHeader.set("issueSubCategoryId", null);
		}
		
		if (UtilValidate.isNotEmpty(issueHeaderParamMap.get("issueSubCategoryId"))
				&& ((String) issueHeaderParamMap.get("issueSubCategoryId")).equalsIgnoreCase("Y")) {
			issueHeader.set("issueSubCategoryId", null);
		}

		issueHeader.set("lastUpdatedBy", user.get("partyId"));

		
		
		if (issueHeaderParamMap.get("assignedTo") != null && ((String) issueHeaderParamMap.get("assignedTo")).trim().length() != 0)
			issueHeader.set("assignedTo", issueHeaderParamMap.get("assignedTo"));

		issueNote.set("issueSummary", issueHeaderParamMap.get("issueSummary"));
		issueNote.set("issueDescription", issueHeaderParamMap.get("issueDescription"));
		issueNote.set("issueAdditionalInfo", issueHeaderParamMap.get("issueAdditionalInfo"));
		try {
			issueHeader.store();
			issueNote.store();
		} catch (GenericEntityException e) {
			addDBError(request, e);
			return "success";
		}

		String comment = (String) issueHeaderParamMap.get("comment");
		String issueStatus = (String) issueHeaderParamMap.get("issueStatusId");
		
		if("5".equals(issueStatus)){
			GenericValue category = delegator.findOne("IssueCategory", false, UtilMisc.toMap("issueCategoryId", issueHeaderParamMap.get("issueCategoryId")));
			if (category != null){
				String ownerId = category.getString("ownerId");
				IssuetrackingUtil.sendMailToEmployee(dispatcher, delegator, issueHeaderParamMap, ownerId, (String)issueHeaderParamMap.get("issueSummary"), 
						(String)issueHeaderParamMap.get("issueDescription"),(String)issueHeaderParamMap.get("issueAdditionalInfo"));
			}
		}
		if("6".equals(issueStatus)){
			IssuetrackingUtil.sendMailToCustomer(dispatcher, delegator, issueHeaderParamMap, userLogin.getString("partyId"), "Acknowledgment Mail");
		}
		if("7".equals(issueStatus)){
			IssuetrackingUtil.sendMailToEmployee(dispatcher, delegator, issueHeaderParamMap, assigned, (String)issueHeaderParamMap.get("issueSummary"), 
					(String)issueHeaderParamMap.get("issueDescription"),(String)issueHeaderParamMap.get("issueAdditionalInfo"));
		}
		
		List statusList = delegator.findList("IssueStatus", EntityCondition.makeCondition("issueStatusId", EntityOperator.EQUALS, issueStatus), null, null, null, false);
		GenericValue statusListNew = EntityUtil.getFirst(statusList);
		String changeStatus = statusListNew.getString("issueStatusCaption");
		if (comment != null && comment.trim().length() > 0) {
			Map<String, Object> commentMap = UtilMisc.toMap("commentedBy", user.get("partyId"), "response", issueHeaderParamMap.get("comment"), "issueId",
					issueHeader.get("issueId"),"issueStatusCaption", changeStatus);
			callService("createIssueHistory", commentMap, request);
		}
		request.setAttribute("_EVENT_MESSAGE_", "Issue " + issueHeader.get("issueId") + " updated Successfully");
		uploadAttachments((String) issueHeader.get("issueId"), contentToUpload, request);
		request.setAttribute("issueId", issueHeader.get("issueId"));
		}
		return "success";
	}

	private static void changeStatus(GenericValue issueHeader, String statusId, GenericValue user) {
		String oldStatusId = (String) issueHeader.get("issueStatusId");
		if (oldStatusId.equals(statusId))
			return;
		Timestamp now = UtilDateTime.nowTimestamp();
		Timestamp createdOn = (Timestamp) issueHeader.get("createdStamp");
		GenericValue severity = null;
		try {
			severity = issueHeader.getRelatedOne("IssueSeverity");
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		if (user.get("partyId").equals(issueHeader.get("assignedTo")) && issueHeader.get("responseTime") == null) {
			issueHeader.set("responseTime", now);
			issueHeader.set("responseViolated", TimeUtil.violationAmount(createdOn, now, (Number) severity.get(("responseTime"))));
		} else if (Status.CLOSED.value.equals(statusId)) {
			issueHeader.set("resolutionTime", now);
			issueHeader.set("resolutionViolated", TimeUtil.violationAmount(createdOn, now, (Number) severity.get("resolutionTime")));
		}
		issueHeader.set("issueStatusId", statusId);
	}

	public static String addNewCategory(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		boolean noError = true;
		String ownerId = request.getParameter("ownerId").trim();
		
		//noError &= checkNotEmpty("issueCategoryCaption", "Display Caption ", request);
		
		if(UtilValidate.isEmpty(request.getParameter("issueCategoryCaption").trim())){
			request.setAttribute("_ERROR_MESSAGE_", " Display caption cannot be empty  ");
			return "error";
		}
		if(UtilValidate.isEmpty(ownerId)){
			request.setAttribute("_ERROR_MESSAGE_", " Owner party cannot be empty");
			return "error";
		}
		if(UtilValidate.isEmpty(request.getParameter("issueCategoryDescription").trim() )){
			request.setAttribute("_ERROR_MESSAGE_", "Description cannot be empty");
			return "error";
		}
		String issueCategoryCaption = request.getParameter("issueCategoryCaption").trim();
		List captionList = delegator.findList("IssueCategory",EntityCondition.makeCondition("issueCategoryCaption", EntityOperator.EQUALS,issueCategoryCaption), null, null, null, false);
		if(UtilValidate.isNotEmpty(captionList)){
			request.setAttribute("_ERROR_MESSAGE_", " Display caption cannot be duplicated ");
			return "error";
		}
		/* Check whether the party Id having Issue Owner Permission */
		List partyList = delegator.findList("UserLogin", EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, ownerId), null, null, null, false);
		GenericValue party = EntityUtil.getFirst(partyList);
		if( party != null){
		String partyLogin = party.getString("userLoginId");
		EntityCondition securityCon = EntityCondition.makeCondition(EntityCondition.makeCondition("groupId", EntityOperator.EQUALS, "ISSUEMGR_OWNER"),EntityOperator.OR,
				EntityCondition.makeCondition("groupId", EntityOperator.EQUALS, "FULLADMIN"));
		EntityCondition cond = EntityCondition.makeCondition(UtilMisc.toList(EntityCondition.makeCondition(
				EntityCondition.makeCondition("userLoginId", EntityOperator.EQUALS, partyLogin), EntityOperator.AND,
				securityCon)));

		List usersList = delegator.findList("UserLoginSecurityGroup", cond, null, null, null, false);
		if (usersList.size() == 0) {
			request.setAttribute("_ERROR_MESSAGE_", "Cannot assign the Category to the selected Party. Party selected does not have Issue Owner Permission");
			return "error";
		}
		noError &= checkNotEmpty("ownerId", "Base Owner ", request);
		if (usersList.size() != 0)
			if (!noError)
				return "success";
		Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
		callService("createIssueCategory", paramMap, request);
		request.setAttribute("_EVENT_MESSAGE_", "Category added successfully.");
		return "success";
		}
		else{
			request.setAttribute("_ERROR_MESSAGE_", "Owner party is invalid");
			return "error";
			
		}
	}

	public static String addNewSubCategory(HttpServletRequest request, HttpServletResponse response) {
		boolean noError = true;
		noError &= checkNotEmpty("issueSubCategoryCaption", "Display Caption ", request);
		noError &= checkNotEmpty("categoryId", "Base Category ", request);
		noError &= checkNotEmpty("issueSubCategoryDescription", "Description ", request);
		if (!noError)
			return "success";
		Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
		callService("createIssueSubCategory", paramMap, request);
		request.setAttribute("_EVENT_MESSAGE_", "Sub-Category added successfully.");
		return "success";
	}

	public static String editCategory(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		boolean noError = true;
		noError &= checkNotEmpty("issueCategoryCaption", "Display Caption ", request);
		noError &= checkNotEmpty("ownerId", "Category Owner ", request);
		noError &= checkNotEmpty("issueCategoryDescription", "Description ", request);
		if (!noError)
			return "success";

		GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
		GenericValue value = null;
		String ownerId = request.getParameter("ownerId");
		List partyList = delegator.findList("UserLogin", EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, ownerId), null, null, null, false);
		GenericValue party = EntityUtil.getFirst(partyList);
		if( party != null){
		String partyLogin = party.getString("userLoginId");
		
		/*EntityCondition cond = EntityCondition.makeCondition(UtilMisc.toList(EntityCondition.makeCondition(
				EntityCondition.makeCondition("userLoginId", EntityOperator.EQUALS, partyLogin), EntityOperator.AND,
				EntityCondition.makeCondition("groupId", EntityOperator.EQUALS, "ISSUEMGR_OWNER"))));*/

		EntityCondition securityCon = EntityCondition.makeCondition(EntityCondition.makeCondition("groupId", EntityOperator.EQUALS, "ISSUEMGR_OWNER"),EntityOperator.OR,
				EntityCondition.makeCondition("groupId", EntityOperator.EQUALS, "FULLADMIN"));
		EntityCondition cond = EntityCondition.makeCondition(UtilMisc.toList(EntityCondition.makeCondition(
				EntityCondition.makeCondition("userLoginId", EntityOperator.EQUALS, partyLogin), EntityOperator.AND,
				securityCon)));
		
		List usersList = delegator.findList("UserLoginSecurityGroup", cond, null, null, null, false);
		if (usersList.size() == 0) {
			request.setAttribute("_ERROR_MESSAGE_", "Cannot assign the Category to the selected Party. Party selected does not have Issue Owner Permission");
			return "error";
		}
		if (usersList.size() != 0)
		try {
			value = delegator.findOne("IssueCategory", false, "issueCategoryId", request.getParameter("issueCategoryId"));
		} catch (GenericEntityException e) {
			addDBError(request, e);
			return "success";
		}
		value.set("issueCategoryCaption", request.getParameter("issueCategoryCaption"));
		value.set("issueCategoryDescription", request.getParameter("issueCategoryDescription"));
		value.set("ownerId", request.getParameter("ownerId"));
		try {
			delegator.store(value);
		} catch (GenericEntityException e) {
			addDBError(request, e);
			return "success";
		}
		}
		request.setAttribute("_EVENT_MESSAGE_", "Updated Successfully");
		return "success";
	}

	public static String editSubCategory(HttpServletRequest request, HttpServletResponse response) {
		boolean noError = true;
		noError &= checkNotEmpty("issueSubCategoryCaption", "Display Caption ", request);
		noError &= checkNotEmpty("categoryId", "Base Owner ", request);
		noError &= checkNotEmpty("issueSubCategoryDescription", "Description ", request);
		if (!noError)
			return "success";

		GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
		GenericValue value = null;
		try {
			value = delegator.findOne("IssueSubCategory", false, "issueSubCategoryId", request.getParameter("issueSubCategoryId"));
		} catch (GenericEntityException e) {
			addDBError(request, e);
			return "success";
		}

		value.set("issueSubCategoryCaption", request.getParameter("issueSubCategoryCaption"));
		value.set("issueSubCategoryDescription", request.getParameter("issueSubCategoryDescription"));
		value.set("categoryId", request.getParameter("categoryId"));
		try {
			delegator.store(value);
		} catch (GenericEntityException e) {
			addDBError(request, e);
			return "success";
		}
		request.setAttribute("_EVENT_MESSAGE_", "Updated Successfully");
		return "success";
	}

	public static String getSubCategoryOptions(HttpServletRequest request, HttpServletResponse response) {
		GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
		String categoryId = request.getQueryString();
		EntityCondition condition = EntityCondition.makeCondition("categoryId", categoryId);
		List<GenericValue> subCats = null;
		try {
			subCats = delegator.findList("IssueSubCategory", condition, null, null, null, true);
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		List<String> subCategories = FastList.newInstance();

		for (GenericValue value : subCats)
			subCategories.add(value.get("issueSubCategoryId") + ":" + value.get("issueSubCategoryCaption"));

		if (subCategories.size() > 0) {
			subCategories.add(0, ":  ");
		}
		request.setAttribute("subCat", subCategories);
		return "success";
	}

	private static Map<String, Object> callService(String serviceName, Map<String, Object> paramMap, HttpServletRequest request) {
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Map<String, Object> resultMap = null;
		try {
		    Map<String,Object> serviceCtx = dispatcher.getDispatchContext().getModelService(serviceName).makeValid(paramMap, ModelService.IN_PARAM);
			resultMap = dispatcher.runSync(serviceName, serviceCtx);
		} catch (GenericServiceException e) {
			addError(request, "Error in calling service " + serviceName, e);
		}
		return resultMap;
	}
}