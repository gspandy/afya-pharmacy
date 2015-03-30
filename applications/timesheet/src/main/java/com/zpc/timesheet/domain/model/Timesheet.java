package com.zpc.timesheet.domain.model;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.zpc.timesheet.domain.AttendanceRegisterExportedEvent;
import com.zpc.timesheet.domain.FirstAttendanceRegisterSavedEvent;
import com.zpc.timesheet.dto.AttendanceEntryDto;
import com.zpc.timesheet.dto.AttendanceRegisterDto;
import lombok.*;
import org.axonframework.domain.AbstractAggregateRoot;
import org.nthdimenzion.ddd.domain.annotations.PPT;
import org.nthdimenzion.ddd.domain.model.Interval;
import org.nthdimenzion.object.utils.IIdGenerator;

import javax.persistence.*;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Author: Nthdimenzion
 */


@Entity
@PPT
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@ToString(of = "id",includeFieldNames = true)
@EqualsAndHashCode(of = "id")
@Table(name = "timesheet_entry")
public class Timesheet extends AbstractAggregateRoot<String> {

    public void delete() {
        markDeleted();
    }

    public enum Status {
        SIP{
            public String toString(){
                return "SUBMISSION IN PROCESS";
            }
        },SC{
            public String toString(){
                return "SUBMISSION COMPLETED";
            }
        },VERIFIED,EXPORTED;
    }


    @Id
    private String id;

    private String scheduleId;

    @OneToMany(cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    @JoinColumn(name = "TIMESHEET_ID")
    private Set<AttendanceRegister> attendanceRegisters = Sets.newHashSet();

    @Enumerated(EnumType.STRING)
    private Status status;

    public String getIdentifier() {
        return id;
    }

    public Timesheet(String id, String scheduleId) {
        this.id = id;
        this.scheduleId = scheduleId;
        this.status = Status.SIP;
    }

    public static Timesheet GenerateNewTimesheet(IIdGenerator id,String scheduleId){
        return new Timesheet(id.nextId(),scheduleId);
    }

    public void addNewAttendanceRegister(IIdGenerator idGenerator,Set<String> employees,Interval schedulePeriod,String departmentId){
        final AttendanceRegister attendanceRegister = AttendanceRegister.OpenNewAttendanceRegister(idGenerator, employees, schedulePeriod, departmentId);
        attendanceRegisters.add(attendanceRegister);
    }

    public void saveAttendanceRegister(IIdGenerator idGenerator,AttendanceRegisterDto attendanceRegisterDto) {
        removeTerminatedEmployees(attendanceRegisterDto);
        if(isFirstAttendanceRegisterSaved()){
            registerEvent(new FirstAttendanceRegisterSavedEvent(id,scheduleId));
        }
        final AttendanceRegister attendanceRegister = Iterables.find(attendanceRegisters, new AttendanceRegisterPredicate(attendanceRegisterDto.attendanceRegisterId));
        attendanceRegister.save(idGenerator,attendanceRegisterDto.attendanceEntries);
    }

    private void removeTerminatedEmployees(AttendanceRegisterDto attendanceRegisterDto) {
        String departmentId = attendanceRegisterDto.departmentId;
        List<AttendanceEntryDto> attendanceEntryDtoList = attendanceRegisterDto.attendanceEntries;
        AttendanceEntryDto.removeUnWantedEmployees(departmentId, attendanceEntryDtoList, attendanceRegisterDto.fromDate.toDate());
    }

    private boolean isFirstAttendanceRegisterSaved() {
        return !Iterables.any(attendanceRegisters, savedAttendanceRegisterPredicate);
    }

    public void submitAttendanceRegister(final String attendanceRegisterId){
        Preconditions.checkState(status == Status.SIP);
        final AttendanceRegister attendanceRegister = Iterables.find(attendanceRegisters, new AttendanceRegisterPredicate(attendanceRegisterId));
        attendanceRegister.submit();
        markSubmissionCompletedWhenAllAttendanceRegistersAreSubmitted();
    }

    public void verify(){
        Preconditions.checkState(status == Status.SC);
        this.status = Status.VERIFIED;
    }

    public void exported(){
        Preconditions.checkState(status == Status.VERIFIED || status == Status.EXPORTED );
        this.status = Status.EXPORTED;
        registerEvent(new AttendanceRegisterExportedEvent(id));
    }

    void markSubmissionCompletedWhenAllAttendanceRegistersAreSubmitted() {
        if(CheckIfAllAttendanceRegistersAreSubmitted(attendanceRegisters)){
            this.status = Status.SC;
        }
    }

    static boolean CheckIfAllAttendanceRegistersAreSubmitted(Set<AttendanceRegister> attendanceRegisters) {
        for (AttendanceRegister attendanceRegister : attendanceRegisters) {
            if(!attendanceRegister.isSubmitted())
                return false;
        }
        return true;
    }


    public static Set<String> extractEmployeeIds(List<Map<String, Object>> employees){
        final Set<String> employeeIds = Sets.newHashSet();
        for (Map<String, Object> employee : employees) {
            employeeIds.add((String) employee.get("employeeId"));
        }
        return employeeIds;
    }


    @Transient
    private Predicate<AttendanceRegister> savedAttendanceRegisterPredicate = new SavedAttendanceRegisterPredicate();

    private static class AttendanceRegisterPredicate implements Predicate<AttendanceRegister> {

        private final String attendanceRegisterId;

        AttendanceRegisterPredicate(String attendanceRegisterId) {
            this.attendanceRegisterId = attendanceRegisterId;
        }

        public boolean apply(AttendanceRegister input) {
            return input.getId().equals(attendanceRegisterId);
        }

    }

    private static class SavedAttendanceRegisterPredicate implements Predicate<AttendanceRegister> {

        public boolean apply(AttendanceRegister input) {
            return input.isSaved();
        }

    }

    public Status getTimeSheetStatus(){
        return this.status;
    }

}
