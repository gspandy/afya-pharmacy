package com.ndz.zkoss.util;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.zkoss.util.Locales;
import org.zkoss.util.TimeZones;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.metainfo.Annotation;
import org.zkoss.zk.ui.sys.ComponentCtrl;
import org.zkoss.zkplus.databind.TypeConverter;

public class DateTimeFormater implements TypeConverter {

	public Object coerceToBean(Object arg0, Component arg1) {
		return null;
	}

	public Object coerceToUi(Object val, Component comp) {
		Date date = null;
		if (val instanceof Timestamp) {
			final Timestamp timestamp = (Timestamp) val;
			date = new Date(timestamp.getTime());
		} else if (val instanceof Date) {
			date = (Date) val;
		}

		final Annotation annot = ((ComponentCtrl) comp).getAnnotation("format");
		String pattern = null;
		if (annot != null) {
			pattern = annot.getAttribute("value");
		}

		if (date == null)
			return "";

		// prepare dateFormat and convert Date to String
		final SimpleDateFormat df = new SimpleDateFormat(
				pattern == null ? "dd-MM-yyyy h:mm a" : pattern, Locales.getCurrent());
		df.setTimeZone(TimeZones.getCurrent());
		return df.format(date);
	}

}
