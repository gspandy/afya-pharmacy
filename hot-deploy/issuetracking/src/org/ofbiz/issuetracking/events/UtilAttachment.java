package org.ofbiz.issuetracking.events;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang.StringUtils;
import org.ofbiz.base.util.FileUtil;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;


/**
 * @author sandeep
 * 
 */
public class UtilAttachment {

	public static final String UPLOADCONTENTKEY = "contentToUpload";

	@SuppressWarnings("unchecked")
	public static Map<String, Object> parseRequest(HttpServletRequest request)
			throws UploadException {
		ServletFileUpload sfu = new ServletFileUpload(new DiskFileItemFactory(
				10240, FileUtil.getFile("runtime/tmp")));
		java.util.List<FileItem> list = null;
		try {
			list = sfu.parseRequest(request);
		} catch (FileUploadException e4) {
			throw new UploadException(e4.getMessage());
		}

		Map<String, Object> paramMap = FastMap.newInstance();
		List<FileItem> contentToUpload = FastList.newInstance();
		paramMap.put(UPLOADCONTENTKEY, contentToUpload);
		for (FileItem fileItem : list) {
			if (fileItem.isFormField()) {
				paramMap.put(fileItem.getFieldName(), fileItem.getString());
			} else {
				contentToUpload.add(fileItem);
			}
		}
		return paramMap;
	}

	public static String getFileName(FileItem fileItem) throws Exception {
		String fileName = "";
		Field nameField = fileItem.getClass().getDeclaredField("fileName");
		nameField.setAccessible(true);
		fileName = (String) nameField.get(fileItem);
		return fileName;
	}

	public static void uploadAttachment(String folder, String fileName,
			FileItem fileItem) throws Exception {
		String actualFileName = getFileName(fileItem);
		if (StringUtils.isBlank(actualFileName)) {
			return;
		}
		String extn = actualFileName.substring(actualFileName.lastIndexOf('.'));
		File file = new File(folder, fileName + extn);
		file.createNewFile();
		fileItem.write(file);
	}

	public static void downloadAttachment(String folder, String fileName,
			HttpServletResponse response,GenericDelegator delegator) throws IOException {
		File file = new File(folder, fileName);
		response.setContentType(getMimeType(fileName,delegator));
		FileInputStream fis = new FileInputStream(file);
		OutputStream os = response.getOutputStream();
		int c;
		while ((c = fis.read()) != -1) {
			os.write(c);
		}
		try {
			os.flush();
		} catch (Throwable e) {
			// Intentionally consuming it :)---- occurs when client cancels the
			// download --- Sandeep
		}
	}
	
	public static void downloadAttachment(HttpServletResponse response,HttpServletRequest request,String folder, String fileName) 
				throws IOException {
		File file = new File(folder, fileName);
		response.setContentType(getMimeType(request,fileName));
		FileInputStream fis = new FileInputStream(file);
		OutputStream os = response.getOutputStream();
		int c;
		while ((c = fis.read()) != -1) {
			os.write(c);
		}
		try {
			os.flush();
		} catch (Throwable e) {
			// Intentionally consuming it :)---- occurs when client cancels the
			// download --- Sandeep
		}
	}
	

	public static String getMimeType(String fileName,GenericDelegator delegator) {
		String mimeType = null;
		String extn = fileName.substring(fileName.lastIndexOf('.') + 1);
		try {
			EntityCondition cn1 = EntityCondition.makeCondition("fileExtensionId",EntityOperator.EQUALS,extn);
			EntityCondition cn2 = EntityCondition.makeCondition("fileExtensionId",EntityOperator.EQUALS,extn.toLowerCase());
			List<GenericValue> extensionList = delegator.findList("FileExtension", EntityCondition.makeCondition(cn1,EntityOperator.OR,cn2), 
						null, null, null, true);
			GenericValue extension = EntityUtil.getFirst(extensionList);
			mimeType = extension == null ? null : (String) extension
					.get("mimeTypeId");
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return mimeType != null ? mimeType : "application/octet-stream";
	}
	
	public static String getMimeType(HttpServletRequest request, String fileName) {
		String mimeType = null;
		GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
		String extn = fileName.substring(fileName.lastIndexOf('.') + 1);
		try {
			EntityCondition cn1 = EntityCondition.makeCondition("fileExtensionId",EntityOperator.EQUALS,extn);
			EntityCondition cn2 = EntityCondition.makeCondition("fileExtensionId",EntityOperator.EQUALS,extn.toLowerCase());
			List<GenericValue> extensionList = delegator.findList("FileExtension", EntityCondition.makeCondition(cn1,EntityOperator.OR,cn2), 
						null, null, null, true);
			GenericValue extension = EntityUtil.getFirst(extensionList);
			mimeType = extension == null ? null : (String) extension
					.get("mimeTypeId");
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return mimeType != null ? mimeType : "application/octet-stream";
	}
}