package org.javers.repository.api;

import org.javers.common.collections.Optional;
import org.javers.core.commit.Commit;
import org.javers.core.commit.CommitId;
import org.javers.core.diff.Change;
import org.javers.core.diff.Diff;
import org.javers.core.json.JsonConverter;
import org.javers.core.metamodel.object.*;

import java.util.List;

/**
 * JaVers repository is responsible for persisting diffs & commits calculated by javers core.
 * <br><br>
 *
 * It deals with {@link Diff} <i>aggregate</i>
 * and {@link Change} <i>unwrap object</i>.
 *
 * @author bartosz walacik
 */
public interface JaversRepository {

    /**
     * Snapshots (historical states) of given object
     * in reverse chronological order
     *
     * @param limit choose reasonable limits, production database could contain more records than you expect
     * @return empty List if object is not versioned
     */
    List<CdoSnapshot> getStateHistory(GlobalCdoId globalCdoId, int limit);

    /**
     * Convenient method to query by DTO, see {@link #getStateHistory(GlobalCdoId, int)}
     */
    List<CdoSnapshot> getStateHistory(GlobalIdDTO globalIdDTO, int limit);

    /**
     * Latest snapshot of given object,
     * Optional#EMPTY if object is not versioned
     */
    Optional<CdoSnapshot> getLatest(GlobalCdoId globalCdoId);

    /**
     * Convenient method to query by DTO, see {@link #getLatest(GlobalCdoId)}
     */
    Optional<CdoSnapshot> getLatest(GlobalIdDTO globalIdDTO);

    void persist(Commit commit);

    CommitId getHeadId();

    void setJsonConverter(JsonConverter jsonConverter);

}
