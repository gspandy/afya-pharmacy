package com.hrms.composer;

import com.ndz.zkoss.HrmsUtil;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Div;
import org.zkoss.zul.Messagebox;

import java.sql.Timestamp;
import java.util.*;

/**
 * Created by ASUS on 09-Oct-14.
 */
public class EmployeeLeaveLimitComposer extends HrmsComposer {

    private List employeeLeaveLimitModel = null;

    public List getEmployeeLeaveLimitModel() {
        return employeeLeaveLimitModel;
    }

    public void setEmployeeLeaveLimitModel(List employeeLeaveLimitModel) {
        this.employeeLeaveLimitModel = employeeLeaveLimitModel;
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void executeAfterCompose(Component comp) throws Exception {

        GenericValue postionGv =null;
        String positionId = null;
        GenericValue emplPositionTypeGv = null;
        List<GenericValue> leaveLimit = null;
        String partyId = (String) Executions.getCurrent().getArg().get("partyId");
        postionGv = org.ofbiz.humanresext.util.HumanResUtil.getEmplPositionForParty(partyId,delegator);
        GenericValue personGv = delegator.findOne("Person",UtilMisc.toMap("partyId",partyId),false);
        Map currentYearAndPreviousYearLeave = HrmsUtil.getCurrentAndPreviousYearStartAndEndDate();
        Timestamp currentYearStart = (Timestamp) currentYearAndPreviousYearLeave.get("currentYearStartDate");
        Timestamp currentYearEnd = (Timestamp) currentYearAndPreviousYearLeave.get("currentYearEndDate");
        Timestamp previousYearStart  = (Timestamp) currentYearAndPreviousYearLeave.get("previousYearStartDate");
        Timestamp previousYearEnd  = (Timestamp) currentYearAndPreviousYearLeave.get("previousYearEndDate");

        if(postionGv!=null){
            positionId = (String)postionGv.getString("emplPositionId");
        }
        List<GenericValue> emplPositionTypeList = delegator.findByAnd("EmplPosition",UtilMisc.toMap("emplPositionId",positionId),null,false);
        String positionTypeId = "";
        String positionCategory ="";
        String employeeType = "";
        if(UtilValidate.isNotEmpty(emplPositionTypeList) && UtilValidate.isNotEmpty(personGv)){
            emplPositionTypeGv= EntityUtil.getFirst(emplPositionTypeList);
            positionTypeId = emplPositionTypeGv.getString("emplPositionTypeId");
            positionCategory = personGv.getString("positionCategory");
            employeeType = personGv.getString("employeeType");
            leaveLimit = delegator.findByAnd("EmplLeaveLimit",
                    UtilMisc.toMap("positionCategory",positionCategory,"employeeType",employeeType,"beginYear",currentYearStart,
                            "endYear",currentYearEnd),null,false);
        }

        if(UtilValidate.isNotEmpty(leaveLimit)){
            employeeLeaveLimitModel = new ArrayList();
            for(GenericValue emplLeaveLimit:leaveLimit){
                String positionType = emplLeaveLimit.getString("emplPosTypeId");
                String leaveType = emplLeaveLimit.getString("leaveTypeId");
                Timestamp beginYearTimeStamp = emplLeaveLimit.getTimestamp("beginYear");
                Timestamp endYearTimeStamp = emplLeaveLimit.getTimestamp("endYear");
                java.text.SimpleDateFormat formater = new java.text.SimpleDateFormat("dd-MM-yyyy");
                String beginYear = formater.format(beginYearTimeStamp);
                String endYear = formater.format(endYearTimeStamp);
                if(emplLeaveLimit.getString("emplPosTypeId").trim().length()!=0 && !emplLeaveLimit.getString("emplPosTypeId").equals(positionTypeId)){
                    continue;
                }
                if(emplLeaveLimit.getString("emplPosTypeId").trim().length()==0 && HrmsUtil.isLeaveLimitPriorityIsLower(leaveLimit, positionTypeId, leaveType,beginYearTimeStamp,endYearTimeStamp)) {
                    continue;
                }
                String leaveTypeDesc = getDescriptionFromEnumeration(leaveType);
                EntityCondition equalsConditions = EntityCondition.makeCondition(UtilMisc.toMap(
                        "leaveType", leaveType,
                        "partyId", partyId,
                        "beginYear", beginYearTimeStamp,
                        "endYear", endYearTimeStamp
                ), EntityOperator.AND);
                List<GenericValue> employeeLeaveInfo = delegator.findList("EmployeeLeaveInfo", equalsConditions, null, null, null, false);
                GenericValue employeeLeaveInfoGV = EntityUtil.getFirst(employeeLeaveInfo);
                if(UtilValidate.isNotEmpty(employeeLeaveInfoGV)){
                    int leaveLimitDaysWholePart = getWholePartOfDouble(employeeLeaveInfoGV.getDouble("leaveLimit"));
                    Double leaveLimitDaysFractionalPart = getFractionalPartOfDouble(employeeLeaveInfoGV.getDouble("leaveLimit"));
                    Map modelPartialGv =  UtilMisc.toMap(
                            "partyId",partyId,
                            "leaveType",leaveType,
                            "leaveTypeDesc",leaveTypeDesc,
                            "leaveLimitDaysWholePart",leaveLimitDaysWholePart,
                            "leaveLimitDaysFractionalPart",leaveLimitDaysFractionalPart,
                            "leaveLimit", employeeLeaveInfoGV.get("leaveLimit"),
                            "availedLeave", employeeLeaveInfoGV.get("availedLeave"),
                            "balanceLeave",employeeLeaveInfoGV.get("balanceLeave"),
                            "positionType",employeeLeaveInfoGV.get("positionType"),
                            "beginYear",beginYear,
                            "endYear",endYear
                    );
                    employeeLeaveLimitModel.add(modelPartialGv);
                }else{
                    int leaveLimitDaysWholePart = getWholePartOfDouble(emplLeaveLimit.getDouble("numDays"));
                    Double leaveLimitDaysFractionalPart = getFractionalPartOfDouble(emplLeaveLimit.getDouble("numDays"));
                    if(positionType.trim().length()<=0){
                        positionType=positionTypeId;
                    }
                    Map modelPartialGv =  UtilMisc.toMap(
                            "partyId",partyId,
                            "leaveType",leaveType,
                            "leaveTypeDesc",leaveTypeDesc,
                            "leaveLimit", emplLeaveLimit.get("numDays"),
                            "leaveLimitDaysWholePart",leaveLimitDaysWholePart,
                            "leaveLimitDaysFractionalPart",leaveLimitDaysFractionalPart,
                            "availedLeave", 0.0,
                            "balanceLeave",emplLeaveLimit.get("numDays"),
                            "positionType",positionType,
                            "beginYear",beginYear,
                            "endYear",endYear
                    );
                    employeeLeaveLimitModel.add(modelPartialGv);
                }

                //previous year
                equalsConditions = EntityCondition.makeCondition(UtilMisc.toMap(
                        "leaveType", leaveType,
                        "partyId", partyId,
                        "beginYear", previousYearStart,
                        "endYear", previousYearEnd
                ), EntityOperator.AND);
                beginYear = formater.format(previousYearStart);
                endYear = formater.format(previousYearEnd);
                List<GenericValue> previousYearEmployeeLeaveInfo = delegator.findList("EmployeeLeaveInfo", equalsConditions, null, null, null, false);
                GenericValue previousYearEmployeeLeaveInfoGV = EntityUtil.getFirst(previousYearEmployeeLeaveInfo);
                if(UtilValidate.isNotEmpty(previousYearEmployeeLeaveInfoGV)){
                    int leaveLimitDaysWholePart = getWholePartOfDouble(previousYearEmployeeLeaveInfoGV.getDouble("leaveLimit"));
                    Double leaveLimitDaysFractionalPart = getFractionalPartOfDouble(previousYearEmployeeLeaveInfoGV.getDouble("leaveLimit"));
                    Map modelPartialGv =  UtilMisc.toMap(
                            "partyId",partyId,
                            "leaveType",leaveType,
                            "leaveTypeDesc",leaveTypeDesc,
                            "leaveLimitDaysWholePart",leaveLimitDaysWholePart,
                            "leaveLimitDaysFractionalPart",leaveLimitDaysFractionalPart,
                            "leaveLimit", previousYearEmployeeLeaveInfoGV.get("leaveLimit"),
                            "availedLeave", previousYearEmployeeLeaveInfoGV.get("availedLeave"),
                            "balanceLeave",previousYearEmployeeLeaveInfoGV.get("balanceLeave"),
                            "positionType",previousYearEmployeeLeaveInfoGV.get("positionType"),
                            "beginYear",beginYear,
                            "endYear",endYear
                    );
                    employeeLeaveLimitModel.add(modelPartialGv);
                }else{
                    List previousYearLeaveLimitList = delegator.findByAnd("EmplLeaveLimit",
                            UtilMisc.toMap("positionCategory",positionCategory,"employeeType",employeeType,"beginYear",previousYearStart,
                                    "endYear",previousYearEnd),null,false);
                    GenericValue previousYearLeaveLimit = EntityUtil.getFirst(previousYearLeaveLimitList);
                    int leaveLimitDaysWholePart =0;
                    Double leaveLimitDaysFractionalPart =0.0;
                    Double numDays =0.0;
                    if(UtilValidate.isNotEmpty(previousYearLeaveLimit)){
                        leaveLimitDaysWholePart = getWholePartOfDouble(previousYearLeaveLimit.getDouble("numDays"));
                        leaveLimitDaysFractionalPart = getFractionalPartOfDouble(previousYearLeaveLimit.getDouble("numDays"));
                        numDays = previousYearLeaveLimit.getDouble("numDays");
                    }

                    if(positionType.trim().length()<=0){
                        positionType=positionTypeId;
                    }
                    Map modelPartialGv =  UtilMisc.toMap(
                            "partyId",partyId,
                            "leaveType",leaveType,
                            "leaveTypeDesc",leaveTypeDesc,
                            "leaveLimit", numDays,
                            "leaveLimitDaysWholePart",leaveLimitDaysWholePart,
                            "leaveLimitDaysFractionalPart",leaveLimitDaysFractionalPart,
                            "availedLeave", 0.0,
                            "balanceLeave",numDays,
                            "positionType",positionType,
                            "beginYear",beginYear,
                            "endYear",endYear
                    );
                    employeeLeaveLimitModel.add(modelPartialGv);
                }
            }
        }
    }

    private int getWholePartOfDouble(Double value){
        return value.intValue();
    }

    private Double getFractionalPartOfDouble(Double value){
        return value - value.intValue();
    }

    private String getDescriptionFromEnumeration(String enumId){
        GenericValue enumGv = null;
        try {
            enumGv = delegator.findOne("Enumeration",UtilMisc.toMap("enumId",enumId),false);
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        if(UtilValidate.isNotEmpty(enumGv)){
            return enumGv.getString("description");
        }
        return "";
    }

    public void save(Map leaveLimit,Div profileWindow){
        Integer leaveLimitDaysWholePart = leaveLimit.get("leaveLimitDaysWholePart") instanceof String ? Integer.valueOf((String)leaveLimit.get("leaveLimitDaysWholePart")):(Integer)leaveLimit.get("leaveLimitDaysWholePart");
        Object leaveLimitDaysFractionalPartObject = leaveLimit.get("leaveLimitDaysFractionalPart");
        Double totalNumberOfDays =0.0;
        if(leaveLimitDaysFractionalPartObject instanceof String){
            String leaveLimitDaysFractionalPart =  (String)leaveLimit.get("leaveLimitDaysFractionalPart");
            totalNumberOfDays =  leaveLimitDaysWholePart + Double.valueOf(leaveLimitDaysFractionalPart);
        }else {
            Double leaveLimitDaysFractionalPart =  (Double)leaveLimit.get("leaveLimitDaysFractionalPart");
            totalNumberOfDays =  leaveLimitDaysWholePart + leaveLimitDaysFractionalPart;
        }

        Double availedLeave = (Double)leaveLimit.get("availedLeave");
        Double balanceLeave = totalNumberOfDays - availedLeave;
        if(totalNumberOfDays<availedLeave){
            try {
                Messagebox.show("Cannot update Leave Days less than Availed Leave", "Error", 1, null);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return;
        }
        Timestamp beginYear = UtilDateTime.dateStringToTimestampParser((String)leaveLimit.get("beginYear"),"dd-MM-yyyy");
        Timestamp endYear = UtilDateTime.dateStringToTimestampParser((String)leaveLimit.get("endYear"),"dd-MM-yyyy");
        endYear = UtilDateTime.getDayStart(endYear, TimeZone.getDefault(), Locale.getDefault());

        leaveLimit.remove("leaveTypeDesc");
        leaveLimit.remove("leaveLimitDaysWholePart");
        leaveLimit.remove("leaveLimitDaysFractionalPart");
        leaveLimit.put("leaveLimit", totalNumberOfDays);
        leaveLimit.put("balanceLeave", balanceLeave);
        leaveLimit.put("beginYear",beginYear);
        leaveLimit.put("endYear",endYear);
        GenericValue employeeLeaveInfoGv = delegator.makeValidValue("EmployeeLeaveInfo",leaveLimit);
        try {
            delegator.createOrStore(employeeLeaveInfoGv);
            Messagebox.show("Leave limit updated successfully", "Error", 1, null);
            Events.postEvent("onClick", profileWindow.getFellow("allocateLeaveLimit"), null);
        } catch (GenericEntityException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}
