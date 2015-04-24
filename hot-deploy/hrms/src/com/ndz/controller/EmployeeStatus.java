package com.ndz.controller;

import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.SimpleListModel;
import org.zkoss.zul.Window;

import com.ndz.zkoss.util.HrmsInfrastructure;

public class EmployeeStatus extends GenericForwardComposer {

	private Listbox status;

	public void doAfterCompose(Component employeeStatus) throws Exception {
		super.doAfterCompose(employeeStatus);
		GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");

		List<GenericValue> emplStatus = delegator.findByAnd("StatusItem",
				UtilMisc.toMap("statusTypeId", "EMPL_STATUS"));
		emplStatus.add(0, null);
		SimpleListModel emplStatusList = new SimpleListModel(emplStatus);
		status.setModel(emplStatusList);
		status.setItemRenderer(new com.ndz.zkoss.DropDownGenericValueAdapter(
				"description", "statusId"));

	}

	public static void onClick$btnEdit(Event event)
			 {

		Component comp = event.getTarget();
		GenericValue userLogin = (GenericValue) Executions.getCurrent()
				.getDesktop().getSession().getAttribute("userLogin");
		GenericDelegator delegator = (GenericDelegator) userLogin.getDelegator();
		String lpartyId = userLogin.getString("partyId");
		String partyId = (String) ((Label) comp.getFellow("partyId"))
				.getValue();
		
		GenericValue statusToGV = (GenericValue) ((Listbox) comp.getFellow("statusTo"))
		.getSelectedItem().getValue();
		
		String statusId=statusToGV.getString("statusIdTo");
		
		System.out.println("statusIdstatusId\n\n\n\n"+statusId);
		
		try {
			Map<String, String> context = UtilMisc.toMap("partyId", partyId,
					"createdBy", lpartyId, "statusId", statusId);
			delegator.create("EmplStatus", context);
			Events.postEvent("onClick",comp.getPage().getFellow("emplStatusSearch").getParent().getFellow("statusTypeBtn"),null);
			
			
			try {
				Window win = (Window) comp.getFellow("employeeStatusUpdate");
				win.detach();
				Messagebox.show("Status created for Employee Id " + partyId,"Success", 1, null);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		 
		} catch (GenericEntityException e) {
			try {
				Messagebox.show(statusId+"" + "Status Already created for Employee Id" + partyId,"Error", 1, null);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			e.printStackTrace();
		}
	}

}