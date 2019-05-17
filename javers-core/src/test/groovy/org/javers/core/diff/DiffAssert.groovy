package org.javers.core.diff

import org.javers.core.diff.changetype.NewObject
import org.javers.core.diff.changetype.ObjectRemoved
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
        actual.changes.each { assert expectedClass.isInstance(it)}
        this
    }

    DiffAssert hasOnly(int expectedSize, Class<? extends Change> expectedClass) {
        this.has(expectedSize, expectedClass)
        this.hasChanges(expectedSize)
        this
    }

    DiffAssert hasSize(int expected){
        assert actual.changes.size() == expected
        this
    }

    DiffAssert hasValueChangeAt(String property, Object oldVal, Object newVal) {
        ValueChange change = actual.changes.find{it instanceof ValueChange && it.propertyName == property}
        assert change
        assert change.left == oldVal
        assert change.right == newVal
        this
    }

    DiffAssert hasReferenceChangeAt(String property, def oldRef, def newRef) {
        ReferenceChange change = actual.changes.find{it instanceof ReferenceChange && it.propertyName == property}
        assert change
        assert change.left == oldRef
        assert change.right == newRef
        this
    }

    DiffAssert hasNewObject(def expectedId) {
        assert actual.changes.find { it instanceof NewObject && it.affectedGlobalId == expectedId }
        this
    }

    DiffAssert hasListReferenceAddedAt(String property, def addedRef){
        ListChange change = actual.changes.find{it instanceof ListChange && it.propertyName == property}
        assert change

        ValueAdded removed = change.changes.find{it instanceof ValueAdded}
        assert removed
        assert removed.addedValue instanceof GlobalId
        assert removed.addedValue == addedRef

        this
    }

    DiffAssert hasListReferenceRemovedAt(String property, def removedRef){
        ListChange change = actual.changes.find{it instanceof ListChange && it.propertyName == property}
        assert change

        ValueRemoved removed = change.changes.find{it instanceof ValueRemoved}
        assert removed
        assert removed.removedValue instanceof GlobalId
        assert removed.removedValue == removedRef

        this
    }

    DiffAssert hasObjectRemoved(def globalId){
        def change = actual.changes.find{it instanceof ObjectRemoved && it.affectedGlobalId == globalId}
        assert change, "no ObjectRemoved change with expected globalId: "+globalId
        this
    }
}
