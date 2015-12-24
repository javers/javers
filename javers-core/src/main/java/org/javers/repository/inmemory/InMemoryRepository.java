package org.javers.repository.inmemory;

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
class InMemoryRepository implements JaversRepository {
    private static final Logger logger = LoggerFactory.getLogger(InMemoryRepository.class);

    private Map<GlobalId, LinkedList<CdoSnapshot>> snapshots = new ConcurrentHashMap<>();

    private CommitId head;

    public InMemoryRepository() {
    }

    @Override
    public List<CdoSnapshot> getValueObjectStateHistory(final EntityType ownerEntity, final String path, QueryParams queryParams) {
        Validate.argumentsAreNotNull(ownerEntity, path);

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

        return limit(result, queryParams.getLimit());
    }

    @Override
    public List<CdoSnapshot> getStateHistory(GlobalId globalId, QueryParams queryParams) {
        Validate.argumentIsNotNull(globalId);

        if (snapshots.containsKey(globalId)) {
            return unmodifiableList(limit(snapshots.get(globalId), queryParams.getLimit()));
        }
        return Collections.emptyList();
    }

    @Override
    public List<CdoSnapshot> getStateHistory(ManagedType givenClass, QueryParams queryParams) {
        Validate.argumentIsNotNull(givenClass);
        List<CdoSnapshot> filtered = new ArrayList<>();

        for (CdoSnapshot snapshot : getAll()) {
            if (snapshot.getGlobalId().isTypeOf(givenClass)) {
                filtered.add(snapshot);
            }
        }

        return limit(filtered, queryParams.getLimit());
    }

    @Override
    public List<CdoSnapshot> getPropertyStateHistory(GlobalId globalId, final String propertyName, QueryParams queryParams) {
        Validate.argumentsAreNotNull(globalId, propertyName);

        if (snapshots.containsKey(globalId)) {
            List<CdoSnapshot> filtered = filterByPropertyName(snapshots.get(globalId), propertyName);
            return unmodifiableList(limit(filtered, queryParams.getLimit()));
        }
        return Collections.emptyList();
    }

    @Override
    public List<CdoSnapshot> getPropertyStateHistory(ManagedType givenClass, String propertyName, QueryParams queryParams) {
        Validate.argumentsAreNotNull(givenClass, propertyName);

        QueryParams increasedLimitQueryParams = getQueryParamsWithIncreasedLimit(queryParams);
        List<CdoSnapshot> filtered = filterByPropertyName(getStateHistory(givenClass, increasedLimitQueryParams), propertyName);
        return unmodifiableList(limit(filtered, queryParams.getLimit()));
    }

    private QueryParams getQueryParamsWithIncreasedLimit(QueryParams queryParams) {
        return QueryParamsBuilder.initializeWith(queryParams)
            .limit(queryParams.getLimit() * 10)
            .build();
    }

    private List<CdoSnapshot> limit(List<CdoSnapshot> list, int limit){
        int size = list.size();
        if (size <= limit){
            return list;
        } else {
            return list.subList(0, limit);
        }
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
