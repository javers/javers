package org.javers.core.cases;

import javax.persistence.Id;
import java.math.BigDecimal;

/**
 * @author bartosz.walacik
 */
public class Case208DateTimeTypes {
    @Id
    String id;

    java.util.Date date = new java.util.Date();

    org.joda.time.LocalDateTime yodaDate = org.joda.time.LocalDateTime.now();

    java.time.LocalDateTime java8Date = java.time.LocalDateTime.now();
    java.time.ZonedDateTime java8ZonedDate = java.time.ZonedDateTime.now();

    java.sql.Date dateSql = new java.sql.Date(date.getTime());
    java.sql.Timestamp ts = new java.sql.Timestamp(date.getTime());
    java.sql.Time time = new java.sql.Time(date.getTime());

    BigDecimal bigDecimalNumber = BigDecimal.ONE;

    public Case208DateTimeTypes(String id) {
        this.id = id;
    }
}
