package com.zpc.timesheet.application;

import lombok.AllArgsConstructor;
import lombok.ToString;
import org.joda.time.LocalDate;

import javax.validation.constraints.NotNull;

/**
 * Author: Nthdimenzion
 */

@AllArgsConstructor
@ToString(includeFieldNames = true)
public class AddScheduleCommand {

    @NotNull
    public String description;

    @NotNull
    public LocalDate fromDate;

    @NotNull
    public LocalDate thruDate;

}
