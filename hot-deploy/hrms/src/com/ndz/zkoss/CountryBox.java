package com.ndz.zkoss;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zkplus.databind.BindingListModelList;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.ComboitemRenderer;

public class CountryBox extends Combobox {
	private static final long serialVersionUID = 1L;

	public CountryBox() throws GenericEntityException {
		super();
		GenericDelegator delegator = (GenericDelegator)((HttpServletRequest)Executions.getCurrent().getNativeRequest()).getAttribute("delegator");
		//List data=delegator.findList("CountryCode", null, null, null, null, false);
		List data=delegator.findByAnd("Geo",UtilMisc.toMap("geoTypeId","COUNTRY"));
		this.setModel(new BindingListModelList(data,false));
		this.setItemRenderer(getComboitemRenderer());
		this.setReadonly(true);
		this.setWidth("250px");
	}
	
	private ComboitemRenderer getComboitemRenderer(){
		return new ComboitemRenderer() {
			public void render(Comboitem comboitem, Object object) throws Exception {
				GenericValue gv = (GenericValue)object;
				//comboitem.setLabel(gv.getString("countryName"));
				//comboitem.setValue(gv.getString("countryAbbr"));
				comboitem.setLabel(gv.getString("geoName"));
				comboitem.setValue(gv.getString("geoId"));
				
			}
		};
	}

}
