package org.javers.repository.api;

import org.javers.common.collections.Optional;
import org.javers.core.commit.Commit;
import org.javers.core.commit.CommitId;
import org.javers.core.diff.Change;
import org.javers.core.diff.Diff;
import org.javers.core.json.JsonConverter;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.core.metamodel.object.GlobalCdoId;

import java.util.List;

/**
 * JaVers repository is responsible for persisting diffs & commits calculated by javers core.
 * <br/><br/>
 *
 * It deals with {@link Diff} <i>aggregate</i>
 * and {@link Change} <i>unwrap object</i>.
 *
 * @author bartosz walacik
 */
public interface JaversRepository {

    /**
     * Snapshots (historical states) of object
     * in reverse chronological order
     *
     * @param limit choose reasonable limits, production database could contain more records than you expect
     * @return empty List if object is not versioned
     */
    List<CdoSnapshot> getStateHistory(GlobalCdoId globalId, int limit);

    /**
     * Latest snapshot or Optional#EMPTY if object is not versioned
     */
    Optional<CdoSnapshot> getLatest(GlobalCdoId globalId);

    void persist(Commit commit);

    CommitId getHeadId();

    void setJsonConverter(JsonConverter jsonConverter);

    /**
     * Persists given diff in database. <br/>
     * Implementation should:
     * <ol>
     *     <li/>generate next Diff.id and assign it by calling {@link Diff#assignId(long)}
     *     <li/>save Diff and all its Changes into database,
     *          since Diffs are logically immutable, implementation should use only inserts
     * </ol>
     *
     * @param newDiff fresh Diff which hasn't been persisted yet
     * @see Diff#isNew()
     *
     @Deprecated
     void save(Diff newDiff);
     */

    /**
     * Loads Diff from database, collection of {@link Diff#getChanges()} has to be initialized.
     *
     * @return null if not found
     *
     @Deprecated
     Diff getById(long diffId);
     */

    /**
     * Finds all changes made on single domain object.
     * Outcome list has to be ordered chronologically by {@link Diff#getDiffDate()}.
     *
     * @return never returns null
     *
     @Deprecated
     List<Change> findByGlobalCdoId(GlobalCdoId globalCdoId);
      */
}
