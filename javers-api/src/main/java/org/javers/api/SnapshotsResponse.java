package org.javers.api;

import org.javers.core.metamodel.object.CdoSnapshot;

import java.util.List;

/**
 * @author pawel szymczyk
 */
public class SnapshotsResponse {

    private List<CdoSnapshot> snapshots;

    protected SnapshotsResponse() {
    }

    public SnapshotsResponse(List<CdoSnapshot> snapshots) {
        this.snapshots = snapshots;
    }

    public List<CdoSnapshot> getSnapshots() {
        return snapshots;
    }
}
