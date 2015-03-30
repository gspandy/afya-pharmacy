package com.zpc.timesheet.domain;

import lombok.AllArgsConstructor;
import lombok.ToString;
import net.sf.cglib.core.Local;
import org.joda.time.LocalDate;

import java.io.Serializable;

/**
 * Author: Nthdimenzion
 */

@AllArgsConstructor
@ToString
public class ScheduleReleasedEvent  implements Serializable {
    public final String scheduleId;
    public final LocalDate fromDate;
    public final LocalDate toDate;

}
