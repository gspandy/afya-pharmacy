package com.zpc.timesheet.dto;

import com.zpc.timesheet.domain.model.Timesheet;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.joda.time.LocalDate;

import java.util.Date;

/**
 * Created by Administrator on 9/24/2014.
 */
@Getter
@Setter
@AllArgsConstructor
public class TimesheetDto {
    private String timesheetId;
    public LocalDate fromDate;
    public LocalDate toDate;
    public String description;
    public Timesheet.Status status;

    public TimesheetDto(){}

    public void setFromDate(Date fromDate){
        this.fromDate = new LocalDate(fromDate);
    }

    public void setToDate(Date toDate){
        this.toDate = new LocalDate(toDate);
    }
}
