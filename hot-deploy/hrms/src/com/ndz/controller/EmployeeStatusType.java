package com.ndz.controller;

import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;

import com.ndz.zkoss.util.HrmsInfrastructure;

public class EmployeeStatusType extends GenericForwardComposer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static void onClick$btnCreate(Event event) {

		Component comp = event.getTarget();
		GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
		GenericDelegator delegator = (GenericDelegator) userLogin.getDelegator();
		String statusTypeId = "EMPL_STATUS";
		String description = (String) ((Textbox) comp.getFellow("description")).getValue();
		try {
			Map<String, String> context = UtilMisc.toMap("statusTypeId", statusTypeId, "statusId", delegator.getNextSeqId("StatusItem"), "description",
					description);
			delegator.create("StatusItem", context);
			try {
				Events.postEvent("onClick", comp.getPage().getFellow("employeeStatusType"), null);
				Events.postEvent("onClick", comp.getPage().getFirstRoot().getFellow("emplstatus"), null);
				comp.detach();
				Messagebox.show("Created successfully", "Success", 1, null);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
	}

	public static void onClick$btnSave(Event event) {

		Component comp = event.getTarget();
		GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
		GenericDelegator delegator = (GenericDelegator) userLogin.getDelegator();
		String statusTypeId = "EMPL_STATUS";
		String statusIdLabel = (String) ((Label) comp.getFellow("statusIdLabel")).getValue();
		String description = (String) ((Textbox) comp.getFellow("description")).getValue();
		try {
			Map<String, String> context = UtilMisc.toMap("statusTypeId", statusTypeId, "statusId", statusIdLabel, "description", description);
			GenericValue gv = delegator.makeValue("StatusItem", context);
			gv.store();
			try {
				Events.postEvent("onClick", comp.getPage().getFellow("emplstatus"), null);
				comp.detach();
				Messagebox.show("Updated successfully", "Success", 1, null);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void onClick$createValidStatus(Event event) {

		Component comp = event.getTarget();
		GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
		GenericDelegator delegator = (GenericDelegator) userLogin.getDelegator();

		GenericValue statusFromGV = (GenericValue) ((Listbox) comp.getFellow("statusFrom")).getSelectedItem().getValue();

		String statusId = statusFromGV.getString("statusId");

		GenericValue statusToGV = (GenericValue) ((Listbox) comp.getFellow("statusTo")).getSelectedItem().getValue();

		String statusIdTo = statusToGV.getString("statusId");

		String description = (String) ((Textbox) comp.getFellow("description")).getValue();
		try {
			Map<String, String> context = UtilMisc.toMap("statusId", statusId, "statusIdTo", statusIdTo, "transitionName", description);
			delegator.create("StatusValidChange", context);
			try {
				Messagebox.show("Status created ", "Success", 1, null);
				Events.postEvent("onClick", comp.getPage().getFirstRoot().getFellow("emplstatus"), null);
				comp.detach();

			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} catch (GenericEntityException e) {
			try {
				Messagebox.show("The Following Status has been created Already ", "Error", 1, null);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
	}

	public static void delete(final Event event, final GenericValue gv) throws GenericEntityException, InterruptedException {
		Messagebox.show("Do You Want To Delete this Record?", "Warning", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION, new EventListener() {
			public void onEvent(Event evt) {
				if ("onYes".equals(evt.getName())) {
					try {
						Component comp = event.getTarget();
						GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
						List<GenericValue> emplStatus = delegator.findByAnd("EmplStatus", UtilMisc.toMap("statusId", gv.getString("statusId")));
						if (UtilValidate.isNotEmpty(emplStatus)) {
							Messagebox.show("Selected Employee Status is in use;can't be deleted", "Error", 1, null);
							return;
						}
						delegator.removeByAnd("StatusItem", UtilMisc.toMap("statusId", gv.getString("statusId")));
						Events.postEvent("onClick", comp.getFellow("searchPerCompany"), null);
						Messagebox.show("Deleted successfully", "Success", 1, null);
					} catch (Exception e) {
						try {
							Messagebox.show("Selected Employee Status is in use;can't be deleted", "Error", 1, null);
						} catch (InterruptedException e1) {
							e1.printStackTrace();
						}
					}
				}
			}
		});
	}

}