package org.javers.common.date;

import org.joda.time.LocalDateTime;

public class FakeDateProvider implements DateProvider {

    int year
    int monthOfYear
    int dayOfMonth
    int hourOfDay
    int minuteOfHour

    @Override
    public LocalDateTime now() {
        return new LocalDateTime(year, monthOfYear, dayOfMonth, hourOfDay, minuteOfHour)
    }
}
