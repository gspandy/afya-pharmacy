package com.ndz.controller;

import com.ndz.component.party.LeaveTypeRenderer;
import com.ndz.zkoss.HrmsUtil;
import com.ndz.zkoss.util.HrmsInfrastructure;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.humanresext.util.HumanResUtil;
import org.ofbiz.security.Security;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;

public class LeaveManagementController extends GenericForwardComposer {

    private static final long serialVersionUID = -6132791819499383503L;
    private static String module = LeaveManagementController.class.getName();

    public void doAfterCompose(Component applyLeaveWindow) throws Exception {
        super.doAfterCompose(applyLeaveWindow);
        GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
        Set<String> leaveTypeToDisplay = new HashSet();
        leaveTypeToDisplay.add("description");
        leaveTypeToDisplay.add("enumId");
        EntityCondition condition = EntityCondition.makeCondition("enumTypeId", EntityOperator.EQUALS, "LEAVE_TYPE");
        List<GenericValue> leaveType = delegator.findList("Enumeration", condition, leaveTypeToDisplay, null, null, false);
        leaveType.add(0, null);
        SimpleListModel leaveList = new SimpleListModel(leaveType);

        Listbox applyLeaveType = (Listbox) applyLeaveWindow.getFellow("applyLeaveType");
        applyLeaveType.setModel(leaveList);
        applyLeaveType.setItemRenderer(new LeaveTypeRenderer());
    }

