package com.zpc.timesheet.domain;

import lombok.AllArgsConstructor;
import org.joda.time.LocalDate;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Created by Administrator on 11/13/2014.
 */
public class TimesheetModifyEvent implements Serializable {
    @NotNull
    public final String scheduleId;

    @NotNull
    public LocalDate scheduleStartDate;

    @NotNull
    public LocalDate scheduleEndDate;

    @NotNull
    public String timesheetId;

    public TimesheetModifyEvent(String scheduleId) {
        this.scheduleId = scheduleId;
    }

    public TimesheetModifyEvent(String scheduleId, LocalDate scheduleStartDate, LocalDate scheduleEndDate,String timesheetId) {
        this.scheduleId = scheduleId;
        this.scheduleStartDate = scheduleStartDate;
        this.scheduleEndDate = scheduleEndDate;
        this.timesheetId = timesheetId;
    }
}
