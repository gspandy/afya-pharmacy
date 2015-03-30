package com.ndz.controller;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.humanresext.util.HumanResUtil;
import org.ofbiz.service.GenericDispatcher;
import org.ofbiz.service.LocalDispatcher;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Bandbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.SimpleListModel;
import org.zkoss.zul.Textbox;
import com.ndz.controller.*;
import com.ndz.zkoss.util.HrmsInfrastructure;

public class SalaryHeadAndRuleManagementController extends GenericForwardComposer {

	SearchController controller = new SearchController();

	public void doAfterCompose(Component SalaryHeadRuleWindow) throws Exception {
		super.doAfterCompose(SalaryHeadRuleWindow);

		GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
		/*
		 * EntityCondition condition = EntityCondition.makeCondition("enumTypeId",
		 * EntityOperator.EQUALS, "CLAIM_TYPE");
		 */

		Set<String> SalaryHeadToDisplay = new HashSet();
		Set<String> PayRollRuleToDisplay = new HashSet();

		SalaryHeadToDisplay.add("hrName");
		SalaryHeadToDisplay.add("salaryHeadId");

		PayRollRuleToDisplay.add("ruleName");
		PayRollRuleToDisplay.add("ruleId");

		// =====================SalaryHeadType==============================
		List<GenericValue> salaryHeadType = delegator.findList("SalaryHead", null, SalaryHeadToDisplay, null, null, false);
		salaryHeadType.add(0, null);
		SimpleListModel salaryHeadTypeList = new SimpleListModel(salaryHeadType);

		Listbox applySalaryHeadId = (Listbox) SalaryHeadRuleWindow.getFellow("applySalaryHeadId");
		applySalaryHeadId.setModel(salaryHeadTypeList);
		applySalaryHeadId.setItemRenderer(new SalaryHeadRenderer());
		// =====================UOM===CURRENCY==============================
		List<GenericValue> PayRollRule = delegator.findList("PayrollRule", null, PayRollRuleToDisplay, null, null, false);
		PayRollRule.add(0, null);
		SimpleListModel PayRollRuleList = new SimpleListModel(PayRollRule);

		Listbox PayRollRuleListBox = (Listbox) SalaryHeadRuleWindow.getFellow("applyPayrollRuleId");
		PayRollRuleListBox.setModel(PayRollRuleList);
		PayRollRuleListBox.setItemRenderer(new PayrollRuleRenderer());
		// =================================================================
	}

	public void onEvent(Event event) {

		String claimId = null;
		Messagebox messageBox = new Messagebox();
		try {
			GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
			Component SalaryHeadRuleWindow = event.getTarget();
			GenericDelegator delegator = (GenericDelegator) userLogin.getDelegator();
			String employeeId = userLogin.getString("partyId");

			Listitem salaryHeadIdInput = ((Listbox) SalaryHeadRuleWindow.getFellow("applySalaryHeadId")).getSelectedItem();
			String salaryHeadId = (String) salaryHeadIdInput.getValue();
			
			List<GenericValue> salaryHeadRuleList = delegator.findByAnd("SalaryHeadRule", UtilMisc.toMap("salaryHeadId",salaryHeadId));
			if(salaryHeadRuleList.size() > 0){
				messageBox.show("Salary Head Already Exit", "Error", 1, null);
				return;
			}
			Listitem ruleIdInput = ((Listbox) SalaryHeadRuleWindow.getFellow("applyPayrollRuleId")).getSelectedItem();
			String ruleId = (String) ruleIdInput.getValue();

			Map context = UtilMisc.toMap("userLogin", userLogin, "salaryHeadId", salaryHeadId, "ruleId", ruleId);
			Map result = null;

			LocalDispatcher dispatcher = HrmsInfrastructure.getDispatcher();//GenericDispatcher.getLocalDispatcher("default", delegator);
			result = dispatcher.runSync("createRuleAndSalaryHeadAssoc", context);

			Events.postEvent("onClick$searchButton", SalaryHeadRuleWindow.getPage().getFellow("SalaryHeadRuleWindow"), null);

			messageBox.show("Created Successfully", "Success", 1, null);
			SalaryHeadRuleWindow.detach();

		} catch (Exception e) {
			try {
				messageBox.show("Salary Head Rule is not Created:Some parameter is missing", "Error", 1, null);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
	}

	public static void deleteSalaryHeadRule(Event event, String salaryHeadId, String ruleId) {
		System.out.println("****************SubmitClaim Event Called*********");
		try {
			Component applySalaryHeadWindow = event.getTarget();
			GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
			GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
			LocalDispatcher dispatcher = HrmsInfrastructure.getDispatcher();//GenericDispatcher.getLocalDispatcher("default", delegator);
			Map submitDelete = null;
			submitDelete = UtilMisc.toMap("userLogin", userLogin, "salaryHeadId", salaryHeadId, "ruleId", ruleId);
			dispatcher.runSync("deleteRuleAndSalaryHeadAssoc", submitDelete);
			Events.postEvent("onClick$searchButton", applySalaryHeadWindow.getPage().getFellow("searchPanel"), null);
			Messagebox.show("Salary Head Rule" + salaryHeadId + " : " + ruleId + "Successfully Deleted", "Success", 1, null);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
