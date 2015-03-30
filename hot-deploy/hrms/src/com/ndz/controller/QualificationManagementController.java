package com.ndz.controller;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityComparisonOperator;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionFunction;
import org.ofbiz.service.GenericDispatcher;
import org.ofbiz.service.LocalDispatcher;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.SimpleListModel;
import org.zkoss.zul.Textbox;

import com.ndz.zkoss.GenericValueRenderer;
import com.ndz.zkoss.util.HrmsInfrastructure;

public class QualificationManagementController extends GenericForwardComposer {

	private static final long serialVersionUID = 1L;

	public void doAfterCompose(Component createEmployeeQualification) throws Exception {
		super.doAfterCompose(createEmployeeQualification);
		GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");


		List<GenericValue> QualType = delegator.findList("PartyQualType", null, null, null, null, false);
		SimpleListModel QualTypeList = new SimpleListModel(QualType);
		Listbox listBoxEmployeeQualTypeId = (Listbox) createEmployeeQualification.getFellow("listBoxEmployeeQualTypeId");
		listBoxEmployeeQualTypeId.setModel(QualTypeList);
		listBoxEmployeeQualTypeId.setItemRenderer(new GenericValueRenderer(new String[]{"partyQualTypeId","description"}));
		
		EntityCondition fromCond = EntityConditionFunction.makeCondition(
				"statusTypeId", EntityComparisonOperator.EQUALS, "QUAL_STATUS");
		EntityCondition conditions = EntityCondition.makeCondition(fromCond);
		List<GenericValue> StatusID = delegator.findList("StatusItem",conditions,null, null, null, false);
		SimpleListModel StatusIDList = new SimpleListModel(StatusID);
		Listbox listBoxStatusId = (Listbox) createEmployeeQualification.getFellow("listBoxStatusId");
		listBoxStatusId.setModel(StatusIDList);
		listBoxStatusId.setItemRenderer(new GenericValueRenderer(new String[]{"statusId","description"}));
		
		EntityCondition thruCond = EntityConditionFunction.makeCondition(
				"statusTypeId", EntityComparisonOperator.EQUALS, "PARTYQUAL_VERIFY");
		EntityCondition conditions1 = EntityCondition.makeCondition(thruCond);
		List<GenericValue> StatusVerifyID = delegator.findList("StatusItem",conditions1,null, null, null, false);
		SimpleListModel StatusVerifyIDList = new SimpleListModel(StatusVerifyID);
		Listbox listBoxStatusVerifyId = (Listbox) createEmployeeQualification.getFellow("listBoxVerifyStatusId");
		listBoxStatusVerifyId.setModel(StatusVerifyIDList);
		listBoxStatusVerifyId.setItemRenderer(new GenericValueRenderer(new String[]{"statusId","description"}));
	}

	public void onEvent(Event event) {
		

		try {
			System.out.println("***********OnEventCalled************");
			GenericValue userLogin = (GenericValue) Executions.getCurrent()
					.getDesktop().getSession().getAttribute("userLogin");
			
			Component createEmployeeQualification = event.getTarget();
			GenericDelegator delegator = (GenericDelegator) userLogin.getDelegator();

			String employeeId = userLogin.getString("partyId");
			System.out.println("********Party Id********" + employeeId);

			Listitem EmployeeQualTypeId = ((Listbox) createEmployeeQualification
					.getFellow("listBoxEmployeeQualTypeId")).getSelectedItem();
			String QualTypeId = (String) EmployeeQualTypeId.getValue();
			System.out.println("********QualTypeId********" + QualTypeId);

			String QualificationDesc = ((Textbox) createEmployeeQualification
					.getFellow("textBoxQualificationDesc")).getValue();
			System.out.println("********QualificationDesc********"
					+ QualificationDesc);

			String Title = ((Textbox) createEmployeeQualification
					.getFellow("textBoxTitle")).getValue();
			System.out.println("********Title********" + Title);

			Listitem EmployeeSkillsStatusID = ((Listbox) createEmployeeQualification
					.getFellow("listBoxStatusId"))
					.getSelectedItem();
			String SkillsStatusID = (String) EmployeeSkillsStatusID.getValue();
			System.out.println("********SkillsStatusID********"
					+ SkillsStatusID);

			Listitem listBoxStatusId = ((Listbox) createEmployeeQualification
					.getFellow("listBoxVerifyStatusId")).getSelectedItem();
			String StatusId = (String) listBoxStatusId.getValue();
			System.out.println("********StatusId********" + StatusId);

			Date fromDateInput = (Date) ((Datebox) createEmployeeQualification
					.getFellow("dateBoxQualificationFromDatebox")).getValue();
			Date thruDateInput = (Date) ((Datebox) createEmployeeQualification
					.getFellow("dateBoxQualificationThruDatebox")).getValue();
			if(thruDateInput == null)
				thruDateInput=fromDateInput;

			java.sql.Date fromDate = new java.sql.Date(fromDateInput.getTime());
			java.sql.Date thruDate = new java.sql.Date(thruDateInput.getTime());
			System.out.println("********fromDate********" + fromDate);
			System.out.println("********thruDate********" + thruDate);
						
			Map<String,Object> context = UtilMisc.toMap("userLogin", userLogin, "partyId",
					employeeId, "partyQualTypeId", QualTypeId,
					"qualificationDesc", QualificationDesc, "title", Title,
					"statusId", SkillsStatusID, "verifStatusId", StatusId,
					"fromDate", fromDate, "thruDate", thruDate);
			Map<String,Object> result = null;
			
			LocalDispatcher dispatcher = HrmsInfrastructure.getDispatcher();//GenericDispatcher.getLocalDispatcher("default", delegator);
			result = dispatcher.runSync("createPartyQual", context);
			String partyQualTypeId = (String) result.get("partyQualTypeId");
			Events.postEvent("onClick$searchButton",createEmployeeQualification.getPage().getFellow("searchPanel") , null);
			System.out.println("***********Created with partyQualificationId:"+partyQualTypeId);
			Messagebox.show("Qualification Saved Successfully","Success",1,null);
			createEmployeeQualification.detach();
		} catch (Exception e) {

			e.printStackTrace();
		}

	}
	

}
