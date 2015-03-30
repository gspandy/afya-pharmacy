package com.ndz.controller;

import org.ofbiz.entity.GenericValue;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

public class itemTypeIdListBoxRenderer implements ListitemRenderer {

	public void render(Listitem li, Object data) throws Exception {

		GenericValue val = (GenericValue) data;
		if (val != null) {
			li.setLabel(val.getString("description"));
			li.setValue(val.getString("itemTypeId"));
		}

	}

}
