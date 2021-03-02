package org.javers.common.collections

import spock.lang.Specification

/**
 * @author pawel szymczyk
 */
class ArraysTest extends Specification {

    def "should transform primitive array to list"() {
        given:
        int[] primitiveArray = [1, 2, 3, 4, 5]

        when:
        def list = Arrays.asList(primitiveArray)

        then:
        list.size() == 5
        list.containsAll(1, 2, 3, 4, 5)
    }
}
