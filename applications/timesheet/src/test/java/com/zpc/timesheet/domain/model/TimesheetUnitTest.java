package com.zpc.timesheet.domain.model;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.zpc.timesheet.dto.AttendanceRegisterDto;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.nthdimenzion.ddd.domain.model.Interval;
import org.nthdimenzion.object.StubIdGenerator;
import org.nthdimenzion.object.utils.IIdGenerator;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.*;

import static com.zpc.timesheet.domain.model.DayEntry.ascendingOrderByDate;
import static com.zpc.timesheet.domain.model.Timesheet.GenerateNewTimesheet;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.nthdimenzion.object.utils.Constants.DEFAULT_DATE_FORMAT;

/**
 * Author: Nthdimenzion
 */

public class TimesheetUnitTest {

    String scheduleId = "SC001";
    String departmentIdOne = "DEPT001";
    Set<String> employeeIdForDept1 = new HashSet<>();

    String departmentIdTwo = "DEPT002";
    Set<String> employeeIdForDept2 = Sets.newHashSet("EMP_1_001,EMP_2_002,EMP_3_003");
    static LocalDate startDate = DEFAULT_DATE_FORMAT.parseLocalDate("05-10-2010");
    private static final Interval TWENTY_DAY_SCHEDULE_PERIOD = new Interval(startDate.toDateTimeAtCurrentTime(), startDate.plusDays(20).toDateTimeAtCurrentTime());
    List<Map<String, Object>> listOfMapOfEmployee = Lists.newArrayList();
    @Before
    public void setup(){
        employeeIdForDept1.add("EMP_1_001");
        employeeIdForDept1.add("EMP_1_002");
        employeeIdForDept1.add("EMP_1_003");

        listOfMapOfEmployee.add(new LinkedHashMap<String, Object>(){
            {put("employeeId","EMP_1_003");put("name","Ravi");}});
        listOfMapOfEmployee.add(new LinkedHashMap<String, Object>(){
            {put("employeeId","EMP_1_002");put("name","Sharma");}});
        listOfMapOfEmployee.add(new LinkedHashMap<String, Object>(){
            {put("employeeId","EMP_1_001");put("name","Mohan");}});


    }

    @Test
    public void givenASchedulePeriod_itShouldCalculateDayEntriesForThatPeriod() {
        final Set<DayEntry> dayEntries = DayEntry.MakeEmptyDayEntries(TWENTY_DAY_SCHEDULE_PERIOD);
        assertThat(dayEntries.size(), is(21));
        final DayEntry firstDay = Collections.min(dayEntries, ascendingOrderByDate);
        final DayEntry lastDay = Collections.max(dayEntries, ascendingOrderByDate);
        assertThat(firstDay,is(equalTo(DayEntry.EmptyDayEntry(startDate))));
        assertThat(lastDay,is(equalTo(DayEntry.EmptyDayEntry(startDate.plusDays(20)))));
    }

    @Test
    @Ignore
    public void givenATimesheetWithMultipleAttendanceRegisters_whenFirstAttendanceRegisterIsSaved_itShouldChangeScheduleStatusToActive() {
        IIdGenerator idGenerator = new StubIdGenerator("1");
        Timesheet timesheet = GenerateNewTimesheet(idGenerator, scheduleId);
        timesheet.addNewAttendanceRegister(idGenerator, employeeIdForDept1, TWENTY_DAY_SCHEDULE_PERIOD, departmentIdOne);
        idGenerator = new StubIdGenerator("2");
        timesheet.addNewAttendanceRegister(idGenerator, employeeIdForDept2, TWENTY_DAY_SCHEDULE_PERIOD, departmentIdTwo);

        AttendanceRegisterDto attendanceRegisterDto = new AttendanceRegisterDto();
        attendanceRegisterDto.attendanceRegisterId = "2";
        timesheet.saveAttendanceRegister(idGenerator, attendanceRegisterDto);

        assertThat(timesheet.getUncommittedEventCount(), is(1));


    }

