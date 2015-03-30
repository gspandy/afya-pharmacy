package com.zpc.timesheet.domain.model;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.zpc.timesheet.dto.AttendanceEntryDto;
import lombok.*;
import org.nthdimenzion.ddd.domain.model.Interval;
import org.nthdimenzion.object.utils.IIdGenerator;
import org.nthdimenzion.object.utils.UtilValidator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Author: Nthdimenzion
 */

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@ToString(of = "id")
@Entity
@EqualsAndHashCode(of = "id")
class AttendanceRegister {


    public enum Status {
        NEW, SAVED, SUBMITTED, EXPORTED;
    }

    @Id
    @Getter(AccessLevel.PACKAGE)
    private String id;

    @OneToMany(cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    @JoinColumn(name = "ATTENDANCE_REGISTER_ID")
    private Set<EmployeeAttendanceEntry> employeeAttendanceEntries = Sets.newHashSet();

    @NotNull
    @Enumerated(EnumType.STRING)
    private Status status;

    private String departmentId;

    AttendanceRegister(String id, Set<EmployeeAttendanceEntry> employeeAttendanceEntries, String departmentId) {
        this.id = id;
        this.employeeAttendanceEntries = employeeAttendanceEntries;
        this.departmentId = departmentId;
        this.status = Status.NEW;
    }

    public static AttendanceRegister OpenNewAttendanceRegister(IIdGenerator idGenerator, Set<String> employees, Interval schedulePeriod, String departmentId) {
        Set<EmployeeAttendanceEntry> employeeAttendanceEntries = Sets.newHashSet();
        Set<DayEntry> dayEntries = DayEntry.MakeEmptyDayEntries(schedulePeriod);
        for (String employeeId : employees) {
            EmployeeAttendanceEntry employeeAttendanceEntry = EmployeeAttendanceEntry.EmptyAttendanceEntry(idGenerator.nextId(), employeeId, Sets.newHashSet(dayEntries));
            employeeAttendanceEntries.add(employeeAttendanceEntry);
        }
        AttendanceRegister attendanceRegister = new AttendanceRegister(idGenerator.nextId(), employeeAttendanceEntries, departmentId);
        return attendanceRegister;
    }

    public void save(IIdGenerator idGenerator,List<AttendanceEntryDto> attendanceEntries) {
        if(attendanceEntries.isEmpty()){
            if(this.status == Status.NEW || this.status == Status.SAVED)
                this.status = Status.SAVED;
            return;
        }
        Preconditions.checkArgument(idGenerator!=null && UtilValidator.isNotEmpty(attendanceEntries));
        if(this.status == Status.NEW)
            this.status = Status.SAVED;
        for (AttendanceEntryDto attendanceEntry : attendanceEntries) {
            final Optional<EmployeeAttendanceEntry> employeeAttendanceEntry = Iterables.tryFind(employeeAttendanceEntries, new EmployeeAttendanceEntryPredicate(attendanceEntry.attendanceEntryId));
            if(employeeAttendanceEntry.isPresent()){
                EmployeeAttendanceEntry oldEmployeeAttendanceEntry = employeeAttendanceEntry.get();
                oldEmployeeAttendanceEntry.recordComments(attendanceEntry.comments);
                oldEmployeeAttendanceEntry.recordDayEntries(attendanceEntry.dayEntryDtos);
                oldEmployeeAttendanceEntry.recordSummations(attendanceEntry.nOverTimeSummary,attendanceEntry.dOvertimeSummary,
                                                           attendanceEntry.absent,attendanceEntry.suspension,attendanceEntry.absentAndSuspension,attendanceEntry.compLeave,
                                                           attendanceEntry.annualLeave,attendanceEntry.annualPassageLeave,attendanceEntry.specialLeave,
                                                           attendanceEntry.sickLeave,attendanceEntry.maternityLeave,attendanceEntry.otherUnpaidLeave,
                                                           attendanceEntry.present,attendanceEntry.presentOnFreeDay,attendanceEntry.awayOnDuty,
                                                           attendanceEntry.standby,attendanceEntry.freedays,attendanceEntry.excessFreeDays,
                                                           attendanceEntry.SDPresent,attendanceEntry.SDPresentOnFreeDay,attendanceEntry.SDTransferredAndPresent,
                                                           attendanceEntry.SDWrongDepartmentAndPresent,attendanceEntry.totalShiftDifferential, attendanceEntry.totalDays, attendanceEntry.terminated, attendanceEntry.resigned, attendanceEntry.transferred, attendanceEntry.deserted, attendanceEntry.wrongDepartment);
            }else{
                EmployeeAttendanceEntry newEmployeeAttendanceEntry = EmployeeAttendanceEntry.EmptyAttendanceEntry(idGenerator.nextId(),attendanceEntry.employeeId, new HashSet<DayEntry>());
                newEmployeeAttendanceEntry.recordComments(attendanceEntry.comments);
                newEmployeeAttendanceEntry.recordDayEntries(attendanceEntry.dayEntryDtos);
                newEmployeeAttendanceEntry.recordSummations(attendanceEntry.nOverTimeSummary, attendanceEntry.dOvertimeSummary, attendanceEntry.absent, attendanceEntry.suspension, attendanceEntry.absentAndSuspension, attendanceEntry.compLeave, attendanceEntry.annualLeave, attendanceEntry.annualPassageLeave, attendanceEntry.specialLeave, attendanceEntry.sickLeave, attendanceEntry.maternityLeave, attendanceEntry.otherUnpaidLeave, attendanceEntry.present, attendanceEntry.presentOnFreeDay, attendanceEntry.awayOnDuty, attendanceEntry.standby, attendanceEntry.freedays, attendanceEntry.excessFreeDays, attendanceEntry.SDPresent, attendanceEntry.SDPresentOnFreeDay, attendanceEntry.SDTransferredAndPresent, attendanceEntry.nOverTimeSummary,attendanceEntry.dOvertimeSummary,attendanceEntry.totalDays, attendanceEntry.terminated, attendanceEntry.resigned, attendanceEntry.transferred, attendanceEntry.deserted, attendanceEntry.wrongDepartment);
                employeeAttendanceEntries.add(newEmployeeAttendanceEntry);
            }
        }

    }

    public void submit() {
        Preconditions.checkState(status == Status.SAVED);
        this.status = Status.SUBMITTED;
    }

    public boolean isSubmitted() {
        return status == Status.SUBMITTED;
    }

    public boolean isSaved() {
        return status == Status.SAVED;
    }




    private static class EmployeeAttendanceEntryPredicate implements Predicate<EmployeeAttendanceEntry> {

        private final String employeeAttendanceEntryId;

        EmployeeAttendanceEntryPredicate(String employeeAttendanceEntryId) {
            this.employeeAttendanceEntryId = employeeAttendanceEntryId;
        }

        public boolean apply(EmployeeAttendanceEntry input) {
            return input.getId().equals(employeeAttendanceEntryId);
        }

    }


}

