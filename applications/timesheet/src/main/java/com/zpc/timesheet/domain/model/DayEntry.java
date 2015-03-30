package com.zpc.timesheet.domain.model;

import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;
import lombok.*;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.nthdimenzion.ddd.domain.model.Interval;

import javax.persistence.Embeddable;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

/**
 * Author: Nthdimenzion
 */

@ToString
@Embeddable
@Immutable
@Getter
@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
class DayEntry {

    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    @NotNull
    private LocalDate date;
    private String shiftOne;
    private String shiftTwo;
    private String nOvertime;
    private String dOvertime;

    public static DayEntry EmptyDayEntry(LocalDate date){
        return new DayEntry(date, null, null, null, null);
    }

    public static DayEntry FilledDayEntry(LocalDate date,String shiftOne,String shiftTwo,String nOvertime,String dOvertime){
        return new DayEntry(date, shiftOne, shiftTwo, nOvertime, dOvertime);
    }

    public static Set<DayEntry> MakeEmptyDayEntries(Interval schedulePeriod) {
        final int daysBetween = Days.daysBetween(schedulePeriod.getFromDate(), schedulePeriod.getThruDate()).getDays();
        final HashSet<DayEntry> dayEntries = Sets.newHashSet();
        final LocalDate fromDate = new LocalDate(schedulePeriod.getFromDate());
        for (int i = 0; i <= daysBetween; i++) {
            dayEntries.add(DayEntry.EmptyDayEntry(fromDate.plusDays(i)));
        }

        return dayEntries;
    }

    @Transient
    static Ordering<DayEntry> ascendingOrderByDate = new Ordering<DayEntry>() {
        public int compare(DayEntry left, DayEntry right) {
            return left.getDate().compareTo(right.getDate());
        }
    };


}
