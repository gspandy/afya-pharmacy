package org.ofbiz.issuetracking.util;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import javolution.util.FastList;

/**
 * @author sandeep
 *
 */
public class ErrorUtil {

	public static boolean checkNotEmpty(String field, String fieldCaption,HttpServletRequest request){
		String value = request.getParameter(field);
		if(value == null || value.trim().length() == 0){
			addError(request, fieldCaption + " Cannot be empty", null);
			return false;
		}
		return true;
	}

	public static void addDBError(HttpServletRequest request, Exception e){
		addError(request, "Action could not be performed successfully because of some DB problem", e);
	}

	@SuppressWarnings("unchecked")
	public static void addError(HttpServletRequest request, String message, Exception e){
		List<String> errors = (List<String>)request.getAttribute("_ERROR_MESSAGE_LIST_");
		if(errors == null){
			errors = FastList.newInstance();
			request.setAttribute("_ERROR_MESSAGE_LIST_", errors);
		}
		errors.add(message);
		if(e != null)
			e.printStackTrace();
	}
}
