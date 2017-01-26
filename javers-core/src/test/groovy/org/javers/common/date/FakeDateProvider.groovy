package org.javers.common.date

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class FakeDateProvider implements DateProvider {
    private LocalDateTime localDateTime

    FakeDateProvider() {
        this.localDateTime = localDateTime
    }

    @Override
    LocalDateTime now() {
        localDateTime ? localDateTime : LocalDateTime.now()
    }

    void set(LocalDate localDate) {
        this.localDateTime = localDate.atTime(LocalTime.MIDNIGHT)
    }

    void set(LocalDateTime localDateTime) {
        this.localDateTime = localDateTime
    }
}
