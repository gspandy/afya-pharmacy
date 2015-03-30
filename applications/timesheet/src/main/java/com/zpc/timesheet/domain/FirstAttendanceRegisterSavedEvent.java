package com.zpc.timesheet.domain;

import lombok.AllArgsConstructor;
import lombok.ToString;

/**
 * Author: Nthdimenzion
 */

@AllArgsConstructor
@ToString
public class FirstAttendanceRegisterSavedEvent {
    public final String id;
    public final String scheduleId;
}
