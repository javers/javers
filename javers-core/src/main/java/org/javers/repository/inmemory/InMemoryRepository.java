package org.javers.repository.inmemory;

import org.javers.common.collections.Lists;

import java.time.Instant;
import java.time.LocalDateTime;
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
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Fake impl of JaversRepository
 *
 * @author bartosz walacik
 */
public class InMemoryRepository implements JaversRepository {
    private static final Logger logger = LoggerFactory.getLogger(InMemoryRepository.class);

    private Map<String, LinkedList<String>> snapshots = new ConcurrentHashMap<>();

    private Map<CommitId, Integer> commits = new ConcurrentHashMap<>();
    private AtomicInteger counter = new AtomicInteger();

    private CommitId head;
    private JsonConverter jsonConverter;

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
        if (queryParams.authorLikeIgnoreCase().isPresent()) {
            snapshots = filterSnapshotsByAuthorLikeIgnoreCase(snapshots, queryParams.authorLikeIgnoreCase().get());
        }
        if (hasDates(queryParams)) {
            snapshots = filterSnapshotsByCommitDate(snapshots, queryParams);
        }
        if (hasInstants(queryParams)) {
            snapshots = filterSnapshotsByCommitDateInstant(snapshots, queryParams);
        }
        if (queryParams.changedProperties().size() > 0) {
            snapshots = filterByPropertyNames(snapshots, queryParams.changedProperties());
        }
        if (queryParams.snapshotType().isPresent()){
            snapshots = Lists.positiveFilter(snapshots, snapshot -> snapshot.getType() == queryParams.snapshotType().get());
        }
        snapshots = filterSnapshotsByCommitProperties(snapshots, queryParams.commitProperties());
        snapshots = filterSnapshotsByCommitPropertiesLike(snapshots, queryParams.commitPropertiesLike());

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

    private List<CdoSnapshot> filterSnapshotsByAuthorLikeIgnoreCase(List<CdoSnapshot> snapshots, final String author) {
        return Lists.positiveFilter(snapshots, snapshot -> snapshot.getCommitMetadata().getAuthor().toLowerCase(Locale.ROOT)
                .contains(author.toLowerCase(Locale.ROOT)));
    }

    private List<CdoSnapshot> filterSnapshotsByCommitDate(List<CdoSnapshot> snapshots, final QueryParams queryParams) {
        return Lists.positiveFilter(snapshots, snapshot -> isDateInRange(queryParams, snapshot.getCommitMetadata().getCommitDate()));
    }

    public boolean isDateInRange(QueryParams q, LocalDateTime date) {
        if (q.from().isPresent() && q.from().get().isAfter(date)){
            return false;
        }
        if (q.to().isPresent() && q.to().get().isBefore(date)){
            return false;
        }

        return true;
    }

    private List<CdoSnapshot> filterSnapshotsByCommitDateInstant(List<CdoSnapshot> snapshots, final QueryParams queryParams) {
        return Lists.positiveFilter(snapshots, snapshot -> isInstantInRange(queryParams, snapshot.getCommitMetadata().getCommitDateInstant()));
    }

    private boolean isInstantInRange(QueryParams q, Instant instant) {
        if (q.fromInstant().isPresent() && q.fromInstant().get().isAfter(instant)) {
            return false;
        }
        if (q.toInstant().isPresent() && q.toInstant().get().isBefore(instant)) {
            return false;
        }

        return true;
    }

    private List<CdoSnapshot> filterSnapshotsByCommitProperties(List<CdoSnapshot> snapshots, final Map<String, Collection<String>> commitProperties) {
        return Lists.positiveFilter(snapshots, snapshot ->
            commitProperties.entrySet().stream().allMatch(commitProperty -> {
                Map<String, String> actualCommitProperties = snapshot.getCommitMetadata().getProperties();
                return actualCommitProperties.containsKey(commitProperty.getKey()) &&
                        commitProperty.getValue().contains( actualCommitProperties.get(commitProperty.getKey()));
            })
        );
    }

