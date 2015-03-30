package com.zpc.timesheet.domain;

import com.zpc.sharedkernel.ofbiz.OfbizGateway;
import com.zpc.timesheet.application.GenerateNewTimesheetCommand;
import com.zpc.timesheet.domain.model.Timesheet;
import lombok.NoArgsConstructor;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.joda.time.LocalDate;
import org.nthdimenzion.crud.ICrud;
import org.nthdimenzion.ddd.domain.annotations.DomainService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.transaction.Transactional;

/**
 * Author: Nthdimenzion
 */

@NoArgsConstructor
@DomainService
public class TimesheetService {

    private CommandGateway commandGateway;

    private ICrud crud;

    @Autowired
    public TimesheetService(CommandGateway commandGateway, ICrud crud) {
        this.commandGateway = commandGateway;
        this.crud = crud;
    }

    public String generateNewTimesheet(final String scheduleId,final LocalDate fromDate,final LocalDate toDate){
        GenerateNewTimesheetCommand generateNewTimesheetCommand = new GenerateNewTimesheetCommand(scheduleId, OfbizGateway.getUserId(),fromDate,toDate,(Boolean)OfbizGateway.getTimesheetInfo().get("isAdmin"));
        return commandGateway.sendAndWait(generateNewTimesheetCommand);
    }

    @Transactional
    public void deleteTimesheet(final String timesheetId){
        Timesheet timesheet = crud.find(Timesheet.class, timesheetId);
        crud.remove(timesheet);
    }
}
