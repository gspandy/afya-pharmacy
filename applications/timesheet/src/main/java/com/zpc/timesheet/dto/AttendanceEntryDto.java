package com.zpc.timesheet.dto;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.zpc.timesheet.query.TimesheetFinder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.nthdimenzion.common.service.SpringApplicationContext;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Author: Nthdimenzion
 */
@ToString
@Setter
@Getter
@EqualsAndHashCode(of = "attendanceEntryId")
public class AttendanceEntryDto {

    public String attendanceEntryId;
    public String totalDays;
    public String employeeId;
    public String firstName;
    public String lastName;
    public String positionCategory;
    public String comments;
    public List<DayEntryDto> dayEntryDtos = Lists.newArrayList();




    // Computed Columns start
    public String nOverTimeSummary;
    public String dOvertimeSummary;
    public String absent;
    public String suspension;
    public String absentAndSuspension;
    public String compLeave;
    public String annualLeave;
    public String annualPassageLeave;
    public String specialLeave;
    public String sickLeave;
    public String maternityLeave;
    public String otherUnpaidLeave;
    public String present;
    public String presentOnFreeDay;
    public String awayOnDuty;
    public String standby;
    public String freedays;
    public String excessFreeDays;
    public String SDPresent;
    public String SDPresentOnFreeDay;
    public String SDTransferredAndPresent;
    public String SDWrongDepartmentAndPresent;
    public String totalShiftDifferential;
    public String terminated;
    public String resigned;
    public String transferred;
    public String deserted;
    public String wrongDepartment;
    // Computed Columns end


    public static AttendanceEntryDto findAttendanceEntryForEmployee(final String employeeId,Iterable<AttendanceEntryDto> attendanceEntries){
        return Iterables.find(attendanceEntries, new Predicate<AttendanceEntryDto>() {
            @Override
            public boolean apply(AttendanceEntryDto input) {
                return input.employeeId.equals(employeeId);
            }
        });
    }

    public List<String>  finalValues;

    public List<String> getFinalValues(){
        List<String> finalValues = Lists.newArrayList();
        sortDayEntryDtosByDate(dayEntryDtos);
        for (DayEntryDto dayEntryDto : dayEntryDtos) {
            for (String value : dayEntryDto.getValues()) {
                finalValues.add(value);
            }
        }
        return finalValues;
    }

    private void sortDayEntryDtosByDate(List<DayEntryDto> dayEntryDtos) {
        Collections.sort(dayEntryDtos, new Comparator<DayEntryDto>() {
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            @Override
            public int compare(DayEntryDto o1, DayEntryDto o2) {
                try {
                    return format.parse(o1.date).compareTo(format.parse(o2.date));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return 0;
            }
        });
    }

    public static void setEmployeeNameToAttendanceEntryDto(List<AttendanceEntryDto> attendanceEntryDtos, String departmentId, TimesheetFinder timesheetFinder, Date startDate) {
        List<Map<String, Object>> employees = timesheetFinder.getEmployeesInDepartment(departmentId, startDate);
        for(Map mapOfEmployeeDetails : employees){
            for(AttendanceEntryDto attendanceEntryDto : attendanceEntryDtos){
                if(((String)mapOfEmployeeDetails.get("employeeId")).equals(attendanceEntryDto.getEmployeeId())){
                    attendanceEntryDto.setFirstName((String)mapOfEmployeeDetails.get("firstName"));
                    attendanceEntryDto.setLastName((String) mapOfEmployeeDetails.get("lastName"));
                    attendanceEntryDto.setPositionCategory((String) mapOfEmployeeDetails.get("positionCategory"));
                }
            }
        }
        removeUnWantedEmployees(departmentId, attendanceEntryDtos, startDate);
        //System.out.println("\n\n\nattendanceEntryDtos===="+attendanceEntryDtos.size());
        sortAttendanceEntryDtosByEmployeeId(attendanceEntryDtos);
    }

    private static void sortAttendanceEntryDtosByEmployeeId(List<AttendanceEntryDto> attendanceEntryDtos) {
        Collections.sort(attendanceEntryDtos, new Comparator<AttendanceEntryDto>() {
            @Override
            public int compare(AttendanceEntryDto o1, AttendanceEntryDto o2) {
                int positionCategory = o1.positionCategory.compareTo(o2.positionCategory);
                //return o1.employeeId.compareTo(o2.employeeId);
                return ((positionCategory == 0) ?  getIntegerPartOfEmployeeId(o1.employeeId).compareTo(getIntegerPartOfEmployeeId(o2.employeeId)) :  positionCategory);
            }
        });
    }

    private static Integer getIntegerPartOfEmployeeId(String employeeId){
        Pattern intsOnly = Pattern.compile("\\d+");
        Matcher makeMatch = intsOnly.matcher(employeeId);
        makeMatch.find();
        String intPartOfEmpId = makeMatch.group();
        return Integer.parseInt(intPartOfEmpId);
    }

    public static void removeUnWantedEmployees(String departmentId, List<AttendanceEntryDto> attendanceEntryDtoList, Date startDate) {
        TimesheetFinder timesheetFinder = SpringApplicationContext.getBean("timesheetFinder");
        List<Map<String, Object>> employees = timesheetFinder.getEmployeesInDepartment(departmentId, startDate);
        List<String> allEmpIds = getEmployeeIdsOfActiveEmployees(employees);
        for(Iterator iterator = attendanceEntryDtoList.iterator(); iterator.hasNext();){
            AttendanceEntryDto attendanceEntryDto = (AttendanceEntryDto)iterator.next();
            if(!allEmpIds.contains(attendanceEntryDto.employeeId))
                iterator.remove();
        }
    }

    private static List<String> getEmployeeIdsOfActiveEmployees(List<Map<String, Object>> employees) {
        List<String> listOfEmpIds = Lists.newArrayList();
        for(Map map : employees){
            listOfEmpIds.add((String)map.get("employeeId"));
        }
        return listOfEmpIds;
    }
}
