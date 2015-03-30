package com.zpc.timesheet.application;

import com.zpc.timesheet.dto.AttendanceRegisterDto;
import lombok.AllArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Author: Nthdimenzion
 */

@AllArgsConstructor
@ToString(includeFieldNames = true)
public class SaveTimesheetCommand {

    @NotNull
    public String timesheetId;
    @NotNull
    public List<AttendanceRegisterDto> attendanceRegisterDtos;
    @NotNull
    public String loggedInUser;

}
