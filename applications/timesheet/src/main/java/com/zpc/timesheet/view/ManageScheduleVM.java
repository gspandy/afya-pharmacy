package com.zpc.timesheet.view;

import com.google.common.collect.Lists;
import com.zpc.sharedkernel.ofbiz.OfbizGateway;
import com.zpc.timesheet.application.AddScheduleCommand;
import com.zpc.timesheet.application.ModifyScheduleCommand;
import com.zpc.timesheet.application.ReleaseScheduleCommand;
import com.zpc.timesheet.application.RemoveScheduleCommand;
import com.zpc.timesheet.domain.model.Schedule;
import com.zpc.timesheet.dto.ScheduleDto;
import com.zpc.timesheet.query.TimesheetFinder;
import lombok.Getter;
import lombok.Setter;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.joda.time.LocalDate;
import org.nthdimenzion.common.service.SpringApplicationContext;
import org.nthdimenzion.object.utils.UtilDateTime;
import org.nthdimenzion.object.utils.UtilValidator;
import org.ofbiz.service.LocalDispatcher;
import org.zkoss.bind.ValidationContext;
import org.zkoss.bind.Validator;
import org.zkoss.bind.annotation.*;
import org.zkoss.bind.validator.AbstractValidator;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zul.Messagebox;

import java.util.List;
import java.util.Map;

/**
 * Author: Nthdimenzion
 */

public class ManageScheduleVM {

    enum ButtonLabel {
        ADD {
            public String toString() {
                return "Add";
            }
        },
        UPDATE {
            public String toString() {
                return "Update Schedule";
            }
        }
    }


    enum GridLabel {
        ADD {
            public String toString() {
                return "Add a new Schedule";
            }
        },
        UPDATE {
            public String toString() {
                return "Update Schedule";
            }
        }
    }

    private TimesheetFinder timesheetFinder;
    private CommandGateway commandGateway;

    @Getter
    @Setter
    private boolean canUpdateSchedule = true;

    @Getter
    @Setter
    private ScheduleDto schedule;

    List<ScheduleDto> schedules = Lists.newArrayList();

    @Getter
    @Setter
    private String changeScheduleGridLabel = GridLabel.ADD.toString();

    @Getter
    @Setter
    private String changeScheduleButtonLabel = ButtonLabel.ADD.toString();

    private final LocalDispatcher dispatcher = OfbizGateway.getDispatcher();
    private final Map<String,Object> userLogin = OfbizGateway.getUser();



    @Init
    public void init() {
        timesheetFinder = SpringApplicationContext.getBean("timesheetFinder");
        commandGateway = SpringApplicationContext.getCommandGateway();
        String managerId = OfbizGateway.getPartyId();
        Boolean isAdmin = (Boolean) OfbizGateway.getTimesheetInfo().get("isAdmin");
        refreshSchedules();
       // System.out.println(timesheetFinder.getAnnualLeaveDetailsOfEmployee(managerId,"LT_ANNUAL"));
    }

    @AfterCompose
    public void afterCompose(@ContextParam(ContextType.VIEW) Component view) {
        Selectors.wireComponents(view, this, false);
    }


    public List<ScheduleDto> getSchedules() {
        return schedules;
    }

    public void setSchedules(List<ScheduleDto> schedules) {
        this.schedules = schedules;
    }

    @Command
    @NotifyChange({"schedules"})
    public void release(@BindingParam("schedule") ScheduleDto scheduleDto) {
        ReleaseScheduleCommand releaseScheduleCommand = new ReleaseScheduleCommand(scheduleDto.id, scheduleDto.description, scheduleDto.fromDate, scheduleDto.toDate,userLogin);
        commandGateway.sendAndWait(releaseScheduleCommand);
        Messagebox.show("Schedule Successfully Released","Notification",
                new Messagebox.Button[]{Messagebox.Button.OK},null,
                new EventListener<Messagebox.ClickEvent>() {
                    public void onEvent(Messagebox.ClickEvent event) {
                        if ("onOK".equals(event.getName())) {
                            refreshSchedules();
                            Executions.sendRedirect("/");
                        }
                    }
                });
    }

    @Command
    @NotifyChange({"schedules", "canAddSchedule", "schedule", "canChangeScheduleButtonClicked", "changeScheduleGridLabel", "changeScheduleButtonLabel", "fromDateConstraint", "endDateConstraint"})
    public void addSchedule() {
        schedule = new ScheduleDto();
        schedule = initialiseNewScheduleDateRange(schedule, schedules);
        canUpdateSchedule = false;
        changeScheduleGridLabel = GridLabel.ADD.toString();
        changeScheduleButtonLabel = ButtonLabel.ADD.toString();
    }

    private ScheduleDto initialiseNewScheduleDateRange(ScheduleDto newSchedule, List<ScheduleDto> schedules) {
        if (UtilValidator.isNotEmpty(schedules)) {
            final ScheduleDto lastSchedule = schedules.get(0);
            LocalDate startDate = lastSchedule.getToDate().plusDays(1);
            newSchedule.setStartDate(startDate.toDate());
            //newSchedule.description = UtilDateTime.months[startDate.getMonthOfYear()];
            //LocalDate endDate = startDate.plusDays(ScheduleDto.MAX_DAYS_IN_ATTENDANCE_REGISTER);
            //newSchedule.setEndDate(endDate.toDate());
            return newSchedule;
        } else {
            newSchedule = new ScheduleDto();
        }
        return newSchedule;
    }

