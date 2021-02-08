package org.javers.common.collections

import spock.lang.Specification

class SetsTest extends Specification {

    def "should calculate Difference"() {
        given:
        Set<String> first = ["a", "b", "c"]
        Set<String> second = ["b", "c", "d"]

        when:
        Set<String> difference = Sets.difference(first, second)

        then:
        difference.size() == 1
        difference.contains "a"
    }

    def "should not change arguments when calculating difference"() {
        given:
        Set<String> first = ["a", "b", "c"]
        Set<String> second = ["b", "c", "d"]

        when:
        Sets.difference(first, second);

        then:
        first.size() == 3
        first.containsAll "a", "b", "c"

        second.size() == 3
        second.containsAll "b", "c", "d"
    }
}
