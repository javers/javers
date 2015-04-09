package org.javers.repository.sql.finders;

import org.javers.common.collections.Optional;
import org.javers.core.commit.CommitId;
import org.javers.core.commit.CommitMetadata;
import org.javers.core.json.JsonConverter;
import org.javers.core.metamodel.clazz.ManagedClass;
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

        return Optional.of(queryForCdoSnapshotsInRange(new Range(maxSnapshot.get()), persistentGlobalId).get(0));
    }

    public List<CdoSnapshot> getStateHistory(ManagedClass givenClass, int limit) {
        Optional<Long> classPk = globalIdRepository.findClassPk(givenClass.getClientsClass());
        if (classPk.isEmpty()){
            return Collections.emptyList();
        }

       // Optional<Range> latestSnapshots = selectLatestSnapshotPrimaryKeysByClassPk(classPk.get(), limit);
        //if (latestSnapshots.isEmpty()){
       //     return Collections.emptyList();
       // }

        return null;
    }

    public List<CdoSnapshot> getStateHistory(GlobalId globalId, int limit) {
        PersistentGlobalId persistentGlobalId = globalIdRepository.findPersistedGlobalId(globalId);
        if (!persistentGlobalId.persisted()){
            return Collections.emptyList();
        }

        Optional<Range> latestSnapshots = selectLatestSnapshotPrimaryKeys(persistentGlobalId, limit);
        if (latestSnapshots.isEmpty()){
            return Collections.emptyList();
        }

        return queryForCdoSnapshotsInRange(latestSnapshots.get(), persistentGlobalId);
    }


    //TODO dependency injection
    public void setJsonConverter(JsonConverter jsonConverter) {
        this.jsonConverter = jsonConverter;
    }

    private List<CdoSnapshot> queryForCdoSnapshotsInRange(Range snapshotPkRange, final PersistentGlobalId globalId){

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
                    .withArgument("minSnapshotPk", snapshotPkRange.from)
                    .withArgument("maxSnapshotPk", snapshotPkRange.to);
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
                        jsonConverter.snapshotStateFromJson(resultSet.getString(SNAPSHOT_STATE), globalId); //ManagedClass?
                CdoSnapshotBuilder builder = CdoSnapshotBuilder.cdoSnapshot(globalId, commit);
                builder.withType(snapshotType);
                return builder.withState(state).build();
            }
        });
    }

    private Optional<Range> selectLatestSnapshotPrimaryKeys(PersistentGlobalId globalId, int limit) {
        SelectQuery query = polyJDBC.query()
            .select(SNAPSHOT_PK)
            .from(SNAPSHOT_TABLE_NAME)
            .where(SNAPSHOT_GLOBAL_ID_FK + " = :globalIdPk")
            .withArgument("globalIdPk", globalId.getPrimaryKey())
            .orderBy(SNAPSHOT_PK, Order.DESC)
            .limit(limit);

        List<Long> primaryKeys = queryForLongList(query, polyJDBC);

        if (primaryKeys.isEmpty()){
            return Optional.empty();
        }

        return Optional.of( new Range(primaryKeys.get(primaryKeys.size() - 1), primaryKeys.get(0)) );
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

    private class Range{
        long from;
        long to;

        Range(long fromTo) {
            this.from = fromTo;
            this.to = fromTo;
        }
        Range(long from, long to) {
            this.from = from;
            this.to = to;
        }
    }

    private abstract class SnapshotFilter{
    }

    private class GlobalIdFilter extends SnapshotFilter{
        long globalIdPk;
        GlobalIdFilter(long globalIdPk) {
            this.globalIdPk = globalIdPk;
        }
    }
    private class ClassIdFilter extends SnapshotFilter{
        long classPk;
        ClassIdFilter(long classPk) {
            this.classPk = classPk;
        }
    }
}