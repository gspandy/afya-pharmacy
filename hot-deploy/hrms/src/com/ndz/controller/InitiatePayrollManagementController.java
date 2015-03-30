package com.ndz.controller;

import java.sql.Date;
import java.util.Map;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.GenericDispatcher;
import org.ofbiz.service.LocalDispatcher;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Bandbox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;

import com.ndz.zkoss.util.HrmsInfrastructure;

public class InitiatePayrollManagementController extends GenericForwardComposer {

	SearchController controller = new SearchController();

	public void onEvent(Event event) {

		Messagebox messageBox = new Messagebox();
		try {

			System.out.println("***********OnEventCalled************");
			GenericValue userLogin = (GenericValue) Executions.getCurrent()
					.getDesktop().getSession().getAttribute("userLogin");
			System.out.println("*******userLogin Object*********" + userLogin);
			Component AddPayrollRuleWindow = event.getTarget();
			GenericDelegator delegator = (GenericDelegator) userLogin.getDelegator();

			String partyId = ((Bandbox) AddPayrollRuleWindow
					.getFellow("searchPanel")).getValue();

			java.util.Date periodFromInput = (java.util.Date) ((Datebox) AddPayrollRuleWindow
					.getFellow("periodFrom")).getValue();

			Date periodFrom = null;
			if (periodFromInput != null) {
				periodFrom = new Date(periodFromInput.getTime());
			}
			java.util.Date periodToInput = (java.util.Date) ((Datebox) AddPayrollRuleWindow
					.getFellow("periodTo")).getValue();

			Date periodTo = null;
			if (periodToInput != null) {
				periodTo = new Date(periodToInput.getTime());
			}

			Listitem linkToLMSInput = (Listitem) ((Listbox) AddPayrollRuleWindow
					.getFellow("linkToLMS")).getSelectedItem();
			String linkToLMS = (String) linkToLMSInput.getValue();

			Map context = UtilMisc.toMap("userLogin", userLogin, "partyId",
					partyId, "periodFrom", periodFrom, "periodTo", periodTo,
					"linkToLMS", linkToLMS);

			LocalDispatcher dispatcher = HrmsInfrastructure.getDispatcher();//GenericDispatcher.getLocalDispatcher("default", delegator);
			Map result = dispatcher.runSync("runPayrollService", context);

			String err = "";
			err = (String) result.get("responseMessage");
			if (err != null && err.equals("error"))
				messageBox.show((String) result.get("errorMessage"),"Error",1,null);
			else messageBox.show("Pay Slip Successfully Generated","Success",1,null);


		} catch (Exception e) {
			try {
				// messageBox.show("Pay Slip is not Created:Following Error has occured"+
				// e);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
	}
}
