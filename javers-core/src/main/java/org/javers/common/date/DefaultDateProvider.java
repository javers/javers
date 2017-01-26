package org.javers.common.date;

import java.time.LocalDateTime;

public class DefaultDateProvider implements DateProvider {

    @Override
    public LocalDateTime now() {
        return LocalDateTime.now();
    }
}
