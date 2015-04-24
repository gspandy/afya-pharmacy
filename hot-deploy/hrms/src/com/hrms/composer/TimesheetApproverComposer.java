package com.hrms.composer;

import java.util.ArrayList;

import java.util.Date;

import java.util.List;

import java.util.Locale;

import java.util.Set;

import java.util.TimeZone;

import org.apache.commons.lang.StringUtils;

import org.ofbiz.base.util.UtilDateTime;

import org.ofbiz.base.util.UtilMisc;

import org.ofbiz.entity.GenericDelegator;

import org.ofbiz.entity.GenericEntityException;

import org.ofbiz.entity.GenericValue;

import org.ofbiz.entity.condition.EntityCondition;

import org.ofbiz.entity.condition.EntityOperator;

import org.ofbiz.entity.model.DynamicViewEntity;
import org.ofbiz.entity.transaction.TransactionUtil;

import org.ofbiz.entity.util.EntityListIterator;

import org.zkoss.util.resource.Labels;

import org.zkoss.zhtml.Li;

import org.zkoss.zk.ui.Component;

import org.zkoss.zk.ui.Executions;

import org.zkoss.zk.ui.event.Event;

import org.zkoss.zk.ui.event.EventListener;

import org.zkoss.zk.ui.event.Events;

import org.zkoss.zk.ui.event.ForwardEvent;

import org.zkoss.zkplus.databind.BindingListModel;

import org.zkoss.zkplus.databind.BindingListModelList;

import org.zkoss.zul.Listbox;

import org.zkoss.zul.Listcell;

import org.zkoss.zul.Listitem;

import org.zkoss.zul.ListitemRenderer;

import org.zkoss.zul.Messagebox;

import org.zkoss.zul.Window;

import org.zkoss.zul.event.PagingEvent;

import org.zkoss.zul.ext.Paginal;

import com.ndz.zkoss.EmployeeBox;

import com.ndz.zkoss.HrmsUtil;

