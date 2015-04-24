package com.ndz.zkoss.util;

import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.LocalDispatcher;
import org.zkoss.zk.ui.Executions;

public class HrmsInfrastructure {
	
	public static GenericDelegator getDelegator(){
		return	(GenericDelegator) ((GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin")).getDelegator();
	}
	
	public static LocalDispatcher getDispatcher(){
		return (LocalDispatcher) Executions.getCurrent().getAttributes().get("dispatcher");
	}

}
