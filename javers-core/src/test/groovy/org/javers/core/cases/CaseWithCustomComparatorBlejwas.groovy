package org.javers.core.cases

import org.javers.common.string.Strings
import org.javers.core.JaversBuilder
import org.javers.core.diff.custom.CustomValueComparator
import spock.lang.Ignore
import spock.lang.Specification

// https://stackoverflow.com/questions/66497652/javers-comparison-string-collection-and-boolean-with-defined-rules
@Ignore
class CaseWithCustomComparatorBlejwas extends Specification{

    class Boss {
        private String name
        private List<Employee> subordinates
    }

    class Employee {
        private String name
        private Boolean hasDrivingLicense
        private List<Employee> colleagues
    }

    class StringComparator implements CustomValueComparator<String> {
        boolean equals(String str1, String str2) {
            Strings.emptyIfNull(str1).equals(Strings.emptyIfNull(str2))
        }

        String toString(String s) {
            s
        }
    }

    class  BooleanComparator implements CustomValueComparator<Boolean> {

        boolean equals(Boolean b1, Boolean b2) {
            (b1 as boolean) == (b2 as boolean)
        }

        String toString(Boolean aBoolean) {
            aBoolean.toString()
        }
    }

    def "should use regular CustomValueComparators for String and Boolean" () {
        given:
        Employee employee1 = new Employee(name:"Krzysztof", colleagues:[], hasDrivingLicense: false)
        Employee employee2 = new Employee(name:"Krzysztof")

        Boss boss1 = new Boss(name:"", subordinates:[employee1])
        Boss boss2 = new Boss(subordinates:[employee2])

        def javers = JaversBuilder
                .javers()
                .registerValue(String.class, new StringComparator())
                .registerValue(Boolean.class, new BooleanComparator())
                .build()

        when:
        def diff = javers.compare(boss1, boss2)
        println diff
        println javers.getTypeMapping(Boolean)

        then:
        !diff.changes
    }
}
