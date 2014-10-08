package org.javers.core.diff

import org.javers.core.diff.changetype.NewObject
import org.javers.core.diff.changetype.ObjectRemoved
import org.javers.core.diff.changetype.PropertyChange
import org.javers.core.diff.changetype.ReferenceChange
import org.javers.core.diff.changetype.ValueChange
import org.javers.core.diff.changetype.container.ListChange
import org.javers.core.diff.changetype.container.ValueAdded
import org.javers.core.diff.changetype.container.ValueRemoved
import org.javers.core.metamodel.object.GlobalId

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
        assert change.left == oldVal
        assert change.right == newVal
        this
    }

    DiffAssert hasReferenceChangeAt(String property, def oldRef, def newRef) {
        ReferenceChange change = actual.changes.find{it instanceof ReferenceChange && it.property.name == property}
        assert change
        assert change.left == oldRef
        assert change.right == newRef
        this
    }

    DiffAssert hasNewObject(def expectedId) {
        assert actual.changes.find { it instanceof NewObject && it.affectedCdoId == expectedId }
        this
    }

    /*
    DiffAssert hasNewObject(def expectedId, Map<String, Object> expectedInitialState){
        assert actual.changes.find{it instanceof NewObject && it.affectedCdoId == expectedId}

        expectedInitialState.entrySet().each{ entry ->
            PropertyChange change = actual.changes.find{it instanceof PropertyChange &&
                                                        it.affectedCdoId == expectedId &&
                                                        it.property.name == entry.key}
            assert change, "no PropertyChange for "+ entry.key
            assert !change.left
            assert change.right ==  entry.value
        }
        this
    }*/

    DiffAssert hasListReferenceAddedAt(String property, def addedRef){
        ListChange change = actual.changes.find{it instanceof ListChange && it.property.name == property}
        assert change

        ValueAdded removed = change.changes.find{it instanceof ValueAdded}
        assert removed
        assert removed.addedValue instanceof GlobalId
        assert removed.addedValue == addedRef

        this
    }

    DiffAssert hasListReferenceRemovedAt(String property, def removedRef){
        ListChange change = actual.changes.find{it instanceof ListChange && it.property.name == property}
        assert change

        ValueRemoved removed = change.changes.find{it instanceof ValueRemoved}
        assert removed
        assert removed.removedValue instanceof GlobalId
        assert removed.removedValue == removedRef

        this
    }

    DiffAssert hasObjectRemoved(def globalId){
        assert actual.changes.find{it instanceof ObjectRemoved && it.globalId == globalId}, "no ObjectRemoved change with expected globalId: "+globalId
        this
    }
}
