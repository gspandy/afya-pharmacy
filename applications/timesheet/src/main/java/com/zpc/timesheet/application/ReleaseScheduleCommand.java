package com.zpc.timesheet.application;

import lombok.AllArgsConstructor;
import org.joda.time.LocalDate;

import javax.validation.constraints.NotNull;
import java.util.Map;

/**
 * Author: Nthdimenzion
 */

@AllArgsConstructor
public class ReleaseScheduleCommand {

    @NotNull
    public String scheduleId;

    @NotNull
    public String description;

    @NotNull
    public LocalDate fromDate;

    @NotNull
    public LocalDate toDate;

    @NotNull
    public Map<String,Object> userLogin;

}
