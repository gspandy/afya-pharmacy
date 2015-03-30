package com.zpc.timesheet.domain.model;

import com.google.common.base.Preconditions;
import com.zpc.timesheet.domain.ScheduleReleasedEvent;
import com.zpc.timesheet.domain.TimesheetModifyEvent;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.axonframework.domain.AbstractAggregateRoot;
import org.hibernate.annotations.Type;
import org.joda.time.LocalDate;
import org.nthdimenzion.ddd.domain.annotations.PPT;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

/**
 * Author: Nthdimenzion
 */

@Entity
@PPT
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Getter(AccessLevel.PACKAGE)
@ToString(of = "id",includeFieldNames = true)
public class Schedule extends AbstractAggregateRoot<String> {

    public void remove() {
        markDeleted();
    }

    public enum Status{
        PENDING,RELEASED,ACTIVE,CLOSED
    }

    @Id
    private String id;

    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    @NotNull
    private LocalDate fromDate;

    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    @NotNull
    private LocalDate toDate;

    private String description;

    @Enumerated(EnumType.STRING)
    private Status status;

    Schedule(String id, LocalDate fromDate, LocalDate toDate, String description) {
        Preconditions.checkArgument(fromDate.isBefore(toDate));
        this.id = id;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.description = description;
        this.status = Status.PENDING;
    }

    @Override
    public String getIdentifier() {
        return id;
    }


    public static Schedule MakeNewSchedule(String id, LocalDate fromDate, LocalDate toDate, String description){
        return new Schedule(id,fromDate,toDate,description);
    }

    public void release(LocalDate fromDate,LocalDate toDate,String description){
        Preconditions.checkState(status == Status.PENDING);
        Preconditions.checkArgument(fromDate.isBefore(toDate));
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.description = description;
        this.status = Status.RELEASED;
        registerEvent(new ScheduleReleasedEvent(id,this.fromDate,this.toDate));
    }

    public void activate(){
        this.status = Status.ACTIVE;
    }

    public void close(){
        this.status = Status.CLOSED;
    }

    public void modify(LocalDate fromDate,LocalDate toDate,String description,String timesheetId){
        Preconditions.checkState(status == Status.PENDING || status == Status.RELEASED);
        Preconditions.checkArgument(fromDate.isBefore(toDate));
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.description = description;
        if(timesheetId != null) {
            registerEvent(new TimesheetModifyEvent(id, this.fromDate, this.toDate, timesheetId));
        }
    }

}
