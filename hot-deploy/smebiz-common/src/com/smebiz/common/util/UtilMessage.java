package com.smebiz.common.util;

import java.text.ParseException;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.string.FlexibleStringExpander;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.service.ServiceUtil;

public class UtilMessage {

	
	public static Map createAndLogServiceError(String msg,Locale locale,String module){
		return ServiceUtil.returnError(msg);
	}
	
	public static Map createAndLogServiceError(Map results,String msg,Locale locale,String module){
		results.put("error_msg", msg);
		return results;
	}
	
	public static Map createAndLogServiceError(Exception t,String msg,Locale locale,String module){
		return ServiceUtil.returnError(msg + t.getMessage());
	}

	 /**
     * Adds a <b>service</b> error message to <b>toplevel</b> and logs it as an ERROR.
     * Returns "error" as a convenience.
     */
    public static String createAndLogEventError(String label, Map serviceResults, Locale locale, String _module) {
        String errorMsg = ServiceUtil.getErrorMessage(serviceResults);
       return "error "+errorMsg;
    }

	public static Map createAndLogServiceError(String errorMessage, String string2,
			Locale locale, String module) {
		// TODO Auto-generated method stub
		return ServiceUtil.returnError(errorMessage);
	}

	public static Map createAndLogServiceError(String errorMessage,
			Map map, Locale locale, String module) {
		// TODO Auto-generated method stub
		Map results = ServiceUtil.returnError(errorMessage);
		results.putAll(map);
		return results;
	}

	public static Map createAndLogServiceError(ParseException pe,
			Locale locale, String module) {
		
		return ServiceUtil.returnError(pe.getMessage());
	}

	public static String createAndLogEventError(HttpServletRequest request,
			String string, Locale locale, String module) {
		
		return "error";
	}

	public static String createAndLogEventError(HttpServletRequest request,
			GenericEntityException e, Locale locale, String module) {
		// TODO Auto-generated method stub
		return "error";
	}


	public static String createAndLogEventError(HttpServletRequest request,
			String string, Map input, Locale locale, String module) {
		// TODO Auto-generated method stub
		return "error";
	}

	public static String createAndLogEventError(HttpServletRequest request,
			Exception e, Locale locale, String module) {
		// TODO Auto-generated method stub
		return "error";
	}

	public static Map createAndLogServiceError(Map ensureResult, String module) {
		// TODO Auto-generated method stub
		Map returnVal =  ServiceUtil.returnError(module);
		returnVal.putAll(ensureResult);
		return returnVal;
	}

	public static Map createAndLogServiceError(Throwable ex,
			Locale locale, String module) {
		// TODO Auto-generated method stub
		Map returnVal =  ServiceUtil.returnError(ex.getMessage());
		return returnVal;
}


}
