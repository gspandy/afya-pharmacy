package com.zpc.timesheet.dto;

import com.google.common.collect.Lists;
import org.joda.time.LocalDate;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Author: Nthdimenzion
 */

public class ScheduleDtoUnitTest {
    private List<ScheduleDto> listOfSchedules = Lists.newArrayList();

    public void setup(){
        listOfSchedules.add(new ScheduleDto("1","january", new LocalDate(2014,1,1),new LocalDate(2014,1,1).plusDays(31),"Pending"));
        listOfSchedules.add(new ScheduleDto("2","February", new LocalDate(2014,1,1),new LocalDate(2014,1,1).plusDays(31),"Pending"));
    }

    @Test
    public void givenASchedule_whenTheScheduleDurationIsGreaterThan30Days_itShouldBeMarkedAsInvalid(){
        ScheduleDto scheduleDto = new ScheduleDto("id","invalid schedule", LocalDate.now(),LocalDate.now().plusDays(31),"PENDING");
        assertThat(scheduleDto.isNotValid(),is(true));
    }

    @Test
    @Ignore
    public void givenASchedule_whenTheScheduleDurationIsExactly30Days_itShouldBeMarkedAsValid(){
        ScheduleDto scheduleDto = new ScheduleDto("id","invalid schedule", LocalDate.now(),LocalDate.now().plusDays(30),"PENDING");
        assertThat(scheduleDto.isNotValid(),is(false));
    }

    @Test
    public void givenAListOfSchedules_whenAScheduleDoesNotOverlapAnyPreviousSchedule_thenNewScheduleShouldBeCreated(){

    }

    @Test
    public void givenAListOfSchedules_whenAScheduleOverlapsAPreviousSchedule_thenNewScheduleShouldNotBeCreated(){

    }

    @Test
    public void givenAListOfSchedules_whenAScheduleHasEmptyScheduleId_thenNewScheduleShouldNotBeCreated(){

    }

}
