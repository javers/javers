package org.javers.repository.api;

import org.javers.common.validation.Validate;
import org.javers.core.metamodel.object.GlobalId;

public final class SnapshotIdentifier {
    final GlobalId globalId;
    final long version;

    public SnapshotIdentifier(GlobalId globalId, long version) {
        Validate.argumentIsNotNull(globalId);
        Validate.argumentCheck(version > 0, "Version is not a positive number.");
        this.globalId = globalId;
        this.version = version;
    }

    public GlobalId getGlobalId() {
        return globalId;
    }

    public long getVersion() {
        return version;
    }
}
