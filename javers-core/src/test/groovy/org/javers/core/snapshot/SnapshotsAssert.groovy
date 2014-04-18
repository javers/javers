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

    SnapshotsAssert hasSnapshot(def expectedId) {
        assert actual.find {it -> it.globalId == expectedId}
        this
    }

    SnapshotsAssert hasSnapshot(def expectedId, String expectedCommitId, Map<String, Object> expectedState){
        CdoSnapshot found = actual.find {it -> it.globalId == expectedId && it.commitId == expectedCommitId }
        assertState(found, expectedState)
    }

    SnapshotsAssert hasSnapshot(def expectedId, Map<String, Object> expectedState){
        CdoSnapshot found = actual.find {it -> it.globalId == expectedId}
        assertState(found, expectedState)
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
