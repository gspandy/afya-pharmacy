package com.ndz.zkoss.handler;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ofbiz.webapp.view.ViewHandler;
import org.ofbiz.webapp.view.ViewHandlerException;

public class ZULViewHandler implements ViewHandler {

	private static final Log log = LogFactory.getLog(ZULViewHandler.class);
	private ServletContext _ctx;

	public String getName() {
		return null;
	}

	public void init(ServletContext context) throws ViewHandlerException {
		_ctx = context;
	}

	public void render(String name, String page, String info,
			String contentType, String encoding, HttpServletRequest request,
			HttpServletResponse response) throws ViewHandlerException {

		RequestDispatcher dispatcher = request.getRequestDispatcher(page);

		try {
			dispatcher.forward(request, response);
		} catch (ServletException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void setName(String name) {

	}

}
