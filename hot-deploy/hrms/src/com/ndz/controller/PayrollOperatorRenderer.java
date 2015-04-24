package com.ndz.controller;

/**
 * @author samir
 */
import org.ofbiz.entity.GenericValue;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

public class PayrollOperatorRenderer implements ListitemRenderer {

	public void render(Listitem li, Object data) throws Exception {
		// TODO Auto-generated method stub

		if (data instanceof GenericValue) {
			GenericValue val = (GenericValue) data;
			li.setLabel(val.getString("operatorId"));
			li.setValue(val.getString("operatorId"));
		}
	}

}
