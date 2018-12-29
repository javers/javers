package org.javers.core

import org.javers.common.date.DateProvider

import java.time.LocalDateTime

class TikDateProvider implements DateProvider {
    private LocalDateTime localDateTime = LocalDateTime.now()

    void set(LocalDateTime now) {
        localDateTime = now
    }

    @Override
    synchronized LocalDateTime now() {
        def now = localDateTime
        localDateTime = localDateTime.plusSeconds(1)
        now
    }
}
