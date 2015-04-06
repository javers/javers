package org.javers.repository.api;

import org.javers.common.collections.Lists;
import org.javers.common.collections.Optional;
import org.javers.common.collections.Predicate;
import org.javers.common.validation.Validate;
import org.javers.core.commit.Commit;
import org.javers.core.commit.CommitId;
import org.javers.core.json.JsonConverter;
import org.javers.core.metamodel.clazz.Entity;
import org.javers.core.metamodel.clazz.ManagedClass;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.core.metamodel.object.GlobalId;
import org.javers.core.metamodel.object.ValueObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static java.util.Collections.unmodifiableList;

/**
 * Fake impl of JaversRepository
 *
 * @author bartosz walacik
 */
class InMemoryRepository implements JaversRepository {
    private static final Logger logger = LoggerFactory.getLogger(InMemoryRepository.class);

    private Map<GlobalId, LinkedList<CdoSnapshot>> snapshots = new HashMap<>();

    private CommitId head;

    public InMemoryRepository() {
    }

    @Override
    public List<CdoSnapshot> getValueObjectStateHistory(final Entity ownerEntity, final String path, int limit) {
        Validate.argumentsAreNotNull(ownerEntity, path);

        List<CdoSnapshot> result =  Lists.positiveFilter(getAll(), new Predicate<CdoSnapshot>() {
            @Override
            public boolean apply(CdoSnapshot input) {
                if (!(input.getGlobalId() instanceof ValueObjectId)) {
                    return false;
                }
                ValueObjectId id = (ValueObjectId) input.getGlobalId();

                return id.getOwnerId().getCdoClass().getClientsClass().equals(ownerEntity.getClientsClass())
                        && id.getFragment().equals(path);
            }
        });

        return limit(result, limit);
    }

    @Override
    public List<CdoSnapshot> getStateHistory(GlobalId globalId, int limit) {
        Validate.argumentIsNotNull(globalId);

        if (snapshots.containsKey(globalId)) {
            return unmodifiableList(limit(snapshots.get(globalId), limit));
        }
        return Collections.EMPTY_LIST;
    }

    @Override
    public List<CdoSnapshot> getStateHistory(ManagedClass givenClass, int limit) {
        Validate.argumentIsNotNull(givenClass);
        List<CdoSnapshot> filtered = new ArrayList<>();

        for (CdoSnapshot snapshot : getAll()) {
            if (snapshot.getGlobalId().getCdoClass().getClientsClass().equals(givenClass.getClientsClass())) {
                filtered.add(snapshot);
            }
        }

        return limit(filtered,limit);
    }

    @Override
    public List<CdoSnapshot> getPropertyStateHistory(GlobalId globalId, final String propertyName, int limit) {
        Validate.argumentsAreNotNull(globalId, propertyName);

        if (snapshots.containsKey(globalId)) {
            List<CdoSnapshot> filtered = filterByPropertyName(snapshots.get(globalId), propertyName);
            return unmodifiableList(limit(filtered, limit));
        }
        return Collections.EMPTY_LIST;
    }

    @Override
    public List<CdoSnapshot> getPropertyStateHistory(ManagedClass givenClass, String propertyName, int limit) {
        Validate.argumentsAreNotNull(givenClass, propertyName);

        List<CdoSnapshot> filtered = filterByPropertyName(getStateHistory(givenClass, limit * 10), propertyName);
        return unmodifiableList(limit(filtered, limit));
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

    private void persist(CdoSnapshot snapshot) {
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