    @Test
    @Ignore
    public void givenATimesheetWithMultipleAttendanceRegisters_whenSecondAttendanceRegisterIsSaved_itShouldNotUpdateScheduleStatus() {
        IIdGenerator idGenerator = new StubIdGenerator("1");
        Timesheet timesheet = GenerateNewTimesheet(idGenerator, scheduleId);
        timesheet.addNewAttendanceRegister(idGenerator, employeeIdForDept1, TWENTY_DAY_SCHEDULE_PERIOD, departmentIdOne);
        idGenerator = new StubIdGenerator("2");
        timesheet.addNewAttendanceRegister(idGenerator, employeeIdForDept2, TWENTY_DAY_SCHEDULE_PERIOD, departmentIdTwo);
        AttendanceRegisterDto attendanceRegisterDto = new AttendanceRegisterDto();
        attendanceRegisterDto.attendanceRegisterId = "1";
        timesheet.saveAttendanceRegister(idGenerator, attendanceRegisterDto);
        timesheet.commitEvents();

        AttendanceRegisterDto secondAttendanceRegister = new AttendanceRegisterDto();
        secondAttendanceRegister.attendanceRegisterId = "2";
        timesheet.saveAttendanceRegister(idGenerator, secondAttendanceRegister);

        assertThat(timesheet.getUncommittedEventCount(), is(0));


    }

    @Test
    @Ignore
    public void givenATimesheet_whenAllAttendanceRegistersAreSubmitted_itShouldCompleteTimesheetSubmissionProcess() {
        IIdGenerator idGenerator = new StubIdGenerator("1");
        AttendanceRegister attendanceRegister =  TestDataUtil.getDummyAttendanceRegister();
        Timesheet timesheet = GenerateNewTimesheet(idGenerator, scheduleId);
        ReflectionTestUtils.setField(attendanceRegister,"status", AttendanceRegister.Status.SUBMITTED);
        Set<AttendanceRegister> attendanceRegisterx = Sets.newHashSet();
        attendanceRegisterx.add(attendanceRegister);
        ReflectionTestUtils.setField(timesheet,"attendanceRegisters", attendanceRegisterx);
        timesheet.markSubmissionCompletedWhenAllAttendanceRegistersAreSubmitted();
        assertThat("SC",is(timesheet.getTimeSheetStatus().name()));
    }

    @Test
    @Ignore
    public void givenATimesheet_whenAllAttendanceRegistersAreNotSubmitted_itShouldNotCompleteTimesheetSubmissionProcessAndStatusShouldBeSIP() {
        IIdGenerator idGenerator = new StubIdGenerator("1");
        AttendanceRegister attendanceRegister =  TestDataUtil.getDummyAttendanceRegister();
        Timesheet timesheet = GenerateNewTimesheet(idGenerator, scheduleId);
        ReflectionTestUtils.setField(attendanceRegister,"status", AttendanceRegister.Status.SAVED);
        Set<AttendanceRegister> attendanceRegisterx = Sets.newHashSet();
        attendanceRegisterx.add(attendanceRegister);
        ReflectionTestUtils.setField(timesheet,"attendanceRegisters", attendanceRegisterx);
        timesheet.markSubmissionCompletedWhenAllAttendanceRegistersAreSubmitted();
        assertThat("SIP",is(timesheet.getTimeSheetStatus().name()));
    }

    @Test
    public void givenAListContainingMapOfEmployeeObjects_shouldReturnASetContainingTheEmployeeIds(){
        Set<String> expectedSetOfEmployeeIds = Timesheet.extractEmployeeIds(listOfMapOfEmployee);
//        assertThat(employeeIdForDept1, is(expectedSetOfEmployeeIds));
        assertThat(expectedSetOfEmployeeIds.size(), is(3));
        assertThat(expectedSetOfEmployeeIds, is(employeeIdForDept1));
        assertThat(employeeIdForDept1, is(expectedSetOfEmployeeIds));
    }

}
