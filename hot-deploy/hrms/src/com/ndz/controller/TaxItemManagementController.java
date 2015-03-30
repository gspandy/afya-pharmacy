package com.ndz.controller;

import java.math.BigDecimal;
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
import org.ofbiz.entity.condition.EntityComparisonOperator;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionFunction;
import org.ofbiz.service.GenericDispatcher;
import org.ofbiz.service.LocalDispatcher;
import org.zkoss.zhtml.Messagebox;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.SimpleListModel;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.ndz.zkoss.util.HrmsInfrastructure;

public class TaxItemManagementController extends GenericForwardComposer {

	SearchController controller = new SearchController();

	public void doAfterCompose(Component SalaryHeadRuleWindow) throws Exception {
		super.doAfterCompose(SalaryHeadRuleWindow);

		GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");

		Set<String> categoryIdToDisplay = new HashSet();
		Set<String> itemTypeIdToDisplay = new HashSet();
		Set<String> itemGroupIdToDisplay = new HashSet();

		categoryIdToDisplay.add("categoryId");
		categoryIdToDisplay.add("categoryName");

		itemTypeIdToDisplay.add("itemTypeId");
		itemTypeIdToDisplay.add("description");

		itemGroupIdToDisplay.add("itemGroupId");
		itemGroupIdToDisplay.add("description");

		// ======================TaxCategory====================
		List<GenericValue> categoryIdList = delegator.findList("TaxCategory",
				null, categoryIdToDisplay, null, null, false);
		categoryIdList.add(0, null);
		SimpleListModel taxCategoryList = new SimpleListModel(categoryIdList);

		Listbox taxCategoryListBox = (Listbox) SalaryHeadRuleWindow
				.getFellow("categoryId");
		taxCategoryListBox.setModel(taxCategoryList);
		taxCategoryListBox.setItemRenderer(new taxCategoryIdRenderer());

		// ======================next_Pay_Grade_Id====================
		List<GenericValue> itemTypeIdList = delegator.findList("TaxItemType",
				null, itemTypeIdToDisplay, null, null, false);
		itemTypeIdList.add(0, null);
		SimpleListModel taxItemTypeList = new SimpleListModel(itemTypeIdList);

		Listbox itemTypeIdListBox = (Listbox) SalaryHeadRuleWindow
				.getFellow("itemTypeId");
		itemTypeIdListBox.setModel(taxItemTypeList);
		itemTypeIdListBox.setItemRenderer(new itemTypeIdListBoxRenderer());
		// ======================next_Pay_Grade_Id====================
		List<GenericValue> itemGroupIdList = delegator.findList("TaxItemGroup",
				null, itemGroupIdToDisplay, null, null, false);
		itemGroupIdList.add(0, null);
		SimpleListModel TaxItemGroupList = new SimpleListModel(itemGroupIdList);

		Listbox itemGroupIdListBox = (Listbox) SalaryHeadRuleWindow
				.getFellow("itemGroupId");
		itemGroupIdListBox.setModel(TaxItemGroupList);
		itemGroupIdListBox.setItemRenderer(new itemGroupIdListBoxRenderer());
		// ======================next_Pay_Grade_Id====================
	}

