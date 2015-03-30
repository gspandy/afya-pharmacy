package com.ndz.controller;

import java.util.Date;
import java.util.Map;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Bandbox;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;

import com.ndz.zkoss.util.HrmsInfrastructure;

public class TaxSlab extends GenericForwardComposer {

	public static void onClick$btnCreate(Event event) {
		Component comp = event.getTarget();
		GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
		GenericDelegator delegator = (GenericDelegator) userLogin.getDelegator();

		Integer slabMax = ((Intbox) comp.getFellow("slabMax")).getValue();
		Integer slabMin = ((Intbox) comp.getFellow("slabMin")).getValue();
		Integer taxRate = ((Intbox) comp.getFellow("taxRate")).getValue();
		String geoId = (String) ((Combobox) comp.getFellow("searchPanel")).getSelectedItem().getValue();

		Listitem taxTypeItem = ((Listbox) comp.getFellow("taxType")).getSelectedItem();
		String taxType = (String) taxTypeItem.getValue();

		Listitem personTypeItem = ((Listbox) comp.getFellow("personType")).getSelectedItem();
		String personType = (String) personTypeItem.getValue();

		Date fromDateInput = (Date) ((Datebox) comp.getFellow("fromDate")).getValue();

		java.sql.Date fromDate = new java.sql.Date(fromDateInput.getTime());

		Date thruDateInput = (Date) ((Datebox) comp.getFellow("thruDate")).getValue();

		java.sql.Date thruDate = null;
		if (thruDateInput != null)
			thruDate = new java.sql.Date(thruDateInput.getTime());

		Map context = UtilMisc.toMap("taxType", taxType, "personType", personType, "geoId", geoId, "slabMax",Double.valueOf(slabMax), "slabMin",Double.valueOf(slabMin), "taxRate",Double.valueOf(taxRate),
				"fromDate", fromDate, "thruDate", thruDate);

		try {
			delegator.create("TaxSlab", context);
			Messagebox.show("Tax Slab Successfully Created ", "Success", 1, null);
			if (comp.getPage().getFellowIfAny("searchPanel") != null)
				Events.postEvent("onClick$searchButton", comp.getPage().getFellow("searchPanel"), null);
			comp.getFellow("createTaxSlab").detach();
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void deleteTaxSlab(final GenericValue gv, final Button searchPerCompany) throws InterruptedException {
		Messagebox.show("Do You Want To Delete this Record?", "Warning", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION, new EventListener() {
			public void onEvent(Event evt) {
				if ("onYes".equals(evt.getName())) {
					try {
						GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
						GenericValue deleteRecord = delegator.makeValue("TaxSlab", UtilMisc.toMap("geoId", gv.getString("geoId"), "personType",
								gv.getString("personType"), "taxType", gv.getString("taxType"), "slabMin", gv.getDouble("slabMin"), "fromDate",
								gv.getDate("fromDate")));
						delegator.removeValue(deleteRecord);
						Events.postEvent("onClick",searchPerCompany,null);
						Messagebox.show("Deleted Successfully","Success",1,null);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});
	}

}
