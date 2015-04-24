package com.ndz.controller;

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
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;

import com.ndz.zkoss.util.HrmsInfrastructure;

public class PayrollPreferencesController extends GenericForwardComposer {

	public void onEvent(Event event) {

		String claimId = null;
		Messagebox messageBox = new Messagebox();
		try {
			GenericValue userLogin = (GenericValue) Executions.getCurrent()
					.getDesktop().getSession().getAttribute("userLogin");
			Component employeePreferences = event.getTarget();
			GenericDelegator delegator = (GenericDelegator) userLogin.getDelegator();

			String employeeId = userLogin.getString("partyId");

			String partyId = ((Textbox) employeePreferences
					.getFellow("partyId1")).getValue();

			String bankName = ((Textbox) employeePreferences
					.getFellow("bankName1")).getValue();
			String bankAccountNumber = ((Textbox) employeePreferences
					.getFellow("bankAccountNumber1")).getValue();
			String panNumber = ((Textbox) employeePreferences
					.getFellow("panNumber1")).getValue();
			String pfAccountNumber = ((Textbox) employeePreferences
					.getFellow("pfAccountNumber1")).getValue();

			Map context = UtilMisc.toMap("userLogin", userLogin, "partyId",
					partyId, "bankName", bankName, "bankAccountNumber",
					bankAccountNumber, "panNumber", panNumber,
					"pfAccountNumber", pfAccountNumber);
			Map result = null;

			LocalDispatcher dispatcher = HrmsInfrastructure.getDispatcher();//GenericDispatcher.getLocalDispatcher("default", delegator);
			result = dispatcher.runSync("updatePreferences", context);

			messageBox.show("Preferences Updated Successfully ","Success",1,null);
			//employeePreferences.detach();
		} catch (Exception e) {
			try {
				messageBox
						.show("Preferences is not updated:Some parameter is missing","Error",1,null);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
	}
}