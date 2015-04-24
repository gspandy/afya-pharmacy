package com.ndz.controller;

/*
 * 
 * @author samir
 * */

import com.ndz.component.party.LeaveTypeRenderer;
import com.ndz.zkoss.util.HrmsInfrastructure;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.LocalDispatcher;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.*;
import org.zkoss.zul.api.Textbox;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.*;

public class ConfigureLeaveManagementController extends GenericForwardComposer {

    public void doAfterCompose( Component allocateLeaveWindow ) throws Exception {
        super.doAfterCompose(allocateLeaveWindow);
        GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
        Set<String> leaveTypeToDisplay = new HashSet();
        leaveTypeToDisplay.add("description");
        leaveTypeToDisplay.add("leaveTypeId");

        List<GenericValue> leaveType = delegator.findByAnd("Enumeration", UtilMisc.toMap("enumTypeId", "LEAVE_TYPE"));
        leaveType.add(0, null);
        SimpleListModel leaveList = new SimpleListModel(leaveType);

        Listbox applyLeaveType = (Listbox) allocateLeaveWindow.getFellow("applyLeaveType");
        applyLeaveType.setModel(leaveList);
        applyLeaveType.setItemRenderer(new LeaveTypeRenderer());
    }

    @SuppressWarnings("unchecked")
    public void onEvent(Event event) {
        try {
            System.out.println("***********Allocate Event Called");
            Component allocateLeaveWindow = event.getTarget();
            GenericValue userLogin = (GenericValue) Executions.getCurrent()
                    .getDesktop().getSession().getAttribute("userLogin");
            GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
            Listitem leaveTypeInput = ((Listbox) allocateLeaveWindow
                    .getFellow("applyLeaveType")).getSelectedItem();
            String leaveType = (String) leaveTypeInput.getValue();

            Listitem emplPosTypeInput = ((Listbox) allocateLeaveWindow
                    .getFellow("positionType")).getSelectedItem();
            String emplPosType = " ";
            if(UtilValidate.isNotEmpty(emplPosTypeInput))
                emplPosType = (String) emplPosTypeInput.getValue();

            Date fromDateInput = (Date) ((Datebox) allocateLeaveWindow
                    .getFellow("allocateLeaveBeginYear")).getValue();
            Date thruDateInput = (Date) ((Datebox) allocateLeaveWindow
                    .getFellow("allocateLeaveEndYear")).getValue();
            String description = (String) ((org.zkoss.zul.Textbox)allocateLeaveWindow.getFellow("allocateleaveComment")).getValue();
			/*
			 * String employeeId = ((Bandbox) allocateLeaveWindow
			 * .getFellow("searchPanel")).getValue();
			 */
            List existingLeaveLimitList = null;
            existingLeaveLimitList = delegator.findByAnd("EmplLeaveLimit",
                    UtilMisc.toMap("leaveTypeId", leaveType));
            GenericValue existingLeaveLimitGv = EntityUtil
                    .getFirst(existingLeaveLimitList);
            Timestamp d_fromDate =null;
            if(existingLeaveLimitGv!=null){
                d_fromDate=existingLeaveLimitGv
                        .getTimestamp("beginYear");
            }
            Timestamp d_thruDate =null;
            if(existingLeaveLimitGv!=null){
                d_thruDate=existingLeaveLimitGv.getTimestamp("endYear");
            }
            String numDaysType = ((Textbox) allocateLeaveWindow
                    .getFellow("allocateleaveDays")).getValue();

            Comboitem fractionalPartOfDays = ((Combobox) allocateLeaveWindow
                    .getFellow("fractionalPartForLeave")).getSelectedItem();
            String fractionalPartOfDay = (String) fractionalPartOfDays.getValue();

            Double numDays = new Double(numDaysType) + new Double(fractionalPartOfDay);

            Radio emplyoeeTypeGroup = ((Radiogroup) allocateLeaveWindow.getFellow("emp_administration")).getSelectedItem();
            String employeeType = (String)emplyoeeTypeGroup.getValue();

            Comboitem positionCategoriesList = ((Combobox) allocateLeaveWindow
                    .getFellow("positionCategories")).getSelectedItem();
            String positionCategory = (String) positionCategoriesList.getValue();

            if (thruDateInput == null)
                thruDateInput = fromDateInput;
            Timestamp fromDate = new Timestamp(fromDateInput.getTime());
            java.sql.Timestamp thruDate = new java.sql.Timestamp(thruDateInput.getTime());
            thruDate = UtilDateTime.getDayStart(thruDate);

            LocalDispatcher dispatcher = HrmsInfrastructure.getDispatcher();//GenericDispatcher.getLocalDispatcher("default", delegator);

            Map<String, String> allocateLeave = null;

            allocateLeave = UtilMisc.toMap("userLogin", userLogin,"leaveTypeId", leaveType, "emplPosTypeId", emplPosType,
                    "beginYear", fromDate, "endYear", thruDate, "numDays",
                    numDays,"description",description,"partyId",userLogin.getString("partyId"),"employeeType",employeeType,"positionCategory",positionCategory);
            List<GenericValue> checkDuplicateEmplLeaveLimit = null;
            if(UtilValidate.isEmpty(emplPosType)){
                checkDuplicateEmplLeaveLimit = delegator.findByAnd("EmplLeaveLimit", UtilMisc.toMap("leaveTypeId",leaveType,
                    /*"emplPosTypeId",emplPosType,*/
                        "employeeType",employeeType,
                        "positionCategory",positionCategory),null,false);
            }else{
                checkDuplicateEmplLeaveLimit = delegator.findByAnd("EmplLeaveLimit", UtilMisc.toMap("leaveTypeId",leaveType,
                        "emplPosTypeId",emplPosType,
                        "employeeType",employeeType,
                        "positionCategory",positionCategory),null,false);
            }


            for(GenericValue emplLeaveLimit :checkDuplicateEmplLeaveLimit){
                Timestamp beginYear = emplLeaveLimit.getTimestamp("beginYear");
                Timestamp endYear = emplLeaveLimit.getTimestamp("endYear");
                /*"beginYear",fromDate,"endYear",thruDate*/
                if(beginYear.equals(fromDate) || endYear.equals(thruDate)){
                    Messagebox.show("Allocate Leave Limit Already Exists in the Range","Error",1,null);
                    return;
                }else if(UtilDateTime.isTimestampWithinRange(thruDate,beginYear,endYear)){
                    Messagebox.show("Allocate Leave Limit Already Exists in the Range","Error",1,null);
                    return;
                }else if(UtilDateTime.isTimestampWithinRange(fromDate,beginYear,endYear)){
                    Messagebox.show("Allocate Leave Limit Already Exists in the Range","Error",1,null);
                    return;
                }
            }
            dispatcher.runSync("createLeaveLimitService", allocateLeave);
            Events.postEvent("onClick$searchPerCompany",
                    allocateLeaveWindow.getPage().getFellow("searchPanel"),
                    null);
            allocateLeaveWindow.detach();

            Messagebox.show(Labels
                    .getLabel("Hrms_LeaveAllocatedSuccessfully"), Labels
                    .getLabel("HRMS_SUCCESS"), 1, null);
			/*} else {
				Messagebox
						.show(
								"One Record exists in this time period;Cannot create new record",
								Labels.getLabel("HRMS_SUCCESS"), 1, null);
			}*/

        } catch (Exception e) {

            e.printStackTrace();
        }

    }



