package org.javers.core.metamodel.object;

import org.javers.common.validation.Validate;
import org.javers.core.commit.CommitId;
import org.javers.core.commit.CommitMetadata;
import org.javers.core.graph.Cdo;
import org.javers.core.metamodel.property.Property;
import org.javers.core.metamodel.type.ManagedType;

import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

import static java.util.Collections.unmodifiableList;
import static org.javers.common.validation.Validate.argumentIsNotNull;
import static org.javers.core.metamodel.object.SnapshotType.INITIAL;
import static org.javers.core.metamodel.object.SnapshotType.TERMINAL;

/**
 * Historical state of a domain object captured as the property->value Map.
 * Values and primitives are stored 'by value'.
 * Referenced Entities and ValueObjects are stored 'by reference' using {@link GlobalId}
 *
 * @author bartosz walacik
 */
public final class CdoSnapshot extends Cdo {
    private CommitMetadata commitMetadata;
    private final CdoSnapshotState state;
    private final SnapshotType type;
    private final List<String> changed;
    private final long version;
    private final GlobalId globalId;

    /**
     * should be assembled by {@link CdoSnapshotBuilder}
     */
    CdoSnapshot(GlobalId globalId,
                CommitMetadata commitMetadata,
                CdoSnapshotState state,
                SnapshotType type,
                List<String> changed,
                ManagedType managedType,
                long version) {
        super(managedType);
        Validate.argumentsAreNotNull(state, commitMetadata, type, managedType, globalId);
        this.state = state;
        this.commitMetadata = commitMetadata;
        this.type = type;
        this.changed = changed;
        this.version = version;
        this.globalId = globalId;
    }

    /**
     * @return {@link Optional#EMPTY}
     */
    @Override
    public Optional<Object> getWrappedCdo() {
        return Optional.empty();
    }

    @Override
    public GlobalId getGlobalId() {
        return globalId;
    }

    public int size() {
        return state.size();
    }

    @Override
    public Object getPropertyValue(String propertyName) {
        return state.getPropertyValue(propertyName);
    }

    /**
     * returns default values for null primitives
     */
    @Override
    public Object getPropertyValue(Property property) {
        return state.getPropertyValue(property);
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
        Validate.argumentIsNotNull(property);
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

    <R> List<R> mapProperties(BiFunction<String, Object, R> mapper) {
        return getState().mapProperties(mapper);
    }

    void forEachProperty(BiConsumer<String, Object> consumer) {
        getState().forEachProperty(consumer);
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

    /**
     * Object version number.<br/>
     * Initial snapshot of given object has version 1, next has version 2.
     * <br/><br/>
     *
     * <b>Warning!</b> Version field was added in JaVers v. 1.4.4.
     * All snapshots persisted in JaversRepository before this release
     * have version 0.
     * <br/>
     * If it isn't OK for you, run manual DB update.
     * See <a href="http://javers.org/documentation/features/#release-notes">release-notes</a>
     * for v. 1.4.4
     *
     * @since 1.4.4
     */
    public long getVersion() {
        return version;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder
            .append("Snapshot{commit:").append(getCommitMetadata().getId()).append(", ")
            .append("id:").append(getGlobalId()).append(", ")
            .append("version:").append(getVersion()).append(", ")
            .append(getState()+"}");
        return stringBuilder.toString();
    }
}
