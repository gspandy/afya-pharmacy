package com.ndz.controller;

import java.sql.Date;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.GenericDispatcher;
import org.ofbiz.service.LocalDispatcher;
import org.zkoss.zhtml.Messagebox;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Bandbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.ndz.zkoss.util.HrmsInfrastructure;

public class ValidTaxDeclManagementController extends GenericForwardComposer {

	SearchController controller = new SearchController();

	public void onEvent(Event event) {
		try {
			GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
			Component AddPayrollRuleWindow = event.getTarget();
			GenericDelegator delegator = (GenericDelegator) userLogin.getDelegator();
			String hrName = (String) ((Textbox) AddPayrollRuleWindow.getFellow("hrName")).getValue();
			Comboitem countryBoxItem = ((Combobox) AddPayrollRuleWindow.getFellow("countrybandbox")).getSelectedItem();
			String geoId = countryBoxItem == null ? null : countryBoxItem.getValue().toString();
			String createdBy = userLogin.getString("partyId");
			Listitem modificationTypeInput = (Listitem) ((Listbox) AddPayrollRuleWindow.getFellow("modificationType")).getSelectedItem();
			String modificationType = (String) modificationTypeInput.getValue();
			String modifyLatestBy = null;
			Date modifyLatestByDate = null;
			if (modificationType.equals("Monthly")) {
				modifyLatestBy = (String) ((Intbox) AddPayrollRuleWindow.getFellow("modifyLatestBy")).getValue().toString();
			} else {
				java.util.Date modifyLatestByDateInput = (java.util.Date) ((Datebox) AddPayrollRuleWindow.getFellow("modifyLatestByDate")).getValue();
				modifyLatestByDate = new Date(modifyLatestByDateInput.getTime());

			}
			java.text.DateFormat df = new java.text.SimpleDateFormat("dd-MM-yyyy");
			Object fromDateObj = null;
			Listitem startDateInput = (Listitem) ((Listbox) AddPayrollRuleWindow.getFellow("startDate")).getSelectedItem();
			String sDate = (String) startDateInput.getValue();
			fromDateObj = df.parse(sDate);
			java.util.Date FromDate = (java.util.Date) fromDateObj;
			java.sql.Date startDate = new java.sql.Date(FromDate.getTime());
			java.text.DateFormat df1 = new java.text.SimpleDateFormat("dd-MM-yyyy");
			Object thruDateObj = null;
			Listitem endDateInput = (Listitem) ((Listbox) AddPayrollRuleWindow.getFellow("endDate")).getSelectedItem();
			String eDate = (String) endDateInput.getValue();
			thruDateObj = df1.parse(eDate);
			java.util.Date ThruDate = (java.util.Date) thruDateObj;
			java.sql.Date endDate = new java.sql.Date(ThruDate.getTime());
			Map<String, Object> context = UtilMisc.toMap("userLogin", userLogin, "hrName", hrName, "geoId", geoId, "createdBy", createdBy, "modificationType",
					modificationType, "modifyLatestBy", modifyLatestBy, "startDate", startDate, "endDate", endDate, "modifyLatestByDate", modifyLatestByDate);
			LocalDispatcher dispatcher = HrmsInfrastructure.getDispatcher();//GenericDispatcher.getLocalDispatcher("default", delegator);
			dispatcher.runSync("hr.createTaxDecl", context);
			Events.postEvent("onClick$searchButton", AddPayrollRuleWindow.getPage().getFellow("searchPanel"), null);
			org.zkoss.zul.Messagebox.show("Created Successfully", "Success", 1, null);
			AddPayrollRuleWindow.detach();

		} catch (Exception e) {
			try {
				org.zkoss.zul.Messagebox.show("Created unsuccessfully", "Error", 1, null);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
	}

	public static void showValidTaxDeclWindow(Event event, GenericValue gv) throws SuspendNotAllowedException, InterruptedException, GenericEntityException {

		GenericValue person = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("person");
		String validTaxDeclId = gv.getString("validTaxDeclId");
		String hrName = gv.getString("hrName");
		String geoId = gv.getString("geoId");
		String modificationType = gv.getString("modificationType");
		String modifyLatestBy = gv.getString("modifyLatestBy");
		Date modifyLatestByDate = gv.getDate("modifyLatestByDate");
		Window win = (Window) Executions.createComponents("/zul/payRollManagement/updateValidTaxDecl.zul", null, UtilMisc.toMap("gv", gv));
		win.setTitle("Update Tax Declaration Definition");
		win.doModal();
		Label validTaxDeclIdLabel = (Label) win.getFellow("validTaxDeclId");
		validTaxDeclIdLabel.setValue(validTaxDeclId);
		Textbox hrNameTextBox = (Textbox) win.getFellow("hrName");
		hrNameTextBox.setValue(hrName);
		Combobox geoIdCountryBox = (Combobox) win.getFellow("countrybandbox");
		geoIdCountryBox.setValue(geoId);
		Listbox savedmodificationType = (Listbox) win.getFellow("modificationType");
		Listitem modificationTypeItem = new Listitem();
		modificationTypeItem.setLabel(modificationType);
		modificationTypeItem.setValue(modificationType);
		savedmodificationType.appendItemApi("Monthly", "Monthly");
		savedmodificationType.appendItemApi("Yearly", "Yearly");
		if ("Monthly".equals(modificationType)) {
			savedmodificationType.setSelectedIndex(0);
			win.getFellow("modificationTypeYearly").setVisible(true);
		} else {
			win.getFellow("modifyLatestByDateyearly").setVisible(true);
			savedmodificationType.setSelectedIndex(1);
		}
		Intbox modifyLatestByTextBox = (Intbox) win.getFellow("modifyLatestBy");
		modifyLatestByTextBox.setValue(modifyLatestBy == null ? null : new Integer(modifyLatestBy));
		Datebox modifyLatestByDateDateBox = (Datebox) win.getFellow("modifyLatestByDate");
		modifyLatestByDateDateBox.setValue(modifyLatestByDate);
	}

	public static void DeleteValidTaxDecl(Event event, String validTaxDeclId) {
		try {
			Component applySalaryHeadWindow = event.getTarget();
			GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
			GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
			LocalDispatcher dispatcher = HrmsInfrastructure.getDispatcher();//GenericDispatcher.getLocalDispatcher("default", delegator);
			Map submitDelete = null;

			submitDelete = UtilMisc.toMap("userLogin", userLogin, "validTaxDeclId", validTaxDeclId);
			dispatcher.runSync("hr.deleteTaxDecl", submitDelete);
			Events.postEvent("onClick$searchButton", applySalaryHeadWindow.getPage().getFellow("searchPanel"), null);
			Messagebox.show("Tax Declaration: " + validTaxDeclId + " :Successfully Deleted", "Success", 1, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void EditValidTaxDecl(Event event,GenericValue gv) {
		try {
			Component applyPayrollConditionWindow = event.getTarget();
			GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
			GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
			LocalDispatcher dispatcher = HrmsInfrastructure.getDispatcher();//GenericDispatcher.getLocalDispatcher("default", delegator);
			Map submitUpdate = null;

			String validTaxDeclId = (String) ((Label) applyPayrollConditionWindow.getFellow("validTaxDeclId")).getValue();

			String hrName = (String) ((Textbox) applyPayrollConditionWindow.getFellow("hrName")).getValue();

			Combobox countrybandbox = ((Combobox) applyPayrollConditionWindow.getFellow("countrybandbox"));

			String geoId = countrybandbox.getSelectedItem() != null ? countrybandbox.getSelectedItem().getValue().toString() : countrybandbox.getValue()
					.toString();

			Listitem modificationTypeInput = (Listitem) ((Listbox) applyPayrollConditionWindow.getFellow("modificationType")).getSelectedItem();
			String modificationType = (String) modificationTypeInput.getValue();
			Integer modifyLatestByInt = ((Intbox)applyPayrollConditionWindow.getFellow("modifyLatestBy")).getValue();
			String modifyLatestBy = modifyLatestByInt == null ? null : modifyLatestByInt.toString();

			java.text.DateFormat df = new java.text.SimpleDateFormat("dd-MM-yyyy");
			Object fromDateObj = null;
			Listitem startDateInput = (Listitem) ((Listbox) applyPayrollConditionWindow.getFellow("listboxStartDate")).getSelectedItem();
			String sDate = gv.getString("startDate");
			if(startDateInput != null)
			 sDate = (String) startDateInput.getValue();
			fromDateObj = df.parse(sDate);
			java.util.Date FromDate = (java.util.Date) fromDateObj;
			java.sql.Date startDate = new java.sql.Date(FromDate.getTime());

			java.text.DateFormat df1 = new java.text.SimpleDateFormat("dd-MM-yyyy");
			Object thruDateObj = null;
			Listitem endDateInput = (Listitem) ((Listbox) applyPayrollConditionWindow.getFellow("listboxEndDate")).getSelectedItem();
			String eDate = gv.getString("endDate") ;
			if(endDateInput != null)
			 eDate = (String) endDateInput.getValue();
			thruDateObj = df1.parse(eDate);
			java.util.Date ThruDate = (java.util.Date) thruDateObj;
			java.sql.Date endDate = new java.sql.Date(ThruDate.getTime());

			java.util.Date modifyLatestByDateInput = (java.util.Date) ((Datebox) applyPayrollConditionWindow.getFellow("modifyLatestByDate")).getValue();

			Date modifyLatestByDate = null;
			if (modifyLatestByDateInput != null) {
				modifyLatestByDate = new Date(modifyLatestByDateInput.getTime());
			}
			submitUpdate = UtilMisc.toMap("userLogin", userLogin, "validTaxDeclId", validTaxDeclId, "hrName", hrName, "geoId", geoId, "createdBy",
					userLogin.getString("partyId"), "modificationType", modificationType, "modifyLatestBy", modifyLatestBy, "startDate", startDate, "endDate",
					endDate, "modifyLatestByDate", modifyLatestByDate);
			dispatcher.runSync("hr.updateTaxDecl", submitUpdate);
			Events.postEvent("onClick$searchButton", applyPayrollConditionWindow.getPage().getFellow("searchPanel"), null);
			Messagebox.show("Updated Successfully", "Success", 1, null);

			applyPayrollConditionWindow.getFellow("UpdateValidTaxDeclWindow").detach();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
