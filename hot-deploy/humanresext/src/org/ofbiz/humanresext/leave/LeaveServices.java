package org.ofbiz.humanresext.leave;

import com.nthdimenzion.humanres.payroll.PayrollHelper;
import javolution.util.FastMap;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.humanresext.util.HumanResUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.*;

import static org.ofbiz.humanresext.util.HumanResUtil.getEmailAddress;
import static org.ofbiz.humanresext.util.HumanResUtil.getReportingMangerForParty;

/**
 * @author sandeep, Pankaj
 *
 */

public class LeaveServices {

    public static final String module = LeaveServices.class.getName();

    public static Map<String, Object> newLeaveType(DispatchContext dctx,
                                                   Map<String, ? extends Object> context) {
        String lenumId = (String) context.get("enumId");
        String ldescription = (String) context.get("description");
        String lenumTypeId = (String) context.get("enumTypeId");
        String lenumCode = (String) context.get("enumCode");
        String lsequenceId = (String) context.get("sequenceId");
        GenericDelegator delegator = (GenericDelegator) (GenericDelegator) dctx.getDelegator();
        Map<String, Object> lvVals = FastMap.newInstance();
        lvVals.putAll(UtilMisc.toMap("enumTypeId", lenumTypeId, "description",
                ldescription, "enumId", lenumId, "enumCode", lenumCode,
                "sequenceId", lsequenceId));

        GenericValue lvGV = delegator.makeValue("Enumeration", lvVals);
        try {
            lvGV.create();
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            return ServiceUtil
                    .returnError("An Error occurred creating the Enumeration record"
                            + e.getMessage());
        }
        return ServiceUtil.returnSuccess();
    }

    public static Map<String, Object> createLeaveType(DispatchContext dctx,
                                                      Map<String, ? extends Object> context) {
        String lleaveTypeId = (String) context.get("leaveTypeId");
        String ldescription = (String) context.get("description");
        String lparentTypeId = (String) context.get("parentTypeId");
        Double llimit = (Double) context.get("limit");
        GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
        Map<String, Object> lvVals = FastMap.newInstance();
        lvVals.putAll(UtilMisc.toMap("leaveTypeId", lleaveTypeId,
                "description", ldescription, "parentTypeId", lparentTypeId,
                "leaveLimit", llimit, "hasTable", "N"));

        GenericValue lvGV = delegator.makeValue("EmplLeaveType", lvVals);
        try {
            lvGV.create();
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            return ServiceUtil
                    .returnError("An Error occurred creating the Empl Leave Type record"
                            + e.getMessage());
        }
        return ServiceUtil.returnSuccess();
    }

