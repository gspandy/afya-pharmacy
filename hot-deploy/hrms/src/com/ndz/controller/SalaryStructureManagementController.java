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
import org.ofbiz.entity.condition.EntityComparisonOperator;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.GenericDispatcher;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Bandbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.SimpleListModel;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.ndz.component.party.CurrencyRenderer;
import com.ndz.zkoss.util.HrmsInfrastructure;

public class SalaryStructureManagementController extends GenericForwardComposer {

	SearchController controller = new SearchController();

	@SuppressWarnings({ "deprecation", "unchecked" })
	public void doAfterCompose(Component SalaryHeadRuleWindow) throws Exception {
		super.doAfterCompose(SalaryHeadRuleWindow);

		GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");

		EntityCondition condition = EntityCondition.makeCondition("uomTypeId", EntityOperator.EQUALS, "CURRENCY_MEASURE");

		Set<String> FrequencyTypeIdToDisplay = new HashSet();
		Set<String> CurrencyUomIdToDisplay = new HashSet();
		Set<String> EmpPositionTypeToDisplay = new HashSet();

		FrequencyTypeIdToDisplay.add("salaryFrequencyTypeId");
		FrequencyTypeIdToDisplay.add("description");

		CurrencyUomIdToDisplay.add("uomId");
		CurrencyUomIdToDisplay.add("description");

		EmpPositionTypeToDisplay.add("description");
		EmpPositionTypeToDisplay.add("emplPositionTypeId");

		// ==================CURRENCY_UOM_ID================================
		List<GenericValue> CurrencyList = delegator.findList("Uom", condition, CurrencyUomIdToDisplay, null, null, false);
		CurrencyList.add(0, null);
		SimpleListModel SalaryCurrencyList = new SimpleListModel(CurrencyList);

		Listbox CurrencyBox = (Listbox) SalaryHeadRuleWindow.getFellow("currencyUomId");
		CurrencyBox.setModel(SalaryCurrencyList);
		CurrencyBox.setItemRenderer(new CurrencyRenderer());
		// ======================SALARY_FREQUENCY_TYPE_ID====================
		List<GenericValue> FrequencyTypeList = delegator.findList("SalaryFrequencyType", null, FrequencyTypeIdToDisplay, null, null, false);
		FrequencyTypeList.add(0, null);
		SimpleListModel FrequencyTypeIdList = new SimpleListModel(FrequencyTypeList);

		Listbox FrequencyTypeIdBox = (Listbox) SalaryHeadRuleWindow.getFellow("frequencyTypeId");
		FrequencyTypeIdBox.setModel(FrequencyTypeIdList);
		FrequencyTypeIdBox.setItemRenderer(new SalaryFrequencyTypeRenderer());
		// =================================================================
		List<GenericValue> EmplPositionType = delegator.findList("EmplPositionType", null, EmpPositionTypeToDisplay, null, null, false);
		EmplPositionType.add(0, null);
		SimpleListModel EmpPositionTypeList = new SimpleListModel(EmplPositionType);

		Listbox EmpPositionBox = (Listbox) SalaryHeadRuleWindow.getFellow("positionId");
		EmpPositionBox.setModel(EmpPositionTypeList);
		EmpPositionBox.setItemRenderer(new com.ndz.controller.EmplPositionRenderer());
		// =================================================================
	}