    public static void showLeaveLimitWindow( Event event, GenericValue gv ) throws GenericEntityException, SuspendNotAllowedException, InterruptedException {

        Component editLeaveWindow = event.getTarget();
        String emplPosTypeId = gv.getString("emplPosTypeId");
        String leaveId = gv.getString("partyLeaveId");
        java.math.BigDecimal noOfDaysBigdesimal = gv.getBigDecimal("numDays");
        BigInteger noOfDays = noOfDaysBigdesimal.toBigInteger();
        Timestamp fromDateType = gv.getTimestamp("beginYear");
        Timestamp thruDateType = gv.getTimestamp("endYear");
        String description = gv.getString("description");
        String leaveType = gv.getString("leaveTypeId");
        String partyId = gv.getString("partyId");
        String employeeType = gv.getString("employeeType");
        String positionCategory = gv.getString("positionCategory");
        java.math.BigDecimal noOfDaysFractionalPart = noOfDaysBigdesimal.remainder(java.math.BigDecimal.ONE);

        Window win = (Window) Executions.createComponents("/zul/leaveManagement/updateLeaveLimit.zul", null, null);
        Label savedLeaveId = (Label) win.getFellow("leaveId");
        savedLeaveId.setValue(leaveId);
        Label emplId = (Label) win.getFellow("emplId");
        emplId.setValue(partyId);
        Textbox savednoOfDays = (Textbox) win.getFellow("noOfDays");
        savednoOfDays.setValue(noOfDays.toString());
        Combobox savedFractionalPartOfLeaveDays = (Combobox) win.getFellow("fractionalPartForLeave");
        savedFractionalPartOfLeaveDays.setValue(noOfDaysFractionalPart.toString());
        GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");

        if(employeeType.equals("Administrative")){
            Radio administrativeEmployeeType = (Radio)win.getFellow("emp_administration_administrative");
            administrativeEmployeeType.setSelected(true);
        }else if(employeeType.equals("Non-Administrative")){
            Radio nonAdministrativeEmployeeType = (Radio)win.getFellow("emp_administration_non_administrative");
            nonAdministrativeEmployeeType.setSelected(true);
        }

        Combobox savedPositionCategory =  (Combobox)win.getFellow("positionCategories");
        savedPositionCategory.setValue(positionCategory);

        Set<String> leaveTypeToDisplay = new HashSet();
        leaveTypeToDisplay.add("description");
        leaveTypeToDisplay.add("leaveTypeId");

        List<GenericValue> leaveTypes = delegator.findByAnd("Enumeration", UtilMisc.toMap("enumTypeId", "LEAVE_TYPE"),null,false);

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

        Set employeePositionTypeToDisplay = new HashSet();
        employeePositionTypeToDisplay.add("emplPositionTypeId");
        employeePositionTypeToDisplay.add("description");
        List<GenericValue> employeePositionTypes = delegator.findList("EmplPositionType", null, employeePositionTypeToDisplay, null, null, false);

        Listbox savedPosType = (Listbox) win.getFellow("emplPosTypeId");
        Listitem posTypeTypeItem = new Listitem();
        boolean isAnyItemSelected = false;
        savedPosType.appendItemApi(" ", " ");
        for (int i = 0; i < employeePositionTypes.size(); i++) {
            GenericValue posType = employeePositionTypes.get(i);
            String itemLabel = posType.getString("emplPositionTypeId");
            savedPosType.appendItemApi(posType.getString("description"), itemLabel);
            if (itemLabel.equals(emplPosTypeId)) {
                isAnyItemSelected = true;
                savedPosType.setSelectedIndex(i+1);
            }
        }

        if(!isAnyItemSelected){
            savedPosType.setSelectedIndex(0);
        }

        Datebox savedFromDate = (Datebox) win.getFellow("fromDate");
        savedFromDate.setValue(fromDateType);

        Datebox savedThruDate = (Datebox) win.getFellow("thruDate");
        savedThruDate.setValue(thruDateType);
        Textbox descriptionBox = (Textbox) win.getFellow("comment");
        descriptionBox.setValue(description);
        win.doModal();

    }

