package com.zpc.timesheet.view;

import com.google.common.collect.Lists;
import com.zpc.timesheet.dto.AttendanceEntryDto;
import com.zpc.timesheet.dto.AttendanceRegisterDto;
import com.zpc.timesheet.query.TimesheetFinder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.nthdimenzion.common.service.SpringApplicationContext;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.Init;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;

import java.util.List;
import java.util.Map;

import static com.zpc.sharedkernel.ofbiz.OfbizGateway.*;

/**
 * Author: Nthdimenzion
 */

public class ConsolidatedAttendanceregisterVM {

    List<String> fixedColumnNames = Lists.newArrayList("Shift One", "Shift Two", "N/OT", "D/OT");

    @Getter
    List<String> columnNames = Lists.newArrayList("Total Days","Comments","Employee Id","Employee Name");

    @Getter
    List<AuxHeader> auxHeaders = Lists.newArrayList(new AuxHeader("Employee Details",4));

    @Wire
    private TimesheetFinder timesheetFinder;

    @Getter
    @Setter
    List<AttendanceEntryDto> attendanceEntryDtoList = Lists.newArrayList();

    @Getter
    @Setter
    List<AttendanceRegisterDto> attendanceRegisters = Lists.newArrayList();

    String managerId;
    Boolean isAdmin;
    String timesheetId;
    List<Map<String, Object>> departments;


    @Init
    public void init() {
        managerId = getPartyId();
        isAdmin = (Boolean) getTimesheetInfo().get("isAdmin");
        timesheetFinder = SpringApplicationContext.getBean("timesheetFinder");
        timesheetId = Executions.getCurrent().getParameter("timesheetId");
        departments = timesheetFinder.getDepartmentsManagedBy(managerId, isAdmin);
        //timesheetId = "F32FE4CD-1BD3-4D86-BB46-E2845A24D045";
    }

    private void addSummariesToColumnNames(List<String> columnNames) {
        columnNames.add("N/OT");
        columnNames.add("D/OT");
        columnNames.add("Absent");
        columnNames.add("Suspension");
        columnNames.add("Absent & Suspension");
        columnNames.add("Comp. Leave");
        columnNames.add("Annual Leave");
        columnNames.add("Annual Leave Passage");
        columnNames.add("Special Leave");
        columnNames.add("Sick");
        columnNames.add("Maternity Leave");
        columnNames.add("Other Unpaid Leave");
        columnNames.add("Present");
        columnNames.add("Present On Free Day");
        columnNames.add("Away On Duty");
        columnNames.add("Stand By");
        columnNames.add("Free Days");
        columnNames.add("Terminated");
        columnNames.add("Resigned");
        columnNames.add("Transferred");
        columnNames.add("Deserted");
        columnNames.add("Wrong Dept");
        columnNames.add("Excess Free Days");
        columnNames.add("SD Present");
        columnNames.add("SD Present On Free Day");
        columnNames.add("SD Transferred & Present");
        columnNames.add("SD Wrong Dept & Present");
        columnNames.add("Total Shift Differential");


    }


    @AfterCompose
    public void afterCompose(@ContextParam(ContextType.VIEW) Component view) {
        Selectors.wireComponents(view, this, false);
        attendanceRegisters = timesheetFinder.prepareTimesheetView(timesheetId,managerId,isAdmin);
        refreshConsolidatedAttendanceregister(timesheetId);
        List<String> dayEntryHeaders = AttendanceRegisterDto.fetchDayEntryHeaders(attendanceRegisters.get(0));
        for(String dayEntryHeader: dayEntryHeaders){
            auxHeaders.add(new AuxHeader(dayEntryHeader,4));
        }
        for (int i = 1; i < auxHeaders.size(); i++) {
            columnNames.addAll(fixedColumnNames);
        }
        auxHeaders.add(new AuxHeader("Summaries",28));
        addSummariesToColumnNames(columnNames);

    }

    private void refreshConsolidatedAttendanceregister(String timeshseetId) {
        for (AttendanceRegisterDto attendanceRegister : attendanceRegisters) {
            AttendanceEntryDto.setEmployeeNameToAttendanceEntryDto(attendanceRegister.attendanceEntries, attendanceRegister.departmentId, timesheetFinder, attendanceRegister.fromDate.toDate());
            attendanceEntryDtoList.addAll(attendanceRegister.attendanceEntries);
        }
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public class AuxHeader{
        public String auxHeaderLabel;
        public int colspan;
    }
}
