package com.ndz.controller;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.text.ParseException;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.humanresext.util.HumanResUtil;
import org.ofbiz.service.GenericDispatcher;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Textbox;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
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
import org.ofbiz.security.Security;
import org.ofbiz.service.GenericDispatcher;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Bandbox;
import org.zkoss.zul.Button;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.SimpleListModel;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.ndz.zkoss.util.HrmsInfrastructure;

public class EmplSalaryStructureManagementController extends GenericForwardComposer {

	SearchController controller = new SearchController();

	@SuppressWarnings( { "deprecation", "unchecked" })
	public void doAfterCompose(Component SalaryHeadRuleWindow) throws Exception {
		super.doAfterCompose(SalaryHeadRuleWindow);

		GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");

		EntityCondition condition = EntityCondition.makeCondition("uomTypeId",
				EntityOperator.EQUALS, "CURRENCY_MEASURE");

		Set<String> FrequencyTypeIdToDisplay = new HashSet();
		Set<String> CurrencyUomIdToDisplay = new HashSet();

		FrequencyTypeIdToDisplay.add("salaryFrequencyTypeId");
		FrequencyTypeIdToDisplay.add("description");

		CurrencyUomIdToDisplay.add("uomId");
		CurrencyUomIdToDisplay.add("description");

		// ==================CURRENCY_UOM_ID================================
		List<GenericValue> CurrencyList = delegator.findList("Uom", condition,
				CurrencyUomIdToDisplay, null, null, false);
		CurrencyList.add(0, null);
		SimpleListModel SalaryCurrencyList = new SimpleListModel(CurrencyList);

		Listbox CurrencyBox = (Listbox) SalaryHeadRuleWindow
				.getFellow("currencyUomId");
		CurrencyBox.setModel(SalaryCurrencyList);
		CurrencyBox.setItemRenderer(new CurrencyRenderer());
		// ======================SALARY_FREQUENCY_TYPE_ID====================
		List<GenericValue> FrequencyTypeList = delegator.findList(
				"SalaryFrequencyType", null, FrequencyTypeIdToDisplay, null,
				null, false);
		FrequencyTypeList.add(0, null);
		SimpleListModel FrequencyTypeIdList = new SimpleListModel(
				FrequencyTypeList);

		Listbox FrequencyTypeIdBox = (Listbox) SalaryHeadRuleWindow
				.getFellow("frequencyTypeId");
		FrequencyTypeIdBox.setModel(FrequencyTypeIdList);
		FrequencyTypeIdBox.setItemRenderer(new SalaryFrequencyTypeRenderer());
		// =================================================================
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

			String employeeId = userLogin.getString("partyId");
			System.out.println("********Party Id********" + employeeId);

			String hrName = ((Textbox) AddPayrollRuleWindow
					.getFellow("hrName")).getValue();
			String geoId = ((Bandbox) AddPayrollRuleWindow
					.getFellow("searchPanel")).getValue();
			Listitem salaryFrequencyTypeIdInput = ((Listbox) AddPayrollRuleWindow
					.getFellow("frequencyTypeId")).getSelectedItem();
			String salaryFrequencyTypeId = (String) salaryFrequencyTypeIdInput
					.getValue();
			Listitem currencyUomIdInput = ((Listbox) AddPayrollRuleWindow
					.getFellow("currencyUomId")).getSelectedItem();
			String currencyUomId = (String) currencyUomIdInput.getValue();
			String positionId = ((Textbox) AddPayrollRuleWindow
					.getFellow("positionId")).getValue();
			Date fromDateInput = (Date) ((Datebox) AddPayrollRuleWindow
					.getFellow("fromDate")).getValue();
			Date thruDateInput = (Date) ((Datebox) AddPayrollRuleWindow
					.getFellow("thruDate")).getValue();

			java.sql.Timestamp fromDate = new java.sql.Timestamp(fromDateInput
					.getTime());
			java.sql.Timestamp thruDate = new java.sql.Timestamp(thruDateInput
					.getTime());

			Map context = UtilMisc.toMap("userLogin", userLogin, "hrName",
					hrName, "geoId", geoId, "salaryFrequencyTypeId",
					salaryFrequencyTypeId, "currencyUomId", currencyUomId,
					"positionId", positionId, "fromDate", fromDate, "thruDate",
					thruDate);
			Map result = null;

			LocalDispatcher dispatcher = HrmsInfrastructure.getDispatcher();//GenericDispatcher.getLocalDispatcher("default", delegator);
			result = dispatcher.runSync("createSalaryStructure", context);

			String salaryStructureId = (String) result.get("salaryStructureId");

			Events.postEvent("onClick$searchButton", AddPayrollRuleWindow
					.getPage().getFellow("searchPanel"), null);

			messageBox
					.show("Salary Structure Successfully Created with Salary Structure Id:"
							+ salaryStructureId,"Success",1,null);
			AddPayrollRuleWindow.detach();

		} catch (Exception e) {
			try {
				messageBox
						.show("Salary Structure is not Created:Some parameter is missing","Error",1,null);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
	}

	public static void showSalaryStructureWindow(Event event, GenericValue gv)
			throws SuspendNotAllowedException, InterruptedException {

		Component approveClaimWindow = event.getTarget();
		GenericValue userLogin = (GenericValue) Executions.getCurrent()
				.getDesktop().getSession().getAttribute("userLogin");
		GenericValue person = (GenericValue) Executions.getCurrent()
				.getDesktop().getSession().getAttribute("person");

		String partyId = gv.getString("partyId");
		String salaryHeadId = gv.getString("salaryHeadId");
		String amount = gv.getString("amount");
		String geoId = gv.getString("geoId");
		Date fromDate = gv.getDate("fromDate");
		Date thruDate = gv.getDate("thruDate");


		Window win = (Window) Executions.createComponents(
				"/zul/payRollManagement/updateEmplSalaryStructure.zul", null, null);
		win.setTitle("Update Salary Structure");
		win.doModal();
		Label partyIdLabel = (Label) win
		.getFellow("partyId");
		partyIdLabel.setValue(partyId);		
		Label salaryHeadIdLabel = (Label) win
				.getFellow("salaryHeadId");
		salaryHeadIdLabel.setValue(salaryHeadId);
		Textbox amountBox = (Textbox) win.getFellow("amount");
		amountBox.setValue(amount);
		Label geoIdLabel = (Label) win
				.getFellow("geoId");
		geoIdLabel.setValue(geoId);		
		Datebox fromDateDateBox = (Datebox) win.getFellow("fromDate");
		fromDateDateBox.setValue(fromDate);
		Datebox thruDateDateBox = (Datebox) win.getFellow("thruDate");
		thruDateDateBox.setValue(thruDate);		
		approveClaimWindow.detach();

	}

	public static void DeleteSalaryStructure(Event event,
			String salaryStructureId) {
		System.out.println("****************SubmitClaim Event Called*********");
		try {
			Component applySalaryHeadWindow = event.getTarget();
			GenericValue userLogin = (GenericValue) Executions.getCurrent()
					.getDesktop().getSession().getAttribute("userLogin");
			GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
			LocalDispatcher dispatcher = HrmsInfrastructure.getDispatcher();//GenericDispatcher.getLocalDispatcher("default", delegator);
			Map submitDelete = null;

			submitDelete = UtilMisc.toMap("userLogin", userLogin,
					"salaryStructureId", salaryStructureId);
			dispatcher.runSync("deleteSalaryStructure", submitDelete);
			Events.postEvent("onClick$searchButton", applySalaryHeadWindow
					.getPage().getFellow("searchPanel"), null);
			Messagebox.show("Salary Structure: " + salaryStructureId
					+ " :Successfully Deleted","Success",1,null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void EditEmplSalaryStructure(Event event) {

		System.out.println("****************SubmitClaim Event Called*********");
		try {
			Component applyPayrollConditionWindow = event.getTarget();
			GenericValue userLogin = (GenericValue) Executions.getCurrent()
					.getDesktop().getSession().getAttribute("userLogin");
			GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
			LocalDispatcher dispatcher = HrmsInfrastructure.getDispatcher();//GenericDispatcher.getLocalDispatcher("default", delegator);
			Map submitUpdate = null;

			String partyId = (String) ((Label) applyPayrollConditionWindow
					.getFellow("partyId")).getValue();			
			
			String salaryHeadId = (String) ((Label) applyPayrollConditionWindow
					.getFellow("salaryHeadId")).getValue();

			String amount = (String) ((Textbox) applyPayrollConditionWindow
					.getFellow("amount")).getValue();			
			Double Amount = new Double(amount);	

			String geoId = (String) ((Label) applyPayrollConditionWindow
					.getFellow("geoId")).getValue();

			Date fromDate = (Date) ((Datebox) applyPayrollConditionWindow
					.getFellow("fromDate")).getValue();
			Date thruDate = (Date) ((Datebox) applyPayrollConditionWindow
					.getFellow("thruDate")).getValue();

			submitUpdate = UtilMisc.toMap("userLogin", userLogin, "partyId", partyId,
					"salaryHeadId", salaryHeadId, "amount", Amount,
					"fromDate", fromDate, "thruDate", thruDate);
			dispatcher.runSync("updateEmplSalary", submitUpdate);
			Events.postEvent("onClick$searchButton",
					applyPayrollConditionWindow.getPage().getFellow(
							"searchPanel"), null);
			Messagebox.show("Empl Salary Structure: " + salaryHeadId
					+ " :Successfully Updated","Success",1,null);

			applyPayrollConditionWindow.detach();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
