package com.zpc.timesheet.domain.model;

import com.zpc.timesheet.dto.DayEntryDto;
import lombok.*;
import org.joda.time.LocalDate;
import org.nthdimenzion.object.utils.Constants;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

/**
 * Author: Nthdimenzion
 */
@NoArgsConstructor
@ToString(of = "id")
@Entity
@EqualsAndHashCode(of = "id")
class EmployeeAttendanceEntry {

    @Id
    @Getter(AccessLevel.PACKAGE)
    private String id;
    private String employeeId;
    private String comments;

    @ElementCollection(fetch = FetchType.LAZY)
    private Set<DayEntry> dayEntries;

    // Computed Columns start
    private String nOverTime;
    private String dOvertime;
    private String absent;
    private String suspension;
    private String absentAndSuspension;
    private String compLeave;
    private String annualLeave;
    private String annualPassageLeave;
    private String specialLeave;
    private String sickLeave;
    private String maternityLeave;
    private String otherUnpaidLeave;
    private String present;
    private String presentOnFreeDay;
    private String awayOnDuty;
    private String standby;
    private String freedays;
    private String excessFreeDays;
    private String SDPresent;
    private String SDPresentOnFreeDay;
    private String SDTransferredAndPresent;
    private String SDWrongDepartmentAndPresent;
    private String totalShiftDifferential;
    private String totalDays;

    @Column(name="TERMINATED_DATE")
    private String terminated;
    private String resigned;
    private String transferred;
    private String deserted;
    private String wrongDepartment;
    // Computed Columns end

    private EmployeeAttendanceEntry(final String id,final String employeeId,final Set<DayEntry> dayEntries){
        this.id = id;
        this.employeeId = employeeId;
        this.dayEntries = dayEntries;
    }

    public static EmployeeAttendanceEntry EmptyAttendanceEntry(final String id,final String employeeId,final Set<DayEntry> dayEntries){
        EmployeeAttendanceEntry employeeAttendanceEntry = new EmployeeAttendanceEntry(id,employeeId,dayEntries);
        return employeeAttendanceEntry;
    }

    public EmployeeAttendanceEntry recordDayEntries(List<DayEntryDto> dayEntryDtos){
        dayEntries.clear();
        for (DayEntryDto dayEntryDto : dayEntryDtos) {
            final LocalDate localDate = LocalDate.parse(dayEntryDto.date, Constants.URL_DATE_FORMAT);
            final DayEntry dayEntry = DayEntry.FilledDayEntry(localDate, dayEntryDto.shiftOne, dayEntryDto.shiftTwo, dayEntryDto.nOvertime, dayEntryDto.dOvertime);
            dayEntries.add(dayEntry);
        }
        return this;
    }

    public EmployeeAttendanceEntry recordComments(String comments){
        this.comments = comments;
        return this;
    }

    public void recordSummations(String nOverTimeSummary, String dOvertimeSummary, String absent, String suspension,
                                 String absentAndSuspension, String compLeave, String annualLeave, String annualPassageLeave,
                                 String specialLeave, String sickLeave, String maternityLeave, String otherUnpaidLeave,
                                 String present, String presentOnFreeDay, String awayOnDuty, String standby, String freedays,
                                 String excessFreeDays, String SDPresent, String SDPresentOnFreeDay, String SDTransferredAndPresent,
                                 String SDWrongDepartmentAndPresent, String totalShiftDifferential, String totalDays, String terminated,
                                 String resigned, String transferred, String deserted, String wrongDepartment){
        this.nOverTime = nOverTimeSummary;
        this.dOvertime = dOvertimeSummary;
        this.absent = absent;
        this.suspension = suspension;
        this.absentAndSuspension = absentAndSuspension;
        this.compLeave = compLeave;
        this.annualLeave = annualLeave;
        this.annualPassageLeave = annualPassageLeave;
        this.specialLeave = specialLeave;
        this.sickLeave = sickLeave;
        this.maternityLeave = maternityLeave;
        this.otherUnpaidLeave = otherUnpaidLeave;
        this.present = present;
        this.presentOnFreeDay = presentOnFreeDay;
        this.awayOnDuty = awayOnDuty;
        this.standby = standby;
        this.freedays = freedays;
        this.excessFreeDays = excessFreeDays;
        this.SDPresent = SDPresent;
        this.SDPresentOnFreeDay = SDPresentOnFreeDay;
        this.SDTransferredAndPresent = SDTransferredAndPresent;
        this.SDWrongDepartmentAndPresent = SDWrongDepartmentAndPresent;
        this.totalShiftDifferential = totalShiftDifferential;
        this.totalDays = totalDays;
        this.terminated = terminated;
        this.resigned = resigned;
        this.transferred = transferred;
        this.deserted = deserted;
        this.wrongDepartment = wrongDepartment;
    }

}
