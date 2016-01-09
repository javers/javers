package org.javers.core.metamodel.object;

import org.javers.common.validation.Validate;
import org.javers.core.commit.CommitMetadata;
import org.javers.core.metamodel.property.Property;
import org.javers.core.metamodel.type.ManagedType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.javers.core.metamodel.object.CdoSnapshotStateBuilder.cdoSnapshotState;
import static org.javers.core.metamodel.object.SnapshotType.INITIAL;
import static org.javers.core.metamodel.object.SnapshotType.UPDATE;

/**
 * @author bartosz walacik
 */
public class CdoSnapshotBuilder {
    private final GlobalId globalId;
    private CommitMetadata commitMetadata;
    private SnapshotType type = UPDATE;
    private CdoSnapshotState state;
    private CdoSnapshotStateBuilder stateBuilder = cdoSnapshotState();
    private CdoSnapshot previous;
    private boolean markAllAsChanged;
    private List<String> changed = Collections.EMPTY_LIST;
    private ManagedType managedType;
    private long version;

    private CdoSnapshotBuilder(GlobalId globalId, ManagedType managedType) {
        Validate.argumentsAreNotNull(globalId, managedType);
        this.globalId = globalId;
        this.managedType = managedType;
    }

    public static CdoSnapshot emptyCopyOf(CdoSnapshot snapshot){
        return cdoSnapshot(snapshot.getGlobalId(), snapshot.getManagedType())
                .withCommitMetadata(snapshot.getCommitMetadata())
                .withType(snapshot.getType()).build();
    }

    public static CdoSnapshotBuilder cdoSnapshot(GlobalId globalId, ManagedType managedType) {
        return new CdoSnapshotBuilder(globalId, managedType);}

    public static CdoSnapshotBuilder cdoSnapshot(GlobalId globalId, CommitMetadata commitMetadata, ManagedType managedType) {
        return new CdoSnapshotBuilder(globalId, managedType).withCommitMetadata(commitMetadata);
    }

    public CdoSnapshotBuilder withCommitMetadata(CommitMetadata commitMetadata) {
        this.commitMetadata = commitMetadata;
        return this;
    }

    public CdoSnapshotBuilder withState(CdoSnapshotState state) {
        Validate.argumentIsNotNull(state);
        this.state = state;
        return this;
    }

    public CdoSnapshotBuilder withVersion(long version) {
        this.version = version;
        return this;
    }

    public CdoSnapshot build() {
        if (state == null) {
            state = stateBuilder.build();
        }

        if (previous != null) {
            changed = state.differentValues(previous.getState());
            version = previous.getVersion() + 1;
        } else {
            version = 1L;
        }

        if (markAllAsChanged){
            changed = new ArrayList<>(state.getProperties());
        }

        return new CdoSnapshot(globalId, commitMetadata, state, type, changed, managedType, version);
    }

    public CdoSnapshotBuilder withType(SnapshotType type) {
        Validate.argumentIsNotNull(type);
        this.type = type;
        return this;
    }

    public CdoSnapshotBuilder withPropertyValue(Property property, Object value) {
        stateBuilder.withPropertyValue(property, value);
        return this;
    }

    @Deprecated
    public CdoSnapshotBuilder withInitial(boolean initial) {
        if (initial) {
            this.type = INITIAL;
        } else {
            this.type = UPDATE;
        }
        return this;
    }

    public CdoSnapshotBuilder markAllAsChanged() {
        markAllAsChanged = true;
        return this;
    }

    public CdoSnapshotBuilder withChangedProperties(List<String> changedPropertyNames) {
        changed = new ArrayList<>(changedPropertyNames);
        return this;
    }

    public CdoSnapshotBuilder markChanged(CdoSnapshot previous) {
        this.previous = previous;
        return this;
    }
}
