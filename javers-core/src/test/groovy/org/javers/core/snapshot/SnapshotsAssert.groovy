package org.javers.core.snapshot

import org.javers.core.metamodel.object.CdoSnapshot
import org.javers.core.metamodel.object.GlobalCdoId

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

    SnapshotsAssert hasSnapshot(GlobalCdoId cdoId) {
        CdoSnapshot found = actual.find {it -> it.globalId == cdoId}
        assert found != null
        this
    }
}
