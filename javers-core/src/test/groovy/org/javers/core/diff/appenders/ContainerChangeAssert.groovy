package org.javers.core.diff.appenders

import org.javers.core.diff.changetype.container.ContainerChange
import org.javers.core.diff.changetype.container.ElementValueChange
import org.javers.core.diff.changetype.container.ValueAdded
import org.javers.core.diff.changetype.container.ValueRemoved

/**
 * @author bartosz walacik
 */
class ContainerChangeAssert {

    ContainerChange actual

    static assertThat = { ContainerChange actual ->
        new ContainerChangeAssert(actual: actual)
    }

    ContainerChangeAssert hasSize(int expected) {
        assert actual.changes.size() == expected
        this
    }

    ContainerChangeAssert hasIndexes(List expected) {
        assert actual.changes.collect { it.index } == expected
        this
    }

    ContainerChangeAssert hasReferenceChange(int expectedIndex, def expectedLeftId, def expectedRightId){
        assert actual.changes.find{it.index == expectedIndex && it instanceof ElementValueChange && it.leftValue == expectedLeftId && it.rightValue == expectedRightId}
        this
    }

    ContainerChangeAssert hasReferenceAdded(int expectedIndex, def expectedId) {
        assert actual.changes.find{it.index == expectedIndex && it instanceof ValueAdded && it.addedValue== expectedId}
        this
    }

    ContainerChangeAssert hasReferenceRemoved(int expectedIndex, def expectedId) {
        assert actual.changes.find{it.index == expectedIndex && it instanceof ValueRemoved && it.removedValue == expectedId}
        this
    }
}
