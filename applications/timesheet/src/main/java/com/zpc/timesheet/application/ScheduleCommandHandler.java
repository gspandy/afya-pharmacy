package com.zpc.timesheet.application;

import com.zpc.timesheet.domain.model.Schedule;
import com.zpc.timesheet.query.TimesheetFinder;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.axonframework.commandhandling.annotation.CommandHandler;
import org.axonframework.repository.AbstractRepository;
import org.axonframework.repository.Repository;
import org.nthdimenzion.object.utils.IIdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Author: Nthdimenzion
 */

@Component
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class ScheduleCommandHandler {

    private Repository<Schedule> scheduleRepository;

    private IIdGenerator idGenerator;

    private TimesheetFinder timesheetFinder;

    @Autowired
    public ScheduleCommandHandler(Repository<Schedule> scheduleRepository, IIdGenerator idGenerator, TimesheetFinder timesheetFinder) {
        this.scheduleRepository = scheduleRepository;
        this.idGenerator = idGenerator;
        this.timesheetFinder = timesheetFinder;
    }


    @CommandHandler
    public String addSchedule(AddScheduleCommand addScheduleCommand){
        final Schedule schedule = Schedule.MakeNewSchedule(idGenerator.nextId(), addScheduleCommand.fromDate, addScheduleCommand.thruDate, addScheduleCommand.description);
        scheduleRepository.add(schedule);
        return schedule.getIdentifier();
    }


    @CommandHandler
    public void modifyScheduleCommand(ModifyScheduleCommand modifyScheduleCommand){
        Schedule schedule = scheduleRepository.load(modifyScheduleCommand.id);
        schedule.modify(modifyScheduleCommand.fromDate,modifyScheduleCommand.thruDate,modifyScheduleCommand.description, modifyScheduleCommand.timesheetId);
    }

    @CommandHandler
    public void releaseSchedule(ReleaseScheduleCommand releaseScheduleCommand){
        Schedule schedule = scheduleRepository.load(releaseScheduleCommand.scheduleId);
        schedule.release(releaseScheduleCommand.fromDate,releaseScheduleCommand.toDate,releaseScheduleCommand.description);
    }

    @CommandHandler
    public void removeSchedule(RemoveScheduleCommand removeScheduleCommand){
        Schedule schedule = scheduleRepository.load(removeScheduleCommand.scheduleId);
        schedule.remove();
    }
}

