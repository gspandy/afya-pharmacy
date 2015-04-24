package com.ndz.controller;

import java.util.Map;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.GenericDispatcher;
import org.ofbiz.service.LocalDispatcher;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.ndz.zkoss.util.HrmsInfrastructure;

public class SalaryHeadTypeManagementController extends GenericForwardComposer {

	public static void createSalaryHeadType(Event event) {

		String claimId = null;
		Messagebox messageBox = new Messagebox();
		try {

			GenericValue userLogin = (GenericValue) Executions.getCurrent()
					.getDesktop().getSession().getAttribute("userLogin");
			Component EditSalaryHeadTypeWindow = event.getTarget();
			GenericDelegator delegator = (GenericDelegator) userLogin.getDelegator();

			String employeeId = userLogin.getString("partyId");
			String salaryHeadTypeId = ((Textbox) EditSalaryHeadTypeWindow
					.getFellow("salaryHeadTypeId")).getValue();

			String description = ((Textbox) EditSalaryHeadTypeWindow
					.getFellow("description")).getValue();

			Map context = UtilMisc.toMap("userLogin", userLogin,
					"salaryHeadTypeId", salaryHeadTypeId, "description",
					description);
			Map result = null;

			LocalDispatcher dispatcher = HrmsInfrastructure.getDispatcher();//GenericDispatcher.getLocalDispatcher("default", delegator);
			result = dispatcher.runSync("createSalaryHeadType", context);

			salaryHeadTypeId = (String) result.get("salaryHeadTypeId");
			Component searPanelComponenet =(Component) EditSalaryHeadTypeWindow.getPage().getFellowIfAny("searchPanel");
			if(searPanelComponenet != null)
			Events.postEvent("onClick$searchButton", searPanelComponenet, null);
			messageBox.show("Created Successfully","Success",1,null);
			EditSalaryHeadTypeWindow.getFellow("EditSalaryHeadTypeWindow").detach();

		} catch (Exception e) {
			try {
				messageBox
						.show("Claim Could not be saved:Some parameter is missing","Error",1,null);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			e.printStackTrace();
		}

	}

	public static void editSalaryHeadTypeWindow(Event event, GenericValue gv)
			throws SuspendNotAllowedException, InterruptedException,
			GenericEntityException {

		Component approveClaimWindow = event.getTarget();

		String salaryHeadTypeId = gv.getString("salaryHeadTypeId");
		String description = gv.getString("description");

		Window win = (Window) Executions.createComponents(
				"/zul/payRollManagement/updateSalaryHeadType.zul", null, null);
		win.setTitle("Update Salary Head Type");

		Label salaryHeadTypeIdLabel = (Label) win
				.getFellow("salaryHeadTypeIdLabel");
		salaryHeadTypeIdLabel.setValue(salaryHeadTypeId);

		Textbox salaryHeadTypeIdTextbox = (Textbox) win
				.getFellow("salaryHeadTypeId");
		salaryHeadTypeIdTextbox.setValue(salaryHeadTypeId);

		Textbox descriptionTextbox = (Textbox) win.getFellow("description");
		descriptionTextbox.setValue(description);

		
		win.doModal();
	}

	public static void deleteSalaryHeadType(Event event, String salaryHeadTypeId) {
		try {
			Component applySalaryHeadTypeWindow = event.getTarget();
			GenericValue userLogin = (GenericValue) Executions.getCurrent()
					.getDesktop().getSession().getAttribute("userLogin");
			GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
			LocalDispatcher dispatcher = HrmsInfrastructure.getDispatcher();//GenericDispatcher.getLocalDispatcher("default", delegator);
			Map submitDelete = null;
			submitDelete = UtilMisc.toMap("userLogin", userLogin,
					"salaryHeadTypeId", salaryHeadTypeId);
			dispatcher.runSync("deleteSalaryHeadType", submitDelete);
			Events.postEvent("onClick$searchButton", applySalaryHeadTypeWindow
					.getPage().getFellow("searchPanel"), null);
			Messagebox.show("Salary Head Type Successfully Deleted","Success",1,null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void EditSalaryHeadType(Event event) {

		try {
			Component salaryHeadWindow = event.getTarget();
			GenericValue userLogin = (GenericValue) Executions.getCurrent()
					.getDesktop().getSession().getAttribute("userLogin");
			GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
			LocalDispatcher dispatcher = HrmsInfrastructure.getDispatcher();//GenericDispatcher.getLocalDispatcher("default", delegator);
			Map submitUpdate = null;

			String salaryHeadTypeId = (String) ((Textbox) salaryHeadWindow
					.getFellow("salaryHeadTypeId")).getValue();

			String description = (String) ((Textbox) salaryHeadWindow
					.getFellow("description")).getValue();

			submitUpdate = UtilMisc.toMap("userLogin", userLogin,
					"salaryHeadTypeId", salaryHeadTypeId, "description",
					description);

			Map<String, Object> result = dispatcher.runSync(
					"updateSalaryHeadType", submitUpdate);

			Messagebox messageBox = new Messagebox();
			String err = "";
			err = (String) result.get("responseMessage");
			if (err != null && err.equals("error"))
				messageBox.show((String) result.get("errorMessage"),"Error",1,null);
			else
				messageBox.show("Updated Successfully","Success",1,null);

			salaryHeadWindow.detach();

			Events.postEvent("onClick$searchButton", salaryHeadWindow.getPage()
					.getFellow("searchPanel"), null);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
