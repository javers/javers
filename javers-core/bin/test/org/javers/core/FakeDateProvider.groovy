package org.javers.core

import org.javers.common.date.DateProvider

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZonedDateTime

class FakeDateProvider implements DateProvider {
    private ZonedDateTime dateTime

    FakeDateProvider() {
        this.dateTime = dateTime
    }

    @Override
    ZonedDateTime now() {
        dateTime ? dateTime : ZonedDateTime.now()
    }

    void set(ZonedDateTime dateTime) {
        this.dateTime = dateTime
    }
}
