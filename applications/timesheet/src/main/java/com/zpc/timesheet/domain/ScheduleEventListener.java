package com.zpc.timesheet.domain;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.axonframework.eventhandling.annotation.EventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Author: Nthdimenzion
 */

@Component
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class ScheduleEventListener {

    private TimesheetService timesheetService;

    @Autowired
    public ScheduleEventListener(TimesheetService timesheetService) {
        this.timesheetService = timesheetService;
    }

    @EventHandler
    public void generateNewTimesheetOnScheduleRelease(ScheduleReleasedEvent scheduleReleasedEvent){
        timesheetService.generateNewTimesheet(scheduleReleasedEvent.scheduleId,scheduleReleasedEvent.fromDate,scheduleReleasedEvent.toDate);
    }

    @EventHandler
    public void modifyTimesheetOnUpdateSchedule(TimesheetModifyEvent timesheetModifyEvent){
        timesheetService.deleteTimesheet(timesheetModifyEvent.timesheetId);
        timesheetService.generateNewTimesheet(timesheetModifyEvent.scheduleId, timesheetModifyEvent.scheduleStartDate, timesheetModifyEvent.scheduleEndDate);
    }
}
