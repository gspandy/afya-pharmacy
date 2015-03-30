package com.zpc.timesheet.application;

import com.google.common.base.Preconditions;
import com.zpc.timesheet.domain.TimesheetService;
import com.zpc.timesheet.domain.model.Timesheet;
import com.zpc.timesheet.dto.AttendanceRegisterDto;
import com.zpc.timesheet.query.TimesheetFinder;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.axonframework.commandhandling.annotation.CommandHandler;
import org.axonframework.repository.Repository;
import org.joda.time.DateTime;
import org.nthdimenzion.ddd.domain.model.Interval;
import org.nthdimenzion.object.utils.IIdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Author: Nthdimenzion
 */

@Component
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class TimesheetCommandHandler {

    private Repository<Timesheet> timesheetRepository;

    private IIdGenerator idGenerator;

    private TimesheetFinder timesheetFinder;

    @Autowired
    public TimesheetCommandHandler(Repository<Timesheet> timesheetRepository, IIdGenerator idGenerator, TimesheetFinder timesheetFinder) {
        this.timesheetRepository = timesheetRepository;
        this.idGenerator = idGenerator;
        this.timesheetFinder = timesheetFinder;
    }


    @CommandHandler
    public String generateNewTimesheet(GenerateNewTimesheetCommand generateNewTimesheetCommand){
        final List<Map<String, Object>> departments = timesheetFinder.getDepartmentsManagedBy(generateNewTimesheetCommand.loggedInUser, generateNewTimesheetCommand.isAdmin);
        final Timesheet timesheet = Timesheet.GenerateNewTimesheet(idGenerator, generateNewTimesheetCommand.scheduleId);
        for (Map<String, Object> department : departments) {
            final String departmentId = (String) department.get("departmentId");
            final List<Map<String, Object>> employees = timesheetFinder.getEmployeesInDepartment(departmentId, generateNewTimesheetCommand.scheduleStartDate.toDate());
            final Set<String> employeeIds = Timesheet.extractEmployeeIds(employees);
            final Interval schedulePeriod = new Interval(new DateTime(generateNewTimesheetCommand.scheduleStartDate.toDate().getTime()), new DateTime(generateNewTimesheetCommand.scheduleEndDate.toDate().getTime()));
            timesheet.addNewAttendanceRegister(idGenerator, employeeIds, schedulePeriod, departmentId);
        }
        timesheetRepository.add(timesheet);
        return timesheet.getIdentifier();
    }

    @CommandHandler
    public void saveAttendanceRegister(SaveAttendanceRegisterCommand saveAttendanceRegisterCommand){
        Timesheet timesheet = timesheetRepository.load(saveAttendanceRegisterCommand.timesheetId);
        for (AttendanceRegisterDto attendanceRegisterDto : saveAttendanceRegisterCommand.attendanceRegisterDtos) {
            timesheet.saveAttendanceRegister(idGenerator,attendanceRegisterDto);
        }
    }

    @CommandHandler
    public void saveTimesheet(SaveTimesheetCommand saveTimesheetCommand){
        Timesheet timesheet = timesheetRepository.load(saveTimesheetCommand.timesheetId);
        for (AttendanceRegisterDto attendanceRegisterDto : saveTimesheetCommand.attendanceRegisterDtos) {
            timesheet.saveAttendanceRegister(idGenerator,attendanceRegisterDto);
        }
    }

    @CommandHandler
    public void submitAttendanceRegister(SubmitAttendanceRegisterCommand submitAttendanceRegisterCommand){
        Timesheet timesheet = timesheetRepository.load(submitAttendanceRegisterCommand.timesheetId);
        timesheet.saveAttendanceRegister(idGenerator,submitAttendanceRegisterCommand.attendanceRegisterDto);
        timesheet.submitAttendanceRegister(submitAttendanceRegisterCommand.attendanceRegisterDto.attendanceRegisterId);
    }

    @CommandHandler
    public void verifyTimesheet(VerifyTimesheetCommand verifyTimesheetCommand){
        Timesheet timesheet = timesheetRepository.load(verifyTimesheetCommand.timesheetId);
        timesheet.verify();
    }

    @CommandHandler
    public void exportTimesheet(ExportTimesheetCommand exportTimesheetCommand){
        Timesheet timesheet = timesheetRepository.load(exportTimesheetCommand.timesheetId);
        timesheet.exported();
    }
}

