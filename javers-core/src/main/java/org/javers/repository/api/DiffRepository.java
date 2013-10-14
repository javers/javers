package org.javers.repository.api;

import org.javers.model.domain.Diff;
import org.javers.model.domain.Change;

/**
 * Diff repository is responsible for persisting diffs calculated by javers core.
 * <br/><br/>
 *
 * It deals with {@link Diff} <i>aggregate</i>
 * and {@link Change} <i>value object</i>.
 *
 * @author bartosz walacik
 */
public interface DiffRepository {

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
     */
     void save(Diff newDiff);

    /**
     * Loads Diff from database, collection of {@link Diff#getChanges()} have to be initialized.
     *
     * @return null if not found
     */
     Diff getById(long diffId);
}
