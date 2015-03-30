package com.zpc.timesheet.application;

import com.zpc.timesheet.dto.AttendanceRegisterDto;
import lombok.AllArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Author: Nthdimenzion
 */

@AllArgsConstructor
public class SubmitAttendanceRegisterCommand {

    @NotNull
    public String timesheetId;
    @NotNull
    public String loggedInUser;
    @NotNull
    public AttendanceRegisterDto attendanceRegisterDto;

}
