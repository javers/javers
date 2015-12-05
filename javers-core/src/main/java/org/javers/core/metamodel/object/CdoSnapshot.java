package org.javers.core.metamodel.object;

import org.javers.common.collections.Optional;
import org.javers.common.validation.Validate;
import org.javers.core.commit.CommitId;
import org.javers.core.commit.CommitMetadata;
import org.javers.core.metamodel.property.Property;

import java.util.List;

import static java.util.Collections.unmodifiableList;
import static org.javers.common.validation.Validate.argumentIsNotNull;
import static org.javers.core.metamodel.object.SnapshotType.INITIAL;
import static org.javers.core.metamodel.object.SnapshotType.TERMINAL;

/**
 * Captured state of client's domain object.
 * Values and primitives are stored 'by value',
 * referenced Entities and ValueObjects are stored 'by reference' using {@link GlobalId}
 *
 * @author bartosz walacik
 */
public final class CdoSnapshot extends Cdo {
    private CommitMetadata commitMetadata;
    private final CdoSnapshotState state;
    private final SnapshotType type;
    private final List<String> changed;

    /**
     * should be assembled by {@link CdoSnapshotBuilder}
     */
    CdoSnapshot(GlobalId globalId,
                CommitMetadata commitMetadata,
                CdoSnapshotState state,
                SnapshotType type,
                List<String> changed) {
        super(globalId);
        Validate.argumentsAreNotNull(state, commitMetadata, type);
        this.state = state;
        this.commitMetadata = commitMetadata;
        this.type = type;
        this.changed = changed;
    }

    /**
     * @return {@link Optional#EMPTY}
     */
    @Override
    public Optional<Object> getWrappedCdo() {
        return Optional.empty();
    }

    public int size() {
        return state.size();
    }

    @Override
    public Object getPropertyValue(String propertyName) {
        return state.getPropertyValue(propertyName);
    }

    /**
     * List of propertyNames changed with this snapshot
     * (comparing to latest from repository).
     * <br/>
     * For initial snapshot, returns all properties.
     */
    public List<String> getChanged() {
        return unmodifiableList(changed);
    }

    public boolean hasChangeAt(String propertyName) {
        argumentIsNotNull(propertyName);
        return changed.contains(propertyName);
    }

    @Override
    public boolean isNull(Property property) {
        return state.isNull(property.getName());
    }

    public CommitId getCommitId() {
        return commitMetadata.getId();
    }

    public CommitMetadata getCommitMetadata() {
        return commitMetadata;
    }

    public boolean stateEquals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CdoSnapshot other = (CdoSnapshot) o;
        return this.state.equals(other.state);
    }

    public CdoSnapshotState getState() {
        return state;
    }

    public boolean isInitial() {
        return type == INITIAL;
    }

    public boolean isTerminal() {
        return type == TERMINAL;
    }

    public SnapshotType getType() {
        return type;
    }
}
