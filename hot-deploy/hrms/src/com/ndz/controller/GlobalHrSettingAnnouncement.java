package com.ndz.controller;

import java.sql.Timestamp;
import java.util.*;

import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.SimpleListModel;
import org.zkoss.zul.Textbox;

import com.ndz.zkoss.util.HrmsInfrastructure;

public class GlobalHrSettingAnnouncement extends GenericForwardComposer {
	private static final long serialVersionUID = 1L;
	private static GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");

	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		if ("groupboxAnnouncement".equals(comp.getId())) {
			dataGridAnnouncement(comp);

		}
	}

	@SuppressWarnings("deprecation")
	private void dataGridAnnouncement(Component root) throws GenericEntityException {
		GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
		GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
		String partyId = (String) userLogin.get("partyId");
		Timestamp startDate = new Timestamp(Calendar.getInstance().getTimeInMillis());
		//Timestamp startDateAdd = UtilDateTime.addDaysToTimestamp(startDate, -1);
		EntityExpr fdateCondition = EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO,startDate);
		EntityExpr tdateCondition = EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, startDate);
		EntityCondition dateCondition = EntityCondition.makeCondition(fdateCondition, tdateCondition);

		EntityCondition partyCondition = EntityCondition.makeCondition(EntityOperator.OR,
				EntityCondition.makeCondition(UtilMisc.toMap("role", "RECIPIENT", "partyId", partyId)), EntityCondition.makeCondition("audience", "ALL"));

		EntityCondition condition = EntityCondition.makeCondition(dateCondition, partyCondition);

		List orderBy = new ArrayList();
		orderBy.add("-fromDate");
		List<GenericValue> announcementList = delegator.findList("AnnouncementView", condition, null, orderBy, null, false);
		Grid dataGridAnnouncement = (Grid) root.getFellow("dataGridAnnouncement");
		if (UtilValidate.isNotEmpty(announcementList)) {
			SimpleListModel announcementSimpleListModel = new SimpleListModel(announcementList);
			dataGridAnnouncement.setModel(announcementSimpleListModel);
		} else {
			dataGridAnnouncement.setVisible(false);
		}
	}

	public static void sendAnnouncement(Event event) throws InterruptedException, GenericEntityException {
		GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
		Component saveLeaveType = event.getTarget();
		SearchController controller = (SearchController) saveLeaveType.getAttribute("controller");
		String employeeId = userLogin.getString("partyId");
		String announcementDes = (String) ((Textbox) saveLeaveType.getFellow("announcementDescrip")).getValue();
		Radio announcementType = ((Radiogroup) saveLeaveType.getFellow("announcementType")).getSelectedItem();
		String announcementTypeValue = (String) announcementType.getValue();
		java.sql.Timestamp fromDate = null;
		java.sql.Timestamp thruDate = null;
		Date fDate = (Date) ((Datebox) saveLeaveType.getFellow("fromDate")).getValue();
		if (fDate != null)
			fromDate = new java.sql.Timestamp(fDate.getTime());
		Date tDate = (Date) ((Datebox) saveLeaveType.getFellow("thruDate")).getValue();
		if (tDate != null)
			thruDate = new java.sql.Timestamp(tDate.getTime());
		// "fromPartyId", employeeId,
		Map<String, Object> rawAnnouncement = new HashMap<String, Object>();
        rawAnnouncement.put("announcementId",delegator.getNextSeqId("Announcement"));
        rawAnnouncement.put("announcement",announcementDes);
        rawAnnouncement.put("fromDate",fromDate);
        rawAnnouncement.put("thruDate",thruDate);
        /*UtilMisc.toMap("announcementId", delegator.getNextSeqId("Announcement"), "announcement", announcementDes,
				"fromDate", fromDate, "thruDate", thruDate);*/

		GenericValue storeGv = delegator.makeValue("Announcement", rawAnnouncement);
		delegator.create(storeGv);

		String announcementId = storeGv.getString("announcementId");
		rawAnnouncement.clear();
		delegator.create("AnnouncementParty", UtilMisc.toMap("partyId", employeeId, "role", "CREATOR", "announcementId", announcementId));
		// Check radio button if not all

		if (!announcementTypeValue.equals("all"))
			sendAnnouncement(announcementId, controller.getSelectedItems(), rawAnnouncement);
		else {
			storeGv.setString("audience", "ALL");
			delegator.store(storeGv);
		}
		Messagebox.show("Announcement Sent Successfully", "Success", 1, null);
	}

	private static void sendAnnouncement(String announcementId, Collection<GenericValue> parties, Map<String, Object> rawAnnouncement)
			throws GenericEntityException {

		for (GenericValue party : parties) {
			rawAnnouncement.putAll(UtilMisc.toMap("partyId", party.getString("partyId"), "role", "RECIPIENT", "announcementId", announcementId));
			GenericValue storeGv = delegator.makeValue("AnnouncementParty", rawAnnouncement);
			delegator.create(storeGv);
			rawAnnouncement.clear();
		}
	}

	public static void deleteAnnouncement(Event event, String announcementId) throws InterruptedException, GenericEntityException {
		delegator.removeByAnd("AnnouncementParty", UtilMisc.toMap("announcementId", announcementId));
		delegator.removeByAnd("Announcement", UtilMisc.toMap("announcementId", announcementId));

		Events.postEvent("onClick$searchPerCompany", event.getTarget().getPage().getFellow("searchPanel"), null);
		Messagebox.show("Announcement Deleted", "Success", 1, null);
	}

	public List<GenericValue> showAudiencesFor(String announcementId) throws InterruptedException, GenericEntityException {
		return delegator.findByAnd("AnnouncementParty", UtilMisc.toMap("announcementId", announcementId));
	}

}