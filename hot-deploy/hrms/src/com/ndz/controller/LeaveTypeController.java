package com.ndz.controller;

import java.util.List;
import java.util.Map;

import javolution.util.FastMap;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.service.GenericDispatcher;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.ComponentNotFoundException;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.SimpleListModel;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.ndz.zkoss.util.HrmsInfrastructure;

public class LeaveTypeController extends GenericForwardComposer {

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		if (!"searchPanel".equals(comp.getId()))
			return;
		populateGrid(comp);
	}

	private void populateGrid(Component root) throws GenericEntityException {
		GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
		EntityCondition leaveTypeCondition = EntityCondition.makeCondition(
				"enumTypeId", "LEAVE_TYPE");
		List<GenericValue> leaveTypes = delegator.findList("Enumeration",
				leaveTypeCondition, null, null, null, false);
		SimpleListModel statusIDList = new SimpleListModel(leaveTypes);
		Grid gridLeaveId = (Grid) root.getFellow("dataGrid");
		gridLeaveId.setModel(statusIDList);

	}

	public void onClick$btnSaveLeaveType(Event event)
			throws GenericEntityException {
		try {
			GenericValue userLogin = (GenericValue) Executions.getCurrent()
					.getDesktop().getSession().getAttribute("userLogin");
			Component addNewLeaveType = event.getTarget();
			GenericDelegator delegator = (GenericDelegator) userLogin.getDelegator();

			String LeaveId = ((Textbox) addNewLeaveType
					.getFellow("txtBoxLeaveId")).getValue();
			String LeaveTypeID = ((Label) addNewLeaveType
					.getFellow("lblLeaveTypeID")).getValue();
			String description = ((Textbox) addNewLeaveType
					.getFellow("txtBoxDescription")).getValue();
			String LeaveCode = ((Textbox) addNewLeaveType
					.getFellow("txtBoxLeaveCode")).getValue();
			//String SequenceId = ((Textbox) addNewLeaveType
				//	.getFellow("txtBoxSequenceId")).getValue();

			Map<String, Object> context = UtilMisc.toMap("userLogin",
					userLogin, "enumId", LeaveId, "enumTypeId", LeaveTypeID,
					"description", description, "enumCode", LeaveCode);
			LocalDispatcher dispatcher = HrmsInfrastructure.getDispatcher();//GenericDispatcher.getLocalDispatcher("default", delegator);
			dispatcher.runSync("newLeaveTypeService", context);
			doAfterCompose(addNewLeaveType.getPage().getFellow("searchPanel"));
			Messagebox.show(Labels.getLabel("MessageDescription_SavedSuccessfully"),"Success",1,null);
			addNewLeaveType.detach();
		} catch (Exception e) {
			try {
				Messagebox.show(Labels.getLabel("MessageDescription_Fail"),"Error",1,null);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
	}

	private static final long serialVersionUID = 1L;

	public void delete(Event event, String enumId)
			throws GenericServiceException {
		try {
			Component window = event.getTarget();
			GenericValue userLogin = (GenericValue) Executions.getCurrent()
					.getDesktop().getSession().getAttribute("userLogin");
			GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");

			Map<String, Object> context = UtilMisc.toMap("userLogin",
					userLogin, "enumId", enumId);

			LocalDispatcher dispatcher = HrmsInfrastructure.getDispatcher();//GenericDispatcher.getLocalDispatcher("default", delegator);
			dispatcher.runSync("deleteLeaveTypes", context);
			Messagebox.show(Labels.getLabel("MessageDelete_Successful"),"Success",1,null);
			doAfterCompose(window.getPage().getFellow("searchPanel"));
		} catch (Exception e) {

			try {
				Messagebox.show(Labels.getLabel("MessageDelete_UnSuccessful"),"Error",1,null);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

	}

	public void leaveTypeEdit(Event event, GenericValue gv)
			throws GenericServiceException, SuspendNotAllowedException,
			InterruptedException {
		final Window win = (Window) Executions.createComponents(
				"/zul/GlobalHRSetting/leaveTypeEdit.zul", null, null);
		win.doModal();

		Textbox txtBoxLeaveId = (Textbox) win.getFellow("txtBoxLeaveId");
		txtBoxLeaveId.setValue(gv.getString("enumId"));

		Textbox txtBoxDescription = (Textbox) win
				.getFellow("txtBoxDescription");
		txtBoxDescription.setValue(gv.getString("description"));

		Textbox txtBoxLeaveCode = (Textbox) win.getFellow("txtBoxLeaveCode");
		txtBoxLeaveCode.setValue(gv.getString("enumCode"));

	}

	public void onClick$btnEdit(Event event)
			throws ComponentNotFoundException, Exception {

		GenericValue userLogin = (GenericValue) Executions.getCurrent()
				.getDesktop().getSession().getAttribute("userLogin");

		Component editSkillTypes = event.getTarget();

		GenericDelegator delegator = (GenericDelegator) userLogin.getDelegator();

		String LeaveId = ((Textbox) editSkillTypes
				.getFellow("txtBoxLeaveId")).getValue();
		String LeaveTypeID = ((Label) editSkillTypes
				.getFellow("lblLeaveTypeID")).getValue();
		String description = ((Textbox) editSkillTypes
				.getFellow("txtBoxDescription")).getValue();
		String LeaveCode = ((Textbox) editSkillTypes
				.getFellow("txtBoxLeaveCode")).getValue();
		Map<String, Object> get = FastMap.newInstance();
		GenericValue put = null;
		get.putAll(UtilMisc.toMap("enumId", LeaveId, "enumTypeId", LeaveTypeID, "description", description, "enumCode", LeaveCode));
		try {
			
			put = delegator.findOne("Enumeration", false,"enumId", LeaveId);
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}

		put.putAll(get);
		
		try {
			put.store();
			Messagebox.show(Labels.getLabel("Message_EditSuccessfully"),"Success",1,null);
		} catch (GenericEntityException e) {
			Messagebox.show(Labels.getLabel("Message_EditUnSuccessfully"),"Error",1,null);
		}
		
		doAfterCompose(editSkillTypes.getPage().getFellow("searchPanel"));
		editSkillTypes.detach();

		
	}

}


