package org.javers.repository.inmemory;

import org.javers.common.collections.Lists;

import java.util.Optional;
import org.javers.common.validation.Validate;
import org.javers.core.commit.Commit;
import org.javers.core.commit.CommitId;
import org.javers.core.json.JsonConverter;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.core.metamodel.object.GlobalId;
import org.javers.core.metamodel.object.InstanceId;
import org.javers.core.metamodel.object.ValueObjectId;
import org.javers.core.metamodel.type.EntityType;
import org.javers.core.metamodel.type.ManagedType;
import org.javers.repository.api.JaversRepository;
import org.javers.repository.api.QueryParams;
import org.javers.repository.api.SnapshotIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Fake impl of JaversRepository
 *
 * @author bartosz walacik
 */
public class InMemoryRepository implements JaversRepository {
    private static final Logger logger = LoggerFactory.getLogger(InMemoryRepository.class);

    private Map<GlobalId, LinkedList<CdoSnapshot>> snapshots = new ConcurrentHashMap<>();

    private CommitId head;

    public InMemoryRepository() {
    }

    @Override
    public List<CdoSnapshot> getValueObjectStateHistory(final EntityType ownerEntity, final String path, QueryParams queryParams) {
        Validate.argumentsAreNotNull(ownerEntity, path, queryParams);

        List<CdoSnapshot> result =  Lists.positiveFilter(getAll(), input -> {
            if (!(input.getGlobalId() instanceof ValueObjectId)) {
                return false;
            }
            ValueObjectId id = (ValueObjectId) input.getGlobalId();

            return id.hasOwnerOfType(ownerEntity)
                    && id.getFragment().equals(path);
        });

        return applyQueryParams(result, queryParams);
    }

    @Override
    public List<CdoSnapshot> getStateHistory(GlobalId globalId, QueryParams queryParams) {
        Validate.argumentsAreNotNull(globalId, queryParams);

        List<CdoSnapshot> filtered = new ArrayList<>();

        for (CdoSnapshot snapshot : getAll()) {
            if (snapshot.getGlobalId().equals(globalId)) {
                filtered.add(snapshot);
            }
            if (queryParams.isAggregate() && isParent(globalId, snapshot.getGlobalId())){
                filtered.add(snapshot);
            }
        }

        return applyQueryParams(filtered, queryParams);
    }

    private boolean isParent(GlobalId parentCandidate, GlobalId childCandidate) {
        if (! (parentCandidate instanceof InstanceId && childCandidate instanceof ValueObjectId)){
            return false;
        }

        InstanceId parent = (InstanceId)parentCandidate;
        ValueObjectId child = (ValueObjectId)childCandidate;

        return child.getOwnerId().equals(parent);
    }

    @Override
    public List<CdoSnapshot> getStateHistory(Set<ManagedType> givenClasses, QueryParams queryParams) {
        Validate.argumentsAreNotNull(givenClasses, queryParams);
        List<CdoSnapshot> filtered = new ArrayList<>();

        for (CdoSnapshot snapshot : getAll()) {
            for (ManagedType givenClass : givenClasses) {
                if (snapshot.getGlobalId().isTypeOf(givenClass)) {
                    filtered.add(snapshot);
                }
                if (queryParams.isAggregate() && isParent(givenClass, snapshot.getGlobalId())){
                    filtered.add(snapshot);
                }
            }
        }

        return applyQueryParams(filtered, queryParams);
    }

    private boolean isParent(ManagedType parentCandidate, GlobalId childCandidate) {
        if (! (parentCandidate instanceof EntityType && childCandidate instanceof ValueObjectId)){
            return false;
        }

        EntityType parent = (EntityType)parentCandidate;
        ValueObjectId child = (ValueObjectId)childCandidate;

        return child.getOwnerId().getTypeName().equals(parent.getName());
    }

    private List<CdoSnapshot> applyQueryParams(List<CdoSnapshot> snapshots, final QueryParams queryParams){
        if (queryParams.commitIds().size() > 0) {
            snapshots = filterSnapshotsByCommitIds(snapshots, queryParams.commitIds());
        }
        if (queryParams.toCommitId().isPresent()) {
            snapshots = filterSnapshotsByToCommitId(snapshots, queryParams.toCommitId().get());
        }
        if (queryParams.version().isPresent()) {
            snapshots = Lists.positiveFilter(snapshots, snapshot -> snapshot.getVersion() == queryParams.version().get());
        }
        if (queryParams.author().isPresent()) {
            snapshots = filterSnapshotsByAuthor(snapshots, queryParams.author().get());
        }
        if (queryParams.hasDates()) {
            snapshots = filterSnapshotsByCommitDate(snapshots, queryParams);
        }
        if (queryParams.changedProperty().isPresent()){
            snapshots = filterByPropertyName(snapshots, queryParams.changedProperty().get());
        }
        if (queryParams.snapshotType().isPresent()){
            snapshots = Lists.positiveFilter(snapshots, snapshot -> snapshot.getType() == queryParams.snapshotType().get());
        }
        snapshots = filterSnapshotsByCommitProperties(snapshots, queryParams.commitProperties());
        return trimResultsToRequestedSlice(snapshots, queryParams.skip(), queryParams.limit());
    }

