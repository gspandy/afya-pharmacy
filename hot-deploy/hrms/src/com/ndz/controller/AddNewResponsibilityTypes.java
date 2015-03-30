package com.ndz.controller;

import java.util.Map;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.GenericDispatcher;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.ndz.zkoss.util.HrmsInfrastructure;

public class AddNewResponsibilityTypes extends GenericForwardComposer {

	private static final long serialVersionUID = 1L;

	public void onClick$btnSave(Event event)throws GenericEntityException {

		try {

			GenericValue userLogin = (GenericValue) Executions.getCurrent()
					.getDesktop().getSession().getAttribute("userLogin");

			Component addNewResponsibilityTypes = event.getTarget();
			GenericDelegator delegator = (GenericDelegator) userLogin.getDelegator();

			String ResponsibilityTypeId = ((Textbox) addNewResponsibilityTypes
					.getFellow("txtBoxResponsibilityTypeId")).getValue();
			String Description = ((Textbox) addNewResponsibilityTypes
					.getFellow("txtBoxDescription")).getValue();
			Map<String,Object> context = UtilMisc.toMap("userLogin", userLogin,
					"responsibilityTypeId", ResponsibilityTypeId,
					"description", Description);
			LocalDispatcher dispatcher = HrmsInfrastructure.getDispatcher();//GenericDispatcher.getLocalDispatcher("default", delegator);
			dispatcher.runSync("createResponsibilityType", context);
			Messagebox.show("Saved successfully", "Success", 1, null);
			Component comp = addNewResponsibilityTypes.getPage().getFellowIfAny("searchPanel");
			if(comp != null){
			Events.postEvent("onClick$searchButton", addNewResponsibilityTypes.getPage().getFellow("searchPanel"), null);
			}
			addNewResponsibilityTypes.detach();
		} catch (Exception e) {

			try {
				Messagebox.show("Saved unsuccessfully", "Error", 1, null);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		}

	}

	public void delete(final Event event, final String responsibilityTypeId)
			throws GenericServiceException, InterruptedException {
		Messagebox.show("Do you want to delete this record?", "Warning", Messagebox.YES
				| Messagebox.NO, Messagebox.QUESTION, new EventListener() {
			public void onEvent(Event evt) {
				if ("onYes".equals(evt.getName())) {
		try {
			System.out
					.println("***************Event getting Called*************");
			System.out.println("responsibilityTypeId ---- "
					+ responsibilityTypeId);
			Component window = event.getTarget();
			GenericValue userLogin = (GenericValue) Executions.getCurrent()
					.getDesktop().getSession().getAttribute("userLogin");
			GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");

			Map<String, Object> context = UtilMisc.toMap("userLogin",
					userLogin, "responsibilityTypeId", responsibilityTypeId);

			LocalDispatcher dispatcher = HrmsInfrastructure.getDispatcher();//GenericDispatcher.getLocalDispatcher("default", delegator);
			Map result = dispatcher.runSync("deleteResponsibilityType", context);
			//if(result.get("errorMessage").equals("ERROR")){
			//	Messagebox.show("The selected Responsibility Type is in use;Cannot be deleted.", "Error", 1, null);
			//}else
				Messagebox.show("Deleted successfully", "Success", 1, null);
			Events.postEvent("onClick$searchButton", window.getPage().getFellow("searchPanel"), null);
		} catch (Exception e) {

			try {
				Messagebox.show("Deleted unsuccessfully", "Error", 1, null);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		}
                } 
		 	 }
		}
);

	}

	public void resPonsibilityTypesEdit(Event event, GenericValue gv)
			throws GenericServiceException, SuspendNotAllowedException,
			InterruptedException {
		final Window win = (Window) Executions.createComponents(
				"/zul/GlobalHRSetting/responsibilityTypesEdit.zul", null, null);
		win.doModal();
		Textbox txtDescription = (Textbox) win.getFellow("txtBoxDescription");
		txtDescription.setValue(gv.getString("description"));
		Textbox txtBoxResponsibilityTypeId = (Textbox) win
				.getFellow("txtBoxResponsibilityTypeId");
		txtBoxResponsibilityTypeId.setValue(gv
				.getString("responsibilityTypeId"));
	}

	public void onClick$btnEdit(Event event)
			throws GenericEntityException, GenericServiceException {
        try{
		GenericValue userLogin = (GenericValue) Executions.getCurrent()
				.getDesktop().getSession().getAttribute("userLogin");

		Component comp = event.getTarget();

		GenericDelegator delegator = (GenericDelegator) userLogin.getDelegator();

		String ResponsibilityTypeId = ((Textbox) comp
				.getFellow("txtBoxResponsibilityTypeId")).getValue();
		String description = ((Textbox) comp
				.getFellow("txtBoxDescription")).getValue();

		Map<String, Object> context = UtilMisc.toMap("userLogin", userLogin,
				"responsibilityTypeId", ResponsibilityTypeId, "description", description);

		LocalDispatcher dispatcher = HrmsInfrastructure.getDispatcher();//GenericDispatcher.getLocalDispatcher("default", delegator);

		dispatcher.runSync("updateResponsibilityType", context);
		Messagebox.show("Updated Successfully", "Success", 1, null);
		Events.postEvent("onClick$searchButton", comp.getPage()
				.getFellow("searchPanel"), null);
		comp.detach();
	}
        catch (Exception e) {
			try {
				Messagebox.show("Not Updated", "Error", 1, null);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		}
	}

}
