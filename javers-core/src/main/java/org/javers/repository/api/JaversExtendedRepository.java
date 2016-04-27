package org.javers.repository.api;

import org.javers.common.collections.Lists;
import org.javers.common.collections.Optional;
import org.javers.common.collections.Predicate;
import org.javers.core.commit.Commit;
import org.javers.core.commit.CommitId;
import org.javers.core.diff.Change;
import org.javers.core.diff.changetype.PropertyChange;
import org.javers.core.json.JsonConverter;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.core.metamodel.object.GlobalId;
import org.javers.core.metamodel.type.EntityType;
import org.javers.core.metamodel.type.ManagedType;
import org.javers.core.snapshot.SnapshotDiffer;

import java.util.Collection;
import java.util.List;

import static org.javers.common.validation.Validate.argumentIsNotNull;
import static org.javers.common.validation.Validate.argumentsAreNotNull;

/**
 * @author bartosz walacik
 */
public class JaversExtendedRepository implements JaversRepository {
    private final JaversRepository delegate;
    private final SnapshotDiffer snapshotDiffer;

    public JaversExtendedRepository(JaversRepository delegate, SnapshotDiffer snapshotDiffer) {
        this.delegate = delegate;
        this.snapshotDiffer = snapshotDiffer;
    }

    public List<Change> getPropertyChangeHistory(GlobalId globalId, final String propertyName, boolean newObjects, QueryParams queryParams) {
        argumentsAreNotNull(globalId, propertyName);

        List<CdoSnapshot> snapshots = getPropertyStateHistory(globalId, propertyName, queryParams);
        List<Change> changes = snapshotDiffer.calculateDiffs(snapshots, newObjects);

        return filterByPropertyName(changes, propertyName);
    }

    public List<Change> getPropertyChangeHistory(ManagedType givenClass, final String propertyName, boolean newObjects, QueryParams queryParams) {
        argumentsAreNotNull(givenClass, propertyName);

        List<CdoSnapshot> snapshots = getPropertyStateHistory(givenClass, propertyName, queryParams);
        List<Change> changes = snapshotDiffer.calculateMultiDiffs(snapshots, newObjects);

        return filterByPropertyName(changes, propertyName);
    }

    public List<Change> getChangeHistory(GlobalId globalId, boolean newObjects, QueryParams queryParams) {
        argumentsAreNotNull(globalId, queryParams);

        List<CdoSnapshot> snapshots = getStateHistory(globalId, queryParams);
        return snapshotDiffer.calculateDiffs(snapshots, newObjects);
    }

    public List<Change> getChangeHistory(ManagedType givenClass, boolean newObjects, QueryParams queryParams) {
        argumentsAreNotNull(givenClass, queryParams);

        List<CdoSnapshot> snapshots = getStateHistory(givenClass, queryParams);
        return snapshotDiffer.calculateMultiDiffs(snapshots, newObjects);
    }

    public List<Change> getValueObjectChangeHistory(EntityType ownerEntity, String path, boolean newObjects, QueryParams queryParams) {
        argumentsAreNotNull(ownerEntity, path, queryParams);

        List<CdoSnapshot> snapshots = getValueObjectStateHistory(ownerEntity, path, queryParams);
        return snapshotDiffer.calculateMultiDiffs(snapshots, newObjects);
    }

    public List<Change> getChanges(boolean newObjects, QueryParams queryParams) {
        argumentsAreNotNull(queryParams);

        List<CdoSnapshot> snapshots = getSnapshots(queryParams);
        return snapshotDiffer.calculateDiffs(snapshots, newObjects);
    }

    @Override
    public List<CdoSnapshot> getStateHistory(GlobalId globalId, QueryParams queryParams) {
        argumentsAreNotNull(globalId, queryParams);
        return delegate.getStateHistory(globalId, queryParams);
    }

    @Override
    public List<CdoSnapshot> getPropertyStateHistory(GlobalId globalId, String propertyName, QueryParams queryParams) {
        argumentsAreNotNull(globalId, propertyName, queryParams);
        return delegate.getPropertyStateHistory(globalId, propertyName, queryParams);
    }

    @Override
    public List<CdoSnapshot> getPropertyStateHistory(ManagedType givenClass, String propertyName, QueryParams queryParams) {
        argumentsAreNotNull(givenClass, propertyName, queryParams);
        return delegate.getPropertyStateHistory(givenClass, propertyName, queryParams);
    }

    @Override
    public List<CdoSnapshot> getValueObjectStateHistory(EntityType ownerEntity, String path, QueryParams queryParams) {
        argumentsAreNotNull(ownerEntity, path, queryParams);
        return delegate.getValueObjectStateHistory(ownerEntity, path, queryParams);
    }

    @Override
    public Optional<CdoSnapshot> getLatest(GlobalId globalId) {
        argumentIsNotNull(globalId);
        return delegate.getLatest(globalId);
    }

    @Override
    public List<CdoSnapshot> getSnapshots(QueryParams queryParams) {
        argumentsAreNotNull(queryParams);
        return delegate.getSnapshots(queryParams);
    }

    @Override
    public List<CdoSnapshot> getSnapshots(Collection<SnapshotIdentifier> snapshotIdentifiers) {
        argumentIsNotNull(snapshotIdentifiers);
        return delegate.getSnapshots(snapshotIdentifiers);
    }

    @Override
    public List<CdoSnapshot> getStateHistory(ManagedType givenClass, QueryParams queryParams) {
        return delegate.getStateHistory(givenClass, queryParams);
    }

    @Override
    public void persist(Commit commit) {
        delegate.persist(commit);
    }

    @Override
    public CommitId getHeadId() {
        return delegate.getHeadId();
    }

    @Override
    public void setJsonConverter(JsonConverter jsonConverter) {
    }

    @Override
    public void ensureSchema() {
        delegate.ensureSchema();
    }

    private List<Change> filterByPropertyName(List<Change> changes, final String propertyName) {
        return Lists.positiveFilter(changes, new Predicate<Change>() {
            public boolean apply(Change input) {
                return input instanceof PropertyChange && ((PropertyChange) input).getPropertyName().equals(propertyName);
            }
        });
    }
}
