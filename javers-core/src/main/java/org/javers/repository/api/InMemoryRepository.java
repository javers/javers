package org.javers.repository.api;

import org.javers.common.collections.Optional;
import org.javers.common.validation.Validate;
import org.javers.core.commit.Commit;
import org.javers.core.commit.CommitId;
import org.javers.core.json.JsonConverter;
import org.javers.core.metamodel.object.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

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
            int size = snapshots.get(globalId).size();
            if (size <= limit){
                return Collections.unmodifiableList(snapshots.get(globalId));
            } else {
                return Collections.unmodifiableList(snapshots.get(globalId).subList(size - limit, size));
            }
        }
        return Collections.EMPTY_LIST;
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
        logger.debug("{} snapshot(s) persisted",snapshots.size());
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
}
