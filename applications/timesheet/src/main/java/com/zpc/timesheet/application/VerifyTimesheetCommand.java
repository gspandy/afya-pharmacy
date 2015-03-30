package com.zpc.timesheet.application;

import lombok.AllArgsConstructor;
import org.joda.time.LocalDate;

/**
 * Author: Nthdimenzion
 */

@AllArgsConstructor
public class VerifyTimesheetCommand {

    public String timesheetId;
    public String loggedInUser;

}
