package com.ndz.controller;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
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
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.ndz.zkoss.util.HrmsInfrastructure;

@SuppressWarnings("serial")
public class PayrollRuleManagementController extends GenericForwardComposer {

	public static void createRule(Event event) {

		try {

			System.out.println("***********OnEventCalled************");
			GenericValue userLogin = (GenericValue) Executions.getCurrent()
					.getDesktop().getSession().getAttribute("userLogin");
			System.out.println("*******userLogin Object*********" + userLogin);
			Component AddPayrollRuleWindow = event.getTarget();
			GenericDelegator delegator = (GenericDelegator) userLogin.getDelegator();

			String employeeId = userLogin.getString("partyId");
			System.out.println("********Party Id********" + employeeId);

			String ruleName = ((Textbox) AddPayrollRuleWindow
					.getFellow("ruleName")).getValue();

			String ruleDescription = ((Textbox) AddPayrollRuleWindow
					.getFellow("ruleDescription")).getValue();

			Listitem aggregateFuncInput = ((Listbox) AddPayrollRuleWindow
					.getFellow("aggregateFunc")).getSelectedItem();
			String aggregateFunc = aggregateFuncInput==null?null:(String) aggregateFuncInput.getValue();

			Listitem ignoreZeroInput = ((Listbox) AddPayrollRuleWindow
					.getFellow("ignoreZero")).getSelectedItem();
			String ignoreZero = ignoreZeroInput==null?null:(String) ignoreZeroInput.getValue();

			String defaultValueInput = ((Textbox) AddPayrollRuleWindow
					.getFellow("defaultValue")).getValue();
			BigDecimal defaultValue = null;
			if (defaultValueInput!="")
			{
				defaultValue=new BigDecimal(defaultValueInput);
			
			}
			Date fromDateInput = (Date) ((Datebox) AddPayrollRuleWindow
					.getFellow("fromDate")).getValue();
			Date thruDateInput = (Date) ((Datebox) AddPayrollRuleWindow
					.getFellow("thruDate")).getValue();

			java.sql.Timestamp fromDate = fromDateInput==null?null:new java.sql.Timestamp(fromDateInput
					.getTime());
			java.sql.Timestamp thruDate = thruDateInput==null?null:new java.sql.Timestamp(thruDateInput
					.getTime());

			Map<String, Object> context = UtilMisc.toMap("userLogin",
					userLogin, "ruleName", ruleName, "ruleDescription",
					ruleDescription, "aggregateFunc", aggregateFunc,
					"ignoreZero", ignoreZero, "defaultValue", defaultValue,
					"fromDate", fromDate, "thruDate", thruDate);
			Map<String, Object> result = null;

			LocalDispatcher dispatcher = HrmsInfrastructure.getDispatcher();//GenericDispatcher.getLocalDispatcher("default", delegator);
			result = dispatcher.runSync("createPayrollRule", context);

			String err = "";
			err = (String) result.get("responseMessage");
			if (err != null && err.equals("error"))
				org.zkoss.zul.Messagebox.show((String) result
						.get("errorMessage"),"Error",1,null);
			else
				org.zkoss.zul.Messagebox.show("Payroll Rule:	"
						+ result.get("ruleId") + "	:Successfully Updated","Success",1,null);

			AddPayrollRuleWindow.detach();

			// Events.postEvent("onClick$searchButton", AddPayrollRuleWindow
			// .getPage().getFellow("searchPanel"), null);

		} catch (Exception e) {
			try {

			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
	}

	public static void editPayrollRuleWindow(Event event, GenericValue gv)
			throws SuspendNotAllowedException, InterruptedException,
			GenericEntityException {

		String ruleId = gv.getString("ruleId");
		String ruleName = gv.getString("ruleName");
		String ruleDescription = gv.getString("ruleDescription");
		String defaultValue = gv.getString("defaultValue");
		Timestamp fromDate = gv.getTimestamp("fromDate");
		Timestamp thruDate = gv.getTimestamp("thruDate");
		String aggregateFunc = gv.getString("aggregateFunc");
		String ignoreZero = gv.getString("ignoreZero");
		String savedRuleId = gv.getString("ruleId");

		Map arg = new HashMap();
		
		arg.put("ruleId", ruleId);
		
		Window win = (Window) Executions.createComponents(
				"/zul/payRollManagement/updatePayrollRule.zul", null, arg);
		win.setTitle("Update Payroll Rule");

		Label payrollRuleLabel = (Label) win.getFellow("payrollRule");
		payrollRuleLabel.setValue(ruleId);

		Textbox ruleNameTextbox = (Textbox) win.getFellow("ruleName");
		ruleNameTextbox.setValue(ruleName);

		Textbox ruleDescriptionTextbox = (Textbox) win
				.getFellow("ruleDescription");
		Textbox ruleIdTextBox = (Textbox) win.getFellow("ruleId");
		ruleIdTextBox.setValue(savedRuleId);
		ruleDescriptionTextbox.setValue(ruleDescription);

		Textbox defaultValueTextbox = (Textbox) win.getFellow("defaultValue");
		defaultValueTextbox.setValue(defaultValue);

		Timestamp fromDateTimestamp = (Timestamp) fromDate;
		Datebox fromDateDatebox = (Datebox) win.getFellow("fromDate");
		fromDateDatebox.setValue(fromDateTimestamp);

		Timestamp thruDateTimestamp = (Timestamp) thruDate;
		Datebox thruDateDatebox = (Datebox) win.getFellow("thruDate");
		thruDateDatebox.setValue(thruDateTimestamp);

		Listbox aggregateFuncListbox = (Listbox) win.getFellow("aggregateFunc");

		for (int i = 0; i < 3; i++) {
			Listitem TestingListItem = aggregateFuncListbox.getItemAtIndex(i);
			String itemValue = (String) TestingListItem.getValue();
			if (itemValue.equals(aggregateFunc)) {
				aggregateFuncListbox.setSelectedIndex(i);
				break;
			}
		}

		Listbox ignoreZeroListbox = (Listbox) win.getFellow("ignoreZero");

		for (int i = 0; i < 2; i++) {
			Listitem TestingListItem = ignoreZeroListbox.getItemAtIndex(i);
			String itemValue = (String) TestingListItem.getValue();
			if (itemValue.equals(ignoreZero)) {
				ignoreZeroListbox.setSelectedIndex(i);
				break;
			}
		}

		win.doModal();
	}

	public static void DeletePayrollRule(Event event, String ruleId) {
		System.out.println("****************SubmitClaim Event Called*********");
		try {
			Component applySalaryHeadWindow = event.getTarget();
			GenericValue userLogin = (GenericValue) Executions.getCurrent()
					.getDesktop().getSession().getAttribute("userLogin");
			GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
			LocalDispatcher dispatcher = HrmsInfrastructure.getDispatcher();//GenericDispatcher.getLocalDispatcher("default", delegator);
			Map<String, Object> submitDelete = null;
			submitDelete = UtilMisc.toMap("userLogin", userLogin, "ruleId",
					ruleId);
			dispatcher.runSync("deletePayrollRule", submitDelete);
			Events.postEvent("onClick$searchButton", applySalaryHeadWindow
					.getPage().getFellow("searchPanel"), null);
			Messagebox.show("Payroll Rule: " + ruleId
					+ " :Successfully Deleted","Success",1,null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void EditPayrollRule(Event event) throws InterruptedException {

		System.out.println("****************SubmitClaim Event Called*********");
		String error = "";
		Map<String, Object> result = null;
		try {
			Component salaryHeadWindow = event.getTarget();
			GenericValue userLogin = (GenericValue) Executions.getCurrent()
					.getDesktop().getSession().getAttribute("userLogin");
			GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
			LocalDispatcher dispatcher = HrmsInfrastructure.getDispatcher();//GenericDispatcher.getLocalDispatcher("default", delegator);
			Map<String, Object> submitUpdate = null;

			String ruleId = (String) ((Textbox) salaryHeadWindow
					.getFellow("ruleId")).getValue();

			String ruleName = (String) ((Textbox) salaryHeadWindow
					.getFellow("ruleName")).getValue();

			String ruleDescription = (String) ((Textbox) salaryHeadWindow
					.getFellow("ruleDescription")).getValue();

			String defaultValue = (String) ((Textbox) salaryHeadWindow
					.getFellow("defaultValue")).getValue();
			BigDecimal dftValue =defaultValue==""?null: new BigDecimal(defaultValue.toString());

			Date fromDateType = (Date) ((Datebox) salaryHeadWindow
					.getFellow("fromDate")).getValue();

			Timestamp fromDate = fromDateType==null?null:new Timestamp(fromDateType.getTime());
			Date thruDateType = (Date) ((Datebox) salaryHeadWindow
					.getFellow("thruDate")).getValue();
			Timestamp thruDate = thruDateType==null?null:new Timestamp(thruDateType.getTime());
			Listitem aggregateFuncItem = (Listitem) ((Listbox) salaryHeadWindow
					.getFellow("aggregateFunc")).getSelectedItem();
			String aggregateFunc = aggregateFuncItem==null?null:(String) aggregateFuncItem.getValue();

			Listitem ignoreZeroItem = (Listitem) ((Listbox) salaryHeadWindow
					.getFellow("ignoreZero")).getSelectedItem();
			String ignoreZero = ignoreZeroItem==null?null:(String) ignoreZeroItem.getValue();

			submitUpdate = UtilMisc.toMap("userLogin", userLogin, "ruleId",
					ruleId, "ruleName", ruleName, "ruleDescription",
					ruleDescription, "defaultValue", dftValue, "fromDate",
					fromDate, "thruDate", thruDate, "aggregateFunc",
					aggregateFunc, "ignoreZero", ignoreZero);

			result = dispatcher.runSync("updatePayrollRule", submitUpdate);
			Events.postEvent("onClick$searchButton", salaryHeadWindow
					.getPage().getFellow("searchPanel"), null);

			String err = "";
			err = (String) result.get("responseMessage");
			error = (String) result.get("errorMessage");
			if (err != null && err.equals("error"))
				org.zkoss.zul.Messagebox.show((String) result
						.get("errorMessage"),"Error",1,null);
			else
				org.zkoss.zul.Messagebox.show("Payroll Rule:	" + ruleId
						+ "	:Successfully Updated","Success",1,null);

			salaryHeadWindow.detach();

			Events.postEvent("onClick$searchButton", salaryHeadWindow.getPage()
					.getFellow("searchPanel"), null);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// =====================================================================================

	public static void addCondition(Event event) throws InterruptedException {

		System.out.println("****************SubmitClaim Event Called*********");
		String error = "";
		Map<String, Object> result = null;
		try {
			Component UpdatePayrollRuleWindow = event.getTarget();
			GenericValue userLogin = (GenericValue) Executions.getCurrent()
					.getDesktop().getSession().getAttribute("userLogin");
			GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
			LocalDispatcher dispatcher = HrmsInfrastructure.getDispatcher();//GenericDispatcher.getLocalDispatcher("default", delegator);
			Map<String, Object> submitUpdate = null;

			String ruleId = (String) ((Label) UpdatePayrollRuleWindow
					.getFellow("payrollRule")).getValue();
			System.out.println("\n\n\n\n\n\n\nRule Id" + ruleId);
			String conditionId = (String) ((Bandbox) UpdatePayrollRuleWindow
					.getFellow("conditionListBox")).getValue();

			String actionId = (String) ((Bandbox) UpdatePayrollRuleWindow
					.getFellow("actionListBox")).getValue();

			submitUpdate = UtilMisc.toMap("userLogin", userLogin, "ruleId",
					ruleId, "conditionId", conditionId, "actionId", actionId);

			result = dispatcher.runSync("createRuleConditionAction",
					submitUpdate);

			String err = "";
			err = (String) result.get("responseMessage");
			error = (String) result.get("errorMessage");
			/*
			 * if (err != null && err.equals("error"))
			 * org.zkoss.zul.Messagebox.show((String) result
			 * .get("errorMessage")); else
			 */
			org.zkoss.zul.Messagebox
					.show("Rule Attached with Action and Condition","Success",1,null);

			Events.postEvent("onClick$searchButton", UpdatePayrollRuleWindow
					.getPage().getFellow("searchPanel"), null);
			

		} catch (Exception e) {
			org.zkoss.zul.Messagebox.show((String) result.get("errorMessage"),"Success",1,null);
			e.printStackTrace();
		}
	}
	
	
	
	public static void delete(Event event, String ruleId,String conditionId,String actionId)
	throws GenericServiceException {
try {
	System.out
			.println("***************Event getting Called*************");
	System.out.println("ruleId ---- " + ruleId+conditionId+actionId);
	Component UpdatePayrollRuleWindow = event.getTarget();
	GenericValue userLogin = (GenericValue) Executions.getCurrent()
			.getDesktop().getSession().getAttribute("userLogin");
	GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");

	Map<String, Object> context = UtilMisc.toMap("userLogin",
			userLogin, "ruleId", ruleId,"conditionId",conditionId,"actionId",actionId);

	LocalDispatcher dispatcher = HrmsInfrastructure.getDispatcher();//GenericDispatcher.getLocalDispatcher("default", delegator);
	dispatcher.runSync("deleteRuleConditionAction", context);
	Messagebox.show("Deleted Successfully", "Success", 1, null);
	Events.postEvent("onClick$searchButton", UpdatePayrollRuleWindow.getPage()
			.getFellow("searchPanel"), null);
} catch (Exception e) {
	
	e.printStackTrace();

}

}
	
	
	
	
	
	

}
