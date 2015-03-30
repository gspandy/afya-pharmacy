package com.ndz.component;
import java.util.Date;

import org.ofbiz.base.util.UtilDateTime;
import org.zkoss.zkplus.databind.TypeConverter;

public class DateConverter implements TypeConverter {

	public Object coerceToBean(java.lang.Object val, org.zkoss.zk.ui.Component comp) {
	return null;
	}
	public Object coerceToUi(java.lang.Object val, final org.zkoss.zk.ui.Component comp) {
		Date d = (Date)val;
		if(val==null)return "";
		return UtilDateTime.formatDate(d);
	}

}