	public static void creatStructure(Event event) {

		Messagebox messageBox = new Messagebox();
		try {

			GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
			Component AddPayrollRuleWindow = event.getTarget().getFellow("AddSalaryStructureWindow");
			GenericDelegator delegator = (GenericDelegator) userLogin.getDelegator();

			String employeeId = userLogin.getString("partyId");

			String hrName = ((Textbox) AddPayrollRuleWindow.getFellow("hrName")).getValue();
			String geoId = ((Combobox) AddPayrollRuleWindow.getFellow("countrybandbox")).getSelectedItem().getValue().toString();
			Listitem salaryFrequencyTypeIdInput = ((Listbox) AddPayrollRuleWindow.getFellow("frequencyTypeId")).getSelectedItem();

			String salaryFrequencyTypeId = (salaryFrequencyTypeIdInput == null) ? null : ((String) salaryFrequencyTypeIdInput.getValue());
			Listitem currencyUomIdInput = ((Listbox) AddPayrollRuleWindow.getFellow("currencyUomId")).getSelectedItem();
			String currencyUomId = (String) currencyUomIdInput.getValue();
			Listitem positionIdInput = ((Listbox) AddPayrollRuleWindow.getFellow("positionId")).getSelectedItem();
			((Listbox) AddPayrollRuleWindow.getFellow("positionId")).getSelectedItem();

			String positionTypeId = (positionIdInput == null) ? null : ((String) positionIdInput.getValue());

			Date fromDateInput = (Date) ((Datebox) AddPayrollRuleWindow.getFellow("fromDate")).getValue();
			Date thruDateInput = (Date) ((Datebox) AddPayrollRuleWindow.getFellow("thruDate")).getValue();

			java.sql.Timestamp fromDate = new java.sql.Timestamp(fromDateInput.getTime());
			java.sql.Timestamp thruDate = new java.sql.Timestamp(thruDateInput.getTime());

			Map result = null;

			LocalDispatcher dispatcher = HrmsInfrastructure.getDispatcher();//GenericDispatcher.getLocalDispatcher("default", delegator);

			Map context = UtilMisc.toMap("userLogin", userLogin, "hrName", hrName, "geoId", geoId, "salaryFrequencyTypeId", salaryFrequencyTypeId,
					"currencyUomId", currencyUomId, "positionId", positionTypeId, "fromDate", fromDate, "thruDate", thruDate);

			result = dispatcher.runSync("createSalaryStructure", context);

			String salaryStructureId = (String) result.get("salaryStructureId");

			EntityCondition ec1 = EntityCondition.makeCondition("salaryHeadTypeId", EntityComparisonOperator.NOT_EQUAL, "Adhoc");
			EntityCondition ec2 = EntityCondition.makeCondition("isMandatory", "Y");
			List<GenericValue> attachedSalaryHeadList = delegator.findList("SalaryHead", EntityConditionList.makeCondition(ec1, ec2), null,
					UtilMisc.toList("-hrName"), null, false);
			for (GenericValue gv : attachedSalaryHeadList) {
				String salaryStructureHeadId = delegator.getNextSeqId("SalaryStructureHead");
				delegator.create(delegator.makeValue(
						"SalaryStructureHead",
						UtilMisc.toMap("salaryStructureHeadId", salaryStructureHeadId, "salaryStructureId", salaryStructureId, "salaryHeadId",
								gv.getString("salaryHeadId"))));
			}

			Messagebox.show("Salary Structure Successfully Created with name :" + hrName, "Success", 1, null);

			Events.postEvent("onClick$searchButton", AddPayrollRuleWindow.getPage().getFellow("searchPanel"), null);

		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	@SuppressWarnings("deprecation")
	public static void showSalaryStructureWindow(Event event, GenericValue gv) throws SuspendNotAllowedException, InterruptedException, GenericEntityException {

		Component approveClaimWindow = event.getTarget();
		GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
		GenericValue person = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("person");

		String salaryStructureId = gv.getString("salaryStructureId");
		String hrName = gv.getString("hrName");
		String geoId = gv.getString("geoId");
		Timestamp fromDate = gv.getTimestamp("fromDate");
		Timestamp thruDate = gv.getTimestamp("thruDate");

		String currencyUomId = gv.getString("currencyUomId");
		String positionId = gv.getString("positionId");
		String salaryFrequencyTypeId = gv.getString("salaryFrequencyTypeId");

		Window win = (Window) Executions.createComponents("/zul/payRollManagement/updateSalaryStructure.zul", null, null);
		win.setTitle("Update Salary Structure");
		win.setAttribute("salaryStructureId", salaryStructureId);

		// /////////////////////////////////////////////////////////////////////////////////////////////
		GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");

		EntityCondition condition = EntityCondition.makeCondition("uomTypeId", EntityOperator.EQUALS, "CURRENCY_MEASURE");

		EntityCondition EmpPositionCondition = EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "EMPL_POS_ACTIVE");

		Set<String> FrequencyTypeIdToDisplay = new HashSet();
		Set<String> CurrencyUomIdToDisplay = new HashSet();
		Set<String> EmpPositionToDisplay = new HashSet();

		FrequencyTypeIdToDisplay.add("salaryFrequencyTypeId");
		FrequencyTypeIdToDisplay.add("description");

		CurrencyUomIdToDisplay.add("uomId");
		CurrencyUomIdToDisplay.add("description");

		EmpPositionToDisplay.add("emplPositionId");
		EmpPositionToDisplay.add("statusId");
		EmpPositionToDisplay.add("emplPositionTypeId");
		// =====================SalaryHeadType==============================
		List<GenericValue> CurrencyList = delegator.findList("Uom", condition, CurrencyUomIdToDisplay, null, null, false);
		CurrencyList.add(0, null);
		SimpleListModel SalaryCurrencyList = new SimpleListModel(CurrencyList);

		Listbox CurrencyBox = (Listbox) win.getFellow("currencyUomId");
		CurrencyBox.setModel(SalaryCurrencyList);
		CurrencyBox.setItemRenderer(new CurrencyRenderer());

		for (int i = 1; i < CurrencyList.size(); i++) {
			GenericValue IndividualsalaryCurrency = CurrencyList.get(i);
			String itemValue = IndividualsalaryCurrency.getString("uomId");
			if (itemValue.equals(currencyUomId)) {
				CurrencyBox.setSelectedIndex(i);
				break;
			}
		}
		// =====================UOM===CURRENCY==============================

		List<GenericValue> FrequencyTypeList = delegator.findList("SalaryFrequencyType", null, FrequencyTypeIdToDisplay, null, null, false);
		FrequencyTypeList.add(0, null);
		SimpleListModel FrequencyTypeIdList = new SimpleListModel(FrequencyTypeList);

		Listbox FrequencyTypeIdBox = (Listbox) win.getFellow("salaryFrequencyTypeId");
		FrequencyTypeIdBox.setModel(FrequencyTypeIdList);
		FrequencyTypeIdBox.setItemRenderer(new SalaryFrequencyTypeRenderer());

		for (int i = 1; i < FrequencyTypeList.size(); i++) {
			GenericValue IndividualFrequencyType = FrequencyTypeList.get(i);
			String itemValue = IndividualFrequencyType.getString("salaryFrequencyTypeId");
			if (itemValue.equals(salaryFrequencyTypeId)) {
				FrequencyTypeIdBox.setSelectedIndex(i);
				break;
			}
		}
		// =====================SalaryComputationType==============================

		List<GenericValue> EmpPosition = delegator.findList("EmplPositionType", null, null, null, null, false);
		EmpPosition.add(0, null);
		SimpleListModel EmpPositionList = new SimpleListModel(EmpPosition);

		Listbox EmpPositionBox = (Listbox) win.getFellow("positionId");
		EmpPositionBox.setModel(EmpPositionList);
		EmpPositionBox.setItemRenderer(new EmplPositionRenderer());

		for (int i = 1; i < EmpPosition.size(); i++) {
			GenericValue IndividualEmpPosition = EmpPosition.get(i);
			String itemValue = IndividualEmpPosition.getString("emplPositionTypeId");
			if (itemValue.equals(positionId)) {
				EmpPositionBox.setSelectedIndex(i);
				break;
			}
		}
		// ========================================================================
		// /////////////////////////////////////////////////////////////////////////////////////////////
		Label salaryStructureIdTestLabel = (Label) win.getFellow("salaryStructureIdTest");
		salaryStructureIdTestLabel.setValue(salaryStructureId);
		Textbox salaryStructureIdTextbox = (Textbox) win.getFellow("salaryStructureId");
		salaryStructureIdTextbox.setValue(salaryStructureId);
		Textbox hrNameLabel = (Textbox) win.getFellow("hrName");
		hrNameLabel.setValue(hrName);
		Textbox geoIdTextBox = (Textbox) win.getFellow("geoId");
		geoIdTextBox.setValue(geoId);
		Datebox fromDateDateBox = (Datebox) win.getFellow("fromDate");
		fromDateDateBox.setValue(fromDate);
		Datebox thruDateDateBox = (Datebox) win.getFellow("thruDate");
		thruDateDateBox.setValue(thruDate);
		win.doModal();

	}

	public static void attachEmployeeSalaryStructureWindow(Event event, GenericValue gv) throws SuspendNotAllowedException, InterruptedException {
		String salaryStructureId = gv.getString("salaryStructureId");
		Executions.sendRedirect("addEmployeeSalaryStructure?salaryStructureId=" + salaryStructureId);
	}

	public static void EditEmployeeSalaryStructureWindow(Event event, GenericValue gv) throws SuspendNotAllowedException, InterruptedException {

		Component approveClaimWindow = event.getTarget();
		@SuppressWarnings("unused")
		GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
		@SuppressWarnings("unused")
		GenericValue person = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("person");

		String salaryStructureId = gv.getString("salaryStructureId");

		System.out.println("\n\n\n\n\n salaryStructureId:	" + salaryStructureId);
		Map<String, String> salaryStructureIdMap = UtilMisc.toMap("salaryStructureId", salaryStructureId);

		Window win = (Window) Executions.createComponents("/zul/payRollManagement/editEmployeeSalaryStructure.zul", null, salaryStructureIdMap);
		win.setTitle("Manage Salary Structure");

		win.setAttribute("salaryStructureId", salaryStructureId);

		win.doModal();

		approveClaimWindow.detach();
	}

	public static void DeleteSalaryStructure(Event event, String salaryStructureId) {
		System.out.println("****************SubmitClaim Event Called*********");
		try {
			Component applySalaryHeadWindow = event.getTarget();
			GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
			GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
			LocalDispatcher dispatcher = HrmsInfrastructure.getDispatcher();//GenericDispatcher.getLocalDispatcher("default", delegator);
			Map submitDelete = null;

			submitDelete = UtilMisc.toMap("userLogin", userLogin, "salaryStructureId", salaryStructureId);
			dispatcher.runSync("deleteSalaryStructure", submitDelete);
			Events.postEvent("onClick$searchButton", applySalaryHeadWindow.getPage().getFellow("searchPanel"), null);
			Messagebox.show("Salary Structure: " + salaryStructureId + " :Successfully Deleted", "Success", 1, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void EditSalaryStructure(Event event) {

		try {
			Component applyPayrollConditionWindow = event.getTarget();
			GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
			GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
			LocalDispatcher dispatcher = HrmsInfrastructure.getDispatcher();//GenericDispatcher.getLocalDispatcher("default", delegator);
			Map submitUpdate = null;

			String salaryStructureId = (String) ((Textbox) applyPayrollConditionWindow.getFellow("salaryStructureId")).getValue();
			String hrName = (String) ((Textbox) applyPayrollConditionWindow.getFellow("hrName")).getValue();
			String geoId = (String) ((Textbox) applyPayrollConditionWindow.getFellow("geoId")).getValue();

			Listitem currencyUomIdListItem = (Listitem) ((Listbox) applyPayrollConditionWindow.getFellow("currencyUomId")).getSelectedItem();
			String currencyUomId = (String) currencyUomIdListItem.getValue();

			Listitem positionIdListItem = (Listitem) ((Listbox) applyPayrollConditionWindow.getFellow("positionId")).getSelectedItem();
			String positionId = (String) positionIdListItem.getValue();

			Listitem salaryFrequencyTypeIdListItem = (Listitem) ((Listbox) applyPayrollConditionWindow.getFellow("salaryFrequencyTypeId")).getSelectedItem();
			String salaryFrequencyTypeId = (String) salaryFrequencyTypeIdListItem.getValue();

			Timestamp fromDate = (Timestamp) ((Datebox) applyPayrollConditionWindow.getFellow("fromDate")).getValue();
			Timestamp thruDate = (Timestamp) ((Datebox) applyPayrollConditionWindow.getFellow("thruDate")).getValue();

			submitUpdate = UtilMisc.toMap("userLogin", userLogin, "salaryStructureId", salaryStructureId, "hrName", hrName, "geoId", geoId, "fromDate",
					fromDate, "thruDate", thruDate, "currencyUomId", currencyUomId, "positionId", positionId, "salaryFrequencyTypeId", salaryFrequencyTypeId);
			dispatcher.runSync("updateSalaryStructure", submitUpdate);
			Events.postEvent("onClick$searchButton", applyPayrollConditionWindow.getPage().getFellow("searchPanel"), null);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void updateCTC(Event event, GenericValue gv) throws GenericServiceException, SuspendNotAllowedException, InterruptedException,
			GenericEntityException {
		final Window win = (Window) Executions.createComponents("/zul/payRollManagement/editCTC.zul", null, null);
		win.doModal();

		Label partyId = (Label) win.getFellow("partyId");
		partyId.setValue(gv.getString("partyId"));

		Label salaryStructureId = (Label) win.getFellow("salaryStructureId");
		salaryStructureId.setValue(gv.getString("salaryStructureId"));

		Textbox salaryHead = (Textbox) win.getFellow("salaryHead");
		salaryHead.setValue(gv.getString("salaryHeadId"));

		Label hrName = (Label) win.getFellow("hrName");
		hrName.setValue(gv.getString("hrName"));

		Textbox amount = (Textbox) win.getFellow("amount");
		amount.setValue(gv.getString("amount"));

		Datebox fromDate = (Datebox) win.getFellow("fromDate");
		fromDate.setValue(gv.getDate("fromDate"));

	}

	public static void updateEmployeeCTC(Event event) throws GenericEntityException, InterruptedException {

		GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
		Component updateCTC = event.getTarget();

		String partyId = (String) ((Label) updateCTC.getFellow("partyId")).getValue();

		String salaryHeadId = (String) ((Textbox) updateCTC.getFellow("salaryHead")).getValue();

		String amount = (String) ((Textbox) updateCTC.getFellow("amount")).getValue();
		String salaryStructureId = (String) ((Label) updateCTC.getFellow("salaryStructureId")).getValue();
		Date lfromDate = (Date) ((Datebox) updateCTC.getFellow("fromDate")).getValue();
		Timestamp fromDate = new Timestamp(lfromDate.getTime());
		Timestamp thruDate = null;
		List<GenericValue> emplSalList = delegator.findByAnd("EmplSal", UtilMisc.toMap("partyId", partyId, "salaryHeadId", salaryHeadId));
		GenericValue emplSalGv = EntityUtil.getFirst(emplSalList);

		Map<String, Object> map = null;
		map = UtilMisc.toMap("partyId", partyId, "salaryHeadId", salaryHeadId, "amount", amount, "fromDate", fromDate, "thruDate", thruDate,
				"salaryStructureId", salaryStructureId);

		emplSalGv.putAll(map);
		emplSalGv.store();
		Events.postEvent("onClick", updateCTC.getPage().getFellow("searchPanel").getFellow("searchPerCompany"), null);
		Messagebox.show("CTC amount Updated succesfully", "Success", 1, null);
		updateCTC.detach();

	}

	public void updatePreferences(Event event, GenericValue gv) throws GenericServiceException, SuspendNotAllowedException, InterruptedException,
			GenericEntityException {
		final Window win = (Window) Executions.createComponents("/zul/payRollManagement/editPrefernces.zul", null, null);
		win.doModal();

		Label partyId = (Label) win.getFellow("partyId");
		partyId.setValue(gv.getString("partyId"));

		Textbox bankName = (Textbox) win.getFellow("bankName");
		bankName.setValue(gv.getString("bankName"));

		Textbox bankAccountNumber = (Textbox) win.getFellow("bankAccountNumber");
		bankAccountNumber.setValue(gv.getString("bankAccountNumber"));

		Textbox panNumber = (Textbox) win.getFellow("panNumber");
		panNumber.setValue(gv.getString("panNumber"));

		Textbox pfAccountNumber = (Textbox) win.getFellow("pfAccountNumber");
		pfAccountNumber.setValue(gv.getString("pfAccountNumber"));

	}

	public static void updateEmployeePreferences(Event event) throws GenericEntityException, InterruptedException {

		GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
		Component updatePreferences = event.getTarget();

		String partyId = (String) ((Label) updatePreferences.getFellow("partyId")).getValue();

		String bankName = (String) ((Textbox) updatePreferences.getFellow("bankName")).getValue();

		String bankAccountNumber = (String) ((Textbox) updatePreferences.getFellow("bankAccountNumber")).getValue();

		String panNumber = (String) ((Textbox) updatePreferences.getFellow("panNumber")).getValue();

		String pfAccountNumber = (String) ((Textbox) updatePreferences.getFellow("pfAccountNumber")).getValue();

		GenericValue emplPreference = null;

		emplPreference = delegator.findByPrimaryKey("Preferences", UtilMisc.toMap("partyId", partyId));

		Map<String, String> map = null;
		map = UtilMisc.toMap("partyId", partyId, "bankName", bankName, "bankAccountNumber", bankAccountNumber, "panNumber", panNumber, "pfAccountNumber",
				pfAccountNumber);

		emplPreference.putAll(map);
		emplPreference.store();
		Messagebox.show("Preferences amount Updated succesfully", "Success", 1, null);

	}

}