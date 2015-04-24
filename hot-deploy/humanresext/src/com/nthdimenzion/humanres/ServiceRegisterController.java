package com.nthdimenzion.humanres;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class ServiceRegisterController {

	public static String execute(HttpServletRequest request,
			HttpServletResponse response) {
		HttpSession session = request.getSession();
		ServletContext context = session.getServletContext();

		ServletContext context1 = context.getContext("/formdesign");
		
		System.out.println(" CONTEXT "+context1.getServletContextName());

		try {
			response.getWriter().write(" SERVICE REGISTER CONTROLLER ");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return "success";
	}
}
