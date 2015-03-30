package com.zpc.timesheet.domain;

import lombok.AllArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

/**
 * Created by Administrator on 10/1/2014.
 */
@AllArgsConstructor
@ToString
public class AttendanceRegisterExportedEvent implements Serializable {
    String timesheetId;
}
