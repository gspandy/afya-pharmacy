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

public class PositionTypeConverter implements TypeConverter{

	@Override
	public Object coerceToBean(Object arg0, Component arg1) {
	return null;
	}

	@Override
	public Object coerceToUi(Object arg0, Component arg1) {
	String emplPositionTypeId = (String)arg0;
	GenericDelegator delegator  = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
	try {
		List<GenericValue> posTypesGvs  = delegator.findByAnd("EmplPositionType", UtilMisc.toMap("emplPositionTypeId", emplPositionTypeId));
		if(UtilValidate.isNotEmpty(posTypesGvs))
			return posTypesGvs.get(0).getString("description");
	} catch (GenericEntityException e) {
		e.printStackTrace();
	}
	return null;
	}

}
