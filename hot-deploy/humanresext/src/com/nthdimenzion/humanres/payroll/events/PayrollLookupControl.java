package com.nthdimenzion.humanres.payroll.events;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang.StringUtils;

public class PayrollLookupControl {

	public static String execute(HttpServletRequest request, HttpServletResponse response) {
		request.setAttribute("expr", "Y");
		String s = request.getParameter("lookupType");
		if (StringUtils.isBlank(s)) {
			return "success";
		} else
			return s;
	}
}
