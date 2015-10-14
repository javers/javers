package org.javers.core.diff

import org.javers.core.JaversBuilder
import org.javers.core.diff.changetype.container.ArrayChange
import spock.lang.Ignore
import spock.lang.Shared
import spock.lang.Specification

/**
 * @author akrystian
 */
class ArrayChangeTest extends Specification {
    @Shared
    def javers = JaversBuilder.javers().build();

    @Ignore
    def "should getLeftArray for simple values"() {
        given:
        def container1 = intArray([1, 2]);
        def container2 = intArray([1, 2, 3])
        def diff = javers.compare(container1, container2)
        ArrayChange arrayChange = diff.changes[0];
        when:
        Object[] leftArray = arrayChange.getLeftArray()
        then:
        leftArray == intArray([1, 2]);
    }

    @Ignore
    def "should getRightArray for simple values"() {
        given:
        def container1 = intArray([1, 2]);
        def container2 = intArray([1, 2, 3])
        def diff = javers.compare(container1, container2)
        ArrayChange arrayChange = diff.changes[0];
        when:
        Object[] rightArray = arrayChange.getRightArray()
        then:
        rightArray == intArray([3]);
    }

    @Ignore
    def "should getLeftArray for objects"() {
        given:
        def container1 = ["a", "b"].toArray();
        def container2 = ["a", "b", "c"].toArray();
        def diff = javers.compare(container1, container2)
        ArrayChange arrayChange = diff.changes[0];
        when:
        Object[] leftArray = arrayChange.getLeftArray()
        then:
        leftArray == ["a", "b"].toArray();
    }

    @Ignore
    def "should getRightArray for objects"() {
        given:
        def container1 = ["a", "b"].toArray();
        def container2 = ["a", "b", "c"].toArray();
        def diff = javers.compare(container1, container2)
        ArrayChange arrayChange = diff.changes[0];
        when:
        Object[] rightArray = arrayChange.getRightArray()
        then:
        rightArray == ["c"].toArray();
    }


    private int[] intArray(List values) {
        def ret = new int[values.size()]
        values.eachWithIndex { def entry, int i -> ret[i] = entry }
        println ret
        ret
    }
}