    private List<CdoSnapshot> filterSnapshotsByCommitPropertiesLike(List<CdoSnapshot> snapshots, final Map<String, String> commitPropertiesLike) {
        return Lists.positiveFilter(snapshots, snapshot ->
            commitPropertiesLike.entrySet().stream().allMatch(commitProperty -> {
                Map<String, String> actualCommitProperties = snapshot.getCommitMetadata().getProperties();
                return actualCommitProperties.containsKey(commitProperty.getKey()) &&
                        actualCommitProperties.get(commitProperty.getKey()).toLowerCase(Locale.ROOT).contains(commitProperty.getValue().toLowerCase(Locale.ROOT));
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

        if (contains(globalId)) {
            return Optional.of(readSnapshots(globalId).peek());
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
            List<CdoSnapshot> objectSnapshots = readSnapshots(snapshotIdentifier.getGlobalId());
            return objectSnapshots.get(objectSnapshots.size() - ((int)snapshotIdentifier.getVersion()));
        });
    }

    private List<SnapshotIdentifier> getPersistedIdentifiers(Collection<SnapshotIdentifier> snapshotIdentifiers) {
        return Lists.positiveFilter(new ArrayList<>(snapshotIdentifiers), snapshotIdentifier -> contains(snapshotIdentifier.getGlobalId()) &&
            snapshotIdentifier.getVersion() <= readSnapshots(snapshotIdentifier.getGlobalId()).size());
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
        commits.put(getHeadId(), counter.incrementAndGet());
    }

    @Override
    public CommitId getHeadId() {
        return head;
    }

    @Override
    public void setJsonConverter(JsonConverter jsonConverter) {
        this.jsonConverter = jsonConverter;
    }

    private List<CdoSnapshot> filterByPropertyNames(List<CdoSnapshot> snapshots, final Set<String> propertyNames){
        return Lists.positiveFilter(snapshots, input -> propertyNames.stream().anyMatch(input::hasChangeAt));
    }

    private List<CdoSnapshot> getAll(){
        List<CdoSnapshot> all = new ArrayList<>();

        snapshots.keySet().forEach(it -> all.addAll(readSnapshots(it)));
        Collections.sort(all, Comparator.comparingInt(o1 -> Integer.MAX_VALUE - getSeq(o1.getCommitMetadata().getId())));

        return all;
    }

    private int getSeq(CommitId commitId) {
        return commits.get(commitId);
    }

    private synchronized void persist(CdoSnapshot snapshot) {
        Validate.conditionFulfilled(jsonConverter != null, "jsonConverter is null");
        String globalIdValue = snapshot.getGlobalId().value();

        LinkedList<String> snapshotsList = snapshots.get(globalIdValue);
        if (snapshotsList == null){
            snapshotsList = new LinkedList<>();
            snapshots.put(globalIdValue, snapshotsList);
        }

        snapshotsList.push(jsonConverter.toJson(snapshot));
    }

    @Override
    public void ensureSchema() {
    }

    private boolean contains(GlobalId globalId) {
        return contains(globalId.value());
    }

    private boolean contains(String globalIdValue) {
        return snapshots.containsKey(globalIdValue);
    }

    private LinkedList<CdoSnapshot> readSnapshots(String globalIdValue) {
        LinkedList<CdoSnapshot> result = new LinkedList<>();

        if (!contains(globalIdValue)) {
            return result;
        }

        snapshots.get(globalIdValue).forEach(it -> result.add(jsonConverter.fromJson(it, CdoSnapshot.class)));
        return result;
    }

    private LinkedList<CdoSnapshot> readSnapshots(GlobalId globalId) {
        return readSnapshots(globalId.value());
    }

    private boolean hasDates(QueryParams q) {
        return q.from().isPresent() || q.to().isPresent();
    }

    public boolean hasInstants(QueryParams q) {
        return q.fromInstant().isPresent() || q.toInstant().isPresent();
    }
}
