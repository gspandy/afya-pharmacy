package com.zpc.timesheet.application;

import lombok.AllArgsConstructor;
import org.joda.time.LocalDate;

import javax.validation.constraints.NotNull;

/**
 * Author: Nthdimenzion
 */

@AllArgsConstructor
public class ModifyScheduleCommand {

    @NotNull
    public String id;

    @NotNull
    public String description;

    @NotNull
    public LocalDate fromDate;

    @NotNull
    public LocalDate thruDate;

    public String timesheetId;
}
