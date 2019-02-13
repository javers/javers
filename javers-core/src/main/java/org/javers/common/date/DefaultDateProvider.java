package org.javers.common.date;

import java.time.ZonedDateTime;

public class DefaultDateProvider implements DateProvider {

    @Override
    public ZonedDateTime now() {
        return ZonedDateTime.now();
    }
}
