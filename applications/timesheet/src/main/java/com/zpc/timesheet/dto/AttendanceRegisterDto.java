package com.zpc.timesheet.dto;

import com.google.common.collect.Lists;
import lombok.Data;
import org.apache.commons.collections.CollectionUtils;
import org.joda.time.LocalDate;
import org.nthdimenzion.object.utils.UtilValidator;

import java.util.*;

/**
 * Author: Nthdimenzion
 */

@Data
public class AttendanceRegisterDto {

    public String timesheetId;
    public String attendanceRegisterId;
    public LocalDate fromDate;
    public LocalDate thruDate;
    public String  description;
    public String status;
    public String departmentId;
    public String departmentName;
    public String departmentCode;
    public List<AttendanceEntryDto> attendanceEntries;

    public boolean isEmptyRegister(){
        return CollectionUtils.isEmpty(attendanceEntries);
    }

    public void addNewEmployeesIntoRegister(List<Map<String, Object>> employeesInDepartment) {
        for (Map<String, Object> employee : employeesInDepartment) {
            AttendanceEntryDto attendanceEntryDto = new AttendanceEntryDto();
            attendanceEntryDto.employeeId = (String) employee.get("employeeId");
            attendanceEntryDto.firstName = (String) employee.get("firstName");
            attendanceEntryDto.lastName = (String) employee.get("lastName");
            if(!attendanceEntries.contains(attendanceEntryDto))
                attendanceEntries.add(attendanceEntryDto);

        }
    }

    public void setFromDate(Date fromDate){
        this.fromDate = new LocalDate(fromDate);
    }

    public void setThruDate(Date thruDate){
        this.thruDate = new LocalDate(thruDate);
    }

    public List<DayEntryDto> fetchDayEntries(){
        if(UtilValidator.isNotEmpty(attendanceEntries)){
            return attendanceEntries.get(0).dayEntryDtos;
        }
        return Collections.emptyList();
    }

    public List<List<DayEntryDto>> fetchDayEntriesDtoOfAllEmployees(){
        List<List<DayEntryDto>> listOfDayEntryDto = new ArrayList<List<DayEntryDto>> ();
        if(UtilValidator.isNotEmpty(attendanceEntries)){
            for(int i=0;i<attendanceEntries.size();i++) {
                listOfDayEntryDto.add(attendanceEntries.get(i).dayEntryDtos);
            }
            return listOfDayEntryDto;
        }
        return Collections.emptyList();
    }

    public static List<String> fetchDayEntryHeaders(AttendanceRegisterDto attendanceRegisterDto){
        final List<DayEntryDto> dayEntryDtos = attendanceRegisterDto.fetchDayEntries();
        List<String> dayEntryHeaders = Lists.newArrayList();
        for (DayEntryDto dayEntryDto : dayEntryDtos) {
            dayEntryHeaders.add(dayEntryDto.getDay() + " " + dayEntryDto.date);
        }
        return dayEntryHeaders;
    }

    public static String getSheetMatchingDepartmentName(AttendanceRegisterDto attendanceRegisterDto){
        return attendanceRegisterDto.departmentName.substring(0, Math.min(attendanceRegisterDto.departmentName.length(),31));
    }

}
