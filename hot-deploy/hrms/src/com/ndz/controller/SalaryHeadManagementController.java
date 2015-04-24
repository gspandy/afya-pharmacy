package com.ndz.controller;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.humanresext.util.HumanResUtil;
import org.ofbiz.service.GenericDispatcher;
import org.ofbiz.service.LocalDispatcher;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Bandbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.SimpleListModel;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.ndz.component.party.ClaimTypeRenderer;
import com.ndz.component.party.CurrencyRenderer;
import com.ndz.zkoss.CountryBox;
import com.ndz.zkoss.util.HrmsInfrastructure;

public class SalaryHeadManagementController extends GenericForwardComposer {
	public void doAfterCompose(Component EditSalaryHeadWindow) throws Exception {
		super.doAfterCompose(EditSalaryHeadWindow);

		GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
		/*
		 * EntityCondition condition = EntityCondition.makeCondition("enumTypeId",
		 * EntityOperator.EQUALS, "CLAIM_TYPE");
		 */
		EntityCondition currencyCondition = EntityCondition.makeCondition("uomTypeId", EntityOperator.EQUALS, "CURRENCY_MEASURE");

		Set<String> SalaryHeadTypeToDisplay = new HashSet();
		Set<String> currencyTypeToDisplay = new HashSet();
		Set<String> SalaryComputationTypeToDisplay = new HashSet();

		SalaryHeadTypeToDisplay.add("salaryHeadTypeId");
		SalaryHeadTypeToDisplay.add("description");

		currencyTypeToDisplay.add("uomId");
		currencyTypeToDisplay.add("description");

		SalaryComputationTypeToDisplay.add("salaryComputationTypeId");
		SalaryComputationTypeToDisplay.add("description");
		// =====================SalaryHeadType==============================
		List<GenericValue> salaryHeadType = delegator.findList("SalaryHeadType", null, SalaryHeadTypeToDisplay, null, null, false);
		salaryHeadType.add(0, null);
		SimpleListModel salaryHeadTypeList = new SimpleListModel(salaryHeadType);

		Listbox applySalaryHeadType = (Listbox) EditSalaryHeadWindow.getFellow("applySalaryHeadType");
		applySalaryHeadType.setModel(salaryHeadTypeList);
		applySalaryHeadType.setItemRenderer(new SalaryHeadTypeRenderer());
		// =====================UOM===CURRENCY==============================
		List<GenericValue> currency = delegator.findList("Uom", currencyCondition, currencyTypeToDisplay, null, null, false);
		currency.add(0, null);
		SimpleListModel currencyList = new SimpleListModel(currency);

		Listbox currencyListBox = (Listbox) EditSalaryHeadWindow.getFellow("applyCurrencyUomId");
		currencyListBox.setModel(currencyList);
		currencyListBox.setItemRenderer(new CurrencyRenderer());
		// =====================SalaryComputationType==============================
		List<GenericValue> SalaryComputationType = delegator.findList("SalaryComputationType", null, SalaryComputationTypeToDisplay, null, null, false);
		SalaryComputationType.add(0, null);
		SimpleListModel SalaryComputationTypeList = new SimpleListModel(SalaryComputationType);

		Listbox SalaryComputationTypeListBox = (Listbox) EditSalaryHeadWindow.getFellow("applyComputationType");
		SalaryComputationTypeListBox.setModel(SalaryComputationTypeList);
		SalaryComputationTypeListBox.setItemRenderer(new SalaryComputationTypeRenderer());
		// ========================================================================
	}

