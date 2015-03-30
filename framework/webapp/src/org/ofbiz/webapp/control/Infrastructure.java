package org.ofbiz.webapp.control;

import javax.servlet.http.HttpServletRequest;

import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.security.Security;
import org.ofbiz.service.GenericDispatcher;

public final class Infrastructure{
	
	private static final ThreadLocal<HttpServletRequest> REQUESTTHREADLOCAL = new ThreadLocal<HttpServletRequest>();
	
	public static void setRequest(HttpServletRequest request){
	REQUESTTHREADLOCAL.set(request);
	}

	public static HttpServletRequest getRequest(){
		return REQUESTTHREADLOCAL.get();
	}
	
	public static GenericDelegator getDelegator(){
		return (GenericDelegator) getRequest().getAttribute("delegator");
	}
	public static Security getSecurity(){
		return (Security) getRequest().getAttribute("security");
	}
	public static GenericDispatcher getDispatcher(){
		return (GenericDispatcher) getRequest().getAttribute("dispatcher");
	}
	
}