package org.javers.core.cases

import org.apache.commons.lang3.math.NumberUtils
import org.javers.core.JaversBuilder
import org.javers.core.diff.custom.CustomValueComparator
import spock.lang.Specification

import java.math.RoundingMode

/**
 * https://github.com/javers/javers/issues/935
 */
class CustomValueComparatorMissingPropertyCase extends Specification {

    class Parent {}

    class Child1 extends Parent {
        String prop1
    }

    class Child2 extends Parent {
        Double prop2
    }

    class Container {
        Parent parent
    }

    class CustomDoubleComparator implements CustomValueComparator<Double> {

        @Override
        boolean equals(Double a, Double b) {
            return round(a) == round(b)
        }

        @Override
        String toString(Double value) {
            return round(value).toString()
        }

        private BigDecimal round(Double val) {
            return NumberUtils.toScaledBigDecimal(val, 4, RoundingMode.HALF_UP)
        }
    }

    def "should compare map values using their concrete type"() {
        given:
        def javers = JaversBuilder.javers()
                .registerValue(Double.class, new CustomDoubleComparator())
                .build()

        when:
        Parent c1 = new Child1(prop1: "Hi")
        Parent c2 = new Child2(prop2: 1.2)

        Container container1 = new Container(parent: c1)

        Container container2 = new Container(parent: c2)

        def diff = javers.compare(container1, container2)

        then:
        diff.changes.size() == 2

        println(diff.changes)
    }

}
