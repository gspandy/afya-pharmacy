package com.ndz.component.party;

import org.ofbiz.entity.GenericValue;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

public class EmployeePositionTypeRenderer implements ListitemRenderer {

	public void render(Listitem li, Object data) throws Exception {
		if (data instanceof GenericValue) {
			GenericValue val = (GenericValue) data;
			if (val != null) {
				li.setLabel(val.getString("description"));
				li.setValue(val.getString("emplPositionTypeId"));
			}

		}
	}
}
