package org.javers.core.snapshot

import org.javers.core.metamodel.object.CdoSnapshot

/**
 * @author bartosz walacik
 */
class SnapshotsAssert {
    List<CdoSnapshot> actual

    static assertThat = { List actual ->
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

    SnapshotsAssert hasSnapshotWithValue(def expectedId, String onProperty, Object expectedValue){
        CdoSnapshot found = actual.find {it -> it.globalId == expectedId}
        assert found != null
        assert found.getPropertyValue(onProperty) == expectedValue
        this
    }
}
