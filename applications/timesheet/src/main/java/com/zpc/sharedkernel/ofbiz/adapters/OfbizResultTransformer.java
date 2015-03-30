package com.zpc.sharedkernel.ofbiz.adapters;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import lombok.AllArgsConstructor;
import org.joda.time.LocalDate;

import java.sql.Timestamp;
import java.util.*;

/**
 * Author: Nthdimenzion
 */
public final class OfbizResultTransformer {

    public static List<Map<String,Object>> TransformOfbizDepartmentMap(Map ofbizResult, boolean isAdmin, boolean isTimeKeeper){
        Preconditions.checkArgument(ofbizResult != null);
        Preconditions.checkArgument(ofbizResult.get("list")!=null);
        final List<Map<String,Object>> result = Lists.newArrayList();
        final List<Map> rows= (List) ofbizResult.get("list");
        if(isAdmin){
            for (Map row : rows) {
                final HashMap<String, Object> outputRow = Maps.newHashMap();
                if(row.get("partyId")!=null)
                    outputRow.put("departmentId", row.get("partyId"));
                if(row.get("groupName")!=null)
                    outputRow.put("departmentName",row.get("groupName"));
                result.add(outputRow);
            }
        }
        else if(isTimeKeeper){
            for (Map row : rows) {
                final HashMap<String, Object> outputRow = Maps.newHashMap();
                if (row.get("partyId") != null)
                    outputRow.put("partyId", row.get("partyId"));
                if (row.get("partyIdTo") != null)
                    outputRow.put("partyIdTo", row.get("partyIdTo"));
                if (row.get("partyIdFrom") != null)
                    outputRow.put("departmentId", row.get("partyIdFrom"));
                if (row.get("groupName") != null)
                    outputRow.put("departmentName", row.get("groupName"));
                result.add(outputRow);
            }
        }
        else {
            for (Map row : rows) {
                final HashMap<String, Object> outputRow = Maps.newHashMap();
                if (row.get("partyId") != null)
                    outputRow.put("partyId", row.get("partyId"));
                if (row.get("partyIdTo") != null)
                    outputRow.put("partyIdTo", row.get("partyIdTo"));
                if (row.get("partyIdFrom") != null)
                    outputRow.put("departmentId", row.get("partyIdFrom"));
                if (row.get("groupName") != null)
                    outputRow.put("departmentName", row.get("groupName"));
                result.add(outputRow);
            }
        }
        List<Map<String,Object>> furnishedResult = removeUnwantedDepartments(result);
        return furnishedResult;
    }

    private static List<Map<String,Object>> removeUnwantedDepartments(List<Map<String, Object>> departments) {
        for(Iterator iterator = departments.iterator(); iterator.hasNext();){
            Map<String, Object> department = (Map<String, Object>)iterator.next();
            if(department.get("departmentId").equals("DEP020"))
                iterator.remove();
        }
        return departments;
    }

    public static List<Map<String, Object>> TransformOfbizEmployeeMap(Map ofbizResult, Date startDate, Set<String> hods) {
        //System.out.println("\n\n\nBefore = =========================="+ofbizResult);
        Preconditions.checkArgument(ofbizResult!=null);
        Preconditions.checkArgument(ofbizResult.get("list")!=null);
        final List<Map<String,Object>> result = Lists.newArrayList();
        final List<Map> list = (List) ofbizResult.get("list");
        List<Map> rows = removeEmployeesIfTerminated(list, startDate);
        for (Map row : rows) {
            final HashMap<String, Object> outputRow = Maps.newLinkedHashMap();
            outputRow.put("employeeId", row.get("partyId"));
            outputRow.put("firstName",row.get("firstName"));
            outputRow.put("lastName",row.get("lastName"));
            if (row.get("positionCategory").equals("Employees"))
                outputRow.put("positionCategory", "ZPC_"+row.get("positionCategory"));
            else {
                if(hods.contains(row.get("partyId")))
                    outputRow.put("positionCategory", "ApexLevel_"+row.get("positionCategory"));
                else
                    outputRow.put("positionCategory", row.get("positionCategory"));
            }
            result.add(outputRow);
        }
        //System.out.println("\n\n\nEmployeesTransformed = =========================="+result);
        return result;
    }

    private static List<Map> removeEmployeesIfTerminated(List<Map> rows, Date startDate) {
        //System.out.println("\n\n\n startDate"+startDate);
        for(Iterator iterator = rows.iterator(); iterator.hasNext();){
            Map row = (Map)iterator.next();
            if(row.get("thruDate") != null && removeIfThruDateIsBeforeStartDate((java.sql.Timestamp) row.get("thruDate"), startDate)){
                iterator.remove();
            }
        }
        return rows;
    }

    private static boolean removeIfThruDateIsBeforeStartDate(Timestamp timestamp, Date startDate){
        Date thruDate = new Date(timestamp.getTime());
        if(thruDate.equals(startDate)){
            LocalDate localDate =  new LocalDate(thruDate);
            thruDate = localDate.toDate();
        }
        return thruDate.before(startDate);
    }

