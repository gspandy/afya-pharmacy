package com.ndz.controller;

import java.util.List;
import java.util.Map;
import java.util.Date;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.service.GenericDispatcher;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.zkoss.util.resource.Labels;
import org.zkoss.zhtml.Messagebox;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.SimpleListModel;
import org.zkoss.zul.Textbox;

import com.ndz.zkoss.DropDownGenericValueAdapter;
import com.ndz.zkoss.util.HrmsInfrastructure;

public class Training extends GenericForwardComposer {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public void doAfterCompose(Component comp)throws Exception{
		super.doAfterCompose(comp);
		trainingClassType(comp);
		status(comp);
	}
	private void trainingClassType(Component root) throws GenericEntityException {
		GenericDelegator delegator= HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
		List<GenericValue> typeList=delegator.findList("TrainingClassType", null, null ,null,null,false);
		typeList.add(0, null);
		SimpleListModel simpleList=new SimpleListModel(typeList);
		Listbox listBoxTrainingClassType=(Listbox) root.getFellow("trainingClassTypeId");
		listBoxTrainingClassType.setModel(simpleList);
		listBoxTrainingClassType.setItemRenderer(new DropDownGenericValueAdapter("description", "trainingClassTypeId"));
	}
	@SuppressWarnings("deprecation")
	private void status(Component root) throws GenericEntityException{
		GenericDelegator delegator=HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
		EntityCondition condition=EntityCondition.makeCondition("statusTypeId", EntityOperator.EQUALS, "EVENT_STATUS");
		List<GenericValue> typeList=delegator.findList("StatusItem",condition , null, null, null, false);
		typeList.add(0,null);
		SimpleListModel simpleList=new SimpleListModel(typeList);
		Listbox listBoxStatusItem=(Listbox) root.getFellow("status");
		listBoxStatusItem.setModel(simpleList);
		listBoxStatusItem.setItemRenderer(new DropDownGenericValueAdapter("statusCode", "statusId"));
	}

	public void onClick$save(Event event)
	throws GenericEntityException {


	GenericValue userLogin = (GenericValue) Executions.getCurrent()
			.getDesktop().getSession().getAttribute("userLogin");

	Component comp = event.getTarget();
	GenericDelegator delegator = (GenericDelegator) userLogin.getDelegator();
	
	String trainingName = ((Textbox) comp
			.getFellow("trainingName")).getValue();
	
	Listitem trainingClassType=((Listbox) comp.getFellow("trainingClassTypeId")).getSelectedItem(); 
	String trainingClassTypeId=(String) trainingClassType.getValue();
	
	Listitem status=((Listbox) comp.getFellow("status")).getSelectedItem();
	String statusId=(String)status.getValue();
	
	String description = ((Textbox) comp
			.getFellow("description")).getValue();
	
	Date startDate = (Date)((Datebox) comp
			.getFellow("startDate")).getValue();
	
	Date endDate = (Date)((Datebox) comp
			.getFellow("endDate")).getValue();
	
	java.sql.Timestamp fromDate = new java.sql.Timestamp(startDate.getTime());
	java.sql.Timestamp thruDate = new java.sql.Timestamp(endDate.getTime());

	Map<String, Object> context = UtilMisc.toMap("userLogin",
			userLogin, "trainingName",trainingName,"trainingClassTypeId",trainingClassTypeId,"currentStatusId","CAL_CONFIRMED","description",description,
			"workEffortTypeId","EVENT","currentStatusId",statusId,"estimatedStartDate",fromDate,"estimatedCompletionDate",thruDate);

	LocalDispatcher dispatcher = HrmsInfrastructure.getDispatcher();//GenericDispatcher.getLocalDispatcher("default", delegator);
	try {
		dispatcher.runSync("addCompanyTraining", context);
	} catch (GenericServiceException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	try {
		Messagebox.show(Labels.getLabel("MessageDescription_SavedSuccessfully"));
	} catch (InterruptedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
} 

}
