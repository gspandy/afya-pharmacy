package org.ofbiz.humanresext.employmentapp;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.LocalDispatcher;

import com.smebiz.common.UploadException;
import com.smebiz.common.UtilAttachment;


/**
 * @author sandeep
 *
 */
public class EmploymentAppActions {
	
	private static final String RESOURCE = "Humanresext.properties";
	private static final String LOCATION = "referalUploadLocation";
	
	@SuppressWarnings("unchecked")
	public static String createEmploymentApp(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Map<String, Object> employmentAppParams = null;
		try {
			employmentAppParams = UtilAttachment.parseRequest(request);
		} catch (UploadException e) {
			String errMsg = UtilProperties.getMessage("IssueErrorLabels","UploadError", request.getLocale());
			request.setAttribute("_ERROR_MESSAGE_", errMsg);
			return "error";
		}
		List<FileItem> contentToUpload = (List<FileItem>) employmentAppParams.remove(UtilAttachment.UPLOADCONTENTKEY);
		FileItem attachment = null;
		String attachmentName = null;
		employmentAppParams.put("userLogin", request.getSession().getAttribute("userLogin"));
		if(contentToUpload != null && contentToUpload.size() > 0){
			attachment = contentToUpload.get(0);
			attachmentName = UtilAttachment.getFileName(attachment);			
			employmentAppParams.put("fileName", attachmentName);
		}
		
		LocalDispatcher dispatcher = (LocalDispatcher)request.getAttribute("dispatcher");
		Map<String, Object> result = dispatcher.runSync("createEmploymentApp", employmentAppParams);
		String applicationId = (String)result.get("applicationId");
		String uploadLocation = UtilProperties.getPropertyValue(RESOURCE, LOCATION);
		if(attachmentName != null && applicationId != null){
			UtilAttachment.uploadAttachment(uploadLocation, applicationId, attachment);
		}
		return "success";
	}

	public static String downloadEmploymentApp(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String applicationId = request.getQueryString();
		String downloadLocation = UtilProperties.getPropertyValue(RESOURCE, LOCATION);
		GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
		GenericValue attachment = null ;
		try {
			attachment = delegator.findOne("EmploymentApp", false, "applicationId", applicationId);
		} catch (GenericEntityException e) {
			request.setAttribute("_ERROR_MESSAGE_", "Attachment corrupted and could not be loaded");
			return "error";
		}
		String apparentName = (String) attachment.get("fileName");
		String actualName = applicationId;
		if(apparentName.lastIndexOf('.') != -1)
			actualName = actualName + apparentName.substring(apparentName.lastIndexOf('.'));
		UtilAttachment.downloadAttachment(downloadLocation, actualName, response,(GenericDelegator)request.getAttribute("delegator"));
		return "success";
	}
}
