package com.hrms.composer;

import java.util.ArrayList;
import java.util.List;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.databind.AnnotateDataBinder;
import org.zkoss.zkplus.databind.BindingListModel;
import org.zkoss.zkplus.databind.BindingListModelList;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Label;
import org.zkoss.zul.Row;
import org.zkoss.zul.RowRenderer;
import org.zkoss.zul.Window;

import com.ndz.zkoss.util.HrmsInfrastructure;

public class EmployeeRequestProcessComposer extends GenericForwardComposer {

	private static final long serialVersionUID = 1L;
	Component requestViewComponent = null;
	Combobox requestStatusCombobox = null;
	Grid requestHistoryGrid = null;
	BindingListModel model;
	BindingListModel historyModel;
	
	List requestStatusList = new ArrayList();
	List requestHistoryList = new ArrayList();
	AnnotateDataBinder binder = null;
		
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		binder = new AnnotateDataBinder(comp);
		requestViewComponent = (Component) comp;
		String requestId = (String)Executions.getCurrent().getParameter("requestId");
		GenericValue userLogin = (GenericValue) Executions.getCurrent()
				.getDesktop().getSession().getAttribute("userLogin");
		GenericDelegator delegator = (GenericDelegator) (GenericDelegator) userLogin.getDelegator();
		this.requestStatusList = getRequestStatus(delegator,requestId);
		this.requestHistoryList = getRequestHistory(delegator,requestId);
		binder.loadAttribute(requestStatusCombobox, "model");
		binder.loadAttribute(requestHistoryGrid, "model");
	}

	public BindingListModel getModel() {
		return new BindingListModelList(this.requestStatusList, false);
	}
	
	public BindingListModel getHistoryModel() {
		return new BindingListModelList(this.requestHistoryList,false);
	}

	
	public List<GenericValue> getRequestStatus(
			GenericDelegator delegator,String requestId)
			throws GenericEntityException {

		List requestResponseList = delegator.findByAnd("PartyReqResponse", UtilMisc.toMap("requestId", requestId));
		List listOrdered = org.ofbiz.entity.util.EntityUtil.orderBy(requestResponseList, UtilMisc.toList("lastUpdatedStamp DESC"));
		GenericValue requestResponseGv = EntityUtil.getFirst(listOrdered);
		String statusId = requestResponseGv.getString("statusTypeId");
		List<GenericValue> requestStatusTypeList = null;
		try {
			requestStatusTypeList = delegator.findByAnd("StatusValidChangeToDetail", UtilMisc.toMap("statusId", statusId));		
					} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return requestStatusTypeList;

	}
	
	protected List<GenericValue>getRequestHistory(GenericDelegator delegator,String requestId){
		List<GenericValue> requestHistoryDetailList = null;
		try {
			requestHistoryDetailList = delegator.findByAnd("PartyReqResponse", UtilMisc.toMap("requestId", requestId));
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return requestHistoryDetailList;
		
	}
	
	public RowRenderer getRenderer() {
		return new RowRenderer() {
			public void render(Row row, Object data) throws Exception {
				GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
				final GenericValue requestHistoryGv = (GenericValue) data;
				String updatedBy = (String)requestHistoryGv.getString("updatedBy");
				String statusTypeId =null;
				statusTypeId = (String)requestHistoryGv.getString("statusTypeId");
				if(statusTypeId.equals("REQUEST_CLOSED"))
					requestViewComponent.getFellow("changestatusbtn").setVisible(false);
				//if(statusTypeId.equals("REQUEST_RESOLVED"))
					//statusTypeId="Resolved";
				List statusList = delegator.findByAnd("StatusItem",org.ofbiz.base.util.UtilMisc.toMap("statusId",statusTypeId));
				GenericValue statusGv = EntityUtil.getFirst(statusList);
				String status =statusGv == null ? null : statusGv.getString("statusCode");
				List responseList = delegator.findByAnd("PartyReqResponse",org.ofbiz.base.util.UtilMisc.toMap("responseId",requestHistoryGv.getString("responseId")));
				GenericValue responseGv = EntityUtil.getFirst(responseList);
				String responseCreatedTime = (String)(responseGv.getTimestamp("createdStamp").toString());
				new Label(responseCreatedTime).setParent(row);
				String modifiedBy = com.ndz.zkoss.HrmsUtil.getFullName(delegator,updatedBy);
				if(modifiedBy!=null){
				new Label(modifiedBy).setParent(row);
				}else{
					new Label(" ").setParent(row);
				}
				new Label(status).setParent(row);
				row.setValue(data);
				((Label)requestViewComponent.getFellow("statusLabel")).setValue(status);
				((Label)requestViewComponent.getFellow("updatedDateLabel")).setValue(responseCreatedTime);
				((Label)requestViewComponent.getFellow("requestMessageLabel")).setValue(((GenericValue)delegator.findByPrimaryKey("PartyRequest", UtilMisc.toMap("requestId", requestHistoryGv.getString("requestId")))).getString("requestMsg"));
				((Label)requestViewComponent.getFellow("requestDescriptionLabel")).setValue(((GenericValue)delegator.findByPrimaryKey("PartyRequest", UtilMisc.toMap("requestId", requestHistoryGv.getString("requestId")))).getString("requestDescription"));
			}

		};
		
	}
	
	public static void openWindow(Comboitem comboitem,String requestId) throws SuspendNotAllowedException, InterruptedException{
		
		String statusId = (String)comboitem.getValue();
		
		Window window = (Window)Executions.createComponents("/zul/employeeRequest/respondToRequest.zul", null, UtilMisc.toMap("statusId", statusId,"requestId",requestId));
		window.doModal();
		Button btn = (Button)window.getFellow("respondButton");
		btn.setLabel(comboitem.getLabel());
		org.zkoss.zul.Caption caption = (org.zkoss.zul.Caption)window.getFellow("respondWindowCaption");
		caption.setLabel(comboitem.getLabel()+" "+"Request");
	}
}
