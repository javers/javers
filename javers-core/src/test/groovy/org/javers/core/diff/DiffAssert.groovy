package org.javers.core.diff

import org.javers.core.diff.changetype.NewObject
import org.javers.core.diff.changetype.ObjectRemoved
import org.javers.core.diff.changetype.PropertyChange
import org.javers.core.diff.changetype.ReferenceChange
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

    DiffAssert hasOnly(int expectedSize, Class<? extends Change> expectedClass) {
        this.has(expectedSize, expectedClass)
        this.hasChanges(expectedSize)
        this
    }

    DiffAssert hasValueChangeAt(String property, Object oldVal, Object newVal) {
        ValueChange change = actual.changes.find{it instanceof ValueChange && it.property.name == property}
        assert change
        assert change.leftValue == oldVal
        assert change.rightValue == newVal
        this
    }

    DiffAssert hasReferenceChangeAt(String property, def oldRef, def newRef) {
        ReferenceChange change = actual.changes.find{it instanceof ReferenceChange && it.property.name == property}
        assert change
        assert change.leftReference == oldRef
        assert change.rightReference == newRef
        this
    }

    //DiffAssert hasNewObject(def globalCdoId){
    //    assert actual.changes.find{it instanceof NewObject && it.globalCdoId == globalCdoId}, "no NewObject change with expected globalId: "+globalCdoId
    //    this
    //}

    DiffAssert hasNewObject(def expectedId, Map<String, Object> expectedInitialState){
        assert actual.changes.find{it instanceof NewObject && it.affectedCdoId == expectedId}

        expectedInitialState.entrySet().each{ entry ->
            PropertyChange change = actual.changes.find{it instanceof PropertyChange &&
                                                        it.affectedCdoId == expectedId &&
                                                        it.property.name == entry.key}
            assert change, "no PropertyChange for "+ entry.key
            assert !left(change)
            assert right(change) ==  entry.value
        }
        this
    }

    DiffAssert hasObjectRemoved(def globalCdoId){
        assert actual.changes.find{it instanceof ObjectRemoved && it.globalCdoId == globalCdoId}, "no ObjectRemoved change with expected globalId: "+globalCdoId
        this
    }

    def left(PropertyChange change){
        if (change instanceof ValueChange){
            return change.leftValue
        }
        if (change instanceof ReferenceChange){
            return change.leftReference
        }
    }

    def right(PropertyChange change){
        if (change instanceof ValueChange){
            return change.rightValue
        }
        if (change instanceof ReferenceChange){
            return change.rightReference
        }
    }
}
