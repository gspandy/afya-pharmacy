package com.hrms.composer;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.GenericDispatcher;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.databind.AnnotateDataBinder;
import org.zkoss.zkplus.databind.BindingListModel;
import org.zkoss.zkplus.databind.BindingListModelList;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Timebox;
import org.zkoss.zul.Window;

import com.ndz.zkoss.EmployeeBox;
import com.ndz.zkoss.util.HrmsInfrastructure;

public class InterviewDetailComposer extends GenericForwardComposer {

	private static final long serialVersionUID = 1L;

	AnnotateDataBinder binder = null;
	Window viewInterviewDeatilWindow = null;
	Listbox viewInterviewDeatilListbox = null;
	BindingListModel model;
	List interviewDetailList = new ArrayList();

	private String candidateId;

	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		binder = new AnnotateDataBinder(comp);
		viewInterviewDeatilWindow = (Window) comp;
		this.candidateId = (String) Executions.getCurrent().getArg().get(
				"partyId");
		this.interviewDetailList = getCandidateInterviewScheduleDetail();

		binder.loadAttribute(viewInterviewDeatilListbox, "model");
	}

	public BindingListModel getModel() {
		return new BindingListModelList(this.interviewDetailList, false);
	}

	protected List<GenericValue> getCandidateInterviewScheduleDetail()
			throws GenericEntityException {

		GenericValue userLogin = (GenericValue) Executions.getCurrent()
				.getDesktop().getSession().getAttribute("userLogin");
		GenericDelegator delegator = (GenericDelegator) userLogin.getDelegator();

		List<GenericValue> interviewScheduleDetailList = new ArrayList<GenericValue>();
		try {
			interviewScheduleDetailList = delegator.findByAnd(
					"PerformanceNote", UtilMisc.toMap("partyId", candidateId));
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return interviewScheduleDetailList;

	}

	public ListitemRenderer getRenderer() {
		return new ListitemRenderer() {
			public void render(Listitem listItem, Object data) throws Exception {
				
				final GenericValue interviewDetailGv = (GenericValue) data;
				
				new Listcell(interviewDetailGv.getString("partyId")).setParent(listItem);
				new Listcell(interviewDetailGv.getString("interviewerId")).setParent(listItem);
				new Listcell(interviewDetailGv.getString("fromDate")).setParent(listItem);
				new Listcell(interviewDetailGv.getString("statusId")) .setParent(listItem);
				
				Listcell scheduleTimeListcell = new Listcell();
				scheduleTimeListcell.setParent(listItem);
				Timebox scheduleTimeBox = new Timebox();
				scheduleTimeBox.setParent(scheduleTimeListcell);
				scheduleTimeBox.setValue(new Date(interviewDetailGv.getTimestamp("scheduleTime").getTime()));
				scheduleTimeBox.setButtonVisible(false);
				scheduleTimeBox.setReadonly(false);
				scheduleTimeBox.setStyle("border:0px");
				listItem.setValue(data);
			}
		};

	}
	public static void deleteScheduleInterview(final Event event, final Timestamp thruDate,final String employeeId,final String roleTypeId1,final Listitem li)
	throws InterruptedException, GenericEntityException {
		Messagebox.show("Do you want to cancel the Interview?", "Warning", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION, new EventListener() {
			public void onEvent( Event evt ) {
				if ("onYes".equals(evt.getName())) {
		GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
     try {
		delegator.removeByAnd("PerformanceNote", UtilMisc.toMap("thruDate", thruDate,"partyId",employeeId,"roleTypeId",roleTypeId1));
		li.detach();
	} catch (GenericEntityException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
     try {
		Messagebox.show("Selected Interview has been cancelled successfully ", "Success", 1, null);
	} catch (InterruptedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
				}					
}
		});
	}
	
	

	public void updateScheduleInterview(Event event,Timestamp thruDate)	throws GenericEntityException, GenericServiceException,	InterruptedException {

		GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
		GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
		Component scheduleInterviewWindow = event.getTarget();

		String prospectRole = (String) ((Textbox) scheduleInterviewWindow.getFellow("prospectRole")).getValue();
		String candidateId = (String) ((Textbox) scheduleInterviewWindow.getFellow("candidateId")).getValue();
		List<GenericValue> empApplication = delegator.findByAnd("EmploymentApp", UtilMisc.toMap("applyingPartyId",candidateId));
		String applicationId = null;
		for (GenericValue appGv : empApplication) {
			applicationId = appGv.getString("applicationId");
			break;
		}

		Date fromDateType = (Date) ((Datebox) scheduleInterviewWindow.getFellow("fromDate")).getValue();
		java.sql.Date fromDate = new java.sql.Date(fromDateType.getTime());

		String interviewerId = (String) ((EmployeeBox) scheduleInterviewWindow.getFellow("interviewerIdBox")).getValue();

		Map scheduleInterview = UtilMisc.toMap("userLogin", userLogin,"applicationId", applicationId, "roleTypeId", prospectRole,
				"fromDate", fromDate, "interviewerId", interviewerId,
				"partyId", candidateId, "scheduleTime",
				null,"thruDate",thruDate);
		LocalDispatcher dispatcher =HrmsInfrastructure.getDispatcher();//GenericDispatcher.getLocalDispatcher("default", delegator);
		dispatcher.runSync("updatePerformanceNote", scheduleInterview);
		this.interviewDetailList = getCandidateInterviewScheduleDetail();
		binder.loadAttribute(viewInterviewDeatilListbox, "model");
		  Messagebox.show("Interview Schedule Details Updated For The Candidate","Success", 1, null);
		 

	}

	private GenericValue selectedInterviewDetail;

	public GenericValue getSelectedInterviewDetail() {
		return selectedInterviewDetail;
	}

	public void setSelectedInterviewDetail(GenericValue selectedInterviewDetail) {
		this.selectedInterviewDetail = selectedInterviewDetail;
	}
}
