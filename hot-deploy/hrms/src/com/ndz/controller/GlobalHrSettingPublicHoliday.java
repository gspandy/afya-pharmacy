package com.ndz.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javolution.util.FastMap;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.humanresext.leave.HolidayActions;
import org.ofbiz.service.GenericDispatcher;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.ComponentNotFoundException;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.ndz.zkoss.util.HrmsInfrastructure;

public class GlobalHrSettingPublicHoliday extends GenericForwardComposer {
	private static final long serialVersionUID = 1L;

	public void onClick$btnSavePublicHoliday(Event event)
			throws GenericEntityException {

		try {

			GenericValue userLogin = (GenericValue) Executions.getCurrent()
					.getDesktop().getSession().getAttribute("userLogin");

			Component addNewPublicHoliday = event.getTarget();
			GenericDelegator delegator = (GenericDelegator) userLogin.getDelegator();
			String HolidayName = ((Textbox) addNewPublicHoliday
					.getFellow("txtBoxHolidayName")).getValue();
			System.out.println("********HolidayName********" + HolidayName);

			Date Date = (Date) ((Datebox) addNewPublicHoliday.getFellow("dateBoxDate")).getValue();
			java.sql.Timestamp fromDate = new java.sql.Timestamp(Date.getTime());
			System.out.println("********Date********" + fromDate);
			GenericValue holiday = delegator.makeValue("PublicHoliday");
			holiday.set("onDate", fromDate);
			holiday.set("description", HolidayName);
			delegator.create(holiday);
			Messagebox.show(Labels.getLabel("MessageDescription_SavedSuccessfully"),"Success",1,null);
			Events.postEvent("onClick$searchButton", addNewPublicHoliday
					.getPage().getFellow("searchPanel"), null);
			addNewPublicHoliday.detach();
		} catch (Exception e) {

			try {
				Messagebox.show(Labels.getLabel("MessageDescription_Fail"),"Error",1,null);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

	}

	public void onClick$btnNonWorkingDays(Event event)
			throws GenericEntityException {

		try {
			GenericValue userLogin = (GenericValue) Executions.getCurrent()
					.getDesktop().getSession().getAttribute("userLogin");

			Component addNewNonWorkingDays = event.getTarget();
			GenericDelegator delegator = (GenericDelegator) userLogin.getDelegator();
			Hbox hbox = (Hbox) addNewNonWorkingDays.getFellow("hr_Weekday");

			java.util.List<Component> children = hbox.getChildren();

			List<String> selectedDays = new ArrayList<String>();

			for (Component child : children) {
				if (child instanceof Checkbox) {
					Checkbox chkBox = (Checkbox) child;
					if (chkBox.isChecked())
						selectedDays.add(chkBox.getLabel());
				}
			}

			HolidayActions.storeNonWorkingDays(delegator, selectedDays
					.toArray(new String[selectedDays.size()]), selectedDays
					.size() > 0 ? false : true);
			Messagebox.show(Labels.getLabel("MessageDescription_SavedSuccessfully"),"Success",1,null);
			/*Events.postEvent("onClick$searchButton", addNewNonWorkingDays
					.getPage().getFellow("searchPanel"), null);*/
			addNewNonWorkingDays.detach();

		} catch (Exception e) {

			try {
				Messagebox.show(Labels.getLabel("MessageDescription_Fail"),"Success",1,null);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

	}

	public void delete(final Event event, final Date onDate) throws GenericServiceException, InterruptedException {
		Messagebox.show("Do You Want To Delete this Record?", "Warning", Messagebox.YES
				| Messagebox.NO, Messagebox.QUESTION, new EventListener() {
			public void onEvent(Event evt) {
				if ("onYes".equals(evt.getName())) {
		try{
		Component window = event.getTarget();

		GenericValue userLogin = (GenericValue) Executions.getCurrent()
				.getDesktop().getSession().getAttribute("userLogin");
		GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");

		Map<String, Object> context = UtilMisc.toMap("userLogin", userLogin,
				"onDate", onDate);

		LocalDispatcher dispatcher = HrmsInfrastructure.getDispatcher();//GenericDispatcher.getLocalDispatcher("default", delegator);
		dispatcher.runSync("deletePublicHoliday", context);
		Messagebox.show(Labels.getLabel("MessageDelete_Successful"),"Success",1,null);
		Events.postEvent("onClick$searchButton", window.getPage().getFellow(
				"searchPanel"), null);
		}
		catch (Exception e) {

			try {
				Messagebox.show(Labels.getLabel("MessageDelete_UnSuccessful"),"Error",1,null);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		}
				}
			}
		});
	}

}
