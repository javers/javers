package org.javers.core.snapshot

import org.javers.common.collections.Multimap
import org.javers.core.metamodel.object.CdoSnapshot
import org.javers.core.metamodel.object.GlobalCdoId
import org.javers.core.metamodel.object.ValueObjectSetId

/**
 * @author bartosz walacik
 */
class SnapshotsAssert {
    Multimap<GlobalCdoId,CdoSnapshot> actual

    static assertThat = { Multimap actual ->
        new SnapshotsAssert(actual: actual)
    }

    SnapshotsAssert hasSize(int expected) {
        assert actual.size() == expected
        this
    }

    SnapshotsAssert hasSnapshotSet(ValueObjectSetId setId, int expectedSize){
        assert actual.containsKey(setId) == true
        Set<CdoSnapshot> set = actual.getSet(setId)

        assert set.size() == expectedSize
        set.each { e -> assert e.globalId == setId }
        this
    }

    SnapshotsAssert hasSnapshot(GlobalCdoId cdoId) {
        CdoSnapshot snapshot = actual.getOne(cdoId)
        assert snapshot.globalId == cdoId
        this
    }
}