    @Command
    @NotifyChange({"schedules", "canAddSchedule", "canChangeScheduleButtonClicked"})
    public void changeSchedule() {
        if(schedules.size()>0 && changeScheduleButtonLabel.equals("Add")){
            for(ScheduleDto scheduleOfList : schedules){
                if(scheduleOfList.description.equalsIgnoreCase(schedule.description)){
                    Messagebox.show("Schedule already exist with given description","Notification",
                            new Messagebox.Button[]{Messagebox.Button.OK},null,
                            new EventListener<Messagebox.ClickEvent>() {
                                public void onEvent(Messagebox.ClickEvent event) {
                                    if ("onOK".equals(event.getName())) {}
                                }
                            });
                    return;
                }
            }
        }
        if (schedule.isNotValid()) {
            return;
        }
        final boolean scheduleOverlapping = schedule.isScheduleOverlappingAnyPreviousSchedule(schedules);
        if (scheduleOverlapping) {
            Messagebox.show("Schedule is overlapping","Notification",
                    new Messagebox.Button[]{Messagebox.Button.OK},null,
                    new EventListener<Messagebox.ClickEvent>() {
                        public void onEvent(Messagebox.ClickEvent event) {
                            if ("onOK".equals(event.getName())) {} } });
            return;
        }

        if (changeScheduleButtonLabel.equals(ButtonLabel.ADD.toString())) {
            AddScheduleCommand addScheduleCommand = new AddScheduleCommand(schedule.description, schedule.fromDate, schedule.toDate);
            commandGateway.sendAndWait(addScheduleCommand);
        } else {
            String timesheetId = null;
            if(schedule.status.equals("RELEASED")) {
               timesheetId = timesheetFinder.getTimesheetIdForGivenSchedule(schedule.id);
            }
            ModifyScheduleCommand modifyScheduleCommand = new ModifyScheduleCommand(schedule.id, schedule.description, schedule.fromDate, schedule.toDate, timesheetId);
            commandGateway.sendAndWait(modifyScheduleCommand);
        }
        refreshSchedules();
        canUpdateSchedule = true;
        schedule = null;
    }

    private void refreshSchedules() {
        schedules = timesheetFinder.getAllSchedules();
    }


    @Command
    @NotifyChange({"schedules", "canAddSchedule", "canChangeScheduleButtonClicked"})
    public void cancelAddingOfSchedule() {
        refreshSchedules();
        canUpdateSchedule = true;
        schedule = null;
    }

    @Command
    @NotifyChange({"schedule", "changeScheduleGridLabel", "changeScheduleButtonLabel", "canChangeScheduleButtonClicked", "fromDateConstraint", "endDateConstraint"})
    public void updateSchedule(@BindingParam("schedule") ScheduleDto scheduleDto) {
        this.schedule = scheduleDto.copy();
        this.schedule.setStartDate(scheduleDto.getFromDate().toDate());
        this.schedule.setEndDate(scheduleDto.getToDate().toDate());
        changeScheduleGridLabel = GridLabel.UPDATE.toString();
        changeScheduleButtonLabel = ButtonLabel.UPDATE.toString();
        canUpdateSchedule = false;
    }


    public boolean getCanChangeScheduleButtonClicked() {
        return !canUpdateSchedule;
    }

    @Command
    @NotifyChange({"schedules"})
    public void removeSchedule(@BindingParam("schedule") ScheduleDto scheduleDto) {
        final String scheduleDtoId = scheduleDto.id;
        Messagebox.show("Are you sure you want to remove the schedule?",
                "Warning", Messagebox.YES | Messagebox.NO,
                Messagebox.QUESTION,
                new EventListener<Event>(){
                    public void onEvent(Event e){
                        if(Messagebox.ON_YES.equals(e.getName())){
                            RemoveScheduleCommand removeScheduleCommand = new RemoveScheduleCommand(scheduleDtoId);
                            commandGateway.sendAndWait(removeScheduleCommand);
                            refreshSchedules();
                            Executions.sendRedirect("/");
                        }else
                        if(Messagebox.ON_NO.equals(e.getName())){ }
                    }
                }
        );
    }

    public Validator getFormValidator(){
        return new AbstractValidator() {
            public void validate(ValidationContext ctx) {
                ScheduleDto scheduleDto = (ScheduleDto)ctx.getProperties(".")[0].getBase();
                String description = scheduleDto.getDescription();
                LocalDate fromDate = scheduleDto.getFromDate();
                LocalDate toDate = scheduleDto.getToDate();
                if (description == null || description.isEmpty()) {
                            addInvalidMessage(ctx, "descriptionEmpty", "*Required ");
                }
                if (fromDate == null) {
                    addInvalidMessage(ctx, "fromDateEmpty", "*Required ");
                }
                if (toDate == null) {
                    addInvalidMessage(ctx, "toDateEmpty", "*Required ");
                }
            }
        };
    }
}
