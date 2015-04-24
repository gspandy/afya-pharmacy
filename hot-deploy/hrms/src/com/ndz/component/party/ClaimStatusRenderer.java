package com.ndz.component.party;

import org.ofbiz.entity.GenericValue;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

public class ClaimStatusRenderer implements ListitemRenderer{


	public void render(Listitem li, Object data) throws Exception {
		GenericValue val = (GenericValue) data;
		
	     li.setLabel(val.getString("description"));
	     li.setValue(val.getString("statusId"));
		
	}

}
