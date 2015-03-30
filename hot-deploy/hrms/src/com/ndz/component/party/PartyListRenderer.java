package com.ndz.component.party;

import org.ofbiz.entity.GenericValue;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.api.Listitem;

public  class PartyListRenderer implements ListitemRenderer{

	
	public void render(org.zkoss.zul.Listitem li, Object data)
			throws Exception {
		// TODO Auto-generated method stub
		GenericValue val = (GenericValue) data;
		
     li.setLabel(val.getString("partyIdTo"));
     li.setValue(val.getString("partyIdTo"));
    
		
	}
	
	
	
	
}