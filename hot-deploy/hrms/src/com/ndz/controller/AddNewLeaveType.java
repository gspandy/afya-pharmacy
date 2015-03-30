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
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Messagebox;

public class AddNewLeaveType extends GenericForwardComposer {
public void onEvent(Event event)  {
		

		try {
			
			GenericValue userLogin = (GenericValue) Executions.getCurrent()
			.getDesktop().getSession().getAttribute("userLogin");
			
			Component addNewResponsibilityTypes = event.getTarget();
			GenericDelegator delegator = (GenericDelegator) userLogin.getDelegator();

			

			
			String ResponsibilityTypeId = ((Textbox) addNewResponsibilityTypes
					.getFellow("txtBoxResponsibilityTypeId")).getValue();
			System.out.println("********SkillTypeId********"
					+ ResponsibilityTypeId);
			String Description = ((Textbox) addNewResponsibilityTypes
					.getFellow("txtBoxDescription")).getValue();
			System.out.println("********Description********"
					+ Description);
						
			Map context = UtilMisc.toMap("userLogin",userLogin,"responsibilityTypeId", ResponsibilityTypeId, "description",
					Description);
			Map result = null;
			
			LocalDispatcher dispatcher = GenericDispatcher.getLocalDispatcher(
					"default", delegator);
			result = dispatcher.runSync("newLeaveTypeService", context);
			Events.postEvent("onClick$searchButton",addNewResponsibilityTypes.getPage().getFellow("searchPanel") , null);
			Messagebox.show("Saved Successfully", "Success", 1, null);
			addNewResponsibilityTypes.detach();
		} catch (Exception e) {
			
			e.printStackTrace();
		}

	}

}
