package org.javers.repository.sql.finders;

import org.javers.core.metamodel.object.CdoSnapshot;

class CdoSnapshotDTO {
    CdoSnapshot snapshot;
    long commitPK;

    CdoSnapshotDTO(CdoSnapshot snapshot, long commitPK) {
        this.snapshot = snapshot;
        this.commitPK = commitPK;
    }

    CdoSnapshot getSnapshot() {
        return snapshot;
    }

    long getCommitPK() {
        return commitPK;
    }
}

