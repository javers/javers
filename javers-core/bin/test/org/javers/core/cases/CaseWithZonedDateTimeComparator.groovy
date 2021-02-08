package org.javers.core.cases

import org.javers.core.JaversBuilder
import spock.lang.Specification

import java.time.ZoneId
import java.time.ZonedDateTime

class CaseWithZonedDateTimeComparator extends Specification {
    class ZonedDateTimeHolder {
        ZonedDateTime zonedDateTime
    }

    def "should used customValueComparator for ZonedDateTime" () {
        given:
        def javers = JaversBuilder
                .javers()
                .registerValue(ZonedDateTime,
                        {a, b -> a.toInstant().getEpochSecond() == b.toInstant().getEpochSecond()},
                        {a -> String.valueOf(a.toInstant().getEpochSecond())}
                )
                .build()

        when:
        println javers.getTypeMapping(ZonedDateTime).prettyPrint()

        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTimeHolder a = new ZonedDateTimeHolder(
                zonedDateTime: now.withZoneSameInstant(ZoneId.of("UTC")))

        ZonedDateTimeHolder b = new ZonedDateTimeHolder(
                zonedDateTime:(now.withZoneSameInstant(ZoneId.of("Z"))))

        def diff = javers.compare(a, b)

        then:
        diff.changes.size()==0
    }
}
