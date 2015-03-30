package com.ndz.zkoss.util;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import org.zkoss.zk.ui.Component;
import org.zkoss.zkplus.databind.TypeConverter;
import org.zkoss.zul.Listcell;

public class CurrencyFormatter implements TypeConverter {

	public static String format(Double d) {
		if(d==null)return "";
		NumberFormat formatter = DecimalFormat.getInstance();
//		formatter.setRoundingMode(RoundingMode.HALF_UP);
		formatter.setGroupingUsed(true);
		formatter.setMaximumFractionDigits(2);
		formatter.setMinimumFractionDigits(2);
		return formatter.format(d);
	}

	public Object coerceToBean(Object arg0, Component arg1) {
		return null;
	}

	public Object coerceToUi(Object arg0, Component arg1) {
		if(arg0 instanceof Double){
			if(arg1 instanceof Listcell)
				((Listcell)arg1).setStyle("text-align:right");
			return format((Double)arg0);
		}
		return arg0;
	}
}
