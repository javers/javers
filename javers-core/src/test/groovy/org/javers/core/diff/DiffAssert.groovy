package org.javers.core.diff

import org.javers.core.diff.changetype.NewObject
import org.javers.core.diff.changetype.ObjectRemoved
import org.javers.core.diff.changetype.ValueChange

/**
 * @author bartosz walacik
 */
class DiffAssert {
    Diff actual

    static DiffAssert assertThat(Diff actual) {
        new DiffAssert(actual: actual)
    }

    DiffAssert hasChanges(int expectedSize) {
        assert actual.changes.size() == expectedSize
        this
    }

    DiffAssert has(int expectedSize, Class<? extends Change> expectedClass) {
        assert actual.changes.grep{it.class == expectedClass}.size() == expectedSize
        this
    }

    DiffAssert hasAllOfType(Class<? extends Change> expectedClass) {
        actual.changes.each { assert it.class == expectedClass}
        this
    }

    DiffAssert hasValueChangeAt(String property, Object oldVal, Object newVal) {
        ValueChange change = actual.changes.find{it instanceof ValueChange && it.property.name == property}
        assert change
        assert change.leftValue == oldVal
        assert change.rightValue == newVal
        this
    }

    DiffAssert hasNewObject(def globalCdoId){
        assert actual.changes.find{it instanceof NewObject && it.globalCdoId == globalCdoId}, "no NewObject change with expected globalId: "+globalCdoId
        this
    }

    DiffAssert hasObjectRemoved(def globalCdoId){
        assert actual.changes.find{it instanceof ObjectRemoved && it.globalCdoId == globalCdoId}, "no ObjectRemoved change with expected globalId: "+globalCdoId
        this
    }

}
