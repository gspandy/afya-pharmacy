package com.ndz.component;

import java.util.List;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.zkoss.zk.ui.Component;
import org.zkoss.zkplus.databind.TypeConverter;

import com.ndz.zkoss.util.HrmsInfrastructure;

public class StatusItemConverter implements TypeConverter{

	@Override
	public Object coerceToBean(Object arg0, Component arg1) {
	return null;
	}

	@Override
	public Object coerceToUi(Object object, Component component) {
	String statusId = (String)object;
	GenericDelegator delegator  = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
	try {
		List<GenericValue> statusItemGvs = delegator.findByAnd("StatusItem", UtilMisc.toMap("statusId", statusId));
		if(UtilValidate.isNotEmpty(statusItemGvs))
			return statusItemGvs.get(0).getString("description");
	} catch (GenericEntityException e) {
		e.printStackTrace();
	}
	return null;
	}

}