    public static List<Map<String, Object>> TransformOfbizLeaveDetailsMap(Map ofbizResult) {
        Preconditions.checkArgument(ofbizResult!=null);
        Preconditions.checkArgument(ofbizResult.get("list")!=null);
        final List<Map<String,Object>> result = Lists.newArrayList();
        final List<Map> rows= (List) ofbizResult.get("list");
        for (Map row : rows) {
            final HashMap<String, Object> outputRow = Maps.newHashMap();
            outputRow.put("leaveLimit", row.get("leaveLimit"));
            result.add(outputRow);
        }
        return result;
    }

    public static List<Map<String, Object>> TransformDepartmentMapFetchedBasedOnDepartmentId(Map ofbizResult) {
        Preconditions.checkArgument(ofbizResult!=null);
        Preconditions.checkArgument(ofbizResult.get("list")!=null);
        final List<Map<String,Object>> result = Lists.newArrayList();
        final List<Map> rows= (List) ofbizResult.get("list");
        for (Map row : rows) {
            final HashMap<String, Object> outputRow = Maps.newHashMap();
            outputRow.put("departmentName", row.get("departmentName"));
            outputRow.put("departmentId",row.get("departmentId"));
            outputRow.put("departmentCode",row.get("departmentCode"));
            result.add(outputRow);
        }
        return result;
    }

    public static List<Map<String, Object>> TransformEmplPositionFulfillmentMapFetchedBasedOnDepartmentId(Map ofbizResult) {
        Preconditions.checkArgument(ofbizResult!=null);
        Preconditions.checkArgument(ofbizResult.get("list")!=null);
        final List<Map<String,Object>> result = Lists.newArrayList();
        final List<Map> rows= (List) ofbizResult.get("list");
        for (Map row : rows) {
            final HashMap<String, Object> outputRow = Maps.newHashMap();
            if (row.get("partyId") != null)
                outputRow.put("partyId", row.get("partyId"));
            if (row.get("emplPositionId") != null)
                outputRow.put("emplPositionId",row.get("emplPositionId"));
            result.add(outputRow);
        }
        return result;
    }

    public static List<Map<String, Object>> TransformDivisionMapFetchedBasedOnDepartmentId(Map ofbizResult) {
        Preconditions.checkArgument(ofbizResult!=null);
        Preconditions.checkArgument(ofbizResult.get("list")!=null);
        final List<Map<String,Object>> result = Lists.newArrayList();
        final List<Map> rows= (List) ofbizResult.get("list");
        for (Map row : rows) {
            final HashMap<String, Object> outputRow = Maps.newHashMap();
            if (row.get("divisionId") != null)
                outputRow.put("divisionId", row.get("divisionId"));
            if (row.get("divisionCode") != null)
                outputRow.put("divisionCode", row.get("divisionCode"));
            if (row.get("divisionName") != null)
                outputRow.put("divisionName", row.get("divisionName"));
            result.add(outputRow);
        }
        return result;
    }

    public static List<Map<String, Object>> TransformEmplPositionMapFetchedBasedOnDepartmentId(Map ofbizResult) {
        Preconditions.checkArgument(ofbizResult!=null);
        Preconditions.checkArgument(ofbizResult.get("list")!=null);
        final List<Map<String,Object>> result = Lists.newArrayList();
        final List<Map> rows= (List) ofbizResult.get("list");
        for (Map row : rows) {
            final HashMap<String, Object> outputRow = Maps.newHashMap();
            if (row.get("divisionId") != null)
                outputRow.put("divisionId", row.get("divisionId"));
            result.add(outputRow);
        }
        return result;
    }

    public static List<Map<String, Object>> TransformPersonMapFetchedBasedPartyId(Map ofbizResult) {
        Preconditions.checkArgument(ofbizResult!=null);
        Preconditions.checkArgument(ofbizResult.get("list")!=null);
        final List<Map<String,Object>> result = Lists.newArrayList();
        final List<Map> rows= (List) ofbizResult.get("list");
        for (Map row : rows) {
            final HashMap<String, Object> outputRow = Maps.newHashMap();
            if (row.get("positionCategory") != null)
                outputRow.put("positionCategory", row.get("positionCategory"));
            result.add(outputRow);
        }
        return result;
    }

    public static Set<String> TransformEmplPositonMap(Map ofbizResult) {
        Preconditions.checkArgument(ofbizResult!=null);
        Preconditions.checkArgument(ofbizResult.get("list")!=null);
        final Set<String> result = Sets.newHashSet();
        final List<Map> rows= (List) ofbizResult.get("list");
        for (Map row : rows) {
            if (row.get("partyId") != null && row.get("emplPositionTypeId") != null && row.get("emplPositionTypeId").toString().trim().equals("HOD"))
                result.add(row.get("partyId").toString().trim());
        }
        return result;
    }
}
