package com.zpc.timesheet.dto;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.ToString;
import org.joda.time.LocalDate;
import org.nthdimenzion.object.utils.Constants;

import java.util.Date;
import java.util.List;

/**
 * Author: Nthdimenzion
 */

@Getter
@ToString(includeFieldNames = true)
public class DayEntryDto {


    public String date;
    public String day;
    public String shiftOne;
    public String shiftTwo;
    public String nOvertime;
    public String dOvertime;
    public List<String> values;


    public List<String> getValues(){
        return Lists.newArrayList(shiftOne,shiftTwo,nOvertime,dOvertime);
    }

    public String getDay(){
        final LocalDate parse = LocalDate.parse(date, Constants.URL_DATE_FORMAT);
        return parse.dayOfWeek().getAsText();
    }

    public String getDayOfMonth(){
        final LocalDate parse = LocalDate.parse(date, Constants.URL_DATE_FORMAT);
        return parse.toString("dd-MMM-yyyy");//dayOfMonth().getAsText();
    }

    public LocalDate getLastDateOfMonth(String date){
        final LocalDate parse = LocalDate.parse(date, Constants.URL_DATE_FORMAT);
        return parse;
    }
}