	public void onEvent(Event event) {

		Messagebox messageBox = new Messagebox();
		try {

			System.out.println("***********OnEventCalled************");
			GenericValue userLogin = (GenericValue) Executions.getCurrent()
					.getDesktop().getSession().getAttribute("userLogin");
			System.out.println("*******userLogin Object*********" + userLogin);
			Component AddPayrollRuleWindow = event.getTarget();
			GenericDelegator delegator = (GenericDelegator) userLogin.getDelegator();

			Listitem categoryIdInput = (Listitem) ((Listbox) AddPayrollRuleWindow
					.getFellow("categoryId")).getSelectedItem();
			String categoryId = (String) categoryIdInput.getValue();

			String itemName = (String) ((Textbox) AddPayrollRuleWindow
					.getFellow("itemName")).getValue();

			Listitem itemTypeIdInput = (Listitem) ((Listbox) AddPayrollRuleWindow
					.getFellow("itemTypeId")).getSelectedItem();
			String itemTypeId = (String) itemTypeIdInput.getValue();

			Listitem itemGroupIdInput = (Listitem) ((Listbox) AddPayrollRuleWindow
					.getFellow("itemGroupId")).getSelectedItem();
			String itemGroupId = (String) itemGroupIdInput.getValue();

			String description = (String) ((Textbox) AddPayrollRuleWindow
					.getFellow("description")).getValue();

			String minAmountInput = (String) ((Textbox) AddPayrollRuleWindow
					.getFellow("minAmount")).getValue();
			BigDecimal minAmount = new BigDecimal(minAmountInput);

			String maxAmountInput = (String) ((Textbox) AddPayrollRuleWindow
					.getFellow("maxAmount")).getValue();
			BigDecimal maxAmount = new BigDecimal(maxAmountInput);

			Date fromDateInput = (Date) ((Datebox) AddPayrollRuleWindow
					.getFellow("fromDate")).getValue();
			Date thruDateInput = (Date) ((Datebox) AddPayrollRuleWindow
					.getFellow("thruDate")).getValue();
			
			Timestamp fromDate = new Timestamp(fromDateInput.getTime());
			Timestamp thruDate = new Timestamp(thruDateInput.getTime());

			Map context = UtilMisc.toMap("userLogin", userLogin, "categoryId",
					categoryId, "itemName", itemName, "itemTypeId", itemTypeId,
					"itemGroupId", itemGroupId, "description", description,
					"minAmount", minAmount, "maxAmount", maxAmount, "fromDate",
					fromDate, "thruDate", thruDate);
			Map result = null;

			LocalDispatcher dispatcher = HrmsInfrastructure.getDispatcher();//GenericDispatcher.getLocalDispatcher("default", delegator);
			result = dispatcher.runSync("hr.createTaxItem", context);

			String itemId = (String) result.get("itemId");

			Events.postEvent("onClick$searchButton", AddPayrollRuleWindow
					.getPage().getFellow("searchPanel"), null);

			messageBox.show("Tax Item Successfully Created with Item Id:"
					+ itemId,"Success",1,null);
			AddPayrollRuleWindow.detach();

		} catch (Exception e) {
			try {
				messageBox
						.show("Pay Grade is not Created:Some parameter is missing","Error",1,null);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
	}

	public static void showTaxItemWindow(Event event, GenericValue gv)
			throws SuspendNotAllowedException, InterruptedException,
			GenericEntityException {

		Component approveClaimWindow = event.getTarget();
		GenericValue userLogin = (GenericValue) Executions.getCurrent()
				.getDesktop().getSession().getAttribute("userLogin");
		GenericValue person = (GenericValue) Executions.getCurrent()
				.getDesktop().getSession().getAttribute("person");

		String itemId = gv.getString("itemId");
		String categoryId = gv.getString("categoryId");
		String itemName = gv.getString("itemName");
		String itemTypeId = gv.getString("itemTypeId");
		String itemGroupId = gv.getString("itemGroupId");
		String description = gv.getString("description");
		String minAmount = gv.getString("minAmount");
		String maxAmount = gv.getString("maxAmount");
		Timestamp fromDate = gv.getTimestamp("fromDate");
		Timestamp thruDate = gv.getTimestamp("thruDate");

		Window win = (Window) Executions.createComponents(
				"/zul/payRollManagement/updateTaxItem.zul", null, null);
		win.setTitle("Update Tax Item");
		win.doModal();

		Label itemIdLabel = (Label) win.getFellow("itemId");
		itemIdLabel.setValue(itemId);

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
		// /////////////////////////////////////////////////////////////////////////////////////
		GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
		EntityCondition condition = EntityConditionFunction.makeCondition(
				"enumTypeId", EntityComparisonOperator.EQUALS, "CLAIM_TYPE");

		Set<String> categoryIdToDisplay = new HashSet();
		Set<String> itemTypeIdToDisplay = new HashSet();
		Set<String> itemGroupIdToDisplay = new HashSet();

		categoryIdToDisplay.add("categoryId");
		categoryIdToDisplay.add("categoryName");

		itemTypeIdToDisplay.add("itemTypeId");
		itemTypeIdToDisplay.add("description");

		itemGroupIdToDisplay.add("itemGroupId");
		itemGroupIdToDisplay.add("description");

		List<GenericValue> categoryIds = delegator.findList("TaxCategory",
				null, categoryIdToDisplay, null, null, false);
		List<GenericValue> itemTypeIds = delegator.findList("TaxItemType",
				null, itemTypeIdToDisplay, null, null, false);
		List<GenericValue> itemGroupIds = delegator.findList("TaxItemGroup",
				null, itemGroupIdToDisplay, null, null, false);

		Listbox savedcategoryId = (Listbox) win.getFellow("categoryId");
		Listitem categoryIdItem = new Listitem();
		categoryIdItem.setLabel(categoryId);
		categoryIdItem.setValue(categoryId);

		Listbox saveditemTypeId = (Listbox) win.getFellow("itemTypeId");
		Listitem itemTypeIdItem = new Listitem();
		itemTypeIdItem.setLabel(itemTypeId);
		itemTypeIdItem.setValue(itemTypeId);

		Listbox saveditemGroupId = (Listbox) win.getFellow("itemGroupId");
		Listitem itemGroupIdItem = new Listitem();
		itemGroupIdItem.setLabel(itemGroupId);
		itemGroupIdItem.setValue(itemGroupId);

		for (int i = 0; i < categoryIds.size(); i++) {
			GenericValue ctgryId = categoryIds.get(i);
			String CategoryId = ctgryId.getString("categoryId");
			savedcategoryId.appendItemApi(ctgryId.getString("categoryId"),
					CategoryId);
			if (CategoryId.equals(categoryId)) {
				savedcategoryId.setSelectedIndex(i);
			}
		}

		for (int i = 0; i < itemTypeIds.size(); i++) {
			GenericValue itemTpId = itemTypeIds.get(i);
			String ItemTypeId = itemTpId.getString("itemTypeId");
			saveditemTypeId.appendItemApi(itemTpId.getString("itemTypeId"),
					ItemTypeId);
			if (ItemTypeId.equals(itemTypeId)) {
				saveditemTypeId.setSelectedIndex(i);
			}
		}

		for (int i = 0; i < itemGroupIds.size(); i++) {
			GenericValue itemGpId = itemGroupIds.get(i);
			String ItemGroupId = itemGpId.getString("itemGroupId");
			saveditemGroupId.appendItemApi(itemGpId.getString("itemGroupId"),
					ItemGroupId);
			if (ItemGroupId.equals(itemGroupId)) {
				saveditemGroupId.setSelectedIndex(i);
			}
		}

		// /////////////////////////////////////////////////////////////////////////////////////

		Textbox itemNameTextBox = (Textbox) win.getFellow("itemName");
		itemNameTextBox.setValue(itemName);

		
	}

	public static void DeleteTaxItem(Event event, String itemId) {
		System.out.println("****************SubmitClaim Event Called*********");
		try {
			Component applySalaryHeadWindow = event.getTarget();
			GenericValue userLogin = (GenericValue) Executions.getCurrent()
					.getDesktop().getSession().getAttribute("userLogin");
			GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
			LocalDispatcher dispatcher = HrmsInfrastructure.getDispatcher();//GenericDispatcher.getLocalDispatcher("default", delegator);
			Map submitDelete = null;

			submitDelete = UtilMisc.toMap("userLogin", userLogin, "itemId",
					itemId);
			dispatcher.runSync("hr.deleteTaxItem", submitDelete);
			Events.postEvent("onClick$searchButton", applySalaryHeadWindow
					.getPage().getFellow("searchPanel"), null);
			Messagebox.show("Tax Item: " + itemId + " :Successfully Deleted","Success",1,null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void EditTaxItem(Event event) {

		System.out.println("****************SubmitClaim Event Called*********");
		try {
			Component applyPayrollConditionWindow = event.getTarget();
			GenericValue userLogin = (GenericValue) Executions.getCurrent()
					.getDesktop().getSession().getAttribute("userLogin");
			GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
			LocalDispatcher dispatcher = HrmsInfrastructure.getDispatcher();//GenericDispatcher.getLocalDispatcher("default", delegator);
			Map submitUpdate = null;

			String itemId = (String) ((Label) applyPayrollConditionWindow
					.getFellow("itemId")).getValue();

			Listitem categoryIdInput = (Listitem) ((Listbox) applyPayrollConditionWindow
					.getFellow("categoryId")).getSelectedItem();
			String categoryId = (String) categoryIdInput.getValue();

			String itemName = (String) ((Textbox) applyPayrollConditionWindow
					.getFellow("itemName")).getValue();

			Listitem itemTypeIdInput = (Listitem) ((Listbox) applyPayrollConditionWindow
					.getFellow("itemTypeId")).getSelectedItem();
			String itemTypeId = (String) itemTypeIdInput.getValue();

			Listitem itemGroupIdInput = (Listitem) ((Listbox) applyPayrollConditionWindow
					.getFellow("itemGroupId")).getSelectedItem();
			String itemGroupId = (String) itemGroupIdInput.getValue();

			String description = (String) ((Textbox) applyPayrollConditionWindow
					.getFellow("description")).getValue();

			String minAmountInput = (String) ((Textbox) applyPayrollConditionWindow
					.getFellow("minAmount")).getValue();
			BigDecimal minAmount = new BigDecimal(minAmountInput);

			String maxAmountInput = (String) ((Textbox) applyPayrollConditionWindow
					.getFellow("maxAmount")).getValue();
			BigDecimal maxAmount = new BigDecimal(maxAmountInput);

			Date fromDate = (Date) ((Datebox) applyPayrollConditionWindow
					.getFellow("fromDate")).getValue();
			Date thruDate = (Date) ((Datebox) applyPayrollConditionWindow
					.getFellow("thruDate")).getValue();

			

			Timestamp fromDate1 = new Timestamp(fromDate.getTime());
			Timestamp thruDate1= new Timestamp(thruDate.getTime());
			
			
			submitUpdate = UtilMisc.toMap("userLogin", userLogin, "itemId",
					itemId, "categoryId", categoryId, "itemName", itemName,
					"itemTypeId", itemTypeId, "itemGroupId", itemGroupId,
					"description", description, "minAmount", minAmount,
					"maxAmount", maxAmount, "fromDate", fromDate1, "thruDate",
					thruDate1);
			dispatcher.runSync("hr.updateTaxItem", submitUpdate);
			Events.postEvent("onClick$searchButton",
					applyPayrollConditionWindow.getPage().getFellow(
							"searchPanel"), null);
			Messagebox.show("Tax Item: " + itemId + " :Successfully Updated","Success",1,null);


		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
