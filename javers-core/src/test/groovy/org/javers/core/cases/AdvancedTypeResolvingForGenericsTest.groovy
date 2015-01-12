package org.javers.core.cases

import org.javers.common.reflection.ConcreteWithActualType
import org.javers.core.JaversBuilder
import org.javers.core.diff.changetype.container.ListChange
import org.javers.core.diff.changetype.container.ValueAdded
import spock.lang.Specification

/**
 * https://github.com/javers/javers/issues/77
 *
 * To resolve this issue, we added {@link org.javers.common.reflection.JaversMember},
 * which cures:
 * JaversException: CLASS_EXTRACTION_ERROR JaVers bootstrap error - Don't know how to extract Class from type 'T'
 *
 * @author bartosz walacik
 */
class AdvancedTypeResolvingForGenericsTest extends Specification{

    def "should resolve actual field type when inherited from Generic superclass"() {
        given:
        def javers = JaversBuilder.javers().build();

        def left = new ConcreteWithActualType([1])
        def right = new ConcreteWithActualType([1,2])

        when:
        def diff = javers.compare(left, right)

        then:
        def change = diff.getChangesByType(ListChange)[0]
        change.changes[0] instanceof ValueAdded
        change.changes[0].index == 1
        change.changes[0].addedValue == 2

    }
}
