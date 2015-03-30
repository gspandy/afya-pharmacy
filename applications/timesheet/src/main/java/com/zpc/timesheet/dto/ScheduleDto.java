package com.zpc.timesheet.dto;

import com.zpc.timesheet.domain.model.Schedule;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.joda.time.Days;
import org.joda.time.Interval;
import org.joda.time.LocalDate;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Messagebox;

import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 9/17/2014.
 */
@Getter
@Setter
@AllArgsConstructor
@ToString
public class ScheduleDto {

    public static final int MAX_DAYS_IN_ATTENDANCE_REGISTER = 28;

    public String id;
    public LocalDate fromDate;
    public LocalDate toDate;
    public String description;
    public String status;

    public Date startDate;
    public Date endDate;


    public ScheduleDto copy(){
        return new ScheduleDto(this.id,this.description,this.fromDate,this.toDate,this.status);
    }

    public ScheduleDto(String id,String description, LocalDate fromDate, LocalDate toDate,String status) {
        this.id = id;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.description = description;
        this.status = status;
        this.startDate = fromDate.toDate();
        this.endDate= toDate.toDate();
    }


    public ScheduleDto() {
        status = Schedule.Status.PENDING.name();
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = new LocalDate(fromDate);
    }

    public void setToDate(Date toDate) {
        this.toDate = new LocalDate(toDate);
    }

    public static String betweenConstraint(LocalDate startDate, LocalDate endDate) {
        return "between " + startDate.toString("yyyyMMdd") + " and " + endDate.toString("yyyyMMdd");
    }

    public static String afterConstraint(LocalDate startDate) {
        return "after " + startDate.toString("yyyyMMdd");
    }

    public static String beforeConstraint(LocalDate startDate) {
        return "before " + startDate.toString("yyyyMMdd");
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
        this.fromDate = new LocalDate(startDate);
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
        if(endDate!=null)
            this.toDate = new LocalDate(endDate);
        else
            this.toDate = null;
    }

    public boolean isScheduleOverlappingAnyPreviousSchedule(List<ScheduleDto> schedules) {
        for (ScheduleDto schedule : schedules) {
            if(schedule.isScheduleOverlapping(this) && isADifferentSchedule(schedule)){
                return true;
            }
        }
        return false;
    }

    private boolean isADifferentSchedule(ScheduleDto schedule) {
        if(this.id==null)
            return true;
        else
            return !this.id.equals(schedule.id);
    }

    boolean isScheduleOverlapping(ScheduleDto scheduleDto){
        Interval currentScheduleInterval = new Interval(fromDate.toDate().getTime(),toDate.plusDays(1).toDate().getTime());
        Interval inputScheduleInterval = new Interval(scheduleDto.fromDate.toDate().getTime(),scheduleDto.toDate.plusDays(1).toDate().getTime());
        return currentScheduleInterval.overlaps(inputScheduleInterval);
    }

    public boolean isNotValid() {
         if(Days.daysBetween(fromDate,toDate.plusDays(1)).isGreaterThan(Days.days(MAX_DAYS_IN_ATTENDANCE_REGISTER))){
             Messagebox.show("Invalid Schedule: Number of days is greater than 28", "Notification",
                     new Messagebox.Button[]{Messagebox.Button.OK}, null,
                     new EventListener<Messagebox.ClickEvent>() {
                         public void onEvent(Messagebox.ClickEvent event) {
                             if ("onOK".equals(event.getName())) {
                             }
                         }
                     });
             return true;
         }
        if(fromDate.isAfter(toDate)){
            Messagebox.show("From-Date is after To-Date","Notification",
                    new Messagebox.Button[]{Messagebox.Button.OK},null,
                    new EventListener<Messagebox.ClickEvent>() {
                        public void onEvent(Messagebox.ClickEvent event) {
                            if ("onOK".equals(event.getName())) {}
                        }
                    });
            return true;
        }
        if(fromDate.equals(toDate)){
            Messagebox.show("From-Date and To-Date are equal","Notification",
                    new Messagebox.Button[]{Messagebox.Button.OK},null,
                    new EventListener<Messagebox.ClickEvent>() {
                        public void onEvent(Messagebox.ClickEvent event) {
                            if ("onOK".equals(event.getName())) {}
                        }
                    });
            return true;
        }
        return  false;
    }
}
