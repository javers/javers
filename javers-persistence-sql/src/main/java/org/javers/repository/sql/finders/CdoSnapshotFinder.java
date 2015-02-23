package org.javers.repository.sql.finders;

import org.javers.common.collections.Optional;
import org.javers.core.commit.CommitId;
import org.javers.core.commit.CommitMetadata;
import org.javers.core.json.JsonConverter;
import org.javers.core.metamodel.object.*;
import org.javers.repository.sql.reposiotries.GlobalIdRepository;
import org.javers.repository.sql.reposiotries.PersistentGlobalId;
import org.joda.time.LocalDateTime;
import org.polyjdbc.core.PolyJDBC;
import org.polyjdbc.core.query.Order;
import org.polyjdbc.core.query.SelectQuery;
import org.polyjdbc.core.query.mapper.ObjectMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import static org.javers.repository.sql.PolyUtil.queryForLongList;
import static org.javers.repository.sql.PolyUtil.queryForOptionalLong;
import static org.javers.repository.sql.schema.FixedSchemaFactory.*;

public class CdoSnapshotFinder {

    private final PolyJDBC polyJDBC;
    private JsonConverter jsonConverter;
    private GlobalIdRepository globalIdRepository;

    public CdoSnapshotFinder(GlobalIdRepository globalIdRepository, PolyJDBC polyJDBC) {
        this.globalIdRepository = globalIdRepository;
        this.polyJDBC = polyJDBC;
    }

    public Optional<CdoSnapshot> getLatest(GlobalId globalId) {
        PersistentGlobalId persistentGlobalId = globalIdRepository.findPersistedGlobalId(globalId);
        if (!persistentGlobalId.persisted()){
            return Optional.empty();
        }

        Optional<Long> maxSnapshot = selectMaxSnapshotPrimaryKey(persistentGlobalId);

        if (maxSnapshot.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(getCdoSnapshotsBySnapshotPk(maxSnapshot.get(), maxSnapshot.get(), persistentGlobalId).get(0));
    }

    public List<CdoSnapshot> getStateHistory(GlobalId globalId, int limit) {
        PersistentGlobalId persistentGlobalId = globalIdRepository.findPersistedGlobalId(globalId);
        if (!persistentGlobalId.persisted()){
            return Collections.emptyList();
        }

        List<Long> latestSnapshots = selectLatestSnapshotPrimaryKeys(persistentGlobalId, limit);

        if (latestSnapshots.isEmpty()){
            return Collections.emptyList();
        }

        long maxSnapshotPk = latestSnapshots.get(0);
        long minSnapshotPk = latestSnapshots.get(latestSnapshots.size()-1);
        return getCdoSnapshotsBySnapshotPk(minSnapshotPk, maxSnapshotPk, persistentGlobalId);
    }

    //TODO dependency injection
    public void setJsonConverter(JsonConverter jsonConverter) {
        this.jsonConverter = jsonConverter;
    }

    private List<CdoSnapshot> getCdoSnapshotsBySnapshotPk(long minSnapshotPk, long maxSnapshotPk, final PersistentGlobalId globalId){
        SelectQuery query =
            polyJDBC.query()
                    .select(SNAPSHOT_STATE + ", " +
                            SNAPSHOT_TYPE + ", " +
                            COMMIT_AUTHOR + ", " +
                            COMMIT_COMMIT_DATE + ", " +
                            COMMIT_COMMIT_ID)
                    .from(SNAPSHOT_TABLE_NAME + " INNER JOIN " +
                          COMMIT_TABLE_NAME + "  ON " + COMMIT_PK + " = " + SNAPSHOT_COMMIT_FK)
                    .where(SNAPSHOT_PK + " between :minSnapshotPk and :maxSnapshotPk AND " +
                           SNAPSHOT_GLOBAL_ID_FK + " = :globalIdPk")
                    .orderBy(SNAPSHOT_PK, Order.DESC)
                    .withArgument("globalIdPk", globalId.getPrimaryKey())
                    .withArgument("minSnapshotPk", minSnapshotPk)
                    .withArgument("maxSnapshotPk", maxSnapshotPk);
        return
        polyJDBC.queryRunner().queryList(query, new ObjectMapper<CdoSnapshot>() {
            @Override
            public CdoSnapshot createObject(ResultSet resultSet) throws SQLException {

                String author = resultSet.getString(COMMIT_AUTHOR);
                LocalDateTime commitDate = new LocalDateTime(resultSet.getTimestamp(COMMIT_COMMIT_DATE));
                CommitId commitId = CommitId.valueOf(resultSet.getString(COMMIT_COMMIT_ID));
                CommitMetadata commit = new CommitMetadata(author, commitDate, commitId);

                SnapshotType snapshotType = SnapshotType.valueOf(resultSet.getString(SNAPSHOT_TYPE));
                CdoSnapshotState state =
                        jsonConverter.snapshotStateFromJson(resultSet.getString(SNAPSHOT_STATE), globalId);
                CdoSnapshotBuilder builder = CdoSnapshotBuilder.cdoSnapshot(globalId, commit);
                builder.withType(snapshotType);
                return builder.withState(state).build();
            }
        });
    }

    private List<Long> selectLatestSnapshotPrimaryKeys(PersistentGlobalId globalId, int limit) {
        SelectQuery query = polyJDBC.query()
            .select(SNAPSHOT_PK)
            .from(SNAPSHOT_TABLE_NAME)
                .where(SNAPSHOT_GLOBAL_ID_FK + " = :globalIdPk")
            .withArgument("globalIdPk", globalId.getPrimaryKey())
            .orderBy(SNAPSHOT_PK, Order.DESC)
            .limit(limit);

        return queryForLongList(query, polyJDBC);
    }

    private Optional<Long> selectMaxSnapshotPrimaryKey(PersistentGlobalId globalId) {
        SelectQuery query = polyJDBC.query()
            .select("MAX(" + SNAPSHOT_PK + ")")
            .from(SNAPSHOT_TABLE_NAME)
            .where(SNAPSHOT_GLOBAL_ID_FK + " = :globalIdPk")
            .withArgument("globalIdPk", globalId.getPrimaryKey());

        Optional<Long> result = queryForOptionalLong(query, polyJDBC);

        if (result.isPresent() && result.get() == 0){
            return Optional.empty();
        }
        return result;
    }
}