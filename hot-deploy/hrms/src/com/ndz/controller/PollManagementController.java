package com.ndz.controller;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.*;

import com.ndz.zkoss.util.HrmsInfrastructure;

public class PollManagementController extends GenericForwardComposer {

	private static final long serialVersionUID = 1L;

	public static void createNewPoll(Event event) throws GenericEntityException, InterruptedException {

		Component createNewPollWindow = event.getTarget();
		createNewPollWindow.getDesktop().getExecution().getNativeRequest();

		String pollQuestion = (String) ((Textbox) createNewPollWindow.getFellow("question")).getValue();
		Date fromDateType = (Date) ((Datebox) createNewPollWindow.getFellow("fromDate")).getValue();
		Date thruDateType = (Date) ((Datebox) createNewPollWindow.getFellow("thruDate")).getValue();

		java.sql.Date fromDate = new java.sql.Date(fromDateType.getTime());
		java.sql.Date thruDate = new java.sql.Date(thruDateType.getTime());

		GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
		String pollId = delegator.getNextSeqId("OpinionPoll");
		delegator.create("OpinionPoll", UtilMisc.toMap("pollId", pollId, "question", pollQuestion, "fromDate", fromDate, "thruDate",
				thruDate, "questionStatus", "Saved"));
		Messagebox.show("Poll Added Successfully", "Success", 1, null);

	}

	public static void submitOpinion(Event event, String pollId) throws GenericEntityException, InterruptedException {
		GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
		Component employeeMainTemplate = event.getTarget();
		employeeMainTemplate.getDesktop().getExecution().getNativeRequest();
		Radio answerType = ((Radiogroup) employeeMainTemplate.getFellow("pollAnswer")).getSelectedItem();
		String answer = (String) answerType.getValue();
		String employeeId = (String) userLogin.getString("partyId");

		GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");

		delegator.create("OpinionPollResponse", UtilMisc.toMap("pollId", pollId, "partyId", employeeId, "response", answer));
		Messagebox.show("Response Submitted Successfully", "Success", 1, null);
	}

	public static void updateOpinion(Event event, String pollId) throws GenericEntityException, InterruptedException {
		GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
		Component pollView = event.getTarget();
		pollView.getDesktop().getExecution().getNativeRequest();
		String employeeId = (String) userLogin.getString("partyId");
		String comments = (String) ((Textbox) pollView.getFellow("response")).getValue();
		GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");

		List opinionResponseList = delegator.findByAnd("OpinionPollResponse", UtilMisc.toMap("pollId", pollId, "partyId", employeeId));
		GenericValue opinionResponseGv = EntityUtil.getFirst(opinionResponseList);
		opinionResponseGv.putAll(UtilMisc.toMap("pollId", pollId, "partyId", employeeId, "comments", comments));
		delegator.store(opinionResponseGv);
		Messagebox.show("Comment Submitted Successfully", "Success", 1, null);

	}

	public static void editPoll(Event event) throws GenericEntityException, InterruptedException {

		Component editPollWindow = event.getTarget();
		editPollWindow.getDesktop().getExecution().getNativeRequest();

		String pollId = (String) ((Textbox) editPollWindow.getFellow("pollId")).getValue();
		String pollQuestion = (String) ((Textbox) editPollWindow.getFellow("question")).getValue();
		Date fromDateType = (Date) ((Datebox) editPollWindow.getFellow("fromDate")).getValue();
		Date thruDateType = (Date) ((Datebox) editPollWindow.getFellow("thruDate")).getValue();

		java.sql.Date fromDate = new java.sql.Date(fromDateType.getTime());
		java.sql.Date thruDate = new java.sql.Date(thruDateType.getTime());

		GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
		GenericValue pollGv = delegator.makeValue("OpinionPoll", UtilMisc.toMap("pollId", pollId, "question", pollQuestion, "fromDate",
				fromDate, "thruDate", thruDate, "questionStatus", "Saved"));
		delegator.createOrStore(pollGv);

		Messagebox.show("Poll Updated Successfully", "Success", 1, null);

	}

	public static void submitPoll(Event event) throws GenericEntityException, InterruptedException {

		Component editPollWindow = event.getTarget();
		editPollWindow.getDesktop().getExecution().getNativeRequest();

		String pollId = (String) ((Textbox) editPollWindow.getFellow("pollId")).getValue();
		String pollQuestion = (String) ((Textbox) editPollWindow.getFellow("question")).getValue();
		Date fromDateType = (Date) ((Datebox) editPollWindow.getFellow("fromDate")).getValue();
		Date thruDateType = (Date) ((Datebox) editPollWindow.getFellow("thruDate")).getValue();

		java.sql.Date fromDate = new java.sql.Date(fromDateType.getTime());
		java.sql.Date thruDate = new java.sql.Date(thruDateType.getTime());

		GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
		GenericValue pollGv = delegator.makeValue("OpinionPoll", UtilMisc.toMap("pollId", pollId, "question", pollQuestion, "fromDate",
				fromDate, "thruDate", thruDate, "questionStatus", "Submitted"));
		delegator.createOrStore(pollGv);

		Messagebox.show("Poll Submitted Successfully", "Success", 1, null);

	}

	public static void deletePoll(Event event, GenericValue gv, final Button btn) throws InterruptedException {
		final Component searchPanel = event.getTarget();
		final String pollId = gv.getString("pollId");
		final GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");

		Messagebox.show("Do You Want To Delete this Record?", "Warning", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION,
				new EventListener() {
					public void onEvent(Event evt) {
						if ("onYes".equals(evt.getName())) {

							try {
								delegator.removeByAnd("OpinionPoll", UtilMisc.toMap("pollId", pollId));
								Events.postEvent("onClick", btn, null);

							} catch (GenericEntityException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							try {
								Messagebox.show("Poll Deleted Sucessfully", Labels.getLabel("HRMS_SUCCESS"), 1, null);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						} else {
							return;
						}
					}
				});

	}
	
	public static List<GenericValue> getOpinionPoll() throws GenericEntityException{
		GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
		java.sql.Date startDate = new java.sql.Date(java.util.Calendar.getInstance().getTimeInMillis());
		EntityCondition fdateCondition = EntityCondition.makeCondition("fromDate",EntityOperator.LESS_THAN_EQUAL_TO, startDate);
		EntityCondition tdateCondition = EntityCondition.makeCondition("thruDate",EntityOperator.GREATER_THAN_EQUAL_TO, startDate);
		EntityCondition dateCondition = EntityCondition.makeCondition(fdateCondition,tdateCondition);
		EntityCondition cond  = EntityCondition.makeCondition("questionStatus",EntityOperator.EQUALS,"Submitted");
		EntityCondition pollcondition = EntityCondition.makeCondition(dateCondition,cond);
		return delegator.findList("OpinionPoll",pollcondition,null,null,null,false);
	}
}
