package org.javers.repository.api;

import org.javers.common.collections.Function;
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

import java.util.*;

import static org.javers.common.validation.Validate.argumentIsNotNull;
import static org.javers.common.validation.Validate.argumentsAreNotNull;

/**
 * @author bartosz walacik
 */
public class JaversExtendedRepository implements JaversRepository {
    private final JaversRepository delegate;
    private final SnapshotDiffer snapshotDiffer;
    private final PreviousSnapshotsCalculator previousSnapshotsCalculator;

    public JaversExtendedRepository(JaversRepository delegate, SnapshotDiffer snapshotDiffer) {
        this.delegate = delegate;
        this.snapshotDiffer = snapshotDiffer;
        previousSnapshotsCalculator = new PreviousSnapshotsCalculator(new Function<Collection<SnapshotIdentifier>, List<CdoSnapshot>>() {
            public List<CdoSnapshot> apply(Collection<SnapshotIdentifier> input) {
                return getSnapshots(input);
            }
        });
    }

    public List<Change> getPropertyChangeHistory(GlobalId globalId, final String propertyName, boolean newObjects, QueryParams queryParams) {
        argumentsAreNotNull(globalId, propertyName);

        List<CdoSnapshot> snapshots = getPropertyStateHistory(globalId, propertyName, queryParams);
        List<Change> changes = getChangesIntroducedBySnapshots(newObjects ? snapshots : skipInitial(snapshots));

        return filterByPropertyName(changes, propertyName);
    }

    public List<Change> getPropertyChangeHistory(ManagedType givenClass, final String propertyName, boolean newObjects, QueryParams queryParams) {
        argumentsAreNotNull(givenClass, propertyName);

        List<CdoSnapshot> snapshots = getPropertyStateHistory(givenClass, propertyName, queryParams);
        List<Change> changes = getChangesIntroducedBySnapshots(newObjects ? snapshots : skipInitial(snapshots));

        return filterByPropertyName(changes, propertyName);
    }

    public List<Change> getChangeHistory(GlobalId globalId, boolean newObjects, QueryParams queryParams) {
        argumentsAreNotNull(globalId);

        List<CdoSnapshot> snapshots = getStateHistory(globalId, queryParams);
        return getChangesIntroducedBySnapshots(newObjects ? snapshots : skipInitial(snapshots));
    }

    public List<Change> getChangeHistory(ManagedType givenClass, boolean newObjects, QueryParams queryParams) {
        argumentsAreNotNull(givenClass);

        List<CdoSnapshot> snapshots = getStateHistory(givenClass, queryParams);
        return getChangesIntroducedBySnapshots(newObjects ? snapshots : skipInitial(snapshots));
    }

    public List<Change> getValueObjectChangeHistory(EntityType ownerEntity, String path, boolean newObjects, QueryParams queryParams) {
        argumentsAreNotNull(ownerEntity, path);

        List<CdoSnapshot> snapshots = getValueObjectStateHistory(ownerEntity, path, queryParams);
        return getChangesIntroducedBySnapshots(newObjects ? snapshots : skipInitial(snapshots));
    }

    @Override
    public List<CdoSnapshot> getStateHistory(GlobalId globalId, QueryParams queryParams) {
        argumentIsNotNull(globalId);
        return delegate.getStateHistory(globalId, queryParams);
    }

    @Override
    public List<CdoSnapshot> getPropertyStateHistory(GlobalId globalId, String propertyName, QueryParams queryParams) {
        argumentsAreNotNull(globalId, propertyName);
        return delegate.getPropertyStateHistory(globalId, propertyName, queryParams);
    }

    @Override
    public List<CdoSnapshot> getPropertyStateHistory(ManagedType givenClass, String propertyName, QueryParams queryParams) {
        argumentsAreNotNull(givenClass, propertyName);
        return delegate.getPropertyStateHistory(givenClass, propertyName, queryParams);
    }

    @Override
    public List<CdoSnapshot> getValueObjectStateHistory(EntityType ownerEntity, String path, QueryParams queryParams) {
        argumentsAreNotNull(ownerEntity, path);
        return delegate.getValueObjectStateHistory(ownerEntity, path, queryParams);
    }

    @Override
    public Optional<CdoSnapshot> getLatest(GlobalId globalId) {
        argumentIsNotNull(globalId);
        return delegate.getLatest(globalId);
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

    private List<CdoSnapshot> skipInitial(List<CdoSnapshot> snapshots) {
        return Lists.negativeFilter(snapshots, new Predicate<CdoSnapshot>() {
            @Override
            public boolean apply(CdoSnapshot snapshot) {
                return snapshot.isInitial();
            }
        });
    }

    private List<Change> getChangesIntroducedBySnapshots(List<CdoSnapshot> snapshots) {
        return snapshotDiffer.calculateDiffs(snapshots, previousSnapshotsCalculator.calculate(snapshots));
    }
}
