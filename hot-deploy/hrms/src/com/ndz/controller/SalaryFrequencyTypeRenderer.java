package com.ndz.controller;

import org.ofbiz.entity.GenericValue;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

public class SalaryFrequencyTypeRenderer implements ListitemRenderer{

	
	public void render(Listitem li, Object data) throws Exception {
		
		if (data instanceof GenericValue) {
		GenericValue val = (GenericValue) data;
		li.setLabel(val.getString("description"));
	     li.setValue(val.getString("salaryFrequencyTypeId"));
	     
		}
		
	}

}
