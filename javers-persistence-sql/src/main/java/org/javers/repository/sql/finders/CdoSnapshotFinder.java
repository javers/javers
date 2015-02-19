package org.javers.repository.sql.finders;

import org.javers.common.collections.Optional;
import org.javers.core.json.JsonConverter;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.core.metamodel.object.GlobalId;
import org.javers.repository.sql.PolyUtil;
import org.polyjdbc.core.PolyJDBC;
import org.polyjdbc.core.query.Order;
import org.polyjdbc.core.query.SelectQuery;
import org.polyjdbc.core.query.mapper.ObjectMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import static org.javers.repository.sql.PolyUtil.queryForOptionalInteger;
import static org.javers.repository.sql.schema.FixedSchemaFactory.*;

public class CdoSnapshotFinder {

    private final PolyJDBC polyJDBC;
    private JsonConverter jsonConverter;

    public CdoSnapshotFinder(PolyJDBC javersPolyJDBC) {
        this.polyJDBC = javersPolyJDBC;
    }

    public Optional<CdoSnapshot> getLatest(GlobalId globalId) {
        PersistentGlobalId persistentGlobalId = findGlobalIdPk(globalId);
        if (!persistentGlobalId.found()){
            return Optional.empty();
        }

        Optional<Integer> maxSnapshot = selectMaxSnapshotPrimaryKey(persistentGlobalId);

        if (maxSnapshot.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(getCdoSnapshotsBySnapshotPk(maxSnapshot.get(), maxSnapshot.get(), persistentGlobalId).get(0));
    }

    public List<CdoSnapshot> getStateHistory(GlobalId globalId, int limit) {
        PersistentGlobalId persistentGlobalId = findGlobalIdPk(globalId);
        if (!persistentGlobalId.found()){
            return Collections.emptyList();
        }

        List<Integer> latestSnapshots = selectLatestSnapshotPrimaryKeys(persistentGlobalId, limit);

        if (latestSnapshots.isEmpty()){
            return Collections.emptyList();
        }

        int minSnapshotPk = latestSnapshots.get(0);
        int maxSnapshotPk = latestSnapshots.get(latestSnapshots.size()-1);
        return getCdoSnapshotsBySnapshotPk(minSnapshotPk, maxSnapshotPk, persistentGlobalId);
    }

    //TODO dependency injection
    public void setJsonConverter(JsonConverter jsonConverter) {
        this.jsonConverter = jsonConverter;
    }

    private PersistentGlobalId findGlobalIdPk(GlobalId globalId){
        SelectQuery query = polyJDBC.query()
            .select(GLOBAL_ID_PK)
            .from(GLOBAL_ID_TABLE_NAME + " as g INNER JOIN " +
                  CDO_CLASS_TABLE_NAME + " as c ON " + CDO_CLASS_PK + " = " + GLOBAL_ID_CLASS_FK)
            .where("g." + GLOBAL_ID_LOCAL_ID + " = :localId " +
                   "AND c." + CDO_CLASS_QUALIFIED_NAME + " = :qualifiedName ")
            .withArgument("localId", jsonConverter.toJson(globalId.getCdoId()))
            .withArgument("qualifiedName", globalId.getCdoClass().getName());

        Optional<Integer> primaryKey = queryForOptionalInteger(query, polyJDBC);

        return new PersistentGlobalId(globalId, primaryKey);
    }

    private List<CdoSnapshot> getCdoSnapshotsBySnapshotPk(int minSnapshotPk, int maxSnapshotPk, PersistentGlobalId globalId){
        SelectQuery query = buildSnapshotsContentQuery(minSnapshotPk, maxSnapshotPk, globalId);

        List<SnapshotWideDto> rows =
        polyJDBC.queryRunner().queryList(query, new ObjectMapper<SnapshotWideDto>() {
            @Override
            public SnapshotWideDto createObject(ResultSet resultSet) throws SQLException {
                return new SnapshotWideDto(resultSet);
            }
        });

        SnapshotAssembler assembler = new SnapshotAssembler(jsonConverter);
        return assembler.assemble(rows, globalId);
    }

    private SelectQuery buildSnapshotsContentQuery(int minSnapshotPk, int maxSnapshotPk, PersistentGlobalId globalId) {
        return polyJDBC.query()
            .select("s." + SNAPSHOT_PK + ", " +
                    "s." + SNAPSHOT_TYPE + ", " +
                    "cm." + COMMIT_AUTHOR + ", " +
                    "cm." + COMMIT_COMMIT_DATE + ", " +
                    "cm." + COMMIT_COMMIT_ID + ", " +
                    "p." + SNAP_PROPERTY_NAME + ", " +
                    "p." + SNAP_PROPERTY_VALUE
            )
            .from(SNAPSHOT_TABLE_NAME + " as s INNER JOIN " +
                  COMMIT_TABLE_NAME + " as cm ON " + COMMIT_PK + " = " + SNAPSHOT_COMMIT_FK + " LEFT OUTER JOIN " +
                  SNAP_PROPERTY_TABLE_NAME + " as p ON " + SNAPSHOT_PK + " = " + SNAPSHOT_FK
            )
            .where("s." + SNAPSHOT_PK + " between :minSnapshotPk and :maxSnapshotPk AND " +
                   "s." + SNAPSHOT_GLOBAL_ID_FK + " = :globalIdPk")
            .orderBy("s." + SNAPSHOT_PK, Order.DESC)
            .withArgument("globalIdPk", globalId.primaryKey.get())
            .withArgument("minSnapshotPk", minSnapshotPk)
            .withArgument("maxSnapshotPk", maxSnapshotPk);
    }

    private List<Integer> selectLatestSnapshotPrimaryKeys(PersistentGlobalId globalId, int limit) {
        SelectQuery query = polyJDBC.query()
            .select(SNAPSHOT_PK)
            .from(SNAPSHOT_TABLE_NAME)
            .where(SNAPSHOT_GLOBAL_ID_FK + " = :globalIdPk")
            .withArgument("globalIdPk", globalId.primaryKey.get())
            .orderBy(SNAPSHOT_PK, Order.ASC)
            .limit(limit);

        return PolyUtil.queryForIntegerList(query, polyJDBC);
    }

    private Optional<Integer> selectMaxSnapshotPrimaryKey(PersistentGlobalId globalId) {
        SelectQuery query = polyJDBC.query()
            .select("MAX(" + SNAPSHOT_PK + ")")
            .from(SNAPSHOT_TABLE_NAME)
            .where(SNAPSHOT_GLOBAL_ID_FK + " = :globalIdPk")
            .withArgument("globalIdPk", globalId.primaryKey.get());

        Optional<Integer> result = queryForOptionalInteger(query, polyJDBC);

        if (result.isPresent() && result.get() == 0){
            return Optional.empty();
        }
        return result;
    }
}