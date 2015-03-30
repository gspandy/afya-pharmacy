package org.ofbiz.humanresext.resume;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;

import com.smebiz.common.UploadException;
import com.smebiz.common.UtilAttachment;

/**
 * @author sandeep
 * 
 */

@SuppressWarnings("unchecked")
public class ResumeActions {

	public static String createPartyResume(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Map<String, Object> paramMap = null;

		try {
			paramMap = UtilAttachment.parseRequest(request);
		} catch (UploadException e) {
			String errMsg = UtilProperties.getMessage("HumanResExtUiLabels",
					"UploadError", request.getLocale());
			request.setAttribute("_ERROR_MESSAGE_", errMsg);
			return "error";
		}

		paramMap.put("userLogin", request.getSession()
				.getAttribute("userLogin"));

		List<FileItem> contentToUpload = (List<FileItem>) paramMap
				.remove(UtilAttachment.UPLOADCONTENTKEY);

		String fileName = contentToUpload.size() > 0 ? UtilAttachment
				.getFileName(contentToUpload.get(0)) : null;
		String uploadLocation = UtilProperties.getPropertyValue(
				"Humanresext.properties", "uploadLocation");

		paramMap.put("fileName", fileName);
		paramMap.put("uploadLocation", uploadLocation);
		LocalDispatcher dispatcher = (LocalDispatcher) request
				.getAttribute("dispatcher");
		try {
			dispatcher.runSync("createPartyResume", paramMap);
		} catch (GenericServiceException e) {
			request.setAttribute("_ERROR_MESSAGE_", "Error in calling service "
					+ "createPartyResume");
			return "error";
		}

		for (FileItem fileItem : contentToUpload) {
			UtilAttachment.uploadAttachment(uploadLocation, paramMap.get(
					"partyId").toString(), fileItem);
		}

		request.setAttribute("_EVENT_MESSAGE_", "Resume creation successful");
		return "success";
	}

	public static String downloadResume(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String resumeId = request.getQueryString();
		GenericDelegator delegator = (GenericDelegator) request
				.getAttribute("delegator");
		GenericValue resume = null;
		try {
			resume = delegator.findOne("PartyResume", false, "resumeId",
					resumeId);
		} catch (GenericEntityException e) {
			request.setAttribute("_ERROR_MESSAGE_",
					"Attachment corrupted and could not be loaded");
			return "error";
		}
		String apparentName = (String) resume.get("fileName");
		String actualName = resume.get("partyId")
				+ apparentName.substring(apparentName.lastIndexOf('.'));
		String path = (String) resume.get("uploadLocation");
		try {
			UtilAttachment.downloadAttachment(path, actualName, response,delegator);
		} catch (IOException e) {
			request.setAttribute("_ERROR_MESSAGE_", apparentName
					+ "could not restored probably corrupted or relocated");
		}
		return "success";
	}
}