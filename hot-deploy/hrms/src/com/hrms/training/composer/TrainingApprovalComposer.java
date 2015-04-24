package com.hrms.training.composer;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.service.GenericDispatcher;
import org.ofbiz.service.LocalDispatcher;
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
import org.zkoss.zul.Panel;
import org.zkoss.zul.event.PagingEvent;

import com.hrms.composer.HrmsComposer;
import com.ndz.zkoss.HrmsUtil;
import com.ndz.zkoss.util.HrmsInfrastructure;

public class TrainingApprovalComposer extends HrmsComposer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1140190248251408524L;

	private BindingListModel model;

	private Panel details;

	private String status;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public BindingListModel getModel() {
		return model;
	}

	public void setModel(BindingListModel model) {
		this.model = model;
	}

	private Listbox approvalListbox;

	private GenericValue selectedRequest;

	public GenericValue getSelectedRequest() {
		return selectedRequest;
	}

	public void setSelectedRequest(GenericValue selectedRequest) {
		this.selectedRequest = selectedRequest;
	}

	@Override
	protected void executeAfterCompose(Component comp) throws Exception {
		List<GenericValue> requestsForApproval = getTeamRequests();
		model = new BindingListModelList(requestsForApproval, false);
		binder.loadAttribute(approvalListbox, "model");
	}

	private List<GenericValue> getTeamRequests() throws GenericEntityException {
		GenericValue userLogin = (GenericValue) Executions.getCurrent().getSession().getAttribute("userLogin");
		String managerPartyId = userLogin.getString("partyId");
		EntityFindOptions findOptions = new EntityFindOptions();
		findOptions.setDistinct(true);
		List<GenericValue> requestsForApproval = delegator.findList("TrainingRequestView", EntityCondition.makeCondition(UtilMisc.toMap(
				"managerId", managerPartyId)), null, null, null, false);
		return requestsForApproval;
	}

	public void onPaging$approvalListPaging(ForwardEvent event) throws Exception {
		int activePage = ((PagingEvent) event.getOrigin()).getActivePage();
		List<GenericValue> requestsForApproval = getTeamRequests();
		model = new BindingListModelList(requestsForApproval, false);
		binder.loadAttribute(approvalListbox, "model");
	}

	public void onClick$statusUpdateButton(Event event) throws Exception {
		
		if(selectedRequest==null){
			Messagebox.show("Please select a Training", "Error", 1, null);
			return;
		}
		if(org.apache.commons.lang.StringUtils.isEmpty(status)){
			return;
		}
		String trainingId = selectedRequest.getString("trainingId");
		String partyId = selectedRequest.getString("partyId");
		String managerId = selectedRequest.getString("managerId");

		GenericValue trainingReqGV = null;
		trainingReqGV = delegator.makeValidValue("TrainingRequest", UtilMisc.toMap("trainingId", trainingId, "partyId", partyId,
				"statusId", status,"managerId",managerId));
		LocalDispatcher dispatcher = HrmsInfrastructure.getDispatcher();//GenericDispatcher.getLocalDispatcher("default", delegator);
		GenericValue userLogin = (GenericValue) Executions.getCurrent().getSession().getAttribute("userLogin");
		
		Map<String,Object> map = new HashMap<String,Object>();
		map.putAll(trainingReqGV.getAllFields());
		map.put("partyId", partyId);
		if(status != null){
		String results = HrmsUtil.checkTrainingSeatAvailability(map);
		}
		//if(ServiceUtil.isError(results)){
			//Messagebox.show("Training is fully booked.");
			//return;
		//}
		
		try{
			delegator.store(trainingReqGV);
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		List<GenericValue> requestsForApproval = getTeamRequests();
		model = new BindingListModelList(requestsForApproval, false);
		binder.loadAttribute(approvalListbox, "model");
		status = null;
	}

	public ListitemRenderer getListitemRenderer() throws Exception {
		return new TrainingListRenderer(this, "TRNG_MGR_APPROVED");
	}

	public void createRow(Listitem trainingListitem,GenericValue gv) {
		new Listcell(gv.getString("trainingId")+"-"+gv.getString("trainingName")).setParent(trainingListitem);
		new Listcell(gv.getString("partyId")).setParent(trainingListitem);
		SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
		new Listcell(formatter.format(gv.getTimestamp("estimatedStartDate"))).setParent(trainingListitem);
		new Listcell(formatter.format(gv.getTimestamp("estimatedCompletionDate"))).setParent(trainingListitem);
	}
}
