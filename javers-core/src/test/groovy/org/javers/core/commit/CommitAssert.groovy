package org.javers.core.commit

import org.javers.core.diff.Change
import org.javers.core.diff.DiffAssert
import org.javers.core.metamodel.object.GlobalId
import org.javers.repository.jql.GlobalIdDTO
import org.javers.core.snapshot.SnapshotsAssert

/**
 * @author bartosz walacik
 */
class CommitAssert {
    Commit actual
    DiffAssert diffAssert
    SnapshotsAssert snapshotsAssert

    private CommitAssert(Commit actual){
        this.actual = actual
        this.diffAssert = DiffAssert.assertThat(actual.diff)
        this.snapshotsAssert = SnapshotsAssert.assertThat(actual.snapshots)
    }

    static assertThat = { Commit actual ->
        new CommitAssert(actual)
    }

    CommitAssert hasSnapshots(int expectedCount){
        snapshotsAssert.hasSize(expectedCount)
        this
    }

    CommitAssert hasSnapshot(GlobalId expectedId){
        assert snapshotsAssert.hasSnapshot(expectedId)
        this
    }

    CommitAssert hasChanges(int expectedCount){
        diffAssert.hasChanges(expectedCount)
        this
    }

    CommitAssert hasId(String expected){
        assert actual.id.value() == expected
        assert actual.snapshots.each {assert it.commitId == actual.id}
        this
    }

    CommitAssert hasChanges(int expectedSize, Class<? extends Change> expectedClass) {
        diffAssert.has(expectedSize, expectedClass)
        this
    }

    CommitAssert hasNewObject(def expectedId){
        diffAssert.hasNewObject(expectedId)
        this
    }

    CommitAssert hasObjectRemoved(def expectedId){
        diffAssert.hasObjectRemoved(expectedId)
        this
    }

    CommitAssert hasValueChangeAt(String property, Object oldVal, Object newVal) {
        diffAssert.hasValueChangeAt(property,oldVal,newVal)
        this
    }

    CommitAssert hasListReferenceAddedAt(String property, def addedRef){
        diffAssert.hasListReferenceAddedAt(property,addedRef)
        this
    }

    CommitAssert hasListReferenceRemovedAt(String property, def removedRef){
        diffAssert.hasListReferenceRemovedAt(property,removedRef)
        this
    }

    CommitAssert hasReferenceChangeAt(String property, def oldRef, def newRef) {
        diffAssert.hasReferenceChangeAt(property,oldRef,newRef)
        this
    }

    CommitAssert hasSnapshot(def expectedSnapshotId, Map<String, Object> expectedState){
        snapshotsAssert.hasSnapshot(expectedSnapshotId, expectedState)
        this
    }

    CommitAssert hasTerminalSnapshot(def expectedSnapshotId){
        snapshotsAssert.hasTerminalSnapshot(expectedSnapshotId)
        this
    }

    CommitAssert shouldHaveTerminalSnapshot(def expectedSnapshotId){
        snapshotsAssert.shouldHaveTerminalSnapshot(expectedSnapshotId)
        this
    }
}