	public void onEvent(Event event) {
		try {
			GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
			Component EditSalaryHeadWindow = event.getTarget();
			GenericDelegator delegator = (GenericDelegator) userLogin.getDelegator();
			String hrName = ((Textbox) EditSalaryHeadWindow.getFellow("applyHrName")).getValue();
			String geoId = (String) ((com.ndz.zkoss.CountryBox) EditSalaryHeadWindow.getFellow("countrybandbox")).getSelectedItem().getValue();
			Listitem isCreditInput = ((Listbox) EditSalaryHeadWindow.getFellow("applyCredit")).getSelectedItem();
			String isCredit = (String) isCreditInput.getValue();
			Listitem salaryHeadTypeInput = ((Listbox) EditSalaryHeadWindow.getFellow("applySalaryHeadType")).getSelectedItem();
			String salaryHeadType = (String) salaryHeadTypeInput.getValue();
			Listitem isTaxableInput = ((Listbox) EditSalaryHeadWindow.getFellow("applyTaxable")).getSelectedItem();
			String isTaxable = (String) isTaxableInput.getValue();
			Listitem isMandatoryInput = ((Listbox) EditSalaryHeadWindow.getFellow("applyMandatory")).getSelectedItem();
			String isMandatory = (String) isMandatoryInput.getValue();
			Listitem currencyUomIdInput = ((Listbox) EditSalaryHeadWindow.getFellow("applyCurrencyUomId")).getSelectedItem();
			String currencyUomId = (String) currencyUomIdInput.getValue();
			Listitem salaryComputationTypeIdInput = ((Listbox) EditSalaryHeadWindow.getFellow("applyComputationType")).getSelectedItem();
			String salaryComputationTypeId = (String) salaryComputationTypeIdInput.getValue();
			Map<String,Object> context = UtilMisc.toMap("salaryHeadId",delegator.getNextSeqId("salaryHeadId"),"hrName", hrName, "isCr", isCredit, "salaryHeadTypeId", salaryHeadType, "isTaxable", isTaxable, "isMandatory",
					isMandatory, "geoId", geoId, "currencyUomId", currencyUomId, "salaryComputationTypeId", salaryComputationTypeId);
			delegator.create(delegator.makeValue("SalaryHead", context));
			Component searchPanelComponent = EditSalaryHeadWindow.getPage().getFellowIfAny("searchPanel");
			if (searchPanelComponent != null)
				Events.postEvent("onClick$searchButton", searchPanelComponent, null);
			Messagebox.show("Created successfully", "Success", 1, null);
			EditSalaryHeadWindow.detach();
		} catch (Exception e) {
			try {
				Messagebox.show("Created unsuccessfully", "Error", 1, null);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
	}

	@SuppressWarnings("deprecation")
	public static void showSalaryHeadWindow(Event event, GenericValue gv) throws SuspendNotAllowedException, InterruptedException, GenericEntityException {

		Component approveClaimWindow = event.getTarget();
		GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
		GenericValue person = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("person");

		String salaryHeadId = gv.getString("salaryHeadId");
		String hrName = gv.getString("hrName");
		String isCr = gv.getString("isCr");
		String salaryHeadTypeId = gv.getString("salaryHeadTypeId");
		String geoId = gv.getString("geoId");
		String isTaxable = gv.getString("isTaxable");
		String isMandatory = gv.getString("isMandatory");
		String currencyUomId = gv.getString("currencyUomId");
		String salaryComputationTypeId = gv.getString("salaryComputationTypeId");

		Window win = (Window) Executions.createComponents("/zul/payRollManagement/updateSalaryHead.zul", null, UtilMisc.toMap("oldHrName", hrName));
		win.setTitle("Update Salary Head");
		GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
		/*
		 * EntityCondition condition = EntityCondition.makeCondition("enumTypeId",
		 * EntityOperator.EQUALS, "CLAIM_TYPE");
		 */
		EntityCondition currencyCondition = EntityCondition.makeCondition("uomTypeId", EntityOperator.EQUALS, "CURRENCY_MEASURE");

		Set<String> SalaryHeadTypeToDisplay = new HashSet();
		Set<String> currencyTypeToDisplay = new HashSet();
		Set<String> SalaryComputationTypeToDisplay = new HashSet();

		SalaryHeadTypeToDisplay.add("salaryHeadTypeId");
		SalaryHeadTypeToDisplay.add("description");

		currencyTypeToDisplay.add("uomId");
		currencyTypeToDisplay.add("description");

		SalaryComputationTypeToDisplay.add("salaryComputationTypeId");
		SalaryComputationTypeToDisplay.add("description");
		// =====================SalaryHeadType==============================
		List<GenericValue> salaryHeadType = delegator.findList("SalaryHeadType", null, SalaryHeadTypeToDisplay, null, null, false);
		salaryHeadType.add(0, null);
		SimpleListModel salaryHeadTypeList = new SimpleListModel(salaryHeadType);

		Listbox applySalaryHeadTypeListBox = (Listbox) win.getFellow("applySalaryHeadType");
		applySalaryHeadTypeListBox.setModel(salaryHeadTypeList);
		applySalaryHeadTypeListBox.setItemRenderer(new SalaryHeadTypeRenderer());

		for (int i = 1; i < salaryHeadType.size(); i++) {
			GenericValue IndividualsalaryHeadType = salaryHeadType.get(i);
			String itemValue = IndividualsalaryHeadType.getString("salaryHeadTypeId");
			if (itemValue.equals(salaryHeadTypeId)) {
				applySalaryHeadTypeListBox.setSelectedIndex(i);
				break;
			}
		}
		// =====================UOM===CURRENCY==============================
		List<GenericValue> currency = delegator.findList("Uom", currencyCondition, currencyTypeToDisplay, null, null, false);
		currency.add(0, null);
		SimpleListModel currencyList = new SimpleListModel(currency);

		Listbox currencyListBox = (Listbox) win.getFellow("applyCurrencyUomId");
		currencyListBox.setModel(currencyList);
		currencyListBox.setItemRenderer(new CurrencyRenderer());

		for (int i = 1; i < currency.size(); i++) {
			GenericValue IndividualCurrency = currency.get(i);
			String itemValue = IndividualCurrency.getString("uomId");
			if (itemValue.equals(currencyUomId)) {
				currencyListBox.setSelectedIndex(i);
				break;
			}
		}
		// =====================SalaryComputationType==============================
		List<GenericValue> SalaryComputationType = delegator.findList("SalaryComputationType", null, SalaryComputationTypeToDisplay, null, null, false);
		SalaryComputationType.add(0, null);
		SimpleListModel SalaryComputationTypeList = new SimpleListModel(SalaryComputationType);

		Listbox SalaryComputationTypeListBox = (Listbox) win.getFellow("applyComputationType");
		SalaryComputationTypeListBox.setModel(SalaryComputationTypeList);
		SalaryComputationTypeListBox.setItemRenderer(new SalaryComputationTypeRenderer());

		for (int i = 1; i < SalaryComputationType.size(); i++) {
			GenericValue IndividualSalaryComputationType = SalaryComputationType.get(i);
			String itemValue = IndividualSalaryComputationType.getString("salaryComputationTypeId");
			if (itemValue.equals(salaryComputationTypeId)) {
				SalaryComputationTypeListBox.setSelectedIndex(i);
				break;
			}
		}
		// ========================================================================

		// salaryHeadName

		Label salaryHeadNameLabel = (Label) win.getFellow("salaryHeadName");
		salaryHeadNameLabel.setValue(salaryHeadId);

		Textbox salaryHeadIdTextbox = (Textbox) win.getFellow("salaryHeadId");
		salaryHeadIdTextbox.setValue(salaryHeadId);

		Textbox hrNameTextbox = (Textbox) win.getFellow("applyHrName");
		hrNameTextbox.setValue(hrName);

		String Credit = "";
		if (isCr.equals("Y"))
			Credit = "YES";
		else
			Credit = "NO";

		Listbox CreditListbox = (Listbox) win.getFellow("applyCredit");
		Listitem CreditListItem = new Listitem();

		for (int i = 1; i <= 2; i++) {
			CreditListItem = CreditListbox.getItemAtIndex(i);
			String itemValue = (String) CreditListItem.getValue();
			if (itemValue.equals(isCr)) {
				CreditListbox.setSelectedIndex(i);
				break;
			}
		}

		/*
		 * Bandbox geoIdBandbox = (Bandbox) win.getFellow("searchPanel");
		 * geoIdBandbox.setValue(geoId);
		 */
		CountryBox geoIdBandbox = (CountryBox) win.getFellow("countrybandbox");
		geoIdBandbox.setValue(geoId);
		String Taxable = "";
		if (isTaxable.equals("Y"))
			Taxable = "YES";
		else
			Taxable = "NO";

		Listbox isTaxableListbox = (Listbox) win.getFellow("applyTaxable");
		Listitem isTaxableListItem = new Listitem();
		for (int i = 1; i <= 2; i++) {
			isTaxableListItem = isTaxableListbox.getItemAtIndex(i);
			String itemValue = (String) isTaxableListItem.getValue();
			if (itemValue.equals(isTaxable)) {
				isTaxableListbox.setSelectedIndex(i);
				break;
			}
		}

		String Mandatory = "";
		if (isMandatory.equals("Y"))
			Mandatory = "YES";
		else
			Mandatory = "NO";

		Listbox isMandatoryListbox = (Listbox) win.getFellow("applyMandatory");
		Listitem isMandatoryListItem = new Listitem();
		for (int i = 1; i <= 2; i++) {
			isMandatoryListItem = isMandatoryListbox.getItemAtIndex(i);
			String itemValue = (String) isMandatoryListItem.getValue();
			if (itemValue.equals(isMandatory)) {
				isMandatoryListbox.setSelectedIndex(i);
				break;
			}
		}

		// approveClaimWindow.detach();

		win.doModal();

	}

	public static void deleteSalaryHead(Event event, String salaryHeadId) {
		System.out.println("****************SubmitClaim Event Called*********");
		try {
			Component applySalaryHeadWindow = event.getTarget();
			GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
			GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
			LocalDispatcher dispatcher = HrmsInfrastructure.getDispatcher();//GenericDispatcher.getLocalDispatcher("default", delegator);
			Map submitDelete = null;
			submitDelete = UtilMisc.toMap("userLogin", userLogin, "salaryHeadId", salaryHeadId);
			dispatcher.runSync("deleteSalaryHead", submitDelete);
			Events.postEvent("onClick$searchButton", applySalaryHeadWindow.getPage().getFellow("searchPanel"), null);
			Messagebox.show("Salary Head Successfully Deleted", "Success", 1, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void EditSalaryHead(Event event) {

		System.out.println("****************SubmitClaim Event Called*********");
		try {
			Component salaryHeadWindow = event.getTarget();
			GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
			GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
			LocalDispatcher dispatcher = HrmsInfrastructure.getDispatcher();//GenericDispatcher.getLocalDispatcher("default", delegator);
			Map submitUpdate = null;
			String salaryHeadId = (String) ((Textbox) salaryHeadWindow.getFellow("salaryHeadId")).getValue();

			String hrName = (String) ((Textbox) salaryHeadWindow.getFellow("applyHrName")).getValue();

			Listitem isCrListItem = (Listitem) ((Listbox) salaryHeadWindow.getFellow("applyCredit")).getSelectedItem();
			String isCr = (String) isCrListItem.getValue();

			Listitem salaryHeadTypeIdListItem = (Listitem) ((Listbox) salaryHeadWindow.getFellow("applySalaryHeadType")).getSelectedItem();
			String salaryHeadTypeId = (String) salaryHeadTypeIdListItem.getValue();

			/*
			 * String geoId = (String) ((Bandbox) salaryHeadWindow
			 * .getFellow("searchPanel")).getValue();
			 */
			Combobox countryBox = ((Combobox) salaryHeadWindow.getFellow("countrybandbox"));
			String geoId = (String) (countryBox.getSelectedItem() == null ? countryBox.getValue() : countryBox.getSelectedItem().getValue());

			Listitem isTaxableListItem = (Listitem) ((Listbox) salaryHeadWindow.getFellow("applyTaxable")).getSelectedItem();
			String isTaxable = (String) isTaxableListItem.getValue();

			Listitem isMandatoryListItem = (Listitem) ((Listbox) salaryHeadWindow.getFellow("applyMandatory")).getSelectedItem();
			String isMandatory = (String) isMandatoryListItem.getValue();

			Listitem currencyUomIdListItem = (Listitem) ((Listbox) salaryHeadWindow.getFellow("applyCurrencyUomId")).getSelectedItem();
			String currencyUomId = (String) currencyUomIdListItem.getValue();

			Listitem salaryComputationTypeIdListItem = (Listitem) ((Listbox) salaryHeadWindow.getFellow("applyComputationType")).getSelectedItem();
			String salaryComputationTypeId = (String) salaryComputationTypeIdListItem.getValue();

			submitUpdate = UtilMisc.toMap("userLogin", userLogin, "salaryHeadId", salaryHeadId, "hrName", hrName, "isCr", isCr, "salaryHeadTypeId",
					salaryHeadTypeId, "geoId", geoId, "isTaxable", isTaxable, "isMandatory", isMandatory, "currencyUomId", currencyUomId,
					"salaryComputationTypeId", salaryComputationTypeId);

			Map<String, Object> result = dispatcher.runSync("updateSalaryHead", submitUpdate);

			Messagebox messageBox = new Messagebox();
			String err = "";
			err = (String) result.get("responseMessage");
			if (err != null && err.equals("error"))
				messageBox.show((String) result.get("errorMessage"), "Error", 1, null);
			else
				messageBox.show("Salary Head" + " " + hrName + " " + "Successfully Updated", "Success", 1, null);

			Events.postEvent("onClick$searchButton", salaryHeadWindow.getPage().getFellow("searchPanel"), null);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
