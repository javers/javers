package org.javers.common.date;

import org.joda.time.LocalDateTime;

public interface DateProvider {

    LocalDateTime now();
}
