package org.javers.repository.api;

import org.javers.common.validation.Validate;
import org.javers.core.commit.Commit;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.core.metamodel.object.GlobalCdoId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Fake impl of JaversRepository
 *
 * @author bartosz walacik
 */
public class InMemoryRepository implements JaversRepository {
    private static final Logger logger = LoggerFactory.getLogger(InMemoryRepository.class);

    private Map<GlobalCdoId, CdoSnapshot> snapshots = new HashMap<>();

    public InMemoryRepository() {
    }

    @Override
    public CdoSnapshot getLatest(GlobalCdoId objectId) {
        Validate.argumentsAreNotNull(objectId);

        return snapshots.get(objectId);
    }

    @Override
    public void persist(Commit commit) {
        Validate.argumentsAreNotNull(commit);
        List<CdoSnapshot> snapshots = commit.getSnapshots();
        for (CdoSnapshot s : snapshots){
            persist(s);
        }
        logger.debug("{} snapshot(s) persisted",snapshots.size());
    }

    private void persist(CdoSnapshot snapshot) {
        snapshots.put(snapshot.getGlobalId(), snapshot);
    }
}
