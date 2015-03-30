package com.smebiz.common;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.ofbiz.base.util.UtilDateTime;

public class UtilDateTimeSME {
	
	public static java.util.Date toDateYMD(String dateTime, String delimiter) {
        if (dateTime == null) {
            return null;
        }
        // dateTime must have one space between the date and time...
        String date = dateTime.substring(0, dateTime.indexOf(" "));
        String time = dateTime.substring(dateTime.indexOf(" ") + 1);

        return toDateYMD(date, time, delimiter);
    }
    /**
     * Converts a date String and a time String into a Date
     *
     * @param date The date String: MM/DD/YYYY
     * @param time The time String: either HH:MM or HH:MM:SS
     * @return A Date made from the date and time Strings
     */
    public static java.util.Date toDate(String date, String time) {
        return toDate(date, time, "/");
    }

    
    public static java.util.Date toDate(String date, String time, String delimiter) {
        if (date == null || time == null) return null;
        String month;
        String day;
        String year;
        String hour;
        String minute;
        String second;

        int dateSlash1 = date.indexOf(delimiter);
        int dateSlash2 = date.lastIndexOf(delimiter);

        if (dateSlash1 <= 0 || dateSlash1 == dateSlash2) return null;
        int timeColon1 = time.indexOf(":");
        int timeColon2 = time.lastIndexOf(":");

        if (timeColon1 <= 0) return null;
        month = date.substring(0, dateSlash1);
        day = date.substring(dateSlash1 + 1, dateSlash2);
        year = date.substring(dateSlash2 + 1);
        hour = time.substring(0, timeColon1);

        if (timeColon1 == timeColon2) {
            minute = time.substring(timeColon1 + 1);
            second = "0";
        } else {
            minute = time.substring(timeColon1 + 1, timeColon2);
            second = time.substring(timeColon2 + 1);
        }

        return UtilDateTime.toDate(month, day, year, hour, minute, second);
    }
    
    /**
     * Converts a date String and a time String into a Date
     *
     * @param date The date String: YYYY-MM-DD
     * @param time The time String: either HH:MM or HH:MM:SS
     * @return A Date made from the date and time Strings
     */
    public static java.util.Date toDateYMD(String date, String time, String delimiter) {
        if (date == null || time == null) return null;
        String month;
        String day;
        String year;
        String hour;
        String minute;
        String second;

        int dateSlash1 = date.indexOf(delimiter);
        int dateSlash2 = date.lastIndexOf(delimiter);

        if (dateSlash1 <= 0 || dateSlash1 == dateSlash2) return null;
        int timeColon1 = time.indexOf(":");
        int timeColon2 = time.lastIndexOf(":");
        System.out.println("********************timeColon1 :" + timeColon1 + "timeColon2 :" + timeColon2);
        if (timeColon1 <= 0) return null;
        year = date.substring(0, dateSlash1);
        month = date.substring(dateSlash1 + 1, dateSlash2);
        day = date.substring(dateSlash2 + 1);
        hour = time.substring(0, timeColon1);

        if (timeColon1 == timeColon2) {
            minute = time.substring(timeColon1 + 1);
            second = "0";
        } else {
            minute = time.substring(timeColon1 + 1, timeColon2);
            second = time.substring(timeColon2 + 1);
        }

        return UtilDateTime.toDate(month, day, year, hour, minute, second);
    }

    /** Gives 1 if both dates are same **/
	public static int daysBetween(Date from, Date to) {
		Calendar fromDate = Calendar.getInstance();
		Calendar thruDate = Calendar.getInstance();
		fromDate.setTime(from);
		thruDate.setTime(to);
		int yearDiff = thruDate.get(Calendar.YEAR)
				- fromDate.get(Calendar.YEAR);

		//int monthDiff = (thruDate.get(Calendar.MONTH) + yearDiff * 12) - fromDate.get(Calendar.MONTH);
		
		int dayDiff = thruDate.get(Calendar.DAY_OF_YEAR) - fromDate.get(Calendar.DAY_OF_YEAR) + yearDiff * 365 + 1; //1 as if employee works for only 1 day then it gives 0 instead of 1
		return dayDiff;
	}
	
	/** Returns Integer difference of months between two dates **/
	public static int monthsBetween(Date from, Date to) {
		Calendar fromDate = Calendar.getInstance();
		Calendar thruDate = Calendar.getInstance();
		fromDate.setTime(from);
		thruDate.setTime(to);
		int yearDiff = thruDate.get(Calendar.YEAR)
				- fromDate.get(Calendar.YEAR);
		int monthDiff = (thruDate.get(Calendar.MONTH) + yearDiff * 12)
				- fromDate.get(Calendar.MONTH);
		//System.out.println("monthDiff: " + monthDiff);
		return monthDiff;
	}
	
	/** Gives exact month Difference in fractions b/w two dates for default Locale **/
    public static double getMonthInterval(Date from, Date to) {
		Timestamp fromStamp = new Timestamp(from.getTime());
		Timestamp toStamp = new Timestamp(to.getTime());
		Timestamp fmonthStart = UtilDateTime.getMonthStart(fromStamp);
		Timestamp fmonthEnd = UtilDateTime.getMonthEnd(fromStamp, TimeZone.getDefault(), Locale.getDefault());
		Timestamp tmonthStart = UtilDateTime.getMonthStart(toStamp);
		Timestamp tmonthEnd = UtilDateTime.getMonthEnd(toStamp, TimeZone.getDefault(), Locale.getDefault());
		
		int daysBtwStartFrom = daysBetween(fmonthStart, from) - 1;
		int daysinStartMonth = daysBetween(fmonthStart,fmonthEnd);
		//System.out.println("daysinStartMonth : " + daysinStartMonth);
		double fracStart = (1.0 *(daysinStartMonth - daysBtwStartFrom))/daysinStartMonth;
		//System.out.println("fracStart : " + fracStart);
		
		int daysBtwToEnd = daysBetween(toStamp, tmonthEnd) - 1;
		//System.out.println("daysBtwToEnd :" + daysBtwToEnd);
		int daysinEndMonth = daysBetween(tmonthStart,tmonthEnd);
		//System.out.println("daysinEndMonth : " + daysinEndMonth);
		double fracEnd = (1.0 * (daysinEndMonth - daysBtwToEnd))/daysinEndMonth;
		//System.out.println("fracEnd : " + fracEnd);
		int monthsBetween = monthsBetween(fmonthEnd, tmonthStart) - 1 ;
		//System.out.println("monthsBetween: " + monthsBetween);
		Double interval = fracStart + fracEnd + monthsBetween; 
		//System.out.println("getMonthInterval fromDate == " + from + " toDate == " + to + " interval == " + interval);

		return interval;
	}
    /* Added By Samir*/
    public static Timestamp substractDaysToTimestamp(Timestamp start, int days) {
		return new Timestamp(start.getTime() - (24L * 60L * 60L * 1000L * days));

	}

}
