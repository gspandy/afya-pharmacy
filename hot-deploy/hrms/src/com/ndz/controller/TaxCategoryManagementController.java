package com.ndz.controller;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Map;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
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
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.ndz.zkoss.util.HrmsInfrastructure;

public class TaxCategoryManagementController extends GenericForwardComposer {

	SearchController controller = new SearchController();

	public void onEvent(Event event) {

		Messagebox messageBox = new Messagebox();
		try {

			System.out.println("***********OnEventCalled************");
			GenericValue userLogin = (GenericValue) Executions.getCurrent()
					.getDesktop().getSession().getAttribute("userLogin");
			System.out.println("*******userLogin Object*********" + userLogin);
			Component AddTaxCategoryWindow = event.getTarget();
			GenericDelegator delegator = (GenericDelegator) userLogin.getDelegator();

			String employeeId = userLogin.getString("partyId");
			System.out.println("********Party Id********" + employeeId);

			String categoryName = (String) ((Textbox) AddTaxCategoryWindow
					.getFellow("categoryName")).getValue();

			/*String geoId = (String) ((Bandbox) AddTaxCategoryWindow
					.getFellow("searchPanel")).getValue();*/
			String geoId = (String)((com.ndz.zkoss.CountryBox)AddTaxCategoryWindow.getFellow("countrybandbox")).getSelectedItem().getValue();			

			String description = (String) ((Textbox) AddTaxCategoryWindow
					.getFellow("description")).getValue();

			String minAmountInput = (String) ((Textbox) AddTaxCategoryWindow
					.getFellow("minAmount")).getValue();
			BigDecimal minAmount = new BigDecimal(minAmountInput);

			String maxAmountInput = (String) ((Textbox) AddTaxCategoryWindow
					.getFellow("maxAmount")).getValue();
			BigDecimal maxAmount = new BigDecimal(maxAmountInput);

			Date fromDateInput = (Date) ((Datebox) AddTaxCategoryWindow
					.getFellow("fromDate")).getValue();
			Date thruDateInput = (Date) ((Datebox) AddTaxCategoryWindow
					.getFellow("thruDate")).getValue();

			Timestamp fromDate = new Timestamp(fromDateInput.getTime());
			Timestamp thruDate = new Timestamp(thruDateInput.getTime());
			
			String createdBy = "admin";

			Map context = UtilMisc.toMap("userLogin", userLogin,
					"categoryName", categoryName, "geoId",geoId,
					"description", description, "minAmount",minAmount,
					"maxAmount", maxAmount, "fromDate",fromDate,
					"thruDate", thruDate,"createdBy",createdBy);
			Map result = null;

			LocalDispatcher dispatcher = HrmsInfrastructure.getDispatcher();//GenericDispatcher.getLocalDispatcher("default", delegator);
			result = dispatcher.runSync("hr.createTaxCategory", context);

			String categoryId = (String) result.get("categoryId");

			Events.postEvent("onClick$searchButton", AddTaxCategoryWindow
					.getPage().getFellow("searchPanel"), null);

			messageBox.show("Tax Category Successfully Created with Tax Category Id:"
					+ categoryId,"Success",1,null);
			AddTaxCategoryWindow.detach();

		} catch (Exception e) {
			try {
				messageBox
						.show("Tax Category is not Created:Some parameter is missing","Error",1,null);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
	}

	public static void showTaxCategoryWindow(Event event, GenericValue gv)
			throws SuspendNotAllowedException, InterruptedException {

		Component applyTaxCategory = event.getTarget();
		GenericValue userLogin = (GenericValue) Executions.getCurrent()
				.getDesktop().getSession().getAttribute("userLogin");
		GenericValue person = (GenericValue) Executions.getCurrent()
				.getDesktop().getSession().getAttribute("person");

		String categoryId = gv.getString("categoryId");
		String categoryName = gv.getString("categoryName");
		String geoId = gv.getString("geoId");
		String description = gv.getString("description");
		String minAmount = gv.getString("minAmount");
		String maxAmount = gv.getString("maxAmount");
		Timestamp fromDate = gv.getTimestamp("fromDate");
		Timestamp thruDate = gv.getTimestamp("thruDate");

		Window win = (Window) Executions.createComponents(
				"/zul/payRollManagement/updateTaxCategory.zul", null, null);
		win.setTitle("Update Tax Category");
		win.doModal();

		Label categoryIdLabel = (Label) win.getFellow("categoryId");
		categoryIdLabel.setValue(categoryId);

		Textbox categoryNameTextBox = (Textbox) win.getFellow("categoryName");
		categoryNameTextBox.setValue(categoryName);

		Textbox geoIdTextBox = (Textbox) win.getFellow("geoId");
		geoIdTextBox.setValue(geoId);

		Textbox descriptionTextBox = (Textbox) win.getFellow("description");
		descriptionTextBox.setValue(description);

		Textbox minAmountTextBox = (Textbox) win.getFellow("minAmount");
		minAmountTextBox.setValue(minAmount);

		Textbox maxAmountTextBox = (Textbox) win.getFellow("maxAmount");
		maxAmountTextBox.setValue(maxAmount);

		Datebox fromDateDateBox = (Datebox) win.getFellow("fromDate");
		fromDateDateBox.setValue(fromDate);

		Datebox thruDateDateBox = (Datebox) win.getFellow("thruDate");
		thruDateDateBox.setValue(thruDate);

	}

	public static void DeleteTaxCategory(Event event, String categoryId) {
		System.out.println("****************SubmitClaim Event Called*********");
		try {
			Component applyTaxCategory = event.getTarget();
			GenericValue userLogin = (GenericValue) Executions.getCurrent()
					.getDesktop().getSession().getAttribute("userLogin");
			GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
			LocalDispatcher dispatcher = HrmsInfrastructure.getDispatcher();//GenericDispatcher.getLocalDispatcher("default", delegator);
			Map submitDelete = null;

			submitDelete = UtilMisc.toMap("userLogin", userLogin, "categoryId",
					categoryId);
			dispatcher.runSync("hr.deleteTaxCategory", submitDelete);
			Events.postEvent("onClick$searchButton", applyTaxCategory.getPage()
					.getFellow("searchPanel"), null);
			Messagebox.show("Tax Category: " + categoryId
					+ " :Successfully Deleted","Success",1,null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void EditTaxCategory(Event event) {

		System.out.println("****************SubmitClaim Event Called*********");
		try {
			Component AddTaxCategoryWindow = event.getTarget();
			GenericValue userLogin = (GenericValue) Executions.getCurrent()
					.getDesktop().getSession().getAttribute("userLogin");
			GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
			LocalDispatcher dispatcher = HrmsInfrastructure.getDispatcher();//GenericDispatcher.getLocalDispatcher("default", delegator);
			Map submitUpdate = null;

			String categoryId = (String) ((Label) AddTaxCategoryWindow
					.getFellow("categoryId")).getValue();

			String categoryName = (String) ((Textbox) AddTaxCategoryWindow
					.getFellow("categoryName")).getValue();

			String geoId = (String) ((Textbox) AddTaxCategoryWindow
					.getFellow("geoId")).getValue();

			String description = (String) ((Textbox) AddTaxCategoryWindow
					.getFellow("description")).getValue();

			String minAmountInput = (String) ((Textbox) AddTaxCategoryWindow
					.getFellow("minAmount")).getValue();
			BigDecimal minAmount = new BigDecimal(minAmountInput);

			String maxAmountInput = (String) ((Textbox) AddTaxCategoryWindow
					.getFellow("maxAmount")).getValue();
			BigDecimal maxAmount = new BigDecimal(maxAmountInput);

			Date fromDate = (Date) ((Datebox) AddTaxCategoryWindow
					.getFellow("fromDate")).getValue();
			Date thruDate = (Date) ((Datebox) AddTaxCategoryWindow
					.getFellow("thruDate")).getValue();

			

			Timestamp fromDate1 = new Timestamp(fromDate.getTime());
			Timestamp thruDate1= new Timestamp(thruDate.getTime());
			
			submitUpdate = UtilMisc.toMap("userLogin", userLogin, "categoryId",
					categoryId, "categoryName", categoryName, "geoId", geoId,
					"description", description, "minAmount", minAmount,
					"maxAmount", maxAmount, "fromDate", fromDate1, "thruDate",
					thruDate1);

			dispatcher.runSync("hr.updateTaxCategory", submitUpdate);
			Events.postEvent("onClick$searchButton",
					AddTaxCategoryWindow.getPage().getFellow(
							"searchPanel"), null);
			Messagebox.show("Tax Category: " + categoryId
					+ " :Successfully Updated","Success",1,null);


		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
