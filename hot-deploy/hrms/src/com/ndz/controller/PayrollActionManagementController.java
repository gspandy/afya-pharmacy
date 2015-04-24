package com.ndz.controller;

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
import org.ofbiz.service.GenericDispatcher;
import org.ofbiz.service.LocalDispatcher;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Bandbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.SimpleListModel;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;
import org.zkoss.zul.Messagebox;

import com.ndz.zkoss.util.HrmsInfrastructure;

public class PayrollActionManagementController extends GenericForwardComposer {

	@SuppressWarnings("deprecation")
	public void doAfterCompose(Component AddPayrollConditionWindow) throws Exception {
		super.doAfterCompose(AddPayrollConditionWindow);

		GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");

		EntityCondition condition = EntityCondition.makeCondition("operatorType", EntityOperator.EQUALS, "ARITHMETIC");

		Set<String> PayrollConditionToDisplay = new HashSet();

		PayrollConditionToDisplay.add("operatorId");
		PayrollConditionToDisplay.add("description");

		// =====================SalaryHeadType==============================
		List<GenericValue> PayrollConditionList = delegator.findList("PayrollOpEnumeration", condition, PayrollConditionToDisplay, null, null, false);
		PayrollConditionList.add(0, null);
		SimpleListModel PayrollConditionListModel = new SimpleListModel(PayrollConditionList);

		Listbox PayrollConditionListBox = (Listbox) AddPayrollConditionWindow.getFellow("operator");
		PayrollConditionListBox.setModel(PayrollConditionListModel);
		PayrollConditionListBox.setItemRenderer(new PayrollOperatorRenderer());
		// =====================UOM===CURRENCY==============================
	}

