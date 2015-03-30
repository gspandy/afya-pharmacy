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

public class PayGradeManagementController extends GenericForwardComposer {

	SearchController controller = new SearchController();

	public void doAfterCompose(Component SalaryHeadRuleWindow) throws Exception {
		super.doAfterCompose(SalaryHeadRuleWindow);

		GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");

		Set<String> nextPayGradeIdToDisplay = new HashSet();

		nextPayGradeIdToDisplay.add("payGradeId");
		nextPayGradeIdToDisplay.add("payGradeName");

		// ======================next_Pay_Grade_Id====================
		List<GenericValue> nextPayGradeIdList = delegator.findList("PayGrade",
				null, nextPayGradeIdToDisplay, null, null, false);
		nextPayGradeIdList.add(0, null);
		SimpleListModel nextPayGradeList = new SimpleListModel(
				nextPayGradeIdList);

		Listbox nextPayGradeIdIdBox = (Listbox) SalaryHeadRuleWindow
				.getFellow("nextPayGradeId");
		nextPayGradeIdIdBox.setModel(nextPayGradeList);
		nextPayGradeIdIdBox.setItemRenderer(new NextPayGradeIdRenderer());
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

			String employeeId = userLogin.getString("partyId");
			System.out.println("********Party Id********" + employeeId);

			String payGradeName = ((Textbox) AddPayrollRuleWindow
					.getFellow("payGradeName")).getValue();
			Listitem nextPayGradeIdItem = ((Listbox) AddPayrollRuleWindow
					.getFellow("nextPayGradeId")).getSelectedItem();
			String nextPayGradeId = ((nextPayGradeIdItem==null)? null:(String) nextPayGradeIdItem.getValue());

			String comments = ((Textbox) AddPayrollRuleWindow
					.getFellow("comments")).getValue();

			Map context = UtilMisc.toMap("userLogin", userLogin,
					"payGradeName", payGradeName, "nextPayGradeId",
					nextPayGradeId, "comments", comments);
			Map result = null;

			LocalDispatcher dispatcher = HrmsInfrastructure.getDispatcher();//GenericDispatcher.getLocalDispatcher("default", delegator);
			result = dispatcher.runSync("createPayGrade", context);

			String payGradeId = (String) result.get("payGradeId");

			Events.postEvent("onClick$searchButton", AddPayrollRuleWindow
					.getPage().getFellow("searchPanel"), null);

			messageBox.show("Pay Grade Successfully Created with Pay Grade Id:"
					+ payGradeId,"Success",1,null);
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

	public static void showPayGradeWindow(Event event, GenericValue gv)
			throws SuspendNotAllowedException, InterruptedException {

		Component approveClaimWindow = event.getTarget();
		GenericValue userLogin = (GenericValue) Executions.getCurrent()
				.getDesktop().getSession().getAttribute("userLogin");
		GenericValue person = (GenericValue) Executions.getCurrent()
				.getDesktop().getSession().getAttribute("person");

		String payGradeId = gv.getString("payGradeId");
		String payGradeName = gv.getString("payGradeName");
		String nextPayGradeId = gv.getString("nextPayGradeId");
		String comments = gv.getString("comments");

		Window win = (Window) Executions.createComponents(
				"/zul/payRollManagement/updatePayGrade.zul", null, null);
		win.setTitle("Update Pay Grade");
		win.doModal();

		Label payGradeIdLabel = (Label) win.getFellow("payGradeId");
		payGradeIdLabel.setValue(payGradeId);

		Textbox payGradeNameTextBox = (Textbox) win.getFellow("payGradeName");
		payGradeNameTextBox.setValue(payGradeName);

		Textbox nextPayGradeIdTextBox = (Textbox) win
				.getFellow("nextPayGradeId");
		nextPayGradeIdTextBox.setValue(nextPayGradeId);

		Textbox commentsTextBox = (Textbox) win.getFellow("comments");
		commentsTextBox.setValue(comments);

		approveClaimWindow.detach();
	}

	public static void DeletePayGrade(Event event, String payGradeId) {
		System.out.println("****************SubmitClaim Event Called*********");
		try {
			Component applySalaryHeadWindow = event.getTarget();
			GenericValue userLogin = (GenericValue) Executions.getCurrent()
					.getDesktop().getSession().getAttribute("userLogin");
			GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
			LocalDispatcher dispatcher = HrmsInfrastructure.getDispatcher();//GenericDispatcher.getLocalDispatcher("default", delegator);
			Map submitDelete = null;

			submitDelete = UtilMisc.toMap("userLogin", userLogin,
					"payGradeId", payGradeId);
			dispatcher.runSync("deletePayGrade", submitDelete);
			Events.postEvent("onClick$searchButton", applySalaryHeadWindow
					.getPage().getFellow("searchPanel"), null);
			Messagebox.show("Pay Grade: " + payGradeId
					+ " :Successfully Deleted","Success",1,null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void EditPayGrade(Event event) {

		System.out.println("****************SubmitClaim Event Called*********");
		try {
			Component applyPayrollConditionWindow = event.getTarget();
			GenericValue userLogin = (GenericValue) Executions.getCurrent()
					.getDesktop().getSession().getAttribute("userLogin");
			GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
			LocalDispatcher dispatcher = HrmsInfrastructure.getDispatcher();//GenericDispatcher.getLocalDispatcher("default", delegator);
			Map submitUpdate = null;

			String payGradeId = (String) ((Label) applyPayrollConditionWindow
					.getFellow("payGradeId")).getValue();

			String payGradeName = (String) ((Textbox) applyPayrollConditionWindow
					.getFellow("payGradeName")).getValue();

			String nextPayGradeId = (String) ((Textbox) applyPayrollConditionWindow
					.getFellow("nextPayGradeId")).getValue();

			String comments = (String) ((Textbox) applyPayrollConditionWindow
					.getFellow("comments")).getValue();

			submitUpdate = UtilMisc.toMap("userLogin", userLogin, "payGradeId",
					payGradeId, "payGradeName", payGradeName, "nextPayGradeId",
					nextPayGradeId, "comments", comments);
			dispatcher.runSync("updatePayGrade", submitUpdate);
			Events.postEvent("onClick$searchButton",
					applyPayrollConditionWindow.getPage().getFellow(
							"searchPanel"), null);
			Messagebox.show("Pay Grade: " + payGradeId
					+ " :Successfully Updated","Success",1,null);

			applyPayrollConditionWindow.detach();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
