package com.smebiz.dynamicform.controller;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.smebiz.dynamicform.FormCategory;
import com.smebiz.dynamicform.input.FormInput;

public class FormLayoutController {

	public static String loadFormLayout(HttpServletRequest req,
			HttpServletResponse resp) {
		HttpSession session = req.getSession(false);
		String formId = req.getParameter("formId");
		if (formId != null && formId.length() > 1) {
			session.setAttribute("formId", formId);
		}
		return "success";
	}
	
	public static String doProcess(HttpServletRequest req,
			HttpServletResponse resp) {
	
		String inputId = req.getParameter("dynamicRuleId");
		if(inputId!=null && inputId.length()>0){
			prepareData(inputId,req);
			return "next";
		}else{
			return "success";
		}
		
	}

	private static void prepareData(String inputId,HttpServletRequest req) {
		ServletContext ctx = (ServletContext)req.getAttribute("servletContext");
		FormCategory root = (FormCategory)ctx.getAttribute("dynaFormCategories");
		
		FormInput input = null;
		for(FormCategory cat : root.getCategories()){
			for(FormInput in:cat.getInputs()){
				if(in.getId().equals(inputId)){
					input = in;
					break;
				}
			}
		}
		
		req.setAttribute("selectedFormInput", input);
		if(FormInput.TABULAR.equals(input.getOutputType())){
			String column = input.getColumns();
			req.setAttribute("COLUMNS",column.split(","));
		}
	}
}
