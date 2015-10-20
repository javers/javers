package org.javers.core.diff.changetype.container

import org.javers.core.JaversBuilder
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import static org.javers.common.collections.Arrays.intArray

/**
 * @author akrystian
 */
class ArrayChangeTest extends Specification {
    @Shared
    def javers = JaversBuilder.javers().build();

    @Unroll
    def "should create leftArray and rightArray for diff #container1 to #container2"() {
        given:
        def diff = javers.compare(container1, container2)
        ArrayChange arrayChange = diff.changes[0];
        when:
        Object[] leftArray = arrayChange.getLeftArray()
        Object[] rightArray = arrayChange.getRightArray()
        then:
        leftArray == expectedLeftArray
        rightArray == expectedRightArray

        where:
        container1 << [intArray(1, 2, 3), intArray(1, 2), ["a", "b", "c"].toArray(), ["a", "b"].toArray()]
        container2 << [intArray(1, 2), intArray(1, 2, 3), ["a", "b"].toArray(), ["a", "b", "c"].toArray()]
        expectedLeftArray << [intArray(3), [], ["c"].toArray(), []]
        expectedRightArray << [[], intArray(3), [], ["c"].toArray()]
    }
}
