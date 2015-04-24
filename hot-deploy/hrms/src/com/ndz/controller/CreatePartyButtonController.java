package com.ndz.controller;

import java.util.Map;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.service.GenericDispatcher;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.GenericComposer;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.api.Listitem;

import com.ndz.zkoss.util.HrmsInfrastructure;

public class CreatePartyButtonController extends GenericComposer {

	public void onClick(Event event) {

		Component partyWindow = event.getTarget().getParent();
		System.out
				.println("***********onClick$createParty Called****************");
		GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
		String firstName = ((Textbox) partyWindow.getFellow("firstName"))
				.getValue();
		String middleName = ((Textbox) partyWindow.getFellow("middleName"))
				.getValue();
		String lastName = ((Textbox) partyWindow.getFellow("lastName"))
				.getValue();
		String userName = ((Textbox) partyWindow.getFellow("userName"))
				.getValue();
		String password = ((Textbox) partyWindow.getFellow("password"))
				.getValue();
		String verifyPassword = ((Textbox) partyWindow.getFellow("verifyPassword"))
		.getValue();

		LocalDispatcher dispatcher = HrmsInfrastructure.getDispatcher();//GenericDispatcher.getLocalDispatcher("default", delegator);

		Map context = UtilMisc.toMap("firstName", firstName, "lastName",
				lastName, "middleName", middleName, "userLoginId", userName,
				"currentPassword", password,"currentPasswordVerify",verifyPassword);
		Map result = null;

		try {
			result = dispatcher.runSync("createPersonAndUserLogin", context);
		} catch (GenericServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String partyId = (String) result.get("partyId");

		System.out.println("********* Created Person with Party Id " + partyId);

		Listitem partyDepartment = (((Listbox) partyWindow
				.getFellow("listPartyGroup")).getSelectedItem());

		String departmentName = (String) partyDepartment.getValue();

		Listitem partyRole = (((Listbox) partyWindow.getFellow("listPartyRole"))
				.getSelectedItem());
		Map contextPartyRole = UtilMisc.toMap("partyId", partyId, "roleTypeId", partyRole);
		Map partyRoleResult = null;
		try {
			partyRoleResult = dispatcher.runSync("createPartyRole", contextPartyRole);
			System.out.println("");
		} catch (GenericServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Listitem partySecurityGroup = (((Listbox) partyWindow
				.getFellow("listsecurityGroup")).getSelectedItem());
	}

}
