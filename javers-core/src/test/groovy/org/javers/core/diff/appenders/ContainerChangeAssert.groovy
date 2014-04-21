package org.javers.core.diff.appenders

import org.javers.core.diff.changetype.ContainerChange
import org.javers.core.diff.changetype.ElementReferenceChange
import org.javers.core.diff.changetype.ReferenceAdded
import org.javers.core.diff.changetype.ReferenceRemoved

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
        assert actual.changes.find{it.index == expectedIndex && it instanceof ElementReferenceChange && it.leftReference == expectedLeftId && it.rightReference == expectedRightId}
        this
    }

    ContainerChangeAssert hasReferenceAdded(int expectedIndex, def expectedId) {
        assert actual.changes.find{it.index == expectedIndex && it instanceof ReferenceAdded && it.addedReference == expectedId}
        this
    }

    ContainerChangeAssert hasReferenceRemoved(int expectedIndex, def expectedId) {
        assert actual.changes.find{it.index == expectedIndex && it instanceof ReferenceRemoved && it.removedReference == expectedId}
        this
    }
}