	public void onEvent(Event event) {

		try {
			GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
			Component AddPayrollActionWindow = event.getTarget();
			GenericDelegator delegator = (GenericDelegator) userLogin.getDelegator();
			String actionName = ((Textbox) AddPayrollActionWindow.getFellow("actionName")).getValue();
			String bandBoxId = (String) ((Listbox) AddPayrollActionWindow.getFellow("actionList1")).getSelectedItem().getAttribute("bandBoxId");
			String operandOne = ((Bandbox) AddPayrollActionWindow.getFellow(bandBoxId)).getValue();
			String bandBoxId2 = (String) ((Listbox) AddPayrollActionWindow.getFellow("actionList2")).getSelectedItem().getAttribute("bandBoxId2");
			String operandTwo = ((Bandbox) AddPayrollActionWindow.getFellow(bandBoxId2)).getValue();
			Listitem operatorListItem = ((Listbox) AddPayrollActionWindow.getFellow("operator")).getSelectedItem();
			String operator = (String) operatorListItem.getValue();
			Map<String, Object> context = UtilMisc.toMap("userLogin", userLogin, "actionName", actionName, "operandOne", operandOne, "operandTwo", operandTwo,
					"operatorId", operator,"actionListOne",bandBoxId,"actionListTwo",bandBoxId2);
			Map<String, Object> result = null;
			LocalDispatcher dispatcher = HrmsInfrastructure.getDispatcher();//GenericDispatcher.getLocalDispatcher("default", delegator);
			result = dispatcher.runSync("createPayrollAction", context);
			String err = "";
			err = (String) result.get("responseMessage");
			if (err != null && err.equals("error"))
				org.zkoss.zul.Messagebox.show((String) result.get("errorMessage"), "Error", 1, null);
			else
				org.zkoss.zul.Messagebox.show("Created Successfully", "Success", 1, null);

			AddPayrollActionWindow.detach();

		} catch (Exception e) {
			try {
				org.zkoss.zul.Messagebox.show("Created UnSuccessfully", "Error", 1, null);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
	}

	public static void editPayrollActionWindow(Event event, GenericValue gv) throws SuspendNotAllowedException, InterruptedException, GenericEntityException {

		String actionId = gv.getString("actionId");
		String actionName = gv.getString("actionName");
		String operandOne = gv.getString("operandOne");
		String operandTwo = gv.getString("operandTwo");
		String operatorId = gv.getString("operatorId");
		String actionListOne = gv.getString("actionListOne");
		String actionListTwo = gv.getString("actionListTwo");

		Window win = (Window) Executions.createComponents("/zul/payRollManagement/updatePayrollAction.zul", null,
				UtilMisc.toMap("searchBtn", event.getTarget().getFellow("searchPerCompany")));
		win.setTitle("Update Payroll Action");

		/****** Setting List Box Items and Selected List Item ******/

		GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");

		EntityCondition condition = EntityCondition.makeCondition("operatorType", EntityOperator.EQUALS, "ARITHMETIC");

		Set<String> PayrollConditionToDisplay = new HashSet<String>();

		PayrollConditionToDisplay.add("operatorId");
		PayrollConditionToDisplay.add("description");

		List<GenericValue> PayrollConditionList = delegator.findList("PayrollOpEnumeration", condition, PayrollConditionToDisplay, null, null, false);
		SimpleListModel PayrollConditionListModel = new SimpleListModel(PayrollConditionList);

		Listbox PayrollConditionListBox = (Listbox) win.getFellow("operator");
		PayrollConditionListBox.setModel(PayrollConditionListModel);
		PayrollConditionListBox.setItemRenderer(new PayrollOperatorRenderer());

		for (int i = 0; i < PayrollConditionList.size(); i++) {
			GenericValue IndividualPayrollCondition = PayrollConditionList.get(i);
			String itemValue = IndividualPayrollCondition.getString("operatorId");
			if (itemValue.equals(operatorId)) {
				PayrollConditionListBox.setSelectedIndex(i);
				break;
			}
		}
		/***********************************************************/

		Listbox actionList1 = (Listbox) win.getFellow("actionList1");
		for(int i = 0 ; actionList1.getItems().size() > i ; i++ ){
			Listitem li = actionList1.getItemAtIndex(i);
			String id = li.getAttribute("bandBoxId").toString();
			if(actionListOne.equalsIgnoreCase(id)){
				actionList1.setSelectedItem(li);
				togglebandbox(win,id);
				((Bandbox) win.getFellow(id)).setValue(operandOne);
				break;
			}
		}
		
		Listbox actionList2 = (Listbox) win.getFellow("actionList2");
		for(int i = 0 ; actionList2.getItems().size() > i ; i++ ){
			Listitem li = actionList2.getItemAtIndex(i);
			String id = li.getAttribute("bandBoxId2").toString();
			if(actionListTwo.equalsIgnoreCase(id)){
				actionList2.setSelectedItem(li);
				togglebandbox2(win,id);
				((Bandbox) win.getFellow(id)).setValue(operandTwo);
				break;
			}
		}
		
		((Label)win.getFellow("actionName")).setValue(actionName);
		((Label)win.getFellow("actionId")).setValue(actionId);

		win.doModal();
	}
	
	private static void togglebandbox(Window win , String id){
		((Bandbox) win.getFellow("employeeAttribute")).setVisible(false);
		((Bandbox) win.getFellow("bandboxtaxItem")).setVisible(false);
		((Bandbox) win.getFellow("bandboxcondition")).setVisible(false);
		((Bandbox) win.getFellow("bandboxAction")).setVisible(false);
		((Bandbox) win.getFellow("bandboxSalary")).setVisible(false);
		((Bandbox) win.getFellow("textinput")).setVisible(false);
		((Bandbox) win.getFellow(id)).setVisible(true);
	}
	
	private static void togglebandbox2(Window win , String id){
		((Bandbox) win.getFellow("employeeAttribute2")).setVisible(false);
		((Bandbox) win.getFellow("bandboxtaxItem2")).setVisible(false);
		((Bandbox) win.getFellow("bandboxcondition2")).setVisible(false);
		((Bandbox) win.getFellow("bandboxAction2")).setVisible(false);
		((Bandbox) win.getFellow("bandboxSalary2")).setVisible(false);
		((Bandbox) win.getFellow("textinput1")).setVisible(false);
		((Bandbox) win.getFellow(id)).setVisible(true);
	}

	public static void DeletePayrollAction(Event event, String actionId) {
		try {
			Component applySalaryHeadWindow = event.getTarget();
			GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
			GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
			LocalDispatcher dispatcher = HrmsInfrastructure.getDispatcher();//GenericDispatcher.getLocalDispatcher("default", delegator);
			Map<String, Object> submitDelete = null;
			submitDelete = UtilMisc.toMap("userLogin", userLogin, "actionId", actionId);
			dispatcher.runSync("deletePayrollAction", submitDelete);
			Events.postEvent("onClick$searchButton", applySalaryHeadWindow.getPage().getFellow("searchPanel"), null);
			Messagebox.show("Deleted Successfully", "Success", 1, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void Edit1PayrollAction(Event event, String actionId, String actionName, String operandOne, String operandOneType, String operandTwo,
			String operandTwoType, String operatorId) {

		System.out.println("****************SubmitClaim Event Called*********");
		try {
			Component applyPayrollConditionWindow = event.getTarget();
			GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
			GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
			LocalDispatcher dispatcher = HrmsInfrastructure.getDispatcher();//GenericDispatcher.getLocalDispatcher("default", delegator);
			Map submitUpdate = null;
			submitUpdate = UtilMisc.toMap("userLogin", userLogin, "actionId", actionId, "actionName", actionName, "operandOne", operandOne, "operandOneType",
					operandOneType, "operandTwo", operandTwo, "operandTwoType", operandTwoType, "operatorId", operatorId);
			dispatcher.runSync("updatePayrollAction", submitUpdate);
			Events.postEvent("onClick$searchButton", applyPayrollConditionWindow.getPage().getFellow("searchPanel"), null);
			Messagebox.show("Payroll Action: " + actionId + " :Successfully Updated", "Success", 1, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void EditPayrollAction(Event event) {
		try {
			Component salaryHeadWindow = event.getTarget();
			GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
			GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
			LocalDispatcher dispatcher = HrmsInfrastructure.getDispatcher();//GenericDispatcher.getLocalDispatcher("default", delegator);
			String actionId = (String) ((Label) salaryHeadWindow.getFellow("actionId")).getValue();
			String actionName = ((Label) salaryHeadWindow.getFellow("actionName")).getValue();
			String bandBoxId = (String) ((Listbox) salaryHeadWindow.getFellow("actionList1")).getSelectedItem().getAttribute("bandBoxId");
			String operandOne = ((Bandbox) salaryHeadWindow.getFellow(bandBoxId)).getValue();
			String bandBoxId2 = (String) ((Listbox) salaryHeadWindow.getFellow("actionList2")).getSelectedItem().getAttribute("bandBoxId2");
			String operandTwo = ((Bandbox) salaryHeadWindow.getFellow(bandBoxId2)).getValue();
			Listitem operatorListItem = ((Listbox) salaryHeadWindow.getFellow("operator")).getSelectedItem();
			String operator = (String) operatorListItem.getValue();
			Map<String, Object> context = UtilMisc.toMap("userLogin", userLogin,"actionId", actionId,"actionName", actionName, "operandOne", operandOne, "operandTwo", operandTwo,
					"operatorId", operator,"actionListOne",bandBoxId,"actionListTwo",bandBoxId2);
			Map<String, Object> result = dispatcher.runSync("updatePayrollAction", context);
			String err = "";
			err = (String) result.get("responseMessage");
			if (err != null && err.equals("error"))
				org.zkoss.zul.Messagebox.show("Updated UnSuccessfully", "Error", 1, null);
			else
				org.zkoss.zul.Messagebox.show("Updated Successfully", "Success", 1, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
