package com.smebiz.sfa.util;

import java.util.Enumeration;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.service.LocalDispatcher;

public class UtilCommon {

	public static final String module = UtilCommon.class.getName();

	/**
	 * Get a request parameter, or null if the string was empty. This simplifies
	 * the trinary logic of empty strings vs null strings into a simple boolean,
	 * either the string exists and has content or it is null.
	 */
	public static String getParameter(HttpServletRequest request,
			String parameterName) {
		String result = request.getParameter(parameterName);
		if (result == null)
			return null;
		result = result.trim();
		if (result.length() == 0)
			return null;
		return result;
	}

	/**
	 * Check if a request has an error set.
	 */
	public static Boolean hasError(HttpServletRequest request) {
		Enumeration<String> attrs = request.getAttributeNames();
		while (attrs.hasMoreElements()) {
			String a = attrs.nextElement();
			if ("_ERROR_MESSAGE_LIST_".equals(a) || "_ERROR_MESSAGE_".equals(a)
					|| "opentapsErrors".equals(a)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * This method will read the donePage parameter and return it to the
	 * controller as the result. It is called from controller.xml using <event
	 * type="java" path="com.smebiz.sfaext.util.UtilCommon"
	 * invoke="donePageRequestHelper"/> Then it can be used with <response
	 * name="returnValue" .../> to determine what to do next.
	 * 
	 * @return The donePage parameter if it exists, otherwise "error"
	 */
	public static String donePageRequestHelper(HttpServletRequest request,
			HttpServletResponse response) {
		GenericDelegator delegator = (GenericDelegator) request
				.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request
				.getAttribute("dispatcher");
		Map parameters = UtilHttp.getParameterMap(request);

		String donePage = (String) parameters.get("donePage");
		if (donePage == null) {
			donePage = (String) parameters.get("DONE_PAGE");
		}
		if (donePage == null) {
			// special case after service-multi
			Set<String> keys = parameters.keySet();
			for (String current : keys) {
				if (current.startsWith("donePage")) {
					donePage = (String) parameters.get(current);
					break;
				}
			}
		}
		if (donePage == null) {
			donePage = "error";
		}

		String errorPage = getParameter(request, "errorPage");
		if (errorPage != null && hasError(request)) {
			Debug.logInfo("donePageRequestHelper: goto errorPage [" + errorPage
					+ "]", module);
			return errorPage;
		}

		Debug.logInfo(
				"donePageRequestHelper: goto donePage [" + donePage + "]",
				module);
		return donePage;
	}

}