    public static void applyLeave(Event event) {
        try {
            Component applyLeaveWindow = event.getTarget();
            applyLeaveWindow.getDesktop().getExecution().getNativeRequest();
            GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");


            GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
            GenericValue leave = delegator.makeValue("EmplLeave");
            Date fromDateInput = (Date) ((Datebox) applyLeaveWindow.getFellow("applyLeaveFromDate")).getValue();
            Date thruDateInput = (Date) ((Datebox) applyLeaveWindow.getFellow("applyLeaveThruDate")).getValue();
            // if (thruDateInput == null)
            // thruDateInput = fromDateInput;
            Timestamp fromDate = new Timestamp(fromDateInput.getTime());
            Timestamp thruDate = new Timestamp(thruDateInput.getTime());

			/*int noOfDays = org.ofbiz.humanresext.util.HumanResUtil.getWorkingDaysBetween(fromDate, thruDate, delegator);
			if (noOfDays <= 0) {
				Messagebox.show("Leave Cannot be Applied on Non Working Days/Public Holidays", "Error", 1, null);
				return;
			}

			Double lpaidDays = HumanResUtil.getWorkingDaysBetween(fromDate, thruDate, delegator).doubleValue();*/

            String numDaysType = ((org.zkoss.zul.api.Textbox) applyLeaveWindow.getFellow("allocateleaveDays")).getValue();
            Comboitem fractionalPartOfDays = ((Combobox) applyLeaveWindow.getFellow("fractionalPartForLeave")).getSelectedItem();
            String fractionalPartOfDay = (String) fractionalPartOfDays.getValue();
            Listitem leaveTypeInput = ((Listbox) applyLeaveWindow.getFellow("applyLeaveType")).getSelectedItem();
            String leaveType = (String) leaveTypeInput.getValue();
            Double lpaidDays = new Double(numDaysType) + new Double(fractionalPartOfDay);


            leave.set("leaveTypeId", leaveType);
            leave.set("fromDate", fromDate);
            leave.set("thruDate", thruDate);
            leave.set("description", (((Textbox) applyLeaveWindow.getFellow("applyleaveReason")).getValue()));
            leave.set("contactNumber",Long.valueOf (((Textbox) applyLeaveWindow.getFellow("applyLeaveContactNumber")).getValue()));
            leave.set("leaveStatusId", "LT_SAVED"); // First leave is just saved
            leave.set("paidDays", lpaidDays);
            leave.set("updatedBy", userLogin.getString("partyId"));
            // try {
            // leave.set("contactNumber",
            // NumberFormat.getInstance().parse((((Textbox)
            // applyLeaveWindow.getFellow("applyLeaveContactNumber")).getValue())));
            // } catch (WrongValueException e) {
            // TODO Auto-generated catch block
            // e.printStackTrace();
            // } catch (ParseException e) {
            // TODO Auto-generated catch block
            // e.printStackTrace();
            // }
            leave.set("contactAddress", (((Textbox) applyLeaveWindow.getFellow("applyLeaveContactAddress")).getValue()));

            String emplId = null;
            Boolean mgnrBoxVisible = ((Combobox) applyLeaveWindow.getFellow("managerBox")).isVisible();
            if (mgnrBoxVisible) {
                org.zkoss.zul.api.Comboitem requestingEmployeeId = ((Combobox) applyLeaveWindow.getFellow("managerBox")).getSelectedItem();
                emplId = requestingEmployeeId != null ? (String) requestingEmployeeId.getValue() : userLogin.getString("partyId");
            } else
                emplId = userLogin.getString("partyId");

            GenericValue person = delegator.findOne("Person",UtilMisc.toMap("partyId",emplId),false);

			/*
			 * Debug.logVerbose("************* \n\n empl party id: " + emplId,
			 * module);
			 */
            String managerPositionId1 = null;
            managerPositionId1 = HumanResUtil.getManagerPositionIdForParty(emplId, delegator);

            Security security = (Security) Executions.getCurrent().getAttributes().get("security");
            boolean isManager = security.hasPermission("HUMANRES_MGR", userLogin);

            if (managerPositionId1 != null || isManager) {

            	String mgrPositionId=managerPositionId1;
            	if(managerPositionId1 == null){
            		GenericValue empPos=HumanResUtil.getEmplPositionForParty(emplId, delegator);
            		mgrPositionId=empPos==null?null:empPos.getString("emplPositionId");
            	}

                String leaveId = delegator.getNextSeqId("EmplLeave");
                leave.set("partyId", emplId);
                leave.set("leaveId", leaveId);
                leave.set("statusType", "0");
                leave.set("mgrPositionId", mgrPositionId);

                GenericValue emplPositionGv = HumanResUtil.getEmplPositionForParty(emplId, delegator);
                String emplPosType = emplPositionGv == null ? null : emplPositionGv.getString("emplPositionTypeId");
                String positionCategory = person.getString("positionCategory");
                String employeeType = person.getString("employeeType");

                boolean isEmployeeTerminatedOrResigned=HumanResUtil.checkEmployeeTermination(emplId, delegator);
                if(isEmployeeTerminatedOrResigned){
                	 Messagebox.show("Terminated or Resigned employee can not apply for leave", "Error", 1, null);
                     return;
                }

                EntityCondition posTypeCondition = EntityCondition.makeCondition("emplPosTypeId", EntityOperator.EQUALS, emplPosType);
                EntityCondition leaveTypeCondition = EntityCondition.makeCondition("leaveTypeId", EntityOperator.EQUALS, leaveType);
                EntityCondition positionCategoryCondition = EntityCondition.makeCondition("positionCategory",EntityOperator.EQUALS,positionCategory);
                EntityCondition employeeTypeCondition = EntityCondition.makeCondition("employeeType",EntityOperator.EQUALS,employeeType);
                EntityCondition fromdateCondition = EntityCondition.makeCondition("beginYear", EntityOperator.LESS_THAN_EQUAL_TO, fromDate);
                EntityCondition thrudateCondition = EntityCondition.makeCondition("endYear", EntityOperator.GREATER_THAN_EQUAL_TO, thruDate);
                EntityCondition condition1 = EntityCondition.makeCondition(posTypeCondition, EntityOperator.AND, leaveTypeCondition);
                List conditionList = Arrays.asList(new Object[]{posTypeCondition,leaveTypeCondition,positionCategoryCondition,employeeTypeCondition,fromdateCondition,thrudateCondition});
                List<GenericValue> emplLeaveLimitList = delegator.findList("EmplLeaveLimit", EntityCondition.makeCondition(conditionList,EntityOperator.AND), null, null, null, false);
                if(UtilValidate.isEmpty(emplLeaveLimitList)){
                    EntityCondition posTypeConditionForBlankSpace = EntityCondition.makeCondition("emplPosTypeId", EntityOperator.EQUALS, " ");
                    conditionList = Arrays.asList(new Object[]{leaveTypeCondition,positionCategoryCondition,employeeTypeCondition,posTypeConditionForBlankSpace,fromdateCondition,thrudateCondition});
                    emplLeaveLimitList = delegator.findList("EmplLeaveLimit", EntityCondition.makeCondition(conditionList,EntityOperator.AND), null, null, null, false);
                }
                GenericValue emplLeaveLimitGv = EntityUtil.getFirst(emplLeaveLimitList);
                if(UtilValidate.isEmpty(emplLeaveLimitGv)){
                    Messagebox.show("Leave Limit Does Not Exist, Cannot Apply", "Error", 1, null);
                    return;
                }
                Timestamp beginYear = emplLeaveLimitGv.getTimestamp("beginYear");
                Timestamp endYear = emplLeaveLimitGv.getTimestamp("endYear");
                //List emplLeaveInfo = delegator.findByAnd("EmployeeLeaveInfo", UtilMisc.toMap("partyId", emplId, "leaveType", leaveType, "beginYear", beginYear, "endYear", endYear), null, false);

                //GenericValue emplLeaveInfoGv = EntityUtil.getFirst(emplLeaveInfo);

                GenericValue emplLeaveInfoGv = getEmployeeLeaveInfo(emplId,leaveType,beginYear,endYear);

                if(UtilValidate.isEmpty(emplLeaveInfoGv)){
                    emplLeaveInfoGv = createEmployeeLeaveInfoWithPreviousYearLeave(leaveType, beginYear, employeeType, positionCategory, emplPosType, emplId);
                }
                List<GenericValue> emplLeaveGvList = delegator.findByAnd("EmplLeave",UtilMisc.toMap("partyId",emplId,"leaveTypeId",leaveType,"leaveStatusId","LT_SUBMITTED"),null,false);
                Double totalLeaveSubmitted = 0.0;
                if(UtilValidate.isNotEmpty(emplLeaveGvList)){
                    for(GenericValue emplLeaveGv:emplLeaveGvList){
                        totalLeaveSubmitted = totalLeaveSubmitted + emplLeaveGv.getDouble("paidDays");
                    }
                }


                if(UtilValidate.isNotEmpty(emplLeaveInfoGv)){
                    Map currentAndPreviousYearBalance = getCurrentAndPreviousYearLeaveLimit(leaveType,employeeType, positionCategory, emplPosType, emplId);
                    Double leaveDaysEmployeeCanApply =getTotalBalance(currentAndPreviousYearBalance)-totalLeaveSubmitted;
                    /*if(!HrmsUtil.isPreviousYearLeave(emplLeaveInfoGv)){
                        leaveDaysEmployeeCanApply= emplLeaveInfoGv.getDouble("balanceLeave") - totalLeaveSubmitted;
                    }else{
                        GenericValue previousYearLeaveInfo =  getPreviousLeaveFromEmployeeLeaveInfo(emplId,leaveType);
                        if(UtilValidate.isNotEmpty(previousYearLeaveInfo)){
                            leaveDaysEmployeeCanApply =  emplLeaveInfoGv.getDouble("balanceLeave") + previousYearLeaveInfo.getDouble("balanceLeave") - totalLeaveSubmitted;
                        }else{
                            leaveDaysEmployeeCanApply =  emplLeaveInfoGv.getDouble("balanceLeave") + emplLeaveLimitGv.getDouble("numDays") - totalLeaveSubmitted;
                        }

                    }*/

                    if(leaveDaysEmployeeCanApply.equals(0.0) || lpaidDays > leaveDaysEmployeeCanApply){
                            Messagebox.show("You do not have sufficient balance leave to apply ", "Error", 1, null);
                            return;
                    }
                    if(lpaidDays>leaveDaysEmployeeCanApply){
                        Messagebox.show("You have only "+leaveDaysEmployeeCanApply+" Days leave to apply", "Error", 1, null);
                        return;
                    }
                }else{
                    if(emplLeaveLimitGv.getDouble("numDays")<lpaidDays){
                        Messagebox.show("You cannot apply for leave more than leave limit", "Error", 1, null);
                        return;
                    }
                }

                /*EntityCondition fromdateCondition = EntityCondition.makeCondition("beginYear", EntityOperator.LESS_THAN_EQUAL_TO, fromDate);
                EntityCondition thrudateCondition = EntityCondition.makeCondition("endYear", EntityOperator.GREATER_THAN_EQUAL_TO, thruDate);
                EntityCondition condition2 = EntityCondition.makeCondition(fromdateCondition, EntityOperator.AND, thrudateCondition);
                EntityCondition makecoCondition = EntityCondition.makeCondition(condition1, EntityOperator.AND, condition2);
                List<GenericValue> emplFiscalYearLeaveLimitList = delegator.findList("EmplLeaveLimit", makecoCondition, null, null, null, false);
                if(UtilValidate.isEmpty(emplFiscalYearLeaveLimitList)){
                    conditionList = Arrays.asList(new Object[]{condition2,leaveTypeCondition,positionCategoryCondition,employeeTypeCondition});
                    emplFiscalYearLeaveLimitList = delegator.findList("EmplLeaveLimit", EntityCondition.makeCondition(conditionList,EntityOperator.AND), null, null, null, false);
                }
                if (UtilValidate.isEmpty(emplFiscalYearLeaveLimitList)) {
                    Messagebox.show("Leave can not be applied beyond the allocated time period", "Error", 1, null);
                    return;
                }*/
                EntityCondition employmentCondition1 = EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, userLogin.getString("partyId"));
                EntityCondition employmentCondition2 = EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, fromDate);
                EntityCondition employmentCondition = EntityCondition.makeCondition(employmentCondition1, EntityOperator.AND, employmentCondition2);
                List<GenericValue> employmentList = delegator.findList("Employment", employmentCondition, null, null, null, false);
                if (UtilValidate.isEmpty(employmentList)) {
                    Messagebox.show("Leave Can not Be Applied Behind The Joining Date", "Error", 1, null);
                    return;
                }

                delegator.create(leave);
                Messagebox.show("Leave Saved Successfully", Labels.getLabel("HRMS_SUCCESS"), 1, null);
                applyLeaveWindow.detach();
            } else {
                Messagebox.show("Cannot Apply;Manager has not been Assigned", "Error", 1, null);
                return;
            }

        } catch (Throwable e) {
            e.printStackTrace();
        }

    }

    public static void submitLeave(Event event) throws GenericServiceException, InterruptedException, GenericEntityException {
        String status = updateLeave(event,false);
        if("error".equals(status))
            return;
        Component editLeaveWindow = event.getTarget();
        GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
        /*String employeeId = (String) userLogin.getString("partyId");*/
        String employeeId = ((Label) editLeaveWindow.getFellow("employeeId")).getValue();
        String d_employeeId = (UtilValidate.isNotEmpty(employeeId)) ? employeeId : (String) userLogin.get("partyId");

        String mgrPositionId = ((Label) editLeaveWindow.getFellow("mgrPositionId")).getValue();

        GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
        LocalDispatcher dispatcher = HrmsInfrastructure.getDispatcher();//GenericDispatcher.getLocalDispatcher("default", delegator);
        String leaveId = ((Label) editLeaveWindow.getFellow("leaveId")).getValue();
        Date fromDateInput = ((Datebox) editLeaveWindow.getFellow("fromDate")).getValue();
        Timestamp fromDate = new Timestamp(fromDateInput.getTime());
        Date thruDateInput = ((Datebox) editLeaveWindow.getFellow("thruDate")).getValue();
        Timestamp thruDate = new Timestamp(thruDateInput.getTime());
        int noOfDays = org.ofbiz.humanresext.util.HumanResUtil.getWorkingDaysBetween(fromDate, thruDate, delegator);
        if (noOfDays <= 0) {
            Messagebox.show("Leave Cannot be Submitted on the date specified", "Error", 1, null);
            return;
        }
        Timestamp oldFromDate = null;
        Timestamp oldThruDate = null;
        Date oldFromDateType = null;
        Date oldThruDateType = null;
        String statusId = null;
        List<GenericValue> leaveInfoList = delegator.findByAnd("EmplLeave", UtilMisc.toMap("partyId", d_employeeId));
        for (int i = 0; i < leaveInfoList.size(); i++) {
            GenericValue leaveInfoGv = (GenericValue) leaveInfoList.get(i);
            if (leaveInfoGv != null) {
                oldFromDate = leaveInfoGv.getTimestamp("fromDate");
                oldThruDate = leaveInfoGv.getTimestamp("thruDate");
                oldFromDateType = new Date(oldFromDate.getTime());
                oldThruDateType = new Date(oldThruDate.getTime());
                statusId = leaveInfoGv.getString("leaveStatusId");
                if ((((thruDateInput.compareTo(oldFromDateType) == 1) || (thruDateInput.compareTo(oldFromDateType) == 0)) && ((oldThruDateType
                        .compareTo(fromDateInput) == 1) || (oldThruDateType.compareTo(fromDateInput) == 0)))
                        || ((((oldFromDateType.compareTo(fromDateInput) == 0)) && ((oldThruDateType.compareTo(thruDateInput) == 0))))
                        || ((oldThruDateType.compareTo(fromDateInput) == 0))) {
                    if (!(("LT_ADM_REJECTED".equals(statusId)) || ("LT_MGR_REJECTED".equals(statusId)) || ("LT_SAVED".equals(statusId)))) {
                        Messagebox.show("Leave Already Submitted On This Date", "Error", 1, null);
                        return;
                    }
                }
            }
        }

        Map<String, Object> submitLeave = new HashMap<String, Object>();
        submitLeave.put("userLogin",userLogin);
        submitLeave.put("leaveId",leaveId);
        submitLeave.put("statusType","1");
        submitLeave.put("mgrPositionId",mgrPositionId);
        submitLeave.put("partyId",d_employeeId);
        dispatcher.runSync("submitLeaveService", submitLeave);
        Events.postEvent("onClick$searchButton", editLeaveWindow.getPage().getFellow("searchPanel"), null);
        Messagebox.show(Labels.getLabel("Hrms_LeaveSuccessfullySubmitted"), Labels.getLabel("HRMS_SUCCESS"), 1, null);

    }

    public void processLeave(Event event, GenericValue gv) throws GenericServiceException, SuspendNotAllowedException, InterruptedException,
            GenericEntityException {

        Component applyLeaveWindow = event.getTarget();
        GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
        GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
        LocalDispatcher dispatcher = HrmsInfrastructure.getDispatcher();//GenericDispatcher.getLocalDispatcher("default", delegator);

        String leaveId = gv.getString("leaveId");
        String savedEmployeeId = gv.getString("partyId");
        String leaveTypeId = gv.getString("leaveTypeId");
        String leaveStatusId = gv.getString("leaveStatusId");
        String mgrCommentString = gv.getString("mgrComment");
        Timestamp fromDateTypeInput = gv.getTimestamp("fromDate");
        Timestamp thruDateTypeInput = gv.getTimestamp("thruDate");
        java.sql.Date fromDateType = new java.sql.Date(fromDateTypeInput.getTime());
        java.sql.Date thruDateType = new java.sql.Date(thruDateTypeInput.getTime());
        String contactNumber = gv.getString("contactNumber");
        String reasonFroLeave = gv.getString("description");
        String contactAddress = gv.getString("contactAddress");
        String mgrPositionId = gv.getString("mgrPositionId");
        String mgrPartyId = HumanResUtil.getPartyIdForPositionId(mgrPositionId, delegator);

        GenericValue emplPosGv = HumanResUtil.getEmplPositionForParty(savedEmployeeId, delegator);
        String posType = null;
        if (emplPosGv != null) {
            posType = (String) emplPosGv.getString("emplPositionTypeId");
        }
        GenericValue personGV = delegator.findOne("Person", UtilMisc.toMap("partyId", savedEmployeeId), false);
        EntityCondition emplPosTypeCondition = EntityCondition.makeCondition("emplPosTypeId", EntityOperator.EQUALS, posType);
        EntityCondition leaveTypeIdCondition = EntityCondition.makeCondition("leaveTypeId",EntityOperator.EQUALS,leaveTypeId);
        EntityCondition employeeTypeCondition = EntityCondition.makeCondition("employeeType",EntityOperator.EQUALS,personGV.getString("employeeType"));
        EntityCondition positionCategoryCondition = EntityCondition.makeCondition("positionCategory",EntityOperator.EQUALS,personGV.getString("positionCategory"));
        EntityCondition beginYearCondition = EntityCondition.makeCondition("beginYear", EntityOperator.LESS_THAN_EQUAL_TO,fromDateTypeInput);
        EntityCondition endYearCondition = EntityCondition.makeCondition("endYear", EntityOperator.GREATER_THAN_EQUAL_TO,thruDateTypeInput);
        List conditions = Arrays.asList(leaveTypeIdCondition, employeeTypeCondition, positionCategoryCondition, beginYearCondition, endYearCondition, emplPosTypeCondition);
        List leaveLimitList = delegator.findList("EmplLeaveLimit", EntityCondition.makeCondition(conditions, EntityOperator.AND), null, null, null, false);
        if(UtilValidate.isEmpty(leaveLimitList)){
            emplPosTypeCondition = EntityCondition.makeCondition("emplPosTypeId",EntityOperator.EQUALS," ");
            conditions = Arrays.asList(leaveTypeIdCondition,employeeTypeCondition,positionCategoryCondition,beginYearCondition,endYearCondition,emplPosTypeCondition);
            leaveLimitList = delegator.findList("EmplLeaveLimit", EntityCondition.makeCondition(conditions,EntityOperator.AND), null, null, null, false);
        }
        List listOrdered = org.ofbiz.entity.util.EntityUtil.orderBy(leaveLimitList, UtilMisc.toList("createdStamp DESC"));
        GenericValue leaveLimitGv = EntityUtil.getFirst(listOrdered);
        BigDecimal leaveLimit = new BigDecimal(0);
        Timestamp beginYear = leaveLimitGv.getTimestamp("beginYear");
        Timestamp endYear = leaveLimitGv.getTimestamp("endYear");
        //List employeeLeaveInfoList = delegator.findByAnd("EmployeeLeaveInfo", UtilMisc.toMap("partyId", savedEmployeeId, "leaveType", leaveTypeId, "beginYear", beginYear, "endYear", endYear), null, false);
        GenericValue employeeLeaveInfoGv = getEmployeeLeaveInfo(savedEmployeeId, leaveTypeId, beginYear, endYear);
        Map currentAndPreviousYearLeaveLimit =  getCurrentAndPreviousYearLeaveLimit(leaveTypeId, personGV.getString("employeeType"), personGV.getString("positionCategory"), posType, savedEmployeeId);
        BigDecimal availedLeave = new BigDecimal(0);
        if (employeeLeaveInfoGv != null){
            availedLeave = employeeLeaveInfoGv.getBigDecimal("availedLeave");
            leaveLimit = employeeLeaveInfoGv.getBigDecimal("leaveLimit");
        }else{
            if (leaveLimitGv != null)
                leaveLimit = leaveLimitGv.getBigDecimal("numDays");
        }

        Integer availed = 0;
        //availed = availedLeave.intValue();
        Integer limit = 0;
        //limit = leaveLimit.intValue();
        final Window win = (Window) Executions.createComponents("/zul/leaveManagement/approveLeave.zul", null, UtilMisc.toMap("applyFromDate",fromDateTypeInput,
                "applyThruDate",thruDateTypeInput,"savedEmployeeId",savedEmployeeId,"mgrPartyId",mgrPartyId,"leaveStatus",leaveStatusId));
        ((Label) win.getFellow("availedLeave")).setValue(availedLeave.toString());
        ((Label) win.getFellow("leaveLimit")).setValue(leaveLimit.toString());

        Double previousYearBalance = (Double)currentAndPreviousYearLeaveLimit.get("previousYearLeaveLimit")-(Double)currentAndPreviousYearLeaveLimit.get("previousYearAvailedLeave");
        Double currentYearBalance = (Double)currentAndPreviousYearLeaveLimit.get("currentYearLeaveLimit")-(Double)currentAndPreviousYearLeaveLimit.get("currentYearAvailedLeave");
        ((Label) win.getFellow("PYLeaveLimit")).setValue(previousYearBalance.toString());
        ((Label) win.getFellow("CYLeaveLimit")).setValue(currentYearBalance.toString());

        Label approveLeaveId = (Label) win.getFellow("leaveId");
        approveLeaveId.setValue(leaveId);
        Label employeeId = (Label) win.getFellow("employeeId");
        employeeId.setValue(savedEmployeeId);
        Label approveLeaveTypeId = (Label) win.getFellow("leaveTypeId");
        approveLeaveTypeId.setValue(leaveTypeId);

        Label leaveTypeIdView = (Label) win.getFellow("leaveTypeIdView");
        List<GenericValue> leaveTypeList = delegator.findByAnd("Enumeration", org.ofbiz.base.util.UtilMisc.toMap("enumId", leaveTypeId));
        leaveTypeIdView.setValue(leaveTypeList.get(0).getString("description"));

        final Label leaveStatus = (Label) win.getFellow("leaveStatus");
        leaveStatus.setValue(leaveStatusId);
        Label leaveStatusView = (Label) win.getFellow("leaveStatusView");
        List<GenericValue> statusIdList = delegator.findByAnd("StatusItem", UtilMisc.toMap("statusId", leaveStatusId));
        leaveStatusView.setValue(statusIdList.get(0).getString("description"));

        Datebox submitFromDate = (Datebox) win.getFellow("fromDate");
        submitFromDate.setValue(fromDateType);
        Datebox submitThruDate = (Datebox) win.getFellow("thruDate");
        submitThruDate.setValue(thruDateType);

        Datebox teamsFromDate = (Datebox) win.getFellow("teamsFromDate");
        teamsFromDate.setValue(fromDateType);

        Datebox teamsThruDate = (Datebox) win.getFellow("teamsThruDate");
        teamsThruDate.setValue(thruDateType);

       /* Integer paidDaysInt = new Integer(0);
        Integer totalDaysLeave = new Integer(0);
        String paidDays = "";
        if (gv.getDouble("paidDays") != null) {
            paidDaysInt = gv.getDouble("paidDays").intValue();
            paidDays = paidDaysInt.toString();
        }
        String unpaidDays = "";
        Integer unpaidDaysInt = new Integer("0");
        if (gv.getDouble("unpaidDays") != null) {
            unpaidDaysInt = gv.getDouble("unpaidDays").intValue();
            unpaidDays = unpaidDaysInt.toString();
        }

        totalDaysLeave = paidDaysInt + unpaidDaysInt;*/

        Integer paidDaysWholePart = 0;
        Double paidDaysFractionalPart =0.0;
        String paidDays = "";
        String paidDaysFractionalPartString="";
        if(gv.getDouble("paidDays") != null){
           paidDaysWholePart = gv.getDouble("paidDays").intValue();
           paidDaysFractionalPart = gv.getDouble("paidDays") -paidDaysWholePart;
            paidDays = paidDaysWholePart.toString();
            paidDaysFractionalPartString = paidDaysFractionalPart.toString();
        }

        Integer unpaidDaysWholePart = 0;
        Double unpaidDaysFractionalPart =0.0;
        String unpaidDays = "";
        String unpaidDaysFractionalPartString="";
        if(gv.getDouble("unpaidDays") != null){
            unpaidDaysWholePart = gv.getDouble("unpaidDays").intValue();
            unpaidDaysFractionalPart = gv.getDouble("unpaidDays") -unpaidDaysWholePart;
            unpaidDays = unpaidDaysWholePart.toString();
            unpaidDaysFractionalPartString = unpaidDaysFractionalPart.toString();
        }
        Double totalDaysLeave = paidDaysWholePart + paidDaysFractionalPart + unpaidDaysWholePart + unpaidDaysFractionalPart;
        Label emplLeaveDays = (Label) win.getFellow("emplLeaveDays");
        emplLeaveDays.setValue(totalDaysLeave.toString());
        Textbox paidDaysTextBox = (Textbox) win.getFellow("paidDays");
        paidDaysTextBox.setValue(paidDays);
        Combobox paidDaysCombobox = (Combobox) win.getFellow("fractionalPartForPaidLeave");
        paidDaysCombobox.setValue(paidDaysFractionalPartString);
        Textbox approveUnPaidDays = (Textbox) win.getFellow("unPaidDays");
        approveUnPaidDays.setValue(unpaidDays);
        Combobox unpaidDaysCombobox = (Combobox) win.getFellow("fractionalPartForUnpaidLeave");
        unpaidDaysCombobox.setValue(unpaidDaysFractionalPartString);
        Textbox mgrComment = (Textbox) win.getFellow("mgrComment");
        mgrComment.setValue(mgrCommentString);

        Textbox textboxContactNumber = (Textbox) win.getFellow("contactNumber");
        textboxContactNumber.setValue(contactNumber);
        Textbox textboxReasonFroLeave = (Textbox) win.getFellow("reasonFroLeave");
        textboxReasonFroLeave.setValue(reasonFroLeave);
        Textbox textboxContactAddress = (Textbox) win.getFellow("contactAddress");
        textboxContactAddress.setValue(contactAddress);
        Button approveButton = (Button) win.getFellow("approveLeaveButton");
        /*if("LT_EARNED".equals(leaveTypeId)){
            java.math.BigDecimal totalAccrualLeave = HrmsUtil.getAccrualDays(savedEmployeeId);
            Hbox accLeaveHbox = (Hbox) win.getFellow("accLeaveHbox");
            Label totalAccrualLeaveLbl = (Label) win.getFellow("totalAccrualLeaveLbl");
            if(UtilValidate.isNotEmpty(totalAccrualLeave)){
                totalAccrualLeaveLbl.setValue(totalAccrualLeave.toString());
                accLeaveHbox.setVisible(true);
            }
        }*/

		/* Check for the User Permission */
        String hasPermission = ((Label) win.getFellow("permissionId")).getValue();
        Boolean isAdmin = new Boolean(hasPermission);

        String hasBothPermission = ((Label) win.getFellow("permissionIdForBoth")).getValue();
        Boolean both = new Boolean(hasBothPermission);


        //if (!isAdmin) {
        if((!isAdmin || both) && "LT_SUBMITTED".equals(leaveStatus.getValue())){
            Button approveClaimButton = (Button) win.getFellow("approveLeaveButton");
            approveClaimButton.addEventListener("onClick", new EventListener() {
                public void onEvent(Event event) throws Exception {
                    leaveStatus.setValue("LT_MGR_APPROVED");
                    LeaveManagementController.approveLeave(event, "1", "2", win);
                }
            });

            Button rejectClaimButton = (Button) win.getFellow("rejectLeaveButton");
            rejectClaimButton.addEventListener("onClick", new EventListener() {
                public void onEvent(Event event) throws Exception {
                    leaveStatus.setValue("LT_MGR_REJECTED");
                    LeaveManagementController.approveLeave(event, "1", "1", win);
                }
            });
       // } else {
       }else if(isAdmin && ("LT_MGR_APPROVED".equals(leaveStatus.getValue()))) {
            Button approveClaimButton = (Button) win.getFellow("approveLeaveButton");
            approveClaimButton.addEventListener("onClick", new EventListener() {
                public void onEvent(Event event) throws Exception {
                    leaveStatus.setValue("LT_ADM_APPROVED");
                    LeaveManagementController.approveLeave(event, "1", "2", win);
                }
            });

            Button rejectClaimButton = (Button) win.getFellow("rejectLeaveButton");
            rejectClaimButton.addEventListener("onClick", new EventListener() {
                public void onEvent(Event event) throws Exception {
                    leaveStatus.setValue("LT_ADM_REJECTED");
                    LeaveManagementController.approveLeave(event, "1", "2", win);
                }
            });

        }
        win.doModal();
    }

    public static void approveLeave(final Event event, final String statusType, final String adminStatus, Component comp) throws GenericServiceException,
            Exception {

        final Component approveLeaveWindow = event.getTarget();
        Messagebox.show("Leave Approved/Rejected Can't Be Modified. \n Do You Want To Continue?", "Warning", Messagebox.YES | Messagebox.NO,
                Messagebox.QUESTION, new EventListener() {
                    @SuppressWarnings("unused")
                    public void onEvent(Event evt) {
                        if ("onYes".equals(evt.getName())) {
                            GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
                            GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
                            LocalDispatcher dispatcher = HrmsInfrastructure.getDispatcher();//GenericDispatcher.getLocalDispatcher("default", delegator);
                            String leaveId = (String) ((Label) approveLeaveWindow.getFellow("leaveId")).getValue();
                            String employeeId = (String) ((Label) approveLeaveWindow.getFellow("employeeId")).getValue();

                            Date fromDateType = (Date) ((Datebox) approveLeaveWindow.getFellow("fromDate")).getValue();
                            Date thruDateType = (Date) ((Datebox) approveLeaveWindow.getFellow("thruDate")).getValue();

                            java.sql.Date fromDate = new java.sql.Date(fromDateType.getTime());
                            java.sql.Date thruDate = new java.sql.Date(thruDateType.getTime());
                            Timestamp fromDateTimestamp = new Timestamp(fromDateType.getTime());
                            Timestamp thruDateTimestamp = new Timestamp(thruDateType.getTime());
                            String leaveTypeId = (String) ((Label) approveLeaveWindow.getFellow("leaveTypeId")).getValue();
                            String leaveStatusId = (String) ((Label) approveLeaveWindow.getFellow("leaveStatus")).getValue();
                            Double unPaidDays = 0.0;
                            String padiDaysType = (String) ((Textbox) approveLeaveWindow.getFellow("paidDays")).getValue();
                           /* fractionalPartForUnpaidLeave*/
                            Comboitem fractionalPartOfPaidDays = ((Combobox) approveLeaveWindow.getFellow("fractionalPartForPaidLeave")).getSelectedItem();
                            String paidDaysFractionalPart = (String) fractionalPartOfPaidDays.getValue();
                            Double paidDays = new Double(padiDaysType) + new Double(paidDaysFractionalPart);

                            String unPaidDaysType = (String) ((Textbox) approveLeaveWindow.getFellow("unPaidDays")).getValue();
                            if (unPaidDaysType != ""){
                                Comboitem fractionalPartOfUnpaidDays = ((Combobox) approveLeaveWindow.getFellow("fractionalPartForUnpaidLeave")).getSelectedItem();
                                String unpaidDaysFractionalPart = (String) fractionalPartOfUnpaidDays.getValue();
                                unPaidDays = new Double(unPaidDaysType) + new Double(unpaidDaysFractionalPart);
                            }


                            String emplPosTypeIdOfParty = null;
                            String partyId = null;
                            GenericValue personGV = null;
                            try {
                                personGV = delegator.findOne("Person", UtilMisc.toMap("partyId", employeeId), false);
                            } catch (GenericEntityException e) {
                                e.printStackTrace();
                            }
                            ;
                            if ("LT_ADM_APPROVED".equals(leaveStatusId) || "LT_MGR_APPROVED".equals(leaveStatusId)) {
                                List employeeLeaveLimitList = null;
                                List employeeLeaveInfoPreviousYear =null;
                                GenericValue employeeLeaveInfoGv = null;
                                List employeeLeaveList = null;
                                Map<String,Timestamp> timeStampsOfPreviousYearAndCurrentYear = HrmsUtil.getCurrentAndPreviousYearStartAndEndDate();
                                try {
                                    employeeLeaveList = delegator.findByAnd("EmplLeave", UtilMisc.toMap("leaveId", leaveId));
                                } catch (GenericEntityException e1) {
                                    // TODO Auto-generated catch block
                                    e1.printStackTrace();
                                }
                                GenericValue employeeLeaveGv = EntityUtil.getFirst(employeeLeaveList);
                                partyId = (String) employeeLeaveGv.getString("partyId");
                                Double noOfDaysRequested = paidDays + unPaidDays; // (Double)
                                // employeeLeaveGv.getDouble("paidDays");
                                GenericValue emplPosTypeIdOfPartyGv = null;
                                try {
                                    emplPosTypeIdOfPartyGv = HumanResUtil.getEmplPositionForParty(partyId, delegator);
                                } catch (GenericEntityException e1) {
                                    // TODO Auto-generated catch block
                                    e1.printStackTrace();
                                }
                                if (emplPosTypeIdOfPartyGv != null) {
                                    emplPosTypeIdOfParty = emplPosTypeIdOfPartyGv.getString("emplPositionTypeId");
                                }
                                try {

                                    Double balanceLeave = 0.0;
                                    EntityCondition fromdateCondition = EntityCondition.makeCondition("beginYear", EntityOperator.GREATER_THAN_EQUAL_TO, timeStampsOfPreviousYearAndCurrentYear.get("previousYearStartDate"));
                                    EntityCondition thrudateCondition = EntityCondition.makeCondition("endYear", EntityOperator.LESS_THAN_EQUAL_TO, timeStampsOfPreviousYearAndCurrentYear.get("previousYearEndDate"));
                                    EntityCondition leaveTypeCondition = EntityCondition.makeCondition("leaveType",EntityOperator.EQUALS,leaveTypeId);
                                    EntityCondition partyIdCondition = EntityCondition.makeCondition("partyId",EntityOperator.EQUALS,partyId);
                                    List conditions =  Arrays.asList(partyIdCondition,leaveTypeCondition,fromdateCondition,thrudateCondition);
                                    employeeLeaveInfoPreviousYear = delegator.findList("EmployeeLeaveInfo", EntityCondition.makeCondition(conditions, EntityOperator.AND), null, null, null, false);
                                    employeeLeaveInfoGv = EntityUtil.getFirst(employeeLeaveInfoPreviousYear);
                                    if(UtilValidate.isNotEmpty(employeeLeaveInfoGv)){
                                        balanceLeave = employeeLeaveInfoGv.getDouble("balanceLeave");
                                    }
                                    if(balanceLeave == 0.0){
                                        fromdateCondition = EntityCondition.makeCondition("beginYear", EntityOperator.GREATER_THAN_EQUAL_TO, timeStampsOfPreviousYearAndCurrentYear.get("currentYearStartDate"));
                                        thrudateCondition = EntityCondition.makeCondition("endYear", EntityOperator.LESS_THAN_EQUAL_TO, timeStampsOfPreviousYearAndCurrentYear.get("currentYearEndDate"));
                                        leaveTypeCondition = EntityCondition.makeCondition("leaveTypeId",EntityOperator.EQUALS,leaveTypeId);
                                        EntityCondition empPostTypeCondition = EntityCondition.makeCondition("emplPosTypeId", EntityOperator.EQUALS, emplPosTypeIdOfParty);
                                        EntityCondition empTypeCondition = EntityCondition.makeCondition("employeeType",EntityOperator.EQUALS,personGV.getString("employeeType"));
                                        EntityCondition PostCategoryCondition = EntityCondition.makeCondition("positionCategory",EntityOperator.EQUALS,personGV.getString("positionCategory"));
                                         conditions =  Arrays.asList(empPostTypeCondition,leaveTypeCondition,empTypeCondition,PostCategoryCondition,fromdateCondition,thrudateCondition);
                                        employeeLeaveLimitList = delegator.findList("EmplLeaveLimit", EntityCondition.makeCondition(conditions, EntityOperator.AND), null, null, null, false);
                                        if(UtilValidate.isEmpty(employeeLeaveLimitList)){
                                            empPostTypeCondition = EntityCondition.makeCondition("emplPosTypeId", EntityOperator.EQUALS, " ");
                                            conditions =  Arrays.asList(empPostTypeCondition,leaveTypeCondition,empTypeCondition,PostCategoryCondition,fromdateCondition,thrudateCondition);
                                            employeeLeaveLimitList = delegator.findList("EmplLeaveLimit", EntityCondition.makeCondition(conditions, EntityOperator.AND), null, null, null, false);
                                        }
                                    }
                                } catch (GenericEntityException e1) {
                                    // TODO Auto-generated catch block
                                    e1.printStackTrace();
                                }
                                Double availedLeaves = 0.0;
                                Double balanceLeave = null;
                                Double availedLeave = 0.0;
                                Double leaveLimit = 0.0;
                                if (UtilValidate.isEmpty(employeeLeaveLimitList) && UtilValidate.isEmpty(employeeLeaveInfoGv)) {
                                    try {
                                        Messagebox.show("LeaveLimit Not Exist,Cannot be processed", "Error", 1, null);
                                    } catch (InterruptedException e) {
                                        // TODO Auto-generated catch block
                                        e.printStackTrace();
                                    }
                                    return;
                                }
                                if ("LT_ADM_APPROVED".equals(leaveStatusId)) {
                                    GenericValue employeeLeaveLimitGv =null;
                                    Timestamp beginYear =null;
                                    Timestamp endYear = null;

                                    if(UtilValidate.isNotEmpty(employeeLeaveInfoGv) && employeeLeaveInfoGv.getDouble("balanceLeave")!=0 && employeeLeaveInfoGv.getDouble("balanceLeave") < noOfDaysRequested){
                                        Double availedLeaveLimit =  noOfDaysRequested - employeeLeaveInfoGv.getDouble("balanceLeave");
                                        Map employeeLeaveInfoValues = UtilMisc.toMap("partyId",partyId,"leaveType",leaveTypeId,
                                                "beginYear",timeStampsOfPreviousYearAndCurrentYear.get("currentYearStartDate"),"endYear",timeStampsOfPreviousYearAndCurrentYear.get("currentYearEndDate"),
                                                "availedLeave",availedLeaveLimit,"positionType",emplPosTypeIdOfParty);
                                        createEmployeeLeaveInfo(employeeLeaveInfoValues,personGV.getString("employeeType"),personGV.getString("positionCategory"));
                                        noOfDaysRequested =  noOfDaysRequested - availedLeaveLimit;
                                    }

                                    if(UtilValidate.isNotEmpty(employeeLeaveLimitList)){
                                        employeeLeaveLimitGv = EntityUtil.getFirst(employeeLeaveLimitList);
                                        beginYear = employeeLeaveLimitGv.getTimestamp("beginYear");
                                        endYear = employeeLeaveLimitGv.getTimestamp("endYear");
                                        String partyLeaveId = employeeLeaveLimitGv.getString("partyLeaveId");
                                        List employeeLeaveInfoList = null;
                                        try {
                                            employeeLeaveInfoList = delegator.findByAnd("EmployeeLeaveInfo",
                                                    UtilMisc.toMap("partyId", partyId, "leaveType", leaveTypeId,"beginYear",beginYear,"endYear",endYear),null,false);
                                        } catch (GenericEntityException e1) {
                                            e1.printStackTrace();
                                        }
                                        employeeLeaveInfoGv = EntityUtil.getFirst(employeeLeaveInfoList);
                                    }
                                    if (UtilValidate.isNotEmpty(employeeLeaveInfoGv)) {
                                        beginYear = employeeLeaveInfoGv.getTimestamp("beginYear");
                                        endYear = employeeLeaveInfoGv.getTimestamp("endYear");
                                        availedLeave = employeeLeaveInfoGv.getDouble("availedLeave");
                                        balanceLeave = employeeLeaveInfoGv.getDouble("balanceLeave");
                                        leaveLimit = employeeLeaveInfoGv.getDouble("leaveLimit");
                                    }else{
                                        leaveLimit = employeeLeaveLimitGv.getDouble("numDays");

                                    }
                                    Double prevLeaveLimit = 0.0;
                                    if (balanceLeave == null) {
                                        prevLeaveLimit = availedLeave + 0.0;
                                    } else {
                                        prevLeaveLimit = availedLeave + balanceLeave;
                                    }
                                    Double diffDay = 0.0;
                                    diffDay = leaveLimit - prevLeaveLimit;
                                    if (availedLeave != null) {
                                        availedLeave = availedLeave + noOfDaysRequested;
                                    } else {
                                        availedLeave = availedLeaves + noOfDaysRequested;
                                    }

                                    if (balanceLeave == null) {
                                        balanceLeave = leaveLimit - noOfDaysRequested;
                                    } else {
                                        if (!(balanceLeave.equals(0.0) || balanceLeave < 0.0)) {
                                            balanceLeave = balanceLeave - noOfDaysRequested;
                                        }
                                        if (balanceLeave == 0.0 || balanceLeave < 0.0) {
                                            balanceLeave = 0.0;
                                        }
                                    }

                                    Map leaveInfoMap = UtilMisc.toMap("partyId", partyId, "leaveType", leaveTypeId, "positionType", emplPosTypeIdOfParty,
                                            "leaveLimit", leaveLimit, "availedLeave", availedLeave,
                                            "balanceLeave", balanceLeave,"beginYear",beginYear,"endYear",endYear
                                            );
                                    GenericValue leaveInfoGv = delegator.makeValue("EmployeeLeaveInfo", leaveInfoMap);
                                    try {
                                        delegator.createOrStore(leaveInfoGv);
                                    } catch (GenericEntityException e) {
                                        // TODO Auto-generated catch block
                                        e.printStackTrace();
                                    }
                                }

                            }

                            String mgrComment = (String) ((Textbox) approveLeaveWindow.getFellow("mgrComment")).getValue();
                            String adminComment = (String) ((Textbox) approveLeaveWindow.getFellow("adminComment")).getValue();

                            Map<String, String> processLeave = null;
                            processLeave = UtilMisc.toMap("userLogin", userLogin, "leaveId", leaveId, "partyId", employeeId, "fromDate", fromDate, "thruDate",
                                    thruDate, "leaveStatusId", leaveStatusId, "leaveTypeId", leaveTypeId, "paidDays", paidDays, "unpaidDays", unPaidDays,
                                    "statusType", statusType, "adminStatusType", adminStatus, "mgrComment", mgrComment, "adminComment", adminComment,
                                    "updatedBy", userLogin.getString("partyId"),"employeeType", personGV.getString("employeeType")
                                    , "positionCategory", personGV.getString("positionCategory"));
                            try {
                                dispatcher.runSync("processLeaveService", processLeave);
                            } catch (GenericServiceException e1) {
                                // TODO Auto-generated catch block
                                e1.printStackTrace();
                            }

                            try {
                                Events.postEvent("onClick", approveLeaveWindow.getFellow("refresh"), null);
                                Messagebox.show(Labels.getLabel("HRMS_LEAVEPROCESSMESSAGE"), Labels.getLabel("HRMS_SUCCESS"), 1, null);
                            } catch (InterruptedException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                    }
                });
    }

    public static void showLeaveWindow(Event event, GenericValue gv) throws SuspendNotAllowedException, InterruptedException, GenericEntityException {
        GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
        Component editLeaveWindow = event.getTarget();
        String employeeId = gv.getString("partyId");
        String mgrPositionID=gv.getString("mgrPositionId");
        String leaveId = gv.getString("leaveId");
        String statusId = gv.getString("leaveStatusId");
        Timestamp fromDateType = gv.getTimestamp("fromDate");
        Timestamp thruDateType = gv.getTimestamp("thruDate");
        String contactAddress = gv.getString("contactAddress");
        Long contactNumberType = gv.getLong("contactNumber");
        String contactNumber = contactNumberType.toString();
        String reasonForLeaveDes = gv.getString("description");
        String leaveType = gv.getString("leaveTypeId");
        Double paidDaysDouble = gv.getDouble("paidDays");
        Double unpaidDaysDouble = gv.getDouble("unpaidDays");
        Double totalNumberOfDaysLeave =0.0;
        if(unpaidDaysDouble!=null){
            totalNumberOfDaysLeave = paidDaysDouble+unpaidDaysDouble;
        }else{
            totalNumberOfDaysLeave = paidDaysDouble;
        }

        int totalNumberOfDaysLeaveWholePart = totalNumberOfDaysLeave.intValue();
        Double totalNumberOfDaysLeaveFractionalPart = totalNumberOfDaysLeave -  totalNumberOfDaysLeaveWholePart;

        Window win = (Window) Executions.createComponents("/zul/leaveManagement/updateLeave.zul", null, null);
        Label savedEmployeeId = (Label) win.getFellow("employeeId");
        savedEmployeeId.setValue(employeeId);
        Label savedLeaveId = (Label) win.getFellow("leaveId");
        savedLeaveId.setValue(leaveId);

        Label mgrPositionId = (Label) win.getFellow("mgrPositionId");
        mgrPositionId.setValue(mgrPositionID);

        Set<String> leaveTypeToDisplay = new HashSet();
        leaveTypeToDisplay.add("description");
        leaveTypeToDisplay.add("leaveTypeId");
        List<GenericValue> leaveTypes = delegator.findByAnd("Enumeration", UtilMisc.toMap("enumTypeId", "LEAVE_TYPE"));

        Listbox savedLeaveType = (Listbox) win.getFellow("leaveTypeId");
        Listitem leaveTypeItem = new Listitem();

        for (int i = 0; i < leaveTypes.size(); i++) {
            GenericValue leave = leaveTypes.get(i);
            String itemLabel = leave.getString("enumId");
            savedLeaveType.appendItemApi(leave.getString("description"), itemLabel);
            if (itemLabel.equals(leaveType)) {
                savedLeaveType.setSelectedIndex(i);
            }
        }
        Label savedLeaveStatus = (Label) win.getFellow("leaveStatusId");
        savedLeaveStatus.setValue(statusId);

        Datebox savedFromDate = (Datebox) win.getFellow("fromDate");
        savedFromDate.setValue(fromDateType);

        Datebox savedThruDate = (Datebox) win.getFellow("thruDate");
        savedThruDate.setValue(thruDateType);
        Textbox savedContactAddress = (Textbox) win.getFellow("contactAddress");
        savedContactAddress.setValue(contactAddress);
        Textbox savedContactNumber = (Textbox) win.getFellow("contactNumber");
        savedContactNumber.setValue(contactNumber);
        Textbox reasonForLeave = (Textbox) win.getFellow("reasonForLeave");
        reasonForLeave.setValue(reasonForLeaveDes);

        Textbox numberOfDaysLeave = (Textbox)win.getFellow("allocateleaveDays");
        numberOfDaysLeave.setValue(Long.toString(totalNumberOfDaysLeaveWholePart));
        Combobox numberOfDaysLeaveFractionalPart = (Combobox)win.getFellow("fractionalPartForLeave");
        numberOfDaysLeaveFractionalPart.setValue(totalNumberOfDaysLeaveFractionalPart.toString());

        win.doModal();

    }

    public static String updateLeave(Event event,boolean isSaveOnly) throws InterruptedException, GenericEntityException {
        Component editLeaveWindow = event.getTarget();
        GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
        GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
        LocalDispatcher dispatcher = HrmsInfrastructure.getDispatcher();//GenericDispatcher.getLocalDispatcher("default", delegator);
        String leaveId = ((Label) editLeaveWindow.getFellow("leaveId")).getValue();
        Map<String, String> d_leaveId = UtilMisc.toMap("leaveId", leaveId);
        String employeeId = ((Label) editLeaveWindow.getFellow("employeeId")).getValue();
        GenericValue person = delegator.findOne("Person",UtilMisc.toMap("partyId",employeeId),false);

        Listitem leaveTypeIdInput = ((Listbox) editLeaveWindow.getFellow("leaveTypeId")).getSelectedItem();
        String leaveType = (String) leaveTypeIdInput.getValue();
        String leaveStatus = ((Label) editLeaveWindow.getFellow("leaveStatusId")).getValue();
        String reasonForLeave = (String) ((Textbox) editLeaveWindow.getFellow("reasonForLeave")).getValue();
        Date fromDateInput = (Date) ((Datebox) editLeaveWindow.getFellow("fromDate")).getValue();
        Date thruDateInput = (Date) ((Datebox) editLeaveWindow.getFellow("thruDate")).getValue();

        Timestamp fromDate = new Timestamp(fromDateInput.getTime());
        Timestamp thruDate = new Timestamp(thruDateInput.getTime());
		/*int noOfDays = org.ofbiz.humanresext.util.HumanResUtil.getWorkingDaysBetween(fromDate, thruDate, delegator);
		if (noOfDays <= 0) {
			Messagebox.show("Leave Cannot be Updated on Non Working Days/Public Holidays", "Error", 1, null);
			return "error";
		}*/
        Long contactNumber = Long.valueOf (((Textbox) editLeaveWindow.getFellow("contactNumber")).getValue());
        String contactAddress = ((Textbox) editLeaveWindow.getFellow("contactAddress")).getValue();
        if (userLogin == null) {
            Messagebox.show("userLogin is null, cannot update.");
        }
        /*String d_employeeId = (String) userLogin.get("partyId");*/
        String d_employeeId = (UtilValidate.isNotEmpty(employeeId)) ? employeeId : (String) userLogin.get("partyId");
        GenericValue leaveGV = null;
        GenericValue leaveStatusGV = null;
        try {
            leaveGV = delegator.findByPrimaryKey("EmplLeave", d_leaveId);
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            Messagebox.show("Problems finding the Leave record [" + d_leaveId + "]");
        }

        if (!employeeId.equals((String) leaveGV.get("partyId"))) {
            Messagebox.show("Leave : " + leaveId + " was applied by different party" + leaveGV.get("partyId"));
        }
		/*Double lpaidDays = HumanResUtil.getWorkingDaysBetween(fromDate, thruDate, delegator).doubleValue();*/

        String numDaysType = ((org.zkoss.zul.api.Textbox) editLeaveWindow.getFellow("allocateleaveDays")).getValue();
        Comboitem fractionalPartOfDays = ((Combobox) editLeaveWindow.getFellow("fractionalPartForLeave")).getSelectedItem();
        String fractionalPartOfDay = (String) fractionalPartOfDays.getValue();
        Double lpaidDays = new Double(numDaysType) + new Double(fractionalPartOfDay);


        GenericValue emplPositionGv = HumanResUtil.getEmplPositionForParty(d_employeeId, delegator);
        String emplPosType = emplPositionGv == null ? null : emplPositionGv.getString("emplPositionTypeId");
        String positionCategory = person.getString("positionCategory");
        String employeeType = person.getString("employeeType");

        Double balanceLeave = 0.0;
        Map<String,Timestamp> timeStampsOfPreviousYearAndCurrentYear = HrmsUtil.getCurrentAndPreviousYearStartAndEndDate();
        EntityCondition fromdateCondition = EntityCondition.makeCondition("beginYear", EntityOperator.GREATER_THAN_EQUAL_TO, timeStampsOfPreviousYearAndCurrentYear.get("previousYearStartDate"));
        EntityCondition thrudateCondition = EntityCondition.makeCondition("endYear", EntityOperator.LESS_THAN_EQUAL_TO, timeStampsOfPreviousYearAndCurrentYear.get("previousYearEndDate"));
        EntityCondition leaveTypeCondition = EntityCondition.makeCondition("leaveType",EntityOperator.EQUALS,leaveType);
        EntityCondition partyIdCondition = EntityCondition.makeCondition("partyId",EntityOperator.EQUALS,employeeId);
        List conditions =  Arrays.asList(partyIdCondition,leaveTypeCondition,fromdateCondition,thrudateCondition);
        List employeeLeaveInfoPreviousYear = delegator.findList("EmployeeLeaveInfo", EntityCondition.makeCondition(conditions, EntityOperator.AND), null, null, null, false);
        GenericValue employeeLeaveInfoGv = EntityUtil.getFirst(employeeLeaveInfoPreviousYear);
        if(UtilValidate.isNotEmpty(employeeLeaveInfoGv)){
            balanceLeave = employeeLeaveInfoGv.getDouble("balanceLeave");
        }
        List<GenericValue> emplLeaveLimitList =null;
        if(balanceLeave ==0.0){
            EntityCondition posTypeCondition = EntityCondition.makeCondition("emplPosTypeId", EntityOperator.EQUALS, emplPosType);
            leaveTypeCondition = EntityCondition.makeCondition("leaveTypeId", EntityOperator.EQUALS, leaveType);
            EntityCondition positionCategoryCondition = EntityCondition.makeCondition("positionCategory", EntityOperator.EQUALS, positionCategory);
            EntityCondition employeeTypeCondition = EntityCondition.makeCondition("employeeType",EntityOperator.EQUALS,employeeType);
            fromdateCondition = EntityCondition.makeCondition("beginYear", EntityOperator.GREATER_THAN_EQUAL_TO, timeStampsOfPreviousYearAndCurrentYear.get("currentYearStartDate"));
            thrudateCondition = EntityCondition.makeCondition("endYear", EntityOperator.LESS_THAN_EQUAL_TO, timeStampsOfPreviousYearAndCurrentYear.get("currentYearEndDate"));
            EntityCondition condition1 = EntityCondition.makeCondition(posTypeCondition, EntityOperator.AND, leaveTypeCondition);
            List conditionList = Arrays.asList(new Object[]{posTypeCondition, leaveTypeCondition, positionCategoryCondition, employeeTypeCondition, fromdateCondition, thrudateCondition});
            emplLeaveLimitList = delegator.findList("EmplLeaveLimit", EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, null, null, false);
            if(UtilValidate.isEmpty(emplLeaveLimitList)){
                conditionList = Arrays.asList(new Object[]{leaveTypeCondition,positionCategoryCondition,employeeTypeCondition,fromdateCondition,thrudateCondition});
                emplLeaveLimitList = delegator.findList("EmplLeaveLimit", EntityCondition.makeCondition(conditionList,EntityOperator.AND), null, null, null, false);
            }
        }

        if (UtilValidate.isEmpty(emplLeaveLimitList) && UtilValidate.isEmpty(employeeLeaveInfoGv)) {
            Messagebox.show("Leave Limit Does Not Exist, Cannot Apply", "Error", 1, null);
            return "error";
        }
        Timestamp beginYear = null;
        Timestamp endYear = null;
        GenericValue emplLeaveLimitGv = null;
        if(UtilValidate.isNotEmpty(emplLeaveLimitList)){
            emplLeaveLimitGv = EntityUtil.getFirst(emplLeaveLimitList);
            beginYear =  emplLeaveLimitGv.getTimestamp("beginYear");
            endYear =  emplLeaveLimitGv.getTimestamp("endYear");
            List emplLeaveInfo = delegator.findByAnd("EmployeeLeaveInfo", UtilMisc.toMap("partyId", employeeId, "leaveType", leaveType, "beginYear", beginYear, "endYear", endYear), null, false);
            employeeLeaveInfoGv = EntityUtil.getFirst(emplLeaveInfo);
        }

        List<GenericValue> emplLeaveGvList = delegator.findByAnd("EmplLeave",UtilMisc.toMap("partyId",employeeId,"leaveTypeId",leaveType,"leaveStatusId","LT_SUBMITTED"),null,false);
        Double totalLeaveSubmitted = 0.0;
        if(UtilValidate.isNotEmpty(emplLeaveGvList)){
            for(GenericValue emplLeaveGv:emplLeaveGvList){
                totalLeaveSubmitted = totalLeaveSubmitted + emplLeaveGv.getDouble("paidDays");
            }
        }
        if(UtilValidate.isNotEmpty(employeeLeaveInfoGv)){

            Map currentAndPreviousYearBalance = getCurrentAndPreviousYearLeaveLimit(leaveType,employeeType, positionCategory, emplPosType, employeeId);
            Double leaveDaysEmployeeCanApply =getTotalBalance(currentAndPreviousYearBalance)-totalLeaveSubmitted;
            /*if(!HrmsUtil.isPreviousYearLeave(employeeLeaveInfoGv)){
                leaveDaysEmployeeCanApply= employeeLeaveInfoGv.getDouble("balanceLeave") - totalLeaveSubmitted;
            }else{
                EntityCondition posTypeCondition = EntityCondition.makeCondition("emplPosTypeId", EntityOperator.EQUALS, emplPosType);
                leaveTypeCondition = EntityCondition.makeCondition("leaveTypeId", EntityOperator.EQUALS, leaveType);
                EntityCondition positionCategoryCondition = EntityCondition.makeCondition("positionCategory", EntityOperator.EQUALS, positionCategory);
                EntityCondition employeeTypeCondition = EntityCondition.makeCondition("employeeType",EntityOperator.EQUALS,employeeType);
                fromdateCondition = EntityCondition.makeCondition("beginYear", EntityOperator.GREATER_THAN_EQUAL_TO, timeStampsOfPreviousYearAndCurrentYear.get("currentYearStartDate"));
                thrudateCondition = EntityCondition.makeCondition("endYear", EntityOperator.LESS_THAN_EQUAL_TO, timeStampsOfPreviousYearAndCurrentYear.get("currentYearEndDate"));
                List conditionList = Arrays.asList(new Object[]{posTypeCondition, leaveTypeCondition, positionCategoryCondition, employeeTypeCondition, fromdateCondition, thrudateCondition});
                emplLeaveLimitList = delegator.findList("EmplLeaveLimit", EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, null, null, false);
                if(UtilValidate.isEmpty(emplLeaveLimitList)){
                    conditionList = Arrays.asList(new Object[]{leaveTypeCondition,positionCategoryCondition,employeeTypeCondition,fromdateCondition,thrudateCondition});
                    emplLeaveLimitList = delegator.findList("EmplLeaveLimit", EntityCondition.makeCondition(conditionList,EntityOperator.AND), null, null, null, false);
                }
                emplLeaveLimitGv = EntityUtil.getFirst(emplLeaveLimitList);
                leaveDaysEmployeeCanApply =  employeeLeaveInfoGv.getDouble("balanceLeave") + emplLeaveLimitGv.getDouble("numDays");
            }*/

            if(leaveDaysEmployeeCanApply.equals(0.0) || lpaidDays > leaveDaysEmployeeCanApply){
                    Messagebox.show("You do not have sufficient balance leave to apply ", "Error", 1, null);
                    return "error";

            }
            if(lpaidDays>leaveDaysEmployeeCanApply){
                Messagebox.show("You have only "+leaveDaysEmployeeCanApply+" Days leave to apply", "Error", 1, null);
                return "error";
            }
        }else{
            if(emplLeaveLimitGv.getDouble("numDays")<lpaidDays){
                Messagebox.show("You cannot apply for leave more than leave limit", "Error", 1, null);
                return "error";
            }
        }
        /*if (UtilValidate.isEmpty(emplLeaveLimitList)) {
            Messagebox.show("Leave Limit Does Not Exist, Cannot Apply", "Warning", 1, null);
            return "error";
        }*/
        /*EntityCondition fromdateCondition = EntityCondition.makeCondition("beginYear", EntityOperator.LESS_THAN_EQUAL_TO, fromDate);
        EntityCondition thrudateCondition = EntityCondition.makeCondition("endYear", EntityOperator.GREATER_THAN_EQUAL_TO, thruDate);
        EntityCondition condition2 = EntityCondition.makeCondition(fromdateCondition, EntityOperator.AND, thrudateCondition);
        EntityCondition makecoCondition = EntityCondition.makeCondition(condition1, EntityOperator.AND, condition2);
        List<GenericValue> emplFiscalYearLeaveLimitList = delegator.findList("EmplLeaveLimit", makecoCondition, null, null, null, false);
        if(UtilValidate.isEmpty(emplFiscalYearLeaveLimitList)){
            conditionList = Arrays.asList(new Object[]{condition2,leaveTypeCondition,positionCategoryCondition,employeeTypeCondition});
            emplFiscalYearLeaveLimitList = delegator.findList("EmplLeaveLimit", EntityCondition.makeCondition(conditionList,EntityOperator.AND), null, null, null, false);
        }
        if (UtilValidate.isEmpty(emplFiscalYearLeaveLimitList)) {
            Messagebox.show("Leave can not be applied beyond the allocated time period", "Error", 1, null);
            return "error";
        }*/
        EntityCondition employmentCondition1 = EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, userLogin.getString("partyId"));
        EntityCondition employmentCondition2 = EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, fromDate);
        EntityCondition employmentCondition = EntityCondition.makeCondition(employmentCondition1, EntityOperator.AND, employmentCondition2);
        List<GenericValue> employmentList = delegator.findList("Employment", employmentCondition, null, null, null, false);
        if (UtilValidate.isEmpty(employmentList)) {
            Messagebox.show("Leave Can not Be Applied Behind The Joining Date", "Error", 1, null);
            return "error";
        }

        Map<String, String> updateLeave = null;
        Map result = null;
        updateLeave = UtilMisc.toMap("partyId", employeeId, "leaveId", leaveId, "leaveTypeId", leaveType, "leaveStatusId", leaveStatus, "fromDate", fromDate,
                "thruDate", thruDate, "contactAddress", contactAddress, "contactNumber", contactNumber, "paidDays", lpaidDays, "description", reasonForLeave,
                "updatedBy", d_employeeId);
        leaveGV.putAll(updateLeave);
        try {
            leaveGV.store();
        } catch (GenericEntityException e) {
            Debug.logError(e, module);

        }
        if(isSaveOnly){
            result = ServiceUtil.returnSuccess("Employee Leave updated");
            result.put("leaveId", leaveId);
            Events.postEvent("onClick$searchButton", editLeaveWindow.getPage().getFellow("searchPanel"), null);
            Messagebox.show(Labels.getLabel("HRMS_LEAVEUPDATESUCCESSMESSAGE"), Labels.getLabel("HRMS_SUCCESS"), 1, null);
        }
        return "success";
    }

    public static void deleteLeave(Event event, GenericValue gv, final Button btn) throws GenericEntityException, InterruptedException {

        final Component searchPanel = event.getTarget();
        final String leaveId = gv.getString("leaveId");
        final GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");

        Messagebox.show("Do You Want To Delete this Record?", "Warning", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION, new EventListener() {
            public void onEvent(Event evt) {
                if ("onYes".equals(evt.getName())) {

                    try {
                        delegator.removeByAnd("EmplLeave", UtilMisc.toMap("leaveId", leaveId));
                        Events.postEvent("onClick", btn, null);

                    } catch (GenericEntityException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    try {
                        Messagebox.show(Labels.getLabel("HRMS_LEAVEDELETEMESSAGE"), Labels.getLabel("HRMS_SUCCESS"), 1, null);
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

    public static void updateUserLogins(Event event) throws GenericServiceException {

        GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
        GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
        LocalDispatcher dispatcher = HrmsInfrastructure.getDispatcher();//GenericDispatcher.getLocalDispatcher("default", delegator);
        dispatcher.runSync("disableUserLogin", null);

    }

    public static GenericValue getEmployeeLeaveInfo(String partyId,String leaveTypeId,Timestamp beginYear,Timestamp endYear) throws GenericEntityException {
        GenericDelegator delegator = HrmsInfrastructure.getDelegator();
        EntityCondition beginYearCond =  EntityCondition.makeCondition("beginYear",EntityOperator.GREATER_THAN_EQUAL_TO,beginYear);
        EntityCondition endYearCond =  EntityCondition.makeCondition("endYear",EntityOperator.LESS_THAN_EQUAL_TO,endYear);
        EntityCondition partyIdCond =  EntityCondition.makeCondition("partyId",EntityOperator.EQUALS,partyId);
        EntityCondition leaveTypeCond =  EntityCondition.makeCondition("leaveType",EntityOperator.EQUALS,leaveTypeId);
        List conditions = Arrays.asList(beginYearCond,endYearCond,partyIdCond,leaveTypeCond);
        List employeeLeaveInfoList =  delegator.findList("EmployeeLeaveInfo", EntityCondition.makeCondition(conditions, EntityOperator.AND),null,null,null,false);
        GenericValue employeeLeaveInfo = EntityUtil.getFirst(employeeLeaveInfoList);
        GenericValue previousYearEmployeeLeaveInfo = getPreviousLeaveFromEmployeeLeaveInfo(partyId,leaveTypeId);

        if(UtilValidate.isNotEmpty(employeeLeaveInfo) && UtilValidate.isNotEmpty(previousYearEmployeeLeaveInfo)){
            if(previousYearEmployeeLeaveInfo.getDouble("balanceLeave")>0){
                return previousYearEmployeeLeaveInfo;
            }
        }
        if(UtilValidate.isNotEmpty(employeeLeaveInfo) && UtilValidate.isEmpty(previousYearEmployeeLeaveInfo)){
            return employeeLeaveInfo;
        }
        if(UtilValidate.isNotEmpty(previousYearEmployeeLeaveInfo) && UtilValidate.isEmpty(employeeLeaveInfo)){
            if(previousYearEmployeeLeaveInfo.getDouble("balanceLeave")>0) {
                return previousYearEmployeeLeaveInfo;
            }
        }
        if(UtilValidate.isEmpty(employeeLeaveInfo) && UtilValidate.isEmpty(previousYearEmployeeLeaveInfo)){
            return null;
        }
        return employeeLeaveInfo;
    }
    public static void updatePreviousYearEmployeeLeaveInfo(String partyId,String leaveTypeId,Timestamp beginYear) throws GenericEntityException{
        GenericDelegator delegator = HrmsInfrastructure.getDelegator();
        EntityCondition partyIdCond = EntityCondition.makeCondition("partyId",EntityOperator.EQUALS,partyId);
        EntityCondition leaveTypeCond = EntityCondition.makeCondition("leaveType",EntityOperator.EQUALS,leaveTypeId);
        EntityCondition yearCond = EntityCondition.makeCondition("endYear",EntityOperator.LESS_THAN_EQUAL_TO,beginYear);
        EntityCondition isPreviousYearCond = EntityCondition.makeCondition("isPreviousYearLeave","false");
        List conditions = Arrays.asList(partyIdCond,leaveTypeCond,yearCond,isPreviousYearCond);
        List<GenericValue> employeeLeaveInfoList = delegator.findList("EmployeeLeaveInfo", EntityCondition.makeCondition(conditions, EntityOperator.AND), null, Arrays.asList("endYear DESC"), null, false);
        for(GenericValue employeeLeaveInfoGv:employeeLeaveInfoList){
            employeeLeaveInfoGv.put("isPreviousYearLeave","true");
            delegator.store(employeeLeaveInfoGv);
        }
    }
    public static GenericValue getPreviousLeaveFromEmployeeLeaveInfo(String partyId,String leaveTypeId) throws GenericEntityException {
        GenericDelegator delegator = HrmsInfrastructure.getDelegator();
        Map timestamps = HrmsUtil.getCurrentAndPreviousYearStartAndEndDate();
        EntityCondition partyIdCond = EntityCondition.makeCondition("partyId",EntityOperator.EQUALS,partyId);
        EntityCondition leaveTypeCond = EntityCondition.makeCondition("leaveType",EntityOperator.EQUALS,leaveTypeId);
        EntityCondition endYearCond = EntityCondition.makeCondition("endYear",EntityOperator.LESS_THAN_EQUAL_TO,timestamps.get("previousYearEndDate"));
        EntityCondition beginYearCond = EntityCondition.makeCondition("beginYear",EntityOperator.GREATER_THAN_EQUAL_TO,timestamps.get("previousYearStartDate"));
        List conditions = Arrays.asList(partyIdCond,leaveTypeCond,endYearCond,beginYearCond);
        List<GenericValue> employeeLeaveInfoList = delegator.findList("EmployeeLeaveInfo", EntityCondition.makeCondition(conditions, EntityOperator.AND), null, Arrays.asList("endYear DESC"), null, false);
        return EntityUtil.getFirst(employeeLeaveInfoList);
    }
    public static GenericValue getCurrentLeaveFromEmployeeLeaveInfo(String partyId,String leaveTypeId) throws GenericEntityException {
        GenericDelegator delegator = HrmsInfrastructure.getDelegator();
        Map timestamps = HrmsUtil.getCurrentAndPreviousYearStartAndEndDate();
        EntityCondition partyIdCond = EntityCondition.makeCondition("partyId",EntityOperator.EQUALS,partyId);
        EntityCondition leaveTypeCond = EntityCondition.makeCondition("leaveType",EntityOperator.EQUALS,leaveTypeId);
        EntityCondition endYearCond = EntityCondition.makeCondition("endYear",EntityOperator.LESS_THAN_EQUAL_TO,timestamps.get("currentYearEndDate"));
        EntityCondition beginYearCond = EntityCondition.makeCondition("beginYear",EntityOperator.GREATER_THAN_EQUAL_TO,timestamps.get("currentYearStartDate"));
        List conditions = Arrays.asList(partyIdCond,leaveTypeCond,endYearCond,beginYearCond);
        List<GenericValue> employeeLeaveInfoList = delegator.findList("EmployeeLeaveInfo", EntityCondition.makeCondition(conditions, EntityOperator.AND), null, Arrays.asList("endYear DESC"), null, false);
        return EntityUtil.getFirst(employeeLeaveInfoList);
    }

    public static GenericValue createEmployeeLeaveInfoWithPreviousYearLeave(String leaveType,Timestamp beginYear,String employeeType,String positionCategory,String employeePositionTypeId,String partyId) throws GenericEntityException {

       GenericDelegator delegator = HrmsInfrastructure.getDelegator();
        EntityCondition andConditions = EntityCondition.makeCondition(UtilMisc.toMap("leaveTypeId",leaveType,"employeeType",employeeType,
                "positionCategory",positionCategory,"emplPosTypeId",employeePositionTypeId),EntityOperator.AND);
        EntityCondition dateCond = EntityCondition.makeCondition("endYear",EntityOperator.LESS_THAN_EQUAL_TO,beginYear);
        List conditions = Arrays.asList(andConditions,dateCond);
        List emplLeaveLimit = delegator.findList("EmplLeaveLimit", EntityCondition.makeCondition(conditions,EntityOperator.AND),null,null,null,false);
        if(UtilValidate.isEmpty(emplLeaveLimit)){
             andConditions = EntityCondition.makeCondition(UtilMisc.toMap("leaveTypeId",leaveType,"employeeType",employeeType,
                    "positionCategory",positionCategory),EntityOperator.AND);
             conditions = Arrays.asList(andConditions,dateCond);
            emplLeaveLimit = delegator.findList("EmplLeaveLimit", EntityCondition.makeCondition(conditions,EntityOperator.AND),null,null,null,false);
        }
        emplLeaveLimit = EntityUtil.orderBy(emplLeaveLimit,UtilMisc.toList("beginYear DESC"));
        GenericValue emplLeaveLimitGv = EntityUtil.getFirst(emplLeaveLimit);
        if(UtilValidate.isNotEmpty(emplLeaveLimitGv)){
            Map employeeLeaveInfoValues = UtilMisc.toMap("partyId",partyId,"leaveType",leaveType
                    ,"positionType",employeePositionTypeId,
                    "beginYear",emplLeaveLimitGv.getTimestamp("beginYear"),"endYear",emplLeaveLimitGv.getTimestamp("endYear"));
            List previouslyExistingEmployeeLeaveInfo = delegator.findByAnd("EmployeeLeaveInfo", employeeLeaveInfoValues, null, false);
            if(UtilValidate.isEmpty(previouslyExistingEmployeeLeaveInfo)){
                employeeLeaveInfoValues.put("leaveLimit",emplLeaveLimitGv.getDouble("numDays"));
                employeeLeaveInfoValues.put("availedLeave",0.0);
                employeeLeaveInfoValues.put("balanceLeave",emplLeaveLimitGv.getDouble("numDays"));
                GenericValue employeeLeaveInfo = delegator.makeValue("EmployeeLeaveInfo", employeeLeaveInfoValues);
                delegator.create(employeeLeaveInfo);
                return employeeLeaveInfo;
            }
        }
        return null;
    }

    public static void createEmployeeLeaveInfo(Map employeeLeaveInfoValues,String employeeType,String positionCategory){
        GenericDelegator delegator = HrmsInfrastructure.getDelegator();
        if(isEmployeeLeaveInfoAlreadyExists(employeeLeaveInfoValues)){
            return;
        }
        EntityCondition fromdateCondition = EntityCondition.makeCondition("beginYear", EntityOperator.GREATER_THAN_EQUAL_TO, employeeLeaveInfoValues.get("beginYear"));
        EntityCondition thrudateCondition = EntityCondition.makeCondition("endYear", EntityOperator.LESS_THAN_EQUAL_TO, employeeLeaveInfoValues.get("endYear"));
        EntityCondition leaveTypeCondition = EntityCondition.makeCondition("leaveTypeId",EntityOperator.EQUALS,employeeLeaveInfoValues.get("leaveType"));
        EntityCondition empPostTypeCondition = EntityCondition.makeCondition("emplPosTypeId", EntityOperator.EQUALS, employeeLeaveInfoValues.get("positionType") );
        EntityCondition empTypeCondition = EntityCondition.makeCondition("employeeType",EntityOperator.EQUALS,employeeType);
        EntityCondition PostCategoryCondition = EntityCondition.makeCondition("positionCategory",EntityOperator.EQUALS,positionCategory );
        List conditions =  Arrays.asList(empPostTypeCondition,leaveTypeCondition,empTypeCondition,PostCategoryCondition,fromdateCondition,thrudateCondition);
        try {
            List employeeLeaveLimitList = delegator.findList("EmplLeaveLimit", EntityCondition.makeCondition(conditions, EntityOperator.AND), null, null, null, false);
            if(UtilValidate.isEmpty(employeeLeaveLimitList)){
                empPostTypeCondition = EntityCondition.makeCondition("emplPosTypeId", EntityOperator.EQUALS, " ");
                conditions =  Arrays.asList(empPostTypeCondition,leaveTypeCondition,empTypeCondition,PostCategoryCondition,fromdateCondition,thrudateCondition);
                employeeLeaveLimitList = delegator.findList("EmplLeaveLimit", EntityCondition.makeCondition(conditions, EntityOperator.AND), null, null, null, false);
            }
            GenericValue employeeLeaveLimitGv = EntityUtil.getFirst(employeeLeaveLimitList);
            if(UtilValidate.isNotEmpty(employeeLeaveLimitGv)){
                Double leaveLimit = employeeLeaveLimitGv.getDouble("numDays");
                Double balanceLeave = leaveLimit - (Double)employeeLeaveInfoValues.get("availedLeave") ;
                employeeLeaveInfoValues.put("leaveLimit",leaveLimit);
                employeeLeaveInfoValues.put("balanceLeave",balanceLeave);
                employeeLeaveInfoValues.put("endYear", UtilDateTime.getDayStart((Timestamp)employeeLeaveInfoValues.get("endYear")));
                GenericValue employeeLeaveInfo = delegator.makeValue("EmployeeLeaveInfo",employeeLeaveInfoValues);
                delegator.createOrStore(employeeLeaveInfo);
            }
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
    }

    public static boolean isEmployeeLeaveInfoAlreadyExists(Map employeeLeaveInfoValues){
        GenericDelegator delegator = HrmsInfrastructure.getDelegator();
        EntityCondition fromdateCondition = EntityCondition.makeCondition("beginYear", EntityOperator.GREATER_THAN_EQUAL_TO, employeeLeaveInfoValues.get("beginYear"));
        EntityCondition thrudateCondition = EntityCondition.makeCondition("endYear", EntityOperator.LESS_THAN_EQUAL_TO, employeeLeaveInfoValues.get("endYear"));
        EntityCondition leaveTypeCondition = EntityCondition.makeCondition("leaveType",EntityOperator.EQUALS,employeeLeaveInfoValues.get("leaveType"));
        EntityCondition empPostTypeCondition = EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, employeeLeaveInfoValues.get("partyId") );
        List conditions =  Arrays.asList(empPostTypeCondition,leaveTypeCondition,fromdateCondition,thrudateCondition);
        try {
           List employeeLeaveInfo  = delegator.findList("EmployeeLeaveInfo", EntityCondition.makeCondition(conditions, EntityOperator.AND), null, null, null, false);
            GenericValue employeeLeaveInfoGv = EntityUtil.getFirst(employeeLeaveInfo);
            if(UtilValidate.isNotEmpty(employeeLeaveInfoGv)){
                Double balanceLeave = employeeLeaveInfoGv.getDouble("balanceLeave") -(Double)employeeLeaveInfoValues.get("availedLeave");
                Double availedLeave = (Double)employeeLeaveInfoValues.get("availedLeave")+employeeLeaveInfoGv.getDouble("availedLeave");
                employeeLeaveInfoGv.put("availedLeave",availedLeave);
                employeeLeaveInfoGv.put("balanceLeave",balanceLeave);
                employeeLeaveInfoGv.put("endYear", UtilDateTime.getDayStart((Timestamp)employeeLeaveInfoValues.get("endYear")));
                delegator.store(employeeLeaveInfoGv);
                return true;
            }

        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static Map getCurrentAndPreviousYearLeaveLimit(String leaveType,String employeeType,String positionCategory,String employeePositionTypeId,String partyId) throws GenericEntityException {
        Map currentAndPreviousYearStartAndEndDates = HrmsUtil.getCurrentAndPreviousYearStartAndEndDate();
        Map currentAndPreviousYearLeaveLimit = new HashMap();
        GenericValue previousYearEmployeeLeaveInfo = getPreviousLeaveFromEmployeeLeaveInfo(partyId,leaveType);
        GenericDelegator delegator = HrmsInfrastructure.getDelegator();
        EntityCondition beginYearCond =  EntityCondition.makeCondition("beginYear",EntityOperator.GREATER_THAN_EQUAL_TO,currentAndPreviousYearStartAndEndDates.get("currentYearStartDate"));
        EntityCondition endYearCond =  EntityCondition.makeCondition("endYear",EntityOperator.LESS_THAN_EQUAL_TO,currentAndPreviousYearStartAndEndDates.get("currentYearEndDate"));
        EntityCondition partyIdCond =  EntityCondition.makeCondition("partyId",EntityOperator.EQUALS,partyId);
        EntityCondition leaveTypeCond =  EntityCondition.makeCondition("leaveType",EntityOperator.EQUALS,leaveType);
        List conditions = Arrays.asList(beginYearCond,endYearCond,partyIdCond,leaveTypeCond);
        List employeeLeaveInfoList =  delegator.findList("EmployeeLeaveInfo", EntityCondition.makeCondition(conditions, EntityOperator.AND),null,null,null,false);
        GenericValue employeeLeaveInfo = EntityUtil.getFirst(employeeLeaveInfoList);
        if(UtilValidate.isEmpty(employeeLeaveInfo)){
            GenericValue employeeLeaveLimit = getEmployeeLeaveLimit(leaveType,employeeType,positionCategory,employeePositionTypeId);
            currentAndPreviousYearLeaveLimit.put("currentYearLeaveLimit",employeeLeaveLimit.getDouble("numDays"));
            currentAndPreviousYearLeaveLimit.put("currentYearAvailedLeave",0.0);
        }else{
            currentAndPreviousYearLeaveLimit.put("currentYearLeaveLimit",employeeLeaveInfo.getDouble("leaveLimit"));
            currentAndPreviousYearLeaveLimit.put("currentYearAvailedLeave",employeeLeaveInfo.getDouble("availedLeave"));
        }
        if(UtilValidate.isNotEmpty(previousYearEmployeeLeaveInfo)){
            currentAndPreviousYearLeaveLimit.put("previousYearLeaveLimit",previousYearEmployeeLeaveInfo.getDouble("leaveLimit"));
            currentAndPreviousYearLeaveLimit.put("previousYearAvailedLeave",previousYearEmployeeLeaveInfo.getDouble("availedLeave"));
        }else {
            currentAndPreviousYearLeaveLimit.put("previousYearLeaveLimit",0.0);
            currentAndPreviousYearLeaveLimit.put("previousYearAvailedLeave",0.0);
        }
        return currentAndPreviousYearLeaveLimit;
    }

    public static Double getTotalBalance(Map currentAndPreviousYearLeaveLimit){
        Double currentYearBalance = (Double)currentAndPreviousYearLeaveLimit.get("currentYearLeaveLimit")-(Double)currentAndPreviousYearLeaveLimit.get("currentYearAvailedLeave");
        Double previousYearBalance = (Double)currentAndPreviousYearLeaveLimit.get("previousYearLeaveLimit")-(Double)currentAndPreviousYearLeaveLimit.get("previousYearAvailedLeave");
        return  currentYearBalance+previousYearBalance;
    }

    public static GenericValue getEmployeeLeaveLimit(String leaveType,String employeeType,String positionCategory,String employeePositionTypeId){
        GenericDelegator delegator = HrmsInfrastructure.getDelegator();
        GenericValue employeeLeaveLimitGv = null;
        Map currentAndPreviousYearStartAndEndDates = HrmsUtil.getCurrentAndPreviousYearStartAndEndDate();
        EntityCondition fromdateCondition = EntityCondition.makeCondition("beginYear", EntityOperator.GREATER_THAN_EQUAL_TO, currentAndPreviousYearStartAndEndDates.get("currentYearStartDate"));
        EntityCondition thrudateCondition = EntityCondition.makeCondition("endYear", EntityOperator.LESS_THAN_EQUAL_TO, currentAndPreviousYearStartAndEndDates.get("currentYearEndDate"));
        EntityCondition leaveTypeCondition = EntityCondition.makeCondition("leaveTypeId",EntityOperator.EQUALS,leaveType);
        EntityCondition empPostTypeCondition = EntityCondition.makeCondition("emplPosTypeId", EntityOperator.EQUALS, employeePositionTypeId );
        EntityCondition empTypeCondition = EntityCondition.makeCondition("employeeType",EntityOperator.EQUALS,employeeType);
        EntityCondition PostCategoryCondition = EntityCondition.makeCondition("positionCategory",EntityOperator.EQUALS,positionCategory );
        List conditions =  Arrays.asList(empPostTypeCondition,leaveTypeCondition,empTypeCondition,PostCategoryCondition,fromdateCondition,thrudateCondition);
        try {
            List employeeLeaveLimitList = delegator.findList("EmplLeaveLimit", EntityCondition.makeCondition(conditions, EntityOperator.AND), null, null, null, false);
            if(UtilValidate.isEmpty(employeeLeaveLimitList)){
                empPostTypeCondition = EntityCondition.makeCondition("emplPosTypeId", EntityOperator.EQUALS, " ");
                conditions =  Arrays.asList(empPostTypeCondition,leaveTypeCondition,empTypeCondition,PostCategoryCondition,fromdateCondition,thrudateCondition);
                employeeLeaveLimitList = delegator.findList("EmplLeaveLimit", EntityCondition.makeCondition(conditions, EntityOperator.AND), null, null, null, false);
                employeeLeaveLimitGv = EntityUtil.getFirst(employeeLeaveLimitList);
            }else{
                employeeLeaveLimitGv = EntityUtil.getFirst(employeeLeaveLimitList);
            }
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        return employeeLeaveLimitGv;
    }
}