public class TimesheetApproverComposer

        extends HrmsComposer {

    private static final long serialVersionUID = 1L;

    Listbox timesheetMgrGrid = null;

    BindingListModel model;

    Paginal timesheetPaging;

    List timesheetForApprovalList = new ArrayList();

    @Override
    public void executeAfterCompose(Component comp) throws Exception {

        this.timesheetForApprovalList = getTimesheetsForApproval(delegator, userLogin.getString("partyId"));

        binder.loadAttribute(timesheetMgrGrid, "model");

    }

    public void onClick$search(Event event) throws Exception {

        Component comp = event.getTarget();

        String subOrdinateId = ((EmployeeBox) (comp.getFellow("empBox"))).getValue();

        if (StringUtils.isEmpty(subOrdinateId))
            return;

        this.timesheetForApprovalList = getTimesheetsForApprovalSearch(delegator, userLogin.getString("partyId"),

                subOrdinateId);

        binder.loadAttribute(timesheetMgrGrid, "model");

    }

    public BindingListModel getModel() {

        return new BindingListModelList(this.timesheetForApprovalList, false);

    }

    @SuppressWarnings("deprecation")
    protected List<GenericValue> getTimesheetsForApproval(GenericDelegator delegator, String managerId)

            throws GenericEntityException {

        List<GenericValue> retValue = null;

        try {

            List<String> statusIdList = new ArrayList<String>();

            statusIdList.add("TIMESHEET_REJECTED");

            statusIdList.add("TIMESHEET_COMPLETED");

            statusIdList.add("TIMESHEET_APPROVED");

            EntityCondition cn1 = EntityCondition.makeCondition("statusId", EntityOperator.IN, statusIdList);

            EntityCondition cn2 = EntityCondition.makeCondition("managerPartyId", userLogin.getString("partyId"));

            EntityCondition condition = EntityCondition.makeCondition(cn1, cn2);


            retValue = delegator.findList("TimesheetManagerRoleView", condition, null, UtilMisc.toList("timesheetId"), null, false);


        } catch (GenericEntityException e) {

            // TODO Auto-generated catch block

            e.printStackTrace();

        }

        return retValue;

    }

    @SuppressWarnings("deprecation")
    protected List<GenericValue> getTimesheetsForApprovalSearch(GenericDelegator delegator, String managerId,

                                                                String subOrdinateId) throws GenericEntityException {
        List<GenericValue> retValue = null;
        try {
            List<String> statusIdList = new ArrayList<String>();
            statusIdList.add("TIMESHEET_REJECTED");
            statusIdList.add("TIMESHEET_COMPLETED");
            statusIdList.add("TIMESHEET_APPROVED");
            EntityCondition cn1 = EntityCondition.makeCondition("statusId", EntityOperator.IN, statusIdList);
            EntityCondition cn2 = EntityCondition.makeCondition("emplPartyId", subOrdinateId);
            EntityCondition condition = EntityCondition.makeCondition(cn1, cn2);

            retValue = delegator.findList("TimesheetManagerRoleView", condition, null, UtilMisc.toList("timesheetId"), null, false);

        } catch (GenericEntityException e) {
            e.printStackTrace();
        }

        return retValue;

    }

    public void onPaging$timesheetPaging(ForwardEvent event) throws Exception {

        boolean beganTransaction = TransactionUtil.begin();
        DynamicViewEntity dve = new DynamicViewEntity();
        dve.setEntityName("TimesheetManagerRoleView");
        EntityListIterator iter = delegator.findListIteratorByCondition(dve, EntityCondition

                .makeCondition("managerPartyId", userLogin.getString("partyId")), null, UtilMisc.toList("timesheetId"), null, null);

        int activePage = ((PagingEvent) event.getOrigin()).getActivePage();

        this.timesheetForApprovalList = iter.getPartialList(activePage * timesheetPaging.getPageSize(), timesheetPaging

                .getPageSize());

        iter.close();

        TransactionUtil.commit(beganTransaction);

    }

    public void onClick$approveButton() throws GenericEntityException, InterruptedException {
        final Set<Listitem> items = timesheetMgrGrid.getSelectedItems();
        if (items.isEmpty()) {
            Messagebox.show("No Timesheet selected. Please select a Timesheet", "Error", 1, null);
        } else {

            final GenericDelegator delegator = (GenericDelegator) Executions.getCurrent().getAttributes().get(
                    "delegator");
            final List<GenericValue> completedTimesheets = new ArrayList<GenericValue>();

            boolean allSelectedAreNotCompleted = false;
            for (Listitem li : items) {
                GenericValue timesheet = ((GenericValue) li.getValue());
                if ("TIMESHEET_COMPLETED".equals(timesheet.getString("statusId"))) {
                    completedTimesheets.add(timesheet);
                } else {
                    allSelectedAreNotCompleted = true;
                }
            }
            if (allSelectedAreNotCompleted) {
                Messagebox.show("Only Timesheets which are in completed state can be Approved.", "Error", 1, null);
                return;
            }

            Messagebox.show("Timesheet Approved can't be modified. \n Do you want to continue?", "Warning", Messagebox.YES
                    | Messagebox.NO, Messagebox.QUESTION, new EventListener() {
                public void onEvent(Event evt) throws InterruptedException, GenericEntityException {
                    if ("onYes".equals(evt.getName())) {
                        for (GenericValue ts : completedTimesheets) {
                            delegator.storeByCondition("Timesheet", UtilMisc.toMap("statusId", "TIMESHEET_APPROVED"),
                                    EntityCondition.makeCondition("timesheetId", ts.getString("timesheetId")));
                        }

                        Messagebox.show("All Timesheets(s) approved successfully.", "Success", 1, null);
                        Events.postEvent("onClick", timesheetMgrGrid.getFellowIfAny("approvalTime").getParent().getFellowIfAny("teamtimesheettoolbarbtn"), null);
                    }

                }

            });
        }

    }

    public void onClick$rejectButton() throws InterruptedException {

        Set<Listitem> items = timesheetMgrGrid.getSelectedItems();

        if (items.isEmpty()) {
            Messagebox.show("No Timesheet selected. Please select a Timesheet", "Error", 1, null);
        } else {

            final GenericDelegator delegator = (GenericDelegator) Executions.getCurrent().getAttributes().get(
                    "delegator");
            final List<GenericValue> completedTimesheets = new ArrayList<GenericValue>();

            boolean allSelectedAreNotCompleted = false;
            for (Listitem li : items) {
                GenericValue timesheet = ((GenericValue) li.getValue());
                if ("TIMESHEET_COMPLETED".equals(timesheet.getString("statusId"))) {
                    completedTimesheets.add(timesheet);
                } else {
                    allSelectedAreNotCompleted = true;
                }
            }
            if (allSelectedAreNotCompleted) {
                Messagebox.show("Only Timesheets which are in completed state can be Rejected.", "Error", 1, null);
                return;
            }

            Messagebox.show("Timesheet Rejected can't be modified. \n Do you want to continue?", "Warning", Messagebox.YES
                    | Messagebox.NO, Messagebox.QUESTION, new EventListener() {
                public void onEvent(Event evt) throws InterruptedException, GenericEntityException {
                    if ("onYes".equals(evt.getName())) {
                        for (GenericValue ts : completedTimesheets) {
                            delegator.storeByCondition("Timesheet", UtilMisc.toMap("statusId", "TIMESHEET_REJECTED"),
                                    EntityCondition.makeCondition("timesheetId", ts.getString("timesheetId")));
                        }

                        Messagebox.show("All Timesheets(s) rejected successfully.", "Success", 1, null);
                        Events.postEvent("onClick", timesheetMgrGrid.getFellowIfAny("approvalTime").getParent().getFellowIfAny("teamtimesheettoolbarbtn"), null);
                    }

                }

            });
        }
    }

    public ListitemRenderer getRenderer() {

        return new ListitemRenderer() {

            public void render(Listitem listItem, Object data) throws Exception {

                final GenericValue timeSheetEntry = (GenericValue) data;

                Listcell cell = new Listcell(timeSheetEntry.getString("timesheetId"));

                cell.setParent(listItem);

                new Listcell(HrmsUtil.getStatusItemDescription(timeSheetEntry.getString("statusId")))
                        .setParent(listItem);

                new Listcell(UtilDateTime.formatDate(UtilDateTime.getDayStart(timeSheetEntry.getTimestamp("fromDate"))))

                        .setParent(listItem);

                new Listcell(UtilDateTime.formatDate(UtilDateTime.getDayEnd(timeSheetEntry.getTimestamp("thruDate"))))

                        .setParent(listItem);

                org.ofbiz.entity.GenericValue personGV = delegator.findByPrimaryKey("Person", UtilMisc.toMap("partyId",

                        timeSheetEntry.getString("emplPartyId")));

                new Listcell(timeSheetEntry.getString("emplPartyId") + " " + "-" + " "
                        + personGV.getString("firstName") + " "

                        + personGV.getString("lastName")).setParent(listItem);

                listItem.setTooltiptext("Double Click To View");

                listItem.addEventListener("onDoubleClick", new EventListener() {

                    public void onEvent(Event event) throws Exception {

                        Window window = (Window) Executions.createComponents("/zul/timesheet/viewTimesheet.zul", null,
                                UtilMisc

                                        .toMap("timesheetId", timeSheetEntry.getString("timesheetId"), "emplPartyId",
                                                timeSheetEntry

                                                        .getString("emplPartyId")));

                        Component comp = event.getTarget().getFellowIfAny("timeEntry", true);

                        Component appendDivComp = event.getTarget()
                                .getFellowIfAny("appendEditTimeSheetWindowDiv", true);

                        Component childComp = appendDivComp.getFirstChild();

                        if (comp != null)
                            comp.detach();

                        if (childComp != null)
                            childComp.detach();

                        window.setId("timeEntryWindow");

                        window.doEmbedded();

                        window.focus();

                        window.setParent(appendDivComp);

                    }

                });

                listItem.setValue(timeSheetEntry);

            }

        };

    }

}
