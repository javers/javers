package org.javers.common.date;

import org.joda.time.LocalDateTime;

public class DefaultDateProvider implements DateProvider {

    @Override
    public LocalDateTime now() {
        return new LocalDateTime();
    }
}
