package org.javers.repository.api;

import org.javers.common.validation.Validate;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.core.metamodel.object.GlobalId;

import java.util.Objects;

public final class SnapshotIdentifier {

    private final GlobalId globalId;
    private final long version;

    public SnapshotIdentifier(GlobalId globalId, long version) {
        Validate.argumentIsNotNull(globalId);
        Validate.argumentCheck(version > 0, "Version is not a positive number.");
        this.globalId = globalId;
        this.version = version;
    }

    public static SnapshotIdentifier from(CdoSnapshot snapshot) {
        Validate.argumentIsNotNull(snapshot);
        return new SnapshotIdentifier(snapshot.getGlobalId(), snapshot.getVersion());
    }

    public GlobalId getGlobalId() {
        return globalId;
    }

    public long getVersion() {
        return version;
    }

    public SnapshotIdentifier previous() {
        return new SnapshotIdentifier(getGlobalId(), getVersion() - 1);
    }

    public SnapshotIdentifier next() {
        return new SnapshotIdentifier(getGlobalId(), getVersion() + 1);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SnapshotIdentifier that = (SnapshotIdentifier) o;
        return version == that.version && Objects.equals(globalId, that.globalId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(globalId, version);
    }

    @Override
    public String toString() {
        return "SnapshotIdentifier{id:" + globalId.value() +", ver:" + version + "}";
    }
}
