package org.javers.common.date;

import java.time.LocalDateTime;

public interface DateProvider {
    LocalDateTime now();
}
