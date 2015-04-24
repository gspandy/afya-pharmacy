package com.ndz.controller;

import java.util.List;
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
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.SimpleListModel;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.ndz.zkoss.GenericValueRenderer;
import com.ndz.zkoss.util.HrmsInfrastructure;

public class GlobalHrSettingPositionType extends GenericForwardComposer {

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);

		if ("divController".equals(comp.getId()))
			populateList(comp);

	}

	private void populateList(Component root) throws GenericEntityException {

		GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");

		List<GenericValue> PositionType = delegator.findList("EmplPositionType", null, null, null, null, false);
		SimpleListModel EmplPositionType = new SimpleListModel(PositionType);
		Listbox listBoxEmplPositionTypeId = (Listbox) root.getFellow("listBoxEmplPositionTypeId");
		listBoxEmplPositionTypeId.setModel(EmplPositionType);
		listBoxEmplPositionTypeId.setItemRenderer(new GenericValueRenderer(new String[] { "emplPositionTypeId", "description" }));
	}

	public void onClick$btnSave(Event event) throws GenericEntityException {

		try {

			GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");

			Component addNewPositionType = event.getTarget();
			GenericDelegator delegator = HrmsInfrastructure.getDelegator();//(GenericDelegator) userLogin.getDelegator();
			String EmplPositionTypeId = ((Textbox) addNewPositionType.getFellow("txtEmplPositionTypeId")).getValue();
		
			String Description = ((Textbox) addNewPositionType.getFellow("txtDescription")).getValue();

			Map<String, Object> context = UtilMisc.toMap("userLogin", userLogin, "emplPositionTypeId", EmplPositionTypeId, "description", Description);

			LocalDispatcher dispatcher = HrmsInfrastructure.getDispatcher();//GenericDispatcher.getLocalDispatcher("default", delegator);
			
			
			
			Map result=dispatcher.runSync("createEmplPositionType", context);
			if (result.get("errorMessage")==null)
			{
			System.out.println(result+"\n\n\n\n");
			Messagebox.show(Labels.getLabel("MessageDescription_SavedSuccessfully"), "Success", 1, null);
			}
			else{
				Messagebox.show("Position Already Exists", "Error", 1, null);
			}
		} catch (GenericServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
			
			
		
	

	private static final long serialVersionUID = 1L;

	public void delete(Event event, final String emplPositionTypeId) throws GenericServiceException, InterruptedException {
		final Component window = event.getTarget();
		Messagebox.show("Do You Want To Delete this Record?", "Warning", Messagebox.YES
				| Messagebox.NO, Messagebox.QUESTION, new EventListener() {
			public void onEvent(Event evt) {
				if ("onYes".equals(evt.getName())) {
			                 
		try {
			GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
			GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");

			Map<String, Object> context = UtilMisc.toMap("userLogin", userLogin, "emplPositionTypeId", emplPositionTypeId);

			LocalDispatcher dispatcher = HrmsInfrastructure.getDispatcher();//GenericDispatcher.getLocalDispatcher("default", delegator);
			dispatcher.runSync("deletePositionType", context);
			Messagebox.show(Labels.getLabel("MessageDelete_Successful"), "Success", 1, null);
			Events.postEvent("onClick$searchButton", window.getPage().getFellow("searchPanel"), null);
		} catch (Exception e) {

			try {
				Messagebox.show(Labels.getLabel("MessageDelete_UnSuccessful"), "Error", 1, null);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		}
                } 
		 	 }
		}
);

	}

	public void positionTypesEdit(Event event, GenericValue gv) throws GenericServiceException, SuspendNotAllowedException, InterruptedException {
		final Window win = (Window) Executions.createComponents("/zul/GlobalHRSetting/positionTypeEdit.zul", null, null);
		win.doModal();

		Textbox txtDescription = (Textbox) win.getFellow("txtBoxDescription");
		txtDescription.setValue(gv.getString("description"));

		Textbox txtBoxEmplPositionTypeId = (Textbox) win.getFellow("txtBoxEmplPositionTypeId");
		txtBoxEmplPositionTypeId.setValue(gv.getString("emplPositionTypeId"));
		
	}

	public void onClick$btnEdit(Event event) throws GenericEntityException, GenericServiceException {
		try {
			GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");

			Component comp = event.getTarget();

			GenericDelegator delegator = (GenericDelegator) userLogin.getDelegator();

			String EmplPositionTypeId = ((Textbox) comp.getFellow("txtBoxEmplPositionTypeId")).getValue();
			String description = ((Textbox) comp.getFellow("txtBoxDescription")).getValue();
			
			Map<String, Object> context = UtilMisc.toMap("userLogin", userLogin, "emplPositionTypeId", EmplPositionTypeId, "description", description);

			LocalDispatcher dispatcher = HrmsInfrastructure.getDispatcher();//GenericDispatcher.getLocalDispatcher("default", delegator);

			dispatcher.runSync("updateEmplPositionType", context);
			Messagebox.show(Labels.getLabel("Message_EditSuccessfully"), "Success", 1, null);
			Events.postEvent("onClick$searchButton", comp.getPage().getFellow("searchPanel"), null);

			comp.detach();
		} catch (Exception e) {
			try {
				Messagebox.show(Labels.getLabel("Message_EditUnSuccessfully"), "Error", 1, null);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		}
	}

}