    private List<CdoSnapshot> filterSnapshotsByToCommitId(List<CdoSnapshot> snapshots, CommitId commitId) {
        return Lists.positiveFilter(snapshots, snapshot -> snapshot.getCommitMetadata().getId().isBeforeOrEqual(commitId));
    }

    private List<CdoSnapshot> filterSnapshotsByCommitIds(List<CdoSnapshot> snapshots, final Set<CommitId> commitIds) {
        return Lists.positiveFilter(snapshots, snapshot -> commitIds.contains(snapshot.getCommitId()));
    }

    private List<CdoSnapshot> filterSnapshotsByAuthor(List<CdoSnapshot> snapshots, final String author) {
        return Lists.positiveFilter(snapshots, snapshot -> author.equals(snapshot.getCommitMetadata().getAuthor()));
    }

    private List<CdoSnapshot> filterSnapshotsByCommitDate(List<CdoSnapshot> snapshots, final QueryParams queryParams) {
        return Lists.positiveFilter(snapshots, snapshot -> queryParams.isDateInRange(snapshot.getCommitMetadata().getCommitDate()));
    }

    private List<CdoSnapshot> filterSnapshotsByCommitProperties(List<CdoSnapshot> snapshots, final Map<String, String> commitProperties) {
        return Lists.positiveFilter(snapshots, snapshot ->
            commitProperties.entrySet().stream().allMatch(commitProperty -> {
                Map<String, String> actualCommitProperties = snapshot.getCommitMetadata().getProperties();
                return actualCommitProperties.containsKey(commitProperty.getKey()) &&
                        actualCommitProperties.get(commitProperty.getKey()).equals(commitProperty.getValue());
            })
        );
    }

    private List<CdoSnapshot> trimResultsToRequestedSlice(List<CdoSnapshot> snapshots, int from, int size) {
        int fromIndex = Math.min(from, snapshots.size());
        int toIndex = Math.min(from + size, snapshots.size());
        return new ArrayList<>(snapshots.subList(fromIndex, toIndex));
    }

    @Override
    public Optional<CdoSnapshot> getLatest(GlobalId globalId) {
        Validate.argumentsAreNotNull(globalId);

        if (snapshots.containsKey(globalId)) {
            LinkedList<CdoSnapshot> states = snapshots.get(globalId);
            return Optional.of(states.peek());
        }

        return Optional.empty();
    }

    @Override
    public List<CdoSnapshot> getSnapshots(QueryParams queryParams) {
        Validate.argumentIsNotNull(queryParams);

        return applyQueryParams(getAll(), queryParams);
    }

    @Override
    public List<CdoSnapshot> getSnapshots(Collection<SnapshotIdentifier> snapshotIdentifiers) {
        return Lists.transform(getPersistedIdentifiers(snapshotIdentifiers), snapshotIdentifier -> {
            List<CdoSnapshot> objectSnapshots = snapshots.get(snapshotIdentifier.getGlobalId());
            return objectSnapshots.get(objectSnapshots.size() - ((int)snapshotIdentifier.getVersion()));
        });
    }

    private List<SnapshotIdentifier> getPersistedIdentifiers(Collection<SnapshotIdentifier> snapshotIdentifiers) {
        return Lists.positiveFilter(new ArrayList<>(snapshotIdentifiers), snapshotIdentifier -> snapshots.containsKey(snapshotIdentifier.getGlobalId()) &&
            snapshotIdentifier.getVersion() <= snapshots.get(snapshotIdentifier.getGlobalId()).size());
    }

    @Override
    public void persist(Commit commit) {
        Validate.argumentsAreNotNull(commit);
        List<CdoSnapshot> snapshots = commit.getSnapshots();
        for (CdoSnapshot s : snapshots){
            persist(s);
        }
        logger.debug("{} snapshot(s) persisted", snapshots.size());
        head = commit.getId();
    }

    @Override
    public CommitId getHeadId() {
        return head;
    }

    @Override
    public void setJsonConverter(JsonConverter jsonConverter) {
    }

    private List<CdoSnapshot> filterByPropertyName(List<CdoSnapshot> snapshots, final String propertyName){
        return Lists.positiveFilter(snapshots, input -> input.hasChangeAt(propertyName));
    }

    private List<CdoSnapshot> getAll(){
        List<CdoSnapshot> all = new ArrayList<>();
        for (LinkedList<CdoSnapshot> snapshotsList : snapshots.values()) {
            all.addAll(snapshotsList);
        }

        Collections.sort(all, (o1, o2) -> o2.getCommitId().compareTo(o1.getCommitId()));
        return all;
    }

    private synchronized void persist(CdoSnapshot snapshot) {
        LinkedList<CdoSnapshot> states = snapshots.get(snapshot.getGlobalId());
        if (states == null){
            states = new LinkedList<>();
            snapshots.put(snapshot.getGlobalId(), states);
        }

        states.push(snapshot);
    }

    @Override
    public void ensureSchema() {
    }
}
