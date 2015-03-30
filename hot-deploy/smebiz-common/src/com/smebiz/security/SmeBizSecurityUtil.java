package com.smebiz.security;

import javax.servlet.http.HttpSession;

import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.security.Security;
import org.ofbiz.security.SecurityConfigurationException;
import org.ofbiz.webapp.control.Infrastructure;

public class SmeBizSecurityUtil {
	
	
	
	public static boolean hasEntityPermission(String[] entities, String action,
			HttpSession session) {
		GenericValue userLogin = (GenericValue) session
				.getAttribute("userLogin");
		
		if (userLogin == null) {
			return false;
		}
		for (String entity : entities)
			if (getSecurity(Infrastructure.getDelegator()).hasEntityPermission(entity, action, userLogin))
				return true;
		return false;
	}

	public static boolean hasEntityPermission(String[] entities, String action,
			GenericValue userLogin) {

		if (userLogin == null) {
			return false;
		}
		for (String entity : entities)
			if (getSecurity(Infrastructure.getDelegator()).hasEntityPermission(entity, action, userLogin))
				return true;
		return false;
	}
	
	private static Security getSecurity(GenericDelegator delegator){
	Security security=null;
	try {
		security = org.ofbiz.security.SecurityFactory.getInstance(delegator);
	} catch (SecurityConfigurationException e) {
		e.printStackTrace();
		throw new RuntimeException( " Exception "+e.getMessage());
	}
	return security;
	}
}
