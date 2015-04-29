package org.javers.core.snapshot

import org.javers.core.metamodel.object.CdoSnapshot

/**
 * @author bartosz walacik
 */
class SnapshotsAssert {
    Collection<CdoSnapshot> actual

    static assertThat = { Collection actual ->
        new SnapshotsAssert(actual: actual)
    }

    SnapshotsAssert hasSize(int expected) {
        assert actual.size() == expected
        this
    }

    SnapshotsAssert hasInitialSnapshot(def expectedId) {
        assert actual.find {it -> it.globalId == expectedId}.initial == true
        this
    }

    SnapshotsAssert hasOrdinarySnapshot(def expectedId) {
        assert actual.find {it -> it.globalId == expectedId}.initial == false
        this
    }

    SnapshotsAssert hasSnapshot(def expectedId) {
        assert actual.find {it -> it.globalId == expectedId}
        this
    }

    SnapshotsAssert hasSnapshot(def expectedId, String expectedCommitId, Map<String, Object> expectedState){
        CdoSnapshot found = actual.find {it -> it.globalId == expectedId && it.commitId.value() == expectedCommitId }
        assertState(found, expectedState)
    }

    SnapshotsAssert hasSnapshot(def expectedId, Map<String, Object> expectedState){
        CdoSnapshot found = actual.find {it -> it.globalId == expectedId}
        assertState(found, expectedState)
    }

    SnapshotsAssert hasTerminalSnapshot(def expectedId, String expectedCommitId = null){
        def found = actual.find {
            it -> it.globalId == expectedId && (it.commitId.value() == expectedCommitId || expectedCommitId == null)
        }

        assert found.size() == 0 //terminal snapshot should be empty
        assert found.terminal
        this
    }

    private SnapshotsAssert assertState(CdoSnapshot found, Map<String, Object> expectedState) {
        assert found != null
        assert expectedState.size() == found.size()

        expectedState.entrySet().each{expected ->
            assert found.getPropertyValue(expected.key) == expected.value
        }
        this
    }
}
