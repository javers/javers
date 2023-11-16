package org.javers.core.cases

import org.javers.core.JaversBuilder
import spock.lang.Specification

import java.time.LocalTime
import java.time.MonthDay
import java.time.OffsetTime
import java.time.YearMonth
import java.time.ZoneOffset

class CaseWithJavaTimeFields extends Specification {
    class YearMonthEntity {
        YearMonth yearMonth
    }

    def "should not throw exception in Java 16+ for YearMonth field access" () {
        given:
        def javers = JaversBuilder.javers().build()
        def object1 = new YearMonthEntity(yearMonth: YearMonth.of(2010,1))
        def object2 = new YearMonthEntity(yearMonth: YearMonth.of(2020,1))

        when:
        def diff = javers.compare(object1, object2)

        then:
        diff.changes.size() == 1
    }

    class MonthDayEntity {
        MonthDay monthDay
    }

    def "should not throw exception in Java 16+ for MonthDay field access" () {
        given:
        def javers = JaversBuilder.javers().build()
        def object1 = new MonthDayEntity(monthDay: MonthDay.of(1,1))
        def object2 = new MonthDayEntity(monthDay: MonthDay.of(1,31))

        when:
        def diff = javers.compare(object1, object2)

        then:
        diff.changes.size() == 1
    }

    class OffsetTimeEntity {
        OffsetTime offsetTime
    }

    def "should not throw exception in Java 16+ for OffsetTime field access" () {
        given:
        def javers = JaversBuilder.javers().build()
        def object1 = new OffsetTimeEntity(offsetTime: OffsetTime.of(LocalTime.of(1,2,3), ZoneOffset.of("+04:00")))
        def object2 = new OffsetTimeEntity(offsetTime: OffsetTime.of(LocalTime.of(5,6,7), ZoneOffset.of("+08:00")))

        when:
        def diff = javers.compare(object1, object2)

        then:
        diff.changes.size() == 1
    }
}
