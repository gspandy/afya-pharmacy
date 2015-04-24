package com.ndz.component.party;

import org.ofbiz.entity.GenericValue;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

public class SalaryHeadRenderer implements ListitemRenderer{

	
	public void render(Listitem li, Object data) throws Exception {
		
		if (data instanceof GenericValue) {
		GenericValue val = (GenericValue) data;
	     li.setValue(val.getString("hrName"));		
	     li.setValue(val.getString("salaryHeadId"));
		}
		
	}

}
