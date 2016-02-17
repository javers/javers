package org.javers.common.date

import org.joda.time.LocalDate
import org.joda.time.LocalDateTime
import org.joda.time.LocalTime

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
        this.localDateTime = localDate.toLocalDateTime(LocalTime.MIDNIGHT)
    }

    void set(LocalDateTime localDateTime) {
        this.localDateTime = localDateTime
    }
}
