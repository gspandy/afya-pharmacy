package com.ndz.component.party;

import org.ofbiz.entity.GenericValue;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

public class CurrencyRenderer implements ListitemRenderer{
	String selectedCurrency=null;

	public CurrencyRenderer(){}
	
	public CurrencyRenderer(String selectedCurrency){
		this.selectedCurrency=selectedCurrency;
		
	}
	
	public void render(Listitem li, Object data) throws Exception {
		if (data instanceof GenericValue) {
		GenericValue val = (GenericValue) data;
	     li.setLabel(val.getString("description"));
	     li.setValue(val.getString("uomId"));
	     if(selectedCurrency!=null&&selectedCurrency.equals(val.getString("uomId"))){
	    	 li.setSelected(true);
	    	 ((Listbox)li.getParent()).setDisabled(true);
	     }
	     System.out.println(val.getString("uomId"));
		}
	}

}