    public static Map<String, Object> updateLeaveType(DispatchContext dctx,
                                                      Map<String, ? extends Object> context) {
        String lleaveTypeId = (String) context.get("leaveTypeId");
        String ldescription = (String) context.get("description");
        String lparentTypeId = (String) context.get("parentTypeId");
        Double llimit = (Double) context.get("limit");
        GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
        Map<String, Object> lvT = FastMap.newInstance();
        GenericValue lvGV = null;
        lvT.putAll(UtilMisc.toMap("leaveTypeId", lleaveTypeId, "description",
                ldescription, "parentTypeId", lparentTypeId, "limit", llimit));
        try {
            lvGV = delegator.findOne("EmplLeaveType", false, "leaveTypeId",
                    lleaveTypeId);
        } catch (GenericEntityException e) {
            e.printStackTrace();
            return ServiceUtil
                    .returnError("An Error occurred updating the Empl Leave Type record"
                            + e.getMessage());
        }

        lvGV.putAll(lvT);

        try {
            lvGV.store();
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        return ServiceUtil.returnSuccess();
    }

    public static Map<String, Object> deleteLeaveType(DispatchContext dctx,
                                                      Map<String, ? extends Object> context) {
        String lleaveTypeId = (String) context.get("leaveTypeId");
        GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
        GenericValue lvGV = null;
        try {
            lvGV = delegator.findOne("EmplLeaveType", false, "leaveTypeId",
                    lleaveTypeId);
        } catch (GenericEntityException e) {
            e.printStackTrace();
            return ServiceUtil
                    .returnError("An Error occurred updating the Empl Leave Type record"
                            + e.getMessage());
        }

        try {
            lvGV.remove();
        } catch (GenericEntityException e) {
            e.printStackTrace();
            return ServiceUtil
                    .returnError("An Error occurred deleting the Empl Leave Type record"
                            + e.getMessage());
        }
        return ServiceUtil.returnSuccess();
    }

    public static Map<String, Object> notifyLeaveApplication(
            DispatchContext dctx, Map<String, ? extends Object> context) {
        String partyId = (String) context.get("partyId");
        String fromDate = (String) context.get("fromDate");
        GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
        String emailOfApplicant = null, emailOfApprover = null;
        try {
            GenericValue party = delegator.findOne("Party", false, "partyId",
                    partyId);
            emailOfApplicant = getEmailAddress(party);
            emailOfApprover = getEmailAddress(getReportingMangerForParty(
                    partyId, delegator));
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            return ServiceUtil
                    .returnError("An Error occurred notifiying the leave application"
                            + e.getMessage());
        }

        Map<String, Object> emailContext = new HashMap<String, Object>();
        emailContext.put("sendFrom", UtilProperties.getPropertyValue(
                "LeaveManagement.proprties", "sendFrom"));
        emailContext.put("sendVia", UtilProperties.getPropertyValue(
                "LeaveManagement.proprties", "sendVia"));
        emailContext.put("sendTo", emailOfApprover);
        emailContext.put("sendCc", emailOfApplicant);
        emailContext.put("subject", "New leave application for " + partyId);

        String detailLink = UtilProperties.getPropertyValue(
                "LeaveManagement.proprties", "detailLink");
        detailLink = detailLink + "?partyId=" + partyId + "&endYear="
                + fromDate;

        String body = "Hi, <br><br>" + " New leave applied by " + partyId
                + "<br>" + "<br>From " + fromDate + "<br>To "
                + (String) context.get("thruDate") + "<br> Leave Type "
                + (String) context.get("leaveTypeId")
                + "<br><br>Click here to view details<br> " + detailLink
                + "<br><br>From Admin";
        emailContext.put("body", body);
        emailContext.put("userLogin", context.get("userLogin"));

        LocalDispatcher dispatcher = dctx.getDispatcher();
        try {
            dispatcher.runAsync("sendMail", emailContext);
        } catch (GenericServiceException e) {
            e.printStackTrace();
        }
        return ServiceUtil.returnSuccess();
    }

    /** Creates a new Party Leave Limit record in table Empl_Leave_Limit **/
    public static Map<String, Object> createLeaveLimit(DispatchContext dctx,
                                                       Map<String, Object> context) {
		/*
		 * For assigning Leave Limit to each positionType not to each party,so
		 * partyId is not required
		 */
        // String lpartyId = (String)context.get("partyId");
        String lemplPosTypeId = (String) context.get("emplPosTypeId");
        String leaveTypeId = (String) context.get("leaveTypeId");
        String partyId = (String) context.get("partyId");
        Timestamp lbeginYear = (Timestamp) context.get("beginYear");
        Timestamp lendYear = (Timestamp) context.get("endYear");
        String ldescription = (String) context.get("description");
        String lemployeeType = (String) context.get("employeeType");
        String lpositionCategory = (String) context.get("positionCategory");
        Double lnumDays = (Double) context.get("numDays");
        String lmgrPositionId = (String) context.get("mgrPositionId");
        Double lavailed = 0D; // Number of days of leaves already availed in
        // year which is 0 by default
        GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
        Map<String, Object> leaveVals = UtilMisc.toMap("emplPosTypeId",
                lemplPosTypeId, "leaveTypeId", leaveTypeId, "beginYear",
                lbeginYear, "endYear", lendYear, "description", ldescription,"partyId",partyId,"employeeType",lemployeeType,"positionCategory",lpositionCategory);
        leaveVals.put("numDays", lnumDays);
        leaveVals.put("availed", lavailed);
        leaveVals.put("mgrPositionId", lmgrPositionId);

        GenericValue lvGV = delegator.makeValue("EmplLeaveLimit", leaveVals);
        lvGV.set("partyLeaveId", delegator.getNextSeqId("EmplLeaveLimit"));
        try {
            lvGV.create();
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            return ServiceUtil
                    .returnError("An Error occurred creating the Empl Leave Limit record"
                            + e.getMessage());
        }

        /** Create the loan status record **/
        Map<String, Object> result = ServiceUtil
                .returnSuccess("Employee Leave Limit created");
        result.put("partyLeaveId", lvGV.get("partyLeaveId"));
        return result;
    }

    /** Update leave limit for the employee **/

    public static Map<String, Object> updateLeaveLimit(DispatchContext dctx,
                                                       Map<String, Object> context) {
        Map<String, Object> result = null;
        GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
        String lpartyLeaveId = (String) context.get("partyLeaveId");
        Map<String, Object> plId = FastMap.newInstance();
        plId.put("partyLeaveId", lpartyLeaveId);
		/*
		 * For assigning Leave Limit to each positionType not to each party,so
		 * partyId is not required
		 */
        // String lpartyId = (String)context.get("partyId");
        String lemplPosTypeId = (String) context.get("emplPosTypeId");
        String partyId = (String) context.get("partyId");
        GenericValue lvGV = null;
        try {
            lvGV = delegator.findOne("EmplLeaveLimit", plId, false);
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            return ServiceUtil
                    .returnError("Problems finding the leave limit record ["
                            + lpartyLeaveId + "]");
        }

        String leaveTypeId = (String) context.get("leaveTypeId");
        Timestamp lbeginYear = (Timestamp) context.get("beginYear");
        Timestamp lendYear = (Timestamp) context.get("endYear");
        String ldescription = (String) context.get("description");
        Double lnumDays = (Double) context.get("numDays");
        String lmgrPositionId = (String) context.get("mgrPositionId");

        String lemployeeType = (String) context.get("employeeType");
        String lpositionCategory = (String) context.get("positionCategory");
        // String lpartyLeaveId = null; //Out variable

        Map<String, Object> leaveVals = UtilMisc.toMap("emplPosTypeId",
                lemplPosTypeId, "leaveTypeId", leaveTypeId, "beginYear",
                lbeginYear, "endYear", lendYear, "description", ldescription,"employeeType",lemployeeType,"positionCategory",lpositionCategory);
        leaveVals.put("numDays", lnumDays);
        leaveVals.put("mgrPositionId", lmgrPositionId);
        leaveVals.put("partyId", partyId);
        lvGV.put("partyLeaveId", lpartyLeaveId);
        lvGV.putAll(leaveVals);

        try {
            lvGV.store();
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            return ServiceUtil
                    .returnError("Problems updating the leave limit record ["
                            + lpartyLeaveId + "]" + e.getMessage());
        }
        result = ServiceUtil.returnSuccess("Employee Leave Limit updated");
        result.put("partyLeaveId", lpartyLeaveId);
        return result;
    }

    /** Submit the Leave application 
     * @throws GenericEntityException **/
    public static Map<String, Object> submitLeave(DispatchContext dctx,
                                                  Map<String, Object> context) throws GenericEntityException {
        Map<String, Object> result = null;
        GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();

        String leaveId = (String) context.get("leaveId");
        String statusType = (String) context.get("statusType");
        /**
         * String lleaveTypeId = (String)context.get("leaveTypeId"); String
         * lpartyId = (String)context.get("partyId"); Date lfromDate =
         * (Date)context.get("fromDate");
         *
         * EntityCondition partyCond = EntityCondition.makeCondition("partyId",
         * lpartyId); EntityCondition fromCond =
         * EntityCondition.makeCondition("fromDate", lfromDate); EntityCondition
         * conditions = EntityCondition.makeCondition(partyCond, fromCond);
         * List<GenericValue> data = null; try { data =
         * delegator.findList("EmplLeave", conditions, null, null, null, false);
         * } catch (GenericEntityException e) { Debug.logError(e, module);
         * return
         * ServiceUtil.returnError("Problems finding the leave application [" +
         * lpartyId + ", leave Type" + lleaveTypeId + " from date :" + lfromDate
         * + "]"); }
         *
         * if (data.size() != 1) { //Debug.logError(e, module); return
         * ServiceUtil.returnError(
         * "More than one record found for the leave application [" + lpartyId +
         * ", leave Type" + lleaveTypeId + " from date :" + lfromDate + "]");
         *
         * }
         *
         * GenericValue lvGV = data.get(0);
         **/
        String statusId="LT_SUBMITTED";
        String mgrPosId=(String) context.get("mgrPositionId");
        String employeeId=(String) context.get("partyId");
        GenericValue empPos=HumanResUtil.getEmplPositionForParty(employeeId, delegator);
    	String empPosId=empPos==null?null:empPos.getString("emplPositionId");
        if(mgrPosId.equals(empPosId))
        	statusId="LT_MGR_APPROVED";

        
        GenericValue lvGV = null;
        try {
            lvGV = delegator.findOne("EmplLeave", false, "leaveId", leaveId);
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            return ServiceUtil
                    .returnError("Problems finding the leave application ["
                            + leaveId + "]" + e.getMessage());
        }
        lvGV.put("leaveStatusId",statusId);
        lvGV.put("statusType", statusType);
        if(mgrPosId.equals(empPosId))
        	lvGV.put("adminStatusType","2");
        try {
            lvGV.store();
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            return ServiceUtil
                    .returnError("Problems submitting the leave application ["
                            + leaveId + "]" + e.getMessage());
        }
        result = ServiceUtil
                .returnSuccess("Employee Leave Application Submitted");
        result.put("leaveStatusId", statusId);
        return result;
    }

    /**
     * Update the EmplLeave table with status of Leave application
     *
     * @throws GenericEntityException
     **/

    public static Map<String, Object> processLeave(DispatchContext dctx,
                                                   Map<String, Object> context) throws GenericEntityException {
        Map<String, Object> result = null;
        GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();

        String lleaveId = (String) context.get("leaveId");

        String lleaveTypeId = (String) context.get("leaveTypeId");
        String lpartyId = (String) context.get("partyId");
        Date lfromDate = (Date) context.get("fromDate");
        String employeeType = (String) context.get("employeeType");
        String positionCategory = (String) context.get("positionCategory");
        GenericValue emplPosTypeIdOfPartyGv = HumanResUtil
                .getEmplPositionForParty(lpartyId, delegator);
        String emplPosTypeIdOfParty = emplPosTypeIdOfPartyGv
                .getString("emplPositionTypeId");

        GenericValue lvGV = null; // data.get(0);
        try {

            lvGV = delegator.findOne("EmplLeave", false, "leaveId", lleaveId);
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            return ServiceUtil
                    .returnError("Problems finding the leave application [ "
                            + lleaveId + " ]\n");
        }
        String lpresentStatus = lvGV.getString("leaveStatusId");
        if (lpresentStatus.equals("LT_ADM_APPROVED")
                || lpresentStatus.equals("LT_ADM_REJECTED")) {
            return ServiceUtil.returnError("The leave application [ "
                    + lleaveId
                    + " ] has already been admin approved/rejected \n");
        }

        /**
         * get present availed leaves from Empl_Leave_Limit and update it with
         * new availed leaves Double lalreadyAvailed =
         * lvGV.getDouble("availed"); "availed", lnewAvailed,
         **/

        /** Get current financial year **/
        GenericValue fiscalYearGV = null;
        try {
            fiscalYearGV = PayrollHelper.getFiscalYear(lfromDate,delegator);
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            return ServiceUtil
                    .returnError("Could not find fiscal year from date : "
                            + lfromDate);
        }

        /**
         * Select * from EmplLeaveLimit where beginYear >= lbeginYr and endYear
         * <= lendYr and partyId = lpartyId and leaveTypId = lleaveTypeId
         *
         * i.e. Select leaves allotted for this financial year.
         */
        EntityCondition partyCond = EntityCondition.makeCondition("emplPosTypeId", emplPosTypeIdOfParty);
        EntityCondition typeCond = EntityCondition.makeCondition("leaveTypeId",lleaveTypeId);
        EntityCondition employeeTypeCond = EntityCondition.makeCondition("employeeType",employeeType);
        EntityCondition positionCategoryCond = EntityCondition.makeCondition("positionCategory",positionCategory);
        EntityCondition limitCond = EntityCondition.makeCondition(partyCond,typeCond,employeeTypeCond,positionCategoryCond);


        List<GenericValue> data = null;
        try {
            data = delegator.findList("EmplLeaveLimit", limitCond, null, null,null, false);
            if(UtilValidate.isEmpty(data)){
                 limitCond = EntityCondition.makeCondition(typeCond,employeeTypeCond,positionCategoryCond);
                data = delegator.findList("EmplLeaveLimit", limitCond, null, null,null, false);
            }
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError("Problems finding the leave limit ["
                    + lpartyId + ", leave Type" + lleaveTypeId);
        }

        if (data.size() == 0) {
            Debug.logError("No records found for this party", module);
            return ServiceUtil
                    .returnError("No leave limit records found for this party ["
                            + lpartyId + ", leave Type" + lleaveTypeId);
        }
        GenericValue limitGV = data.get(0);
        Double lavailed = limitGV.getDouble("availed");

        String lmgrComment = (String) context.get("mgrComment");
        String ladminComment = (String) context.get("adminComment");
        String statusType = (String) context.get("statusType");
        String adminStatusType = (String) context.get("adminStatusType");
        String lleaveStatusId = (String) context.get("leaveStatusId");
        Double lpaidDays = (Double) context.get("paidDays");
        Double lunpaidDays = (Double) context.get("unpaidDays");
        String updatedBy = (String) context.get("updatedBy");
        if (lunpaidDays == null) {
            lunpaidDays = 0D;
        }

        /**
         * Now increase the new availed leaves only if the leave was ADMIN
         * APPROVED Double lnewAvailed = lalreadyAvailed + lpaidDays;
         ***/
        if (lleaveStatusId.indexOf("LT_ADM_APPROVED") >= 0) {
            lavailed = lavailed + lpaidDays;
        }

        Map<String, Object> leaveVals = UtilMisc.toMap("partyId", lpartyId,
                "paidDays", lpaidDays, "unpaidDays", lunpaidDays,
                "leaveStatusId", lleaveStatusId, "statusType", statusType,
                "adminStatusType", adminStatusType);
        leaveVals.put("mgrComment", lmgrComment);
        leaveVals.put("updatedBy", updatedBy);
        leaveVals.put("adminComment", ladminComment);
        lvGV.putAll(leaveVals);
        limitGV.put("availed", lavailed);

        try {
            lvGV.store();
            limitGV.store();
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            return ServiceUtil
                    .returnError("Problems processing the leave application ["
                            + lpartyId + ", leave Type" + lleaveTypeId
                            + " from date :" + lfromDate + "] "
                            + e.getMessage());
        }
        result = ServiceUtil
                .returnSuccess("Employee Leave Application Processed and availed leaves changed");
        result.put("leaveStatusId", lleaveStatusId);
        return result;
    }

    public static Map<String, Object> getEmployeeLeaveLimitForLeaveType(DispatchContext dctx,
                                                                        Map<String, Object> context) {
        GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
        String employeeId = (String) context.get("employeeId");
        String leaveTypeId = (String) context.get("leaveTypeId");
        GenericValue person = null;
        GenericValue emplPositionGv = null;
        Double leaveLimit =0.0;
        try {
            Double previousYearBalance =0.0;
            person = delegator.findOne("Person",UtilMisc.toMap("partyId",employeeId),false);
            emplPositionGv = HumanResUtil.getEmplPositionForParty(employeeId, delegator);
            String emplPosType = emplPositionGv == null ? null : emplPositionGv.getString("emplPositionTypeId");
            String positionCategory = person.getString("positionCategory");
            String employeeType = person.getString("employeeType");
            Map currentAndPreviousYearLeaveLimit =  getCurrentAndPreviousYearLeaveLimit(leaveTypeId,employeeType,positionCategory,emplPosType,employeeId,delegator);
            previousYearBalance = (Double)currentAndPreviousYearLeaveLimit.get("previousYearLeaveLimit")-(Double)currentAndPreviousYearLeaveLimit.get("previousYearAvailedLeave");
            leaveLimit = (Double)currentAndPreviousYearLeaveLimit.get("currentYearLeaveLimit")+ previousYearBalance;
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        Map<String, Object> result = ServiceUtil.returnSuccess("Employee Loan Limit Searched");
        result.put("leaveLimit", leaveLimit);
        return result;
    }

    public static Map getCurrentAndPreviousYearLeaveLimit(String leaveType,String employeeType,String positionCategory,String employeePositionTypeId,String partyId,GenericDelegator delegator) throws GenericEntityException {
        Map currentAndPreviousYearStartAndEndDates = getCurrentAndPreviousYearStartAndEndDate();
        Map currentAndPreviousYearLeaveLimit = new HashMap();
        GenericValue previousYearEmployeeLeaveInfo = getPreviousLeaveFromEmployeeLeaveInfo(partyId, leaveType,delegator);
        EntityCondition beginYearCond =  EntityCondition.makeCondition("beginYear", EntityOperator.GREATER_THAN_EQUAL_TO,currentAndPreviousYearStartAndEndDates.get("currentYearStartDate"));
        EntityCondition endYearCond =  EntityCondition.makeCondition("endYear",EntityOperator.LESS_THAN_EQUAL_TO,currentAndPreviousYearStartAndEndDates.get("currentYearEndDate"));
        EntityCondition partyIdCond =  EntityCondition.makeCondition("partyId",EntityOperator.EQUALS,partyId);
        EntityCondition leaveTypeCond =  EntityCondition.makeCondition("leaveType",EntityOperator.EQUALS,leaveType);
        List conditions = Arrays.asList(beginYearCond, endYearCond, partyIdCond, leaveTypeCond);
        List employeeLeaveInfoList =  delegator.findList("EmployeeLeaveInfo", EntityCondition.makeCondition(conditions, EntityOperator.AND),null,null,null,false);
        GenericValue employeeLeaveInfo = EntityUtil.getFirst(employeeLeaveInfoList);
        if(UtilValidate.isEmpty(employeeLeaveInfo)){
            GenericValue employeeLeaveLimit = getEmployeeLeaveLimit(leaveType,employeeType,positionCategory,employeePositionTypeId,true,delegator);
            if(UtilValidate.isNotEmpty(employeeLeaveLimit)){
                currentAndPreviousYearLeaveLimit.put("currentYearLeaveLimit",employeeLeaveLimit.getDouble("numDays"));
            }else{
                currentAndPreviousYearLeaveLimit.put("currentYearLeaveLimit",0.0);
            }
            currentAndPreviousYearLeaveLimit.put("currentYearAvailedLeave",0.0);
        }else{
            currentAndPreviousYearLeaveLimit.put("currentYearLeaveLimit",employeeLeaveInfo.getDouble("leaveLimit"));
            currentAndPreviousYearLeaveLimit.put("currentYearAvailedLeave",employeeLeaveInfo.getDouble("availedLeave"));
        }

        
        if(UtilValidate.isNotEmpty(previousYearEmployeeLeaveInfo)){
            currentAndPreviousYearLeaveLimit.put("previousYearLeaveLimit",previousYearEmployeeLeaveInfo.getDouble("leaveLimit"));
            currentAndPreviousYearLeaveLimit.put("previousYearAvailedLeave",previousYearEmployeeLeaveInfo.getDouble("availedLeave"));
        }else {
            GenericValue employeeLeaveLimit = getEmployeeLeaveLimit(leaveType,employeeType,positionCategory,employeePositionTypeId,false,delegator);
            if(UtilValidate.isNotEmpty(employeeLeaveLimit)){
                currentAndPreviousYearLeaveLimit.put("previousYearLeaveLimit",employeeLeaveLimit.getDouble("numDays"));
            }else{
                currentAndPreviousYearLeaveLimit.put("previousYearLeaveLimit",0.0);
            }
            currentAndPreviousYearLeaveLimit.put("previousYearAvailedLeave",0.0);
        }
        return currentAndPreviousYearLeaveLimit;
    }
    public static GenericValue getPreviousLeaveFromEmployeeLeaveInfo(String partyId,String leaveTypeId,GenericDelegator delegator) throws GenericEntityException {
        Map timestamps = getCurrentAndPreviousYearStartAndEndDate();
        EntityCondition partyIdCond = EntityCondition.makeCondition("partyId",EntityOperator.EQUALS,partyId);
        EntityCondition leaveTypeCond = EntityCondition.makeCondition("leaveType",EntityOperator.EQUALS,leaveTypeId);
        EntityCondition endYearCond = EntityCondition.makeCondition("endYear",EntityOperator.LESS_THAN_EQUAL_TO,timestamps.get("previousYearEndDate"));
        EntityCondition beginYearCond = EntityCondition.makeCondition("beginYear",EntityOperator.GREATER_THAN_EQUAL_TO,timestamps.get("previousYearStartDate"));
        List conditions = Arrays.asList(partyIdCond,leaveTypeCond,endYearCond,beginYearCond);
        List<GenericValue> employeeLeaveInfoList = delegator.findList("EmployeeLeaveInfo", EntityCondition.makeCondition(conditions, EntityOperator.AND), null, Arrays.asList("endYear DESC"), null, false);
        return EntityUtil.getFirst(employeeLeaveInfoList);
    }
    public static GenericValue getEmployeeLeaveLimit(String leaveType,String employeeType,String positionCategory,String employeePositionTypeId,boolean isCurrentYear,GenericDelegator delegator){
        GenericValue employeeLeaveLimitGv = null;
        Map currentAndPreviousYearStartAndEndDates = getCurrentAndPreviousYearStartAndEndDate();
        EntityCondition fromdateCondition = null;
        EntityCondition thrudateCondition = null;
        if(isCurrentYear){
             fromdateCondition = EntityCondition.makeCondition("beginYear", EntityOperator.GREATER_THAN_EQUAL_TO, currentAndPreviousYearStartAndEndDates.get("currentYearStartDate"));
             thrudateCondition = EntityCondition.makeCondition("endYear", EntityOperator.LESS_THAN_EQUAL_TO, currentAndPreviousYearStartAndEndDates.get("currentYearEndDate"));
        }else{
            fromdateCondition = EntityCondition.makeCondition("beginYear", EntityOperator.GREATER_THAN_EQUAL_TO, currentAndPreviousYearStartAndEndDates.get("previousYearStartDate"));
            thrudateCondition = EntityCondition.makeCondition("endYear", EntityOperator.LESS_THAN_EQUAL_TO, currentAndPreviousYearStartAndEndDates.get("previousYearEndDate"));
        }
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

    public static Map<String,Timestamp> getCurrentAndPreviousYearStartAndEndDate(){
        final Timestamp currentYearStartDate = UtilDateTime.getYearStart(new Timestamp(new java.util.Date().getTime()));
        final Timestamp previousYearStartDate = UtilDateTime.getYearStart(UtilDateTime.getPreviousYear(currentYearStartDate));
        final Timestamp currentYearEndDate = UtilDateTime.getYearEnd(currentYearStartDate, TimeZone.getDefault(), Locale.getDefault());
        final Timestamp previousYearEndDate = UtilDateTime.getYearEnd(previousYearStartDate,TimeZone.getDefault(), Locale.getDefault());
        Map timestamps = new HashMap(){{
            put("currentYearStartDate",currentYearStartDate);
            put("currentYearEndDate",currentYearEndDate);
            put("previousYearStartDate",previousYearStartDate);
            put("previousYearEndDate",previousYearEndDate);
        }};
        return timestamps;
    }
}
