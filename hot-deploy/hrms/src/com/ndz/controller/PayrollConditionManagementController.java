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

public class PayrollConditionManagementController extends GenericForwardComposer {
	private static final long serialVersionUID = 1L;

	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);

		GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");

		EntityCondition condition = EntityCondition.makeCondition("operatorType", EntityOperator.EQUALS, "LOGICAL");

		Set<String> PayrollConditionToDisplay = new HashSet<String>();

		PayrollConditionToDisplay.add("operatorId");
		PayrollConditionToDisplay.add("description");

		// =====================SalaryHeadType==============================
		List<GenericValue> PayrollConditionList = delegator.findList("PayrollOpEnumeration", condition, PayrollConditionToDisplay, null, null, false);
		PayrollConditionList.add(0, null);
		SimpleListModel PayrollConditionListModel = new SimpleListModel(PayrollConditionList);

		Listbox PayrollConditionListBox = (Listbox) comp.getFellow("operator");
		PayrollConditionListBox.setModel(PayrollConditionListModel);
		PayrollConditionListBox.setItemRenderer(new PayrollOperatorRenderer());
		// =====================UOM===CURRENCY==============================
	}

	public void onEvent(Event event) {
		try {
			GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
			Component AddPayrollConditionWindow = event.getTarget();
			GenericDelegator delegator = (GenericDelegator) userLogin.getDelegator();
			String conditionName = ((Textbox) AddPayrollConditionWindow.getFellow("conditionName")).getValue();
			String bandBoxId = (String) ((Listbox) AddPayrollConditionWindow.getFellow("actionList1")).getSelectedItem().getAttribute("bandBoxId");
			String operandOne = ((Bandbox) AddPayrollConditionWindow.getFellow(bandBoxId)).getValue();
			String bandBoxId2 = (String) ((Listbox) AddPayrollConditionWindow.getFellow("actionList2")).getSelectedItem().getAttribute("bandBoxId2");
			String operandTwo = ((Bandbox) AddPayrollConditionWindow.getFellow(bandBoxId2)).getValue();
			Listitem operatorListItem = ((Listbox) AddPayrollConditionWindow.getFellow("operator")).getSelectedItem();
			String operator = (String) operatorListItem.getValue();
			Map context = UtilMisc.toMap("userLogin", userLogin, "conditionName", conditionName, "operandOne", operandOne, "operandTwo", operandTwo,
					"operatorId", operator,"actionListOne",bandBoxId,"actionListTwo",bandBoxId2);
			Map result = null;
			LocalDispatcher dispatcher = HrmsInfrastructure.getDispatcher();//GenericDispatcher.getLocalDispatcher("default", delegator);
			result = dispatcher.runSync("createPayrollCondition", context);
			Events.postEvent("onClick$searchButton", AddPayrollConditionWindow.getPage().getFellow("searchPanel"), null);
			Messagebox.show("Created Successfully", "Success", 1, null);
			AddPayrollConditionWindow.detach();
		} catch (Exception e) {
			try {
				Messagebox.show("Created UnSuccessfully", "Error", 1, null);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
	}

	public static void editPayrollConditionWindow(Event event, GenericValue gv) throws SuspendNotAllowedException, InterruptedException, GenericEntityException {

		String conditionId = gv.getString("conditionId");
		String conditionName = gv.getString("conditionName");
		String operandOne = gv.getString("operandOne");
		String operandTwo = gv.getString("operandTwo");
		String operatorId = gv.getString("operatorId");
		String actionListOne = gv.getString("actionListOne");
		String actionListTwo = gv.getString("actionListTwo");

		Window win = (Window) Executions.createComponents("/zul/payRollManagement/updatePayrollCondition.zul", null, null);
		win.setTitle("Update Payroll Condition");

		/****** Setting List Box Items and Selected List Item ******/

		GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");

		EntityCondition condition = EntityCondition.makeCondition("operatorType", EntityOperator.EQUALS, "LOGICAL");

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
			}
		}
		
		((Label)win.getFellow("conditionName")).setValue(conditionName);
		((Textbox)win.getFellow("conditionId")).setValue(conditionId);
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

	public static void DeletePayrollCondition(Event event, String conditionId) {
		System.out.println("****************SubmitClaim Event Called*********");
		try {
			Component applySalaryHeadWindow = event.getTarget();
			GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
			GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
			LocalDispatcher dispatcher = HrmsInfrastructure.getDispatcher();//GenericDispatcher.getLocalDispatcher("default", delegator);
			Map submitDelete = null;
			submitDelete = UtilMisc.toMap("userLogin", userLogin, "conditionId", conditionId);
			dispatcher.runSync("deletePayrollCondition", submitDelete);
			Events.postEvent("onClick$searchButton", applySalaryHeadWindow.getPage().getFellow("searchPanel"), null);
			Messagebox.show("Payroll Condition Successfully Deleted", "Success", 1, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void EditPayrollCondition(Event event) {
		try {
			Component salaryHeadWindow = event.getTarget();
			GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
			GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
			LocalDispatcher dispatcher = HrmsInfrastructure.getDispatcher();//GenericDispatcher.getLocalDispatcher("default", delegator);
			String conditionId = (String) ((Textbox) salaryHeadWindow.getFellow("conditionId")).getValue();
			String conditionName = ((Label) salaryHeadWindow.getFellow("conditionName")).getValue();
			String bandBoxId = (String) ((Listbox) salaryHeadWindow.getFellow("actionList1")).getSelectedItem().getAttribute("bandBoxId");
			String operandOne = ((Bandbox) salaryHeadWindow.getFellow(bandBoxId)).getValue();
			String bandBoxId2 = (String) ((Listbox) salaryHeadWindow.getFellow("actionList2")).getSelectedItem().getAttribute("bandBoxId2");
			String operandTwo = ((Bandbox) salaryHeadWindow.getFellow(bandBoxId2)).getValue();
			Listitem operatorListItem = ((Listbox) salaryHeadWindow.getFellow("operator")).getSelectedItem();
			String operator = (String) operatorListItem.getValue();
			Map context = UtilMisc.toMap("userLogin", userLogin,"conditionId", conditionId, "conditionName", conditionName, "operandOne", operandOne, "operandTwo", operandTwo,
					"operatorId", operator,"actionListOne",bandBoxId,"actionListTwo",bandBoxId2);

			Map<String, Object> result = dispatcher.runSync("updatePayrollCondition", context);

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
