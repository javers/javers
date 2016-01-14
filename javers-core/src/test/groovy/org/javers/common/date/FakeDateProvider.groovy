package org.javers.common.date

import org.joda.time.LocalDateTime

class FakeDateProvider implements DateProvider {
    private LocalDateTime localDateTime

    FakeDateProvider() {
        this.localDateTime = localDateTime
    }

    @Override
    LocalDateTime now() {
        localDateTime ? localDateTime : LocalDateTime.now()
    }

    void set(LocalDateTime localDateTime) {
        this.localDateTime = localDateTime
    }
}
