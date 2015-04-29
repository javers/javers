package org.javers.core.metamodel.object;

import org.javers.common.validation.Validate;
import org.javers.core.commit.CommitMetadata;
import org.javers.core.metamodel.property.Property;

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
    private List<Property> changed = Collections.EMPTY_LIST;

    private CdoSnapshotBuilder(GlobalId globalId, CommitMetadata commitMetadata) {
        Validate.argumentsAreNotNull(globalId, commitMetadata);
        this.globalId = globalId;
        this.commitMetadata = commitMetadata;
    }

    public static CdoSnapshot emptyCopyOf(CdoSnapshot snapshot){
        return cdoSnapshot(snapshot.getGlobalId(), snapshot.getCommitMetadata()).withType(snapshot.getType()).build();
    }

    public static CdoSnapshotBuilder cdoSnapshot(GlobalId globalId, CommitMetadata commitMetadata) {
        return new CdoSnapshotBuilder(globalId, commitMetadata);
    }

    public CdoSnapshotBuilder withState(CdoSnapshotState state) {
        Validate.argumentIsNotNull(state);
        this.state = state;
        return this;
    }

    public CdoSnapshot build() {
        if (state == null) {
            state = stateBuilder.build();
        }

        if (previous != null){
            changed = state.differentValues(previous.getState());
        }

        if (markAllAsChanged){
            changed = new ArrayList<>(state.getProperties());
        }

        return new CdoSnapshot(globalId, commitMetadata, state, type, changed);
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
        changed = new ArrayList<>();
        for (String propertyName : changedPropertyNames) {
            changed.add(globalId.getCdoClass().getProperty(propertyName));
        }
        return this;
    }

    public CdoSnapshotBuilder markChanged(CdoSnapshot previous) {
        this.previous = previous;
        return this;
    }
}
