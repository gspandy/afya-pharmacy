package com.zpc.timesheet.domain;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.axonframework.eventhandling.annotation.EventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.Collections;

/**
 * Created by Administrator on 9/24/2014.
 */
@Component
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class TimesheetEventListener {

    private static final String MARK_SCHEDULE_AS_ACTIVE = "update schedule set status = 'ACTIVE' where id = :scheduleId";
    private static final String MARK_ATTENDANCE_REGISTER_AS_EXPORTED = "update attendance_register set status = 'EXPORTED' where TIMESHEET_ID = :timesheetId";
    private NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    public TimesheetEventListener(DataSource dataSource) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    @EventHandler
    public void markScheduleAsActive(FirstAttendanceRegisterSavedEvent firstAttendanceRegisterSavedEvent){
        jdbcTemplate.update(MARK_SCHEDULE_AS_ACTIVE, Collections.singletonMap("scheduleId",firstAttendanceRegisterSavedEvent.scheduleId));
    }

    @EventHandler
    public void changeAttendanceRegisterStatusToExported(AttendanceRegisterExportedEvent attendanceRegisterExportedEvent){
        jdbcTemplate.update(MARK_ATTENDANCE_REGISTER_AS_EXPORTED,Collections.singletonMap("timesheetId", attendanceRegisterExportedEvent.timesheetId));
    }

}
