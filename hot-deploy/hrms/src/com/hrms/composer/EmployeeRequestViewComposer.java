package com.hrms.composer;

import java.util.ArrayList;
import java.util.List;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.databind.AnnotateDataBinder;
import org.zkoss.zkplus.databind.BindingListModel;
import org.zkoss.zkplus.databind.BindingListModelList;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.ndz.zkoss.util.HrmsInfrastructure;

public class EmployeeRequestViewComposer extends GenericForwardComposer {
	
	private static final long serialVersionUID = 1L;
	
	AnnotateDataBinder binder = null;
	Groupbox employeeRequestGroupBox = null;
	Listbox requestTypeViewListbox = null;
	BindingListModel model;
	List assignedRequestTypeList = new ArrayList();
	
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		binder = new AnnotateDataBinder(comp);
		employeeRequestGroupBox = (Groupbox) comp;

		GenericValue userLogin = (GenericValue) Executions.getCurrent()
				.getDesktop().getSession().getAttribute("userLogin");

		GenericDelegator delegator = (GenericDelegator) userLogin.getDelegator();
		String partyId = (String)userLogin.getString("partyId");
		this.assignedRequestTypeList = getAssignedRequsetType(delegator,partyId);

		binder.loadAttribute(requestTypeViewListbox, "model");
	}

	public BindingListModel getModel() {
		return new BindingListModelList(this.assignedRequestTypeList, false);
	}
	
	protected List<GenericValue> getAssignedRequsetType(
			GenericDelegator delegator,String partyId)
			throws GenericEntityException {

		List<GenericValue> assignedRequestType = null;
		try {
			assignedRequestType = delegator.findByAnd("PartyRequestView", UtilMisc.toMap("asignedPartyId", partyId));		
					} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return assignedRequestType;

	}
	
	public ListitemRenderer getRenderer() {
		return new ListitemRenderer() {
			@SuppressWarnings("unchecked")
			public void render(Listitem listItem, Object data) throws Exception {
				listItem.setTooltiptext("DoubleClick To View/Respond");
				GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
				final GenericValue requestTypeGv = (GenericValue) data;
				new Listcell(requestTypeGv.getString("requestId")).setParent(listItem);	
				new Listcell(requestTypeGv.getString("description")).setParent(listItem);
				GenericValue priorityGv = delegator.findByPrimaryKey("PriorityType", UtilMisc.toMap("priorityTypeId", requestTypeGv.getString("priority")));
				String priorityType = null;
				if(priorityGv!=null)
					priorityType = (String)priorityGv.getString("description");
				List requestResponseList = delegator.findByAnd("PartyReqResponse", UtilMisc.toMap("requestId", requestTypeGv.getString("requestId")));
				List listOrdered = org.ofbiz.entity.util.EntityUtil.orderBy(requestResponseList, UtilMisc.toList("lastUpdatedStamp DESC"));
				GenericValue requestResponseGv = EntityUtil.getFirst(listOrdered);
				if(UtilValidate.isNotEmpty(requestResponseGv)){
				   String statusId = requestResponseGv.getString("statusTypeId");
				GenericValue statusGv = delegator.findByPrimaryKey("StatusItem", UtilMisc.toMap("statusId", statusId));
				if(statusGv!=null){
				String status = statusGv.getString("statusCode");
				new Listcell(status).setParent(listItem);
				}
			   }
				
				if(priorityType!=null)
					
				new Listcell(priorityType).setParent(listItem);
				else
					new Listcell(" ").setParent(listItem);
				new Listcell(requestTypeGv.getString("requestMsg")).setParent(listItem);
				new Listcell(requestTypeGv.getString("firstName")+" " +requestTypeGv.getString("lastName")).setParent(listItem);
				listItem.addEventListener("onDoubleClick", new EventListener() {
					public void onEvent(Event event) throws Exception {
						Clients.evalJavaScript("window.open('/hrms/control/viewRequest?requestId="
			+ requestTypeGv.getString("requestId") + "')");
					}
				});
				listItem.setValue(data);
			}
		};
		
	}



}