    public static void updateLeaveLimit( Event event ) {
        try {
            Component editLeaveLimitWindow = event.getTarget();
            GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
            GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
            LocalDispatcher dispatcher = HrmsInfrastructure.getDispatcher();//GenericDispatcher.getLocalDispatcher("default", delegator);
            String leaveId = (String) ((Label) editLeaveLimitWindow.getFellow("leaveId")).getValue();
            String comment = (String) ((Textbox) editLeaveLimitWindow.getFellow("comment")).getValue();
            String noOfDaysType = (String) ((Textbox) editLeaveLimitWindow.getFellow("noOfDays")).getValue();
            Comboitem fractionalPartForLeave = ((Combobox) editLeaveLimitWindow.getFellow("fractionalPartForLeave")).getSelectedItem();
            String fractionalPartForLeaveString = (String) fractionalPartForLeave.getValue();
            Double noOfDays = new Double(noOfDaysType) + new Double(fractionalPartForLeaveString);
            Date fromDateType = (Date) ((Datebox) editLeaveLimitWindow.getFellow("fromDate")).getValue();
            Date thruDateType = (Date) ((Datebox) editLeaveLimitWindow.getFellow("thruDate")).getValue();
            Radio emplyoeeTypeGroup = ((Radiogroup) editLeaveLimitWindow.getFellow("emp_administration")).getSelectedItem();
            String employeeType = (String)emplyoeeTypeGroup.getValue();

            Comboitem positionCategoriesList = ((Combobox) editLeaveLimitWindow.getFellow("positionCategories")).getSelectedItem();
            String positionCategory = (String) positionCategoriesList.getValue();

            java.sql.Timestamp fromDate = new java.sql.Timestamp(fromDateType.getTime());
            java.sql.Timestamp thruDate = new java.sql.Timestamp(thruDateType.getTime());
            thruDate = UtilDateTime.getDayStart(thruDate, TimeZone.getDefault(), Locale.getDefault());
            Listitem savedleaveTypeId = ((Listbox) editLeaveLimitWindow.getFellow("leaveTypeId")).getSelectedItem();
            String leaveTypeId = (String) savedleaveTypeId.getValue();

            Listitem savedPosTypeId = ((Listbox) editLeaveLimitWindow.getFellow("emplPosTypeId")).getSelectedItem();
            String posTypeId = (String) savedPosTypeId.getValue();


            Map<String, String> updateLeaveLimit = null;
            updateLeaveLimit = UtilMisc.toMap("userLogin", userLogin, "partyLeaveId", leaveId, "emplPosTypeId", posTypeId, "beginYear", fromDate, "endYear", thruDate, "leaveTypeId", leaveTypeId, "numDays", noOfDays,
                    "description", comment,"partyId",userLogin.getString("partyId"),"employeeType",employeeType,"positionCategory",positionCategory);
            List<GenericValue> checkEmplLeaveLimit = delegator.findByAnd("EmplLeaveLimit", UtilMisc.toMap("emplPosTypeId", posTypeId
                    ,"leaveTypeId", leaveTypeId,"employeeType",employeeType,"positionCategory",positionCategory),null,false);
            /*"beginYear", fromDate, "endYear", thruDate*/
            if(UtilValidate.isNotEmpty(checkEmplLeaveLimit)){
                for(GenericValue checkEmplLeaveLimitGv :checkEmplLeaveLimit){
                    String existsPartyLeaveId = checkEmplLeaveLimitGv.getString("partyLeaveId");
                    Timestamp beginYear = checkEmplLeaveLimitGv.getTimestamp("beginYear");
                    Timestamp endYear = checkEmplLeaveLimitGv.getTimestamp("endYear");
                    if(!existsPartyLeaveId.equals(leaveId) && (UtilDateTime.isTimestampWithinRange(fromDate,beginYear,endYear)
                            || UtilDateTime.isTimestampWithinRange(thruDate,beginYear,endYear) || beginYear.equals(thruDate) || endYear.equals(fromDate))) {
                        Events.postEvent("onClick$searchPerCompany", editLeaveLimitWindow.getPage().getFellow("searchPanel"), null);
                        Messagebox.show("Allocate Leave Limit Already Exists", "Error", 1, null);
                        return;
                    }
                }
                if (!updateEmployeeLeaveLimit(noOfDays,leaveId,fromDate,thruDate))
                    return;
                dispatcher.runSync("updateLeaveLimitService", updateLeaveLimit);
                Events.postEvent("onClick$searchPerCompany", editLeaveLimitWindow.getPage().getFellow("searchPanel"), null);
                Messagebox.show(Labels.getLabel("Hrms_LeaveLimitSuccessfullyUpdated"), "Success", 1, null);

            }else{
                if(!updateEmployeeLeaveLimit(noOfDays,leaveId,fromDate,thruDate))
                    return;
                dispatcher.runSync("updateLeaveLimitService", updateLeaveLimit);
                Events.postEvent("onClick$searchPerCompany", editLeaveLimitWindow.getPage().getFellow("searchPanel"), null);
                Messagebox.show(Labels.getLabel("Hrms_LeaveLimitSuccessfullyUpdated"), "Success", 1, null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void deleteLeaveLimit( Event event, GenericValue gv, final Button btn ) throws GenericEntityException, InterruptedException {
        final String leaveLimitId = (String) gv.getString("partyLeaveId");
        final GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
        Messagebox.show("Do You Want To Delete this Record?", "Warning", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION, new EventListener() {
            public void onEvent( Event evt ) {
                if ("onYes".equals(evt.getName())) {

                    try {
                        delegator.removeByAnd("EmplLeaveLimit", UtilMisc.toMap("partyLeaveId", leaveLimitId));
                        Events.postEvent("onClick", btn, null);
                    } catch (GenericEntityException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    try {
                        Messagebox.show("Leave Limit deleted successfully", "Success", 1, null);
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

    public static boolean updateEmployeeLeaveLimit(Double updatedNoOfDays,String leaveId,Timestamp updatedFromDate,Timestamp updatedToDate){
        GenericDelegator delegator = HrmsInfrastructure.getDelegator();
        GenericValue employeeLeave = null;
        List<GenericValue> employeeLeaveLimitPerEmployee = null;
        Double oldNoOfDays = 0.0;
        try {
            employeeLeave = delegator.findOne("EmplLeaveLimit",UtilMisc.toMap("partyLeaveId",leaveId),false);
            if(employeeLeave!=null){
                oldNoOfDays = employeeLeave.getDouble("numDays");
                Timestamp beginYear = employeeLeave.getTimestamp("beginYear");
                Timestamp endYear = employeeLeave.getTimestamp("endYear");
                employeeLeaveLimitPerEmployee = delegator.findByAnd("EmployeeLeaveInfo",UtilMisc.toMap("leaveType",employeeLeave.getString("leaveTypeId"),
                        "positionType",employeeLeave.getString("emplPosTypeId"),
                        "beginYear",beginYear,
                        "endYear",endYear),
                        null,false);
                if(UtilValidate.isEmpty(employeeLeaveLimitPerEmployee)){
                    employeeLeaveLimitPerEmployee = delegator.findByAnd("EmployeeLeaveInfo",UtilMisc.toMap("leaveType",employeeLeave.getString("leaveTypeId"),
                                    "beginYear",beginYear,
                                    "endYear",endYear),
                            null,false);
                }
                employeeLeaveLimitPerEmployee = ignoreNotApplicableLeaveInfo(employeeLeave,employeeLeaveLimitPerEmployee);
                if(updatedNoOfDays<oldNoOfDays){
                    return updateEmployeeLeaveLimitByCheckingCondition(employeeLeaveLimitPerEmployee,updatedNoOfDays,updatedFromDate,updatedToDate);
                }else{
                    storeEmployeeLeaveLimit(employeeLeaveLimitPerEmployee,updatedNoOfDays,updatedFromDate,updatedToDate);
                    return true;
                }
            }
            Messagebox.show("Leave limit type does not exist ", "Error", 1, null);
        }
        catch (GenericEntityException e) {
            e.printStackTrace();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean updateEmployeeLeaveLimitByCheckingCondition(List<GenericValue> employeeLeaveLimitPerEmployee,Double updatedNoOfDays,Timestamp updatedFromDate,Timestamp updatedToDate) throws InterruptedException, GenericEntityException {
        if(UtilValidate.isNotEmpty(employeeLeaveLimitPerEmployee)){
            if(!ConfigureLeaveManagementController.isAnyEmployeeAlreadyExtendedTheLeaveLimit(employeeLeaveLimitPerEmployee,updatedNoOfDays)){
                storeEmployeeLeaveLimit(employeeLeaveLimitPerEmployee,updatedNoOfDays,updatedFromDate,updatedToDate);
            }else{
                Messagebox.show("Cannot update the Days as one or more Employee crossed beyond the leave limit", "Error", 1, null);
                return false;
            }
            return true;
        }
        return true;
    }

    public static void storeEmployeeLeaveLimit(List<GenericValue> employeeLeaveLimitPerEmployee,Double updatedNoOfDays,Timestamp updatedFromDate,Timestamp updatedToDate) throws GenericEntityException {
        GenericDelegator delegator = HrmsInfrastructure.getDelegator();
        for(GenericValue employeeLeaveLimit:employeeLeaveLimitPerEmployee){
            Double availedLeave =  employeeLeaveLimit.getDouble("availedLeave");
            Double balancedLeaveToUpdate =  updatedNoOfDays - availedLeave;
            employeeLeaveLimit.put("leaveLimit",updatedNoOfDays);
            employeeLeaveLimit.put("balanceLeave",balancedLeaveToUpdate);
            if(employeeLeaveLimit.getTimestamp("beginYear").equals(updatedFromDate)&& employeeLeaveLimit.getTimestamp("beginYear").equals(updatedToDate)){
                employeeLeaveLimit.store();
            }else{
                Map employeeLeaveInfoToCreate = UtilMisc.toMap(
                        "partyId",employeeLeaveLimit.getString("partyId"),
                        "leaveType",employeeLeaveLimit.getString("leaveType"),
                        "leaveLimit",employeeLeaveLimit.getDouble("leaveLimit"),
                        "availedLeave",employeeLeaveLimit.getDouble("availedLeave"),
                        "balanceLeave",employeeLeaveLimit.getDouble("balanceLeave"),
                        "beginYear",updatedFromDate,
                        "endYear",updatedToDate,
                        "positionType",employeeLeaveLimit.getString("positionType")
                );
                employeeLeaveLimit.remove();
                GenericValue employeeLeaveInfo = delegator.makeValue("EmployeeLeaveInfo",employeeLeaveInfoToCreate);
                employeeLeaveInfo.create();
            }

        }
    }

    public static boolean isAnyEmployeeAlreadyExtendedTheLeaveLimit(List<GenericValue> employeeLeaveLimitPerEmployee,Double updatedNoOfDays){
        for(GenericValue employeeLeaveLimit:employeeLeaveLimitPerEmployee){
            if(employeeLeaveLimit.getDouble("availedLeave")>updatedNoOfDays){
                return true;
            }
        }
        return false;
    }

    public static List ignoreNotApplicableLeaveInfo(GenericValue employeeLeaveLimit,List<GenericValue> employeeLeaveInfoList) throws GenericEntityException {
        GenericDelegator delegator = HrmsInfrastructure.getDelegator();
        List<GenericValue> newUpdatedList = new ArrayList<GenericValue>();
        String employeeTypeFromLeaveLimit = employeeLeaveLimit.getString("employeeType");
        String positionCategoryFromLeaveLimit = employeeLeaveLimit.getString("positionCategory");
        for(GenericValue employeeLeaveInfo:employeeLeaveInfoList){
            String partyId = employeeLeaveInfo.getString("partyId");
            GenericValue personGV = delegator.findOne("Person", UtilMisc.toMap("partyId", partyId), false);
            if( personGV.getString("employeeType").equals(employeeTypeFromLeaveLimit) && personGV.getString("positionCategory").equals(positionCategoryFromLeaveLimit)){
                newUpdatedList.add(employeeLeaveInfo);
            }
        }
        return newUpdatedList;
    }
}
