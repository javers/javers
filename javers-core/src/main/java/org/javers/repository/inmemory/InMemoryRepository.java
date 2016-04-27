package org.javers.repository.inmemory;

import org.javers.common.collections.Function;
import org.javers.common.collections.Lists;
import org.javers.common.collections.Optional;
import org.javers.common.collections.Predicate;
import org.javers.common.validation.Validate;
import org.javers.core.commit.Commit;
import org.javers.core.commit.CommitId;
import org.javers.core.json.JsonConverter;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.core.metamodel.object.GlobalId;
import org.javers.core.metamodel.object.ValueObjectId;
import org.javers.core.metamodel.type.EntityType;
import org.javers.core.metamodel.type.ManagedType;
import org.javers.repository.api.JaversRepository;
import org.javers.repository.api.QueryParams;
import org.javers.repository.api.QueryParamsBuilder;
import org.javers.repository.api.SnapshotIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Collections.unmodifiableList;

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

        List<CdoSnapshot> result =  Lists.positiveFilter(getAll(), new Predicate<CdoSnapshot>() {
            @Override
            public boolean apply(CdoSnapshot input) {
                if (!(input.getGlobalId() instanceof ValueObjectId)) {
                    return false;
                }
                ValueObjectId id = (ValueObjectId) input.getGlobalId();

                return id.hasOwnerOfType(ownerEntity)
                        && id.getFragment().equals(path);
            }
        });

        return applyQueryParams(result, queryParams);
    }

    @Override
    public List<CdoSnapshot> getStateHistory(GlobalId globalId, QueryParams queryParams) {
        Validate.argumentsAreNotNull(globalId, queryParams);

        if (snapshots.containsKey(globalId)) {
            return unmodifiableList(applyQueryParams(snapshots.get(globalId), queryParams));
        }
        return Collections.emptyList();
    }

    @Override
    public List<CdoSnapshot> getStateHistory(ManagedType givenClass, QueryParams queryParams) {
        Validate.argumentsAreNotNull(givenClass, queryParams);
        List<CdoSnapshot> filtered = new ArrayList<>();

        for (CdoSnapshot snapshot : getAll()) {
            if (snapshot.getGlobalId().isTypeOf(givenClass)) {
                filtered.add(snapshot);
            }
        }

        return applyQueryParams(filtered, queryParams);
    }

    @Override
    public List<CdoSnapshot> getPropertyStateHistory(GlobalId globalId, final String propertyName, QueryParams queryParams) {
        Validate.argumentsAreNotNull(globalId, propertyName, queryParams);

        if (snapshots.containsKey(globalId)) {
            List<CdoSnapshot> filtered = filterByPropertyName(snapshots.get(globalId), propertyName);
            return unmodifiableList(applyQueryParams(filtered, queryParams));
        }
        return Collections.emptyList();
    }

    @Override
    public List<CdoSnapshot> getPropertyStateHistory(ManagedType givenClass, String propertyName, QueryParams queryParams) {
        Validate.argumentsAreNotNull(givenClass, propertyName, queryParams);

        QueryParams increasedLimitQueryParams = getQueryParamsWithIncreasedLimit(queryParams);
        List<CdoSnapshot> filtered = filterByPropertyName(getStateHistory(givenClass, increasedLimitQueryParams), propertyName);
        return unmodifiableList(applyQueryParams(filtered, queryParams));
    }

    private QueryParams getQueryParamsWithIncreasedLimit(QueryParams queryParams) {
        return QueryParamsBuilder.initializeWith(queryParams)
            .limit(queryParams.limit() * 10)
            .build();
    }

    private List<CdoSnapshot> applyQueryParams(List<CdoSnapshot> snapshots, final QueryParams queryParams){
        if (queryParams.commitId().isPresent()) {
            snapshots = filterSnapshotsByCommitId(snapshots, queryParams.commitId().get());
        }
        if (queryParams.version().isPresent()) {
            snapshots = filterSnapshotsByVersion(snapshots, queryParams.version().get());
        }
        if (queryParams.author().isPresent()) {
            snapshots = filterSnapshotsByAuthor(snapshots, queryParams.author().get());
        }
        if (queryParams.hasDates()) {
            snapshots = filterSnapshotsByCommitDate(snapshots, queryParams);
        }
        return trimResultsToRequestedSlice(snapshots, queryParams.skip(), queryParams.limit());
    }

    private List<CdoSnapshot> filterSnapshotsByCommitId(List<CdoSnapshot> snapshots, final CommitId commitId) {
        return Lists.positiveFilter(snapshots, new Predicate<CdoSnapshot>() {
            public boolean apply(CdoSnapshot snapshot) {
                return commitId.equals(snapshot.getCommitId());
            }
        });
    }

    private List<CdoSnapshot> filterSnapshotsByVersion(List<CdoSnapshot> snapshots, final Long version) {
        return Lists.positiveFilter(snapshots, new Predicate<CdoSnapshot>() {
            public boolean apply(CdoSnapshot snapshot) {
                return version == snapshot.getVersion();
            }
        });
    }

    private List<CdoSnapshot> filterSnapshotsByAuthor(List<CdoSnapshot> snapshots, final String author) {
        return Lists.positiveFilter(snapshots, new Predicate<CdoSnapshot>() {
            public boolean apply(CdoSnapshot snapshot) {
                return author.equals(snapshot.getCommitMetadata().getAuthor());
            }
        });
    }

    private List<CdoSnapshot> filterSnapshotsByCommitDate(List<CdoSnapshot> snapshots, final QueryParams queryParams) {
        return Lists.positiveFilter(snapshots, new Predicate<CdoSnapshot>() {
            public boolean apply(CdoSnapshot snapshot) {
                return queryParams.isDateInRange(snapshot.getCommitMetadata().getCommitDate());
            }
        });
    }

    private List<CdoSnapshot> trimResultsToRequestedSlice(List<CdoSnapshot> snapshots, int from, int size) {
        int fromIndex = Math.min(from, snapshots.size());
        int toIndex = Math.min(from + size, snapshots.size());
        return snapshots.subList(fromIndex, toIndex);
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

        return unmodifiableList(applyQueryParams(getAll(), queryParams));
    }

    @Override
    public List<CdoSnapshot> getSnapshots(Collection<SnapshotIdentifier> snapshotIdentifiers) {
        List<SnapshotIdentifier> persistedIdentifiers = getPersistedIdentifiers(snapshotIdentifiers);
        return Lists.transform(persistedIdentifiers, new Function<SnapshotIdentifier, CdoSnapshot>() {
            @Override
            public CdoSnapshot apply(SnapshotIdentifier snapshotIdentifier) {
                List<CdoSnapshot> objectSnapshots = snapshots.get(snapshotIdentifier.getGlobalId());
                return objectSnapshots.get(objectSnapshots.size() - ((int)snapshotIdentifier.getVersion()));
            }
        });
    }

    private List<SnapshotIdentifier> getPersistedIdentifiers(Collection<SnapshotIdentifier> snapshotIdentifiers) {
        return Lists.positiveFilter(new ArrayList<>(snapshotIdentifiers), new Predicate<SnapshotIdentifier>() {
            @Override
            public boolean apply(SnapshotIdentifier snapshotIdentifier) {
                return snapshots.containsKey(snapshotIdentifier.getGlobalId()) &&
                    snapshotIdentifier.getVersion() <= snapshots.get(snapshotIdentifier.getGlobalId()).size();
            }
        });
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
        return Lists.positiveFilter(snapshots, new Predicate<CdoSnapshot>() {
            public boolean apply(CdoSnapshot input) {
                return input.hasChangeAt(propertyName);
            }
        });
    }

    private List<CdoSnapshot> getAll(){
        List<CdoSnapshot> all = new ArrayList<>();
        for (LinkedList<CdoSnapshot> snapshotsList : snapshots.values()) {
            all.addAll(snapshotsList);
        }

        Collections.sort(all, new Comparator<CdoSnapshot>() {
            @Override
            public int compare(CdoSnapshot o1, CdoSnapshot o2) {
            return o2.getCommitId().compareTo(o1.getCommitId());
            }
        });
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
