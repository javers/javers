package org.javers.repository.api;

import org.javers.common.collections.Lists;
import org.javers.common.collections.Optional;
import org.javers.common.collections.Predicate;
import org.javers.common.validation.Validate;
import org.javers.core.commit.Commit;
import org.javers.core.commit.CommitId;
import org.javers.core.json.JsonConverter;
import org.javers.core.metamodel.object.*;
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

    private final GlobalIdFactory globalIdFactory;

    public InMemoryRepository(GlobalIdFactory globalIdFactory) {
        Validate.argumentIsNotNull(globalIdFactory);
        this.globalIdFactory = globalIdFactory;
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
    public List<CdoSnapshot> getPropertyHistory(GlobalId globalId, final String propertyName, int limit) {
        Validate.argumentsAreNotNull(globalId, propertyName);

        if (snapshots.containsKey(globalId)) {
            List<CdoSnapshot> filtered = Lists.positiveFilter(snapshots.get(globalId), new Predicate<CdoSnapshot>() {
                public boolean apply(CdoSnapshot input) {
                    return input.getChanged().contains(propertyName);
                }
            });
            return unmodifiableList(limit(filtered, limit));
        }
        return Collections.EMPTY_LIST;
    }

    private List<CdoSnapshot> limit(List<CdoSnapshot> list, int limit){
        int size = list.size();
        if (size <= limit){
            return list;
        } else {
            return list.subList(size - limit, size);
        }
    }

    @Override
    public Optional<CdoSnapshot> getLatest(GlobalId globalId) {
        Validate.argumentsAreNotNull(globalId);

        if (snapshots.containsKey(globalId)) {
            List<CdoSnapshot> states = snapshots.get(globalId);
            return Optional.of(states.get(states.size() - 1));
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
