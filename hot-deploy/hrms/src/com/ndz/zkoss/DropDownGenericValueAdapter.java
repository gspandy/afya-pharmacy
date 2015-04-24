package com.ndz.zkoss;

import org.ofbiz.entity.GenericValue;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.ComboitemRenderer;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

public class DropDownGenericValueAdapter implements ListitemRenderer,ComboitemRenderer{
	
	private String labelColumn;
	
	private String valueColumn;
	
	public DropDownGenericValueAdapter(String label) {
		this.labelColumn = label;
	}

	public DropDownGenericValueAdapter(String label, String value) {
		this(label);
		this.valueColumn = value;
	}

	public void render(Listitem listitem, Object o) throws Exception {
		if (!(o instanceof GenericValue))
			return; 
		GenericValue entity = (GenericValue)o;
		listitem.setLabel(entity.getString(labelColumn));
		listitem.setValue( (valueColumn != null) ? entity.getString(valueColumn) : entity);
	}

	
	public void render(Comboitem comboitem, Object o) throws Exception {
		if (!(o instanceof GenericValue))
			return; 
		GenericValue entity = (GenericValue)o;
		comboitem.setLabel(entity.getString(labelColumn));
		comboitem.setValue( (valueColumn != null) ? entity.getString(valueColumn) : entity);
		
	}
}