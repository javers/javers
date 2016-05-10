package org.javers.repository.api;

import org.javers.common.collections.Optional;
import org.javers.core.commit.Commit;
import org.javers.core.commit.CommitId;
import org.javers.core.commit.CommitMetadata;
import org.javers.core.diff.Change;
import org.javers.core.json.JsonConverter;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.core.metamodel.object.GlobalId;
import org.javers.core.metamodel.type.EntityType;
import org.javers.core.metamodel.type.ManagedType;

import java.util.Collection;
import java.util.List;

/**
 * JaversRepository is responsible for persisting {@link Commit}s calculated by Javers core.
 * <br><br>
 *
 * It should persist {@link CommitMetadata} and Snapshots,
 * {@link Change}s should not be persisted as they are recalculated by Javers core as diff between relevant snapshots.
 * <br><br>
 *
 *
 * <h2>Hints for JaversRepository implementation</h2>
 * <ul>
 *    <li/>After persisting in database, Commit is considered immutable so it can not be updated.
 *    <li/>Persisting Commit in any kind of database is easy. JaVers provides flexible
 *         JSON serialization/deserialization engine,
 *         designed as abstraction layer between Java types and specific database types.
 *    <li/>Essentially, object-oriented data are persisted as JSON.
 *    <li/>Repository impl should leverage {@link JsonConverter}.
 * </ul>
 *
 *
 * @author bartosz walacik
 */
public interface JaversRepository {

    /**
     * Snapshots (historical states) of given object
     * in reverse chronological order
     *
     * @param queryParams parameters constraining returned list (size limit, date from/to)
     * @return empty List if object is not versioned
     */
    List<CdoSnapshot> getStateHistory(GlobalId globalId, QueryParams queryParams);

    /**
     * Snapshots of all ValueObjects owned by given ownerEntity at given path
     */
    List<CdoSnapshot> getValueObjectStateHistory(EntityType ownerEntity, String path, QueryParams queryParams);

    /**
     * Filtered version of {@link #getStateHistory(GlobalId, QueryParams)},
     * selects snapshots with a change recorded on a given property
     */
    List<CdoSnapshot> getPropertyStateHistory(GlobalId globalId, String propertyName, QueryParams queryParams);

    /**
     * All snapshots of objects within a given managed class,
     * in reverse chronological order
     *
     * @param queryParams parameters constraining returned list (size limit, date from/to)
     * @return empty List if no snapshots found
     */
    List<CdoSnapshot> getStateHistory(ManagedType givenClass, QueryParams queryParams);

    /**
     * Filtered version of {@link #getStateHistory(ManagedType, QueryParams)},
     * selects all snapshots with a change recorded on a given property
     */
    List<CdoSnapshot> getPropertyStateHistory(ManagedType givenClass, String propertyName, QueryParams queryParams);

    /**
     * Latest snapshot of given object,
     * Optional#EMPTY if object is not versioned
     */
    Optional<CdoSnapshot> getLatest(GlobalId globalId);

    /**
     * Snapshots of all objects in reverse chronological order
     *
     * @param queryParams parameters constraining returned list (size limit, date from/to)
     */
    List<CdoSnapshot> getSnapshots(QueryParams queryParams);

    /**
     * Snapshots with specified globalId and version
     */
    List<CdoSnapshot> getSnapshots(Collection<SnapshotIdentifier> snapshotIdentifiers);

    void persist(Commit commit);

    CommitId getHeadId();

    void setJsonConverter(JsonConverter jsonConverter);

    /**
     * Called at the end of JaVers bootstrap,
     * good place to put database schema update
     */
    void ensureSchema();
}
