package com.zpc.timesheet.application;

import org.joda.time.LocalDate;

import javax.validation.constraints.NotNull;
import java.util.Map;

/**
 * Author: Nthdimenzion
 */



public class GenerateNewTimesheetCommand {

    @NotNull
    public String scheduleId;
    @NotNull
    public String loggedInUser;
    @NotNull
    public LocalDate scheduleStartDate;
    @NotNull
    public LocalDate scheduleEndDate;
    @NotNull
    public boolean isAdmin;

    public GenerateNewTimesheetCommand(String scheduleId,String loggedInUser,LocalDate scheduleStartDate,LocalDate scheduleEndDate,boolean isAdmin) {
        this.scheduleId = scheduleId;
        this.loggedInUser = loggedInUser;
        this.scheduleStartDate = scheduleStartDate;
        this.scheduleEndDate = scheduleEndDate;
        this.isAdmin = isAdmin;
    }
}
