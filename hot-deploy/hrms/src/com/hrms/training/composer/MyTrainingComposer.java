package com.hrms.training.composer;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.humanresext.util.HumanResUtil;
import org.ofbiz.service.ServiceUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zkplus.databind.BindingListModel;
import org.zkoss.zkplus.databind.BindingListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.event.PagingEvent;

import com.hrms.composer.HrmsComposer;
import com.ndz.zkoss.HrmsUtil;

public class MyTrainingComposer extends HrmsComposer {

	private long totalSize;
	private Listbox myTrainingsListBox;
	private GenericValue selectedTraining;
	private String status;
	private String employeeId;

	public String getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(String employeeId) {
		this.employeeId = employeeId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public GenericValue getSelectedTraining() {
		return selectedTraining;
	}

	public void setSelectedTraining(GenericValue selectedTraining) {
		this.selectedTraining = selectedTraining;
	}

	public long getTotalSize() {
		return totalSize;
	}

	public void setTotalSize(long totalSize) {
		this.totalSize = totalSize;
	}

	private BindingListModel model;

	public BindingListModel getModel() {
		return model;
	}

	public void setModel(BindingListModel myTrainings) {
		this.model = myTrainings;
	}

	@Override
	protected void executeAfterCompose(Component comp) throws Exception {
		GenericValue userLogin = (GenericValue) Executions.getCurrent().getSession().getAttribute("userLogin");
		String partyId = userLogin.getString("partyId");
		if(UtilValidate.isNotEmpty(employeeId))
        	partyId=employeeId;
        totalSize = delegator.findCountByCondition("TrainingRequestView", EntityCondition.makeCondition("partyId", partyId), null, null);
		
		List<GenericValue> iter = getMyTrainings();
		this.model = new BindingListModelList(iter, false);
		if (myTrainingsListBox != null) {
			binder.loadAttribute(myTrainingsListBox, "model");
		}
	}

	public void onClick$searchButton(Event event) throws Exception{
		GenericValue userLogin = (GenericValue) Executions.getCurrent().getSession().getAttribute("userLogin");
		String partyId = userLogin.getString("partyId");
		if(UtilValidate.isNotEmpty(employeeId))
        	partyId=employeeId;
        totalSize = delegator.findCountByCondition("TrainingRequestView", EntityCondition.makeCondition("partyId", partyId), null, null);
		
		List<GenericValue> iter = getMyTrainings();
		this.model = new BindingListModelList(iter, false);
		if (myTrainingsListBox != null) {
			binder.loadAttribute(myTrainingsListBox, "model");
		}
	}
	private List<GenericValue> getMyTrainings() throws GenericEntityException {
		GenericValue userLogin = (GenericValue) Executions.getCurrent().getSession().getAttribute("userLogin");
		String partyId = userLogin.getString("partyId");
		if(UtilValidate.isNotEmpty(employeeId))
        	partyId=employeeId;
        List<GenericValue> iter = delegator.findList("TrainingRequestView", EntityCondition.makeCondition("partyId", partyId), null, null,
				null, false);
		return iter;
	}

	public void onPaging$approvalListPaging(ForwardEvent event) throws Exception {
		int activePage = ((PagingEvent) event.getOrigin()).getActivePage();
		List<GenericValue> iter = getMyTrainings();
		model = new BindingListModelList(iter, false);
		binder.loadAttribute(myTrainingsListBox, "model");
	}

	public void onClick$updateStatus(Event event) throws Exception {

		if (status == null && selectedTraining == null) {
			Messagebox.show("Please select a Training", "Error", 1, null);
			return;
		} else if(selectedTraining != null && "TRNG_CONFIRMED".equals(selectedTraining.getString("statusId")) && !"TRNG_CANCELLED".equals(status)){
			Messagebox.show("Selected Training Id is already confirmed", "Error", 1, null);
			return;
		}

		GenericValue userLogin = (GenericValue) Executions.getCurrent().getSession().getAttribute("userLogin");
		String partyId = userLogin.getString("partyId");
		if(UtilValidate.isNotEmpty(employeeId))
        	partyId=employeeId;
        
		GenericValue mgrGV = HumanResUtil.getReportingMangerForParty(partyId, delegator);
		String mgrPartyId = null;
		if (mgrGV != null)
			mgrPartyId = mgrGV.getString("partyId");

		GenericValue trainingRequest = delegator.makeValidValue("TrainingRequest", UtilMisc.toMap("partyId", partyId, "trainingId",
				selectedTraining.get("trainingId"), "managerId", mgrPartyId, "statusId", status));
		Map<String,Object> map = new HashMap<String,Object>();
		map.putAll(trainingRequest.getAllFields());
		map.put("partyId", partyId);
		if(status != null){
		 //Map results = dispatcher.runSync("checkTrainingSeatAvailability", map);
		String results = HrmsUtil.checkTrainingSeatAvailability(map);
		if("error".equals(results)){
			Messagebox.show("Training is fully booked. So moving into Waiting list.", "Info", 1, null);
			trainingRequest.setString("statusId", "TRNG_WAITING");
		}
		}else{
			return;
		}
		try {
			delegator.store(trainingRequest);
		} catch (GenericEntityException ge) {
			Messagebox.show("Already applied for the training", "Error", 1, null);
		}
		List<GenericValue> iter = getMyTrainings();
		model = new BindingListModelList(iter, false);
		binder.loadAttribute(myTrainingsListBox, "model");
		status = null;
	}

	public ListitemRenderer getMyTrainingRenderer() {
			return new TrainingListRenderer(this,"TRNG_REQUEST");
	}
	
	public void createRow(Listitem trainingListitem,GenericValue gv) {
		new Listcell(gv.getString("trainingId")).setParent(trainingListitem);
		new Listcell(gv.getString("trainingName")).setParent(trainingListitem);
		SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
		new Listcell(formatter.format(gv.getTimestamp("estimatedStartDate"))).setParent(trainingListitem);
		new Listcell(formatter.format(gv.getTimestamp("estimatedCompletionDate"))).setParent(trainingListitem);
	}
}
