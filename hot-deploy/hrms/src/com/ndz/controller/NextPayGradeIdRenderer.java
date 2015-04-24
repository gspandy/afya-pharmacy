package com.ndz.controller;

import org.ofbiz.entity.GenericValue;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

public class NextPayGradeIdRenderer implements ListitemRenderer{

	
	public void render(Listitem li, Object data) throws Exception {
		
		if (data instanceof GenericValue) {
		GenericValue val = (GenericValue) data;
		li.setLabel(val.getString("payGradeName"));
	     li.setValue(val.getString("payGradeId"));
	     
		}
		
	}

}
