package org.javers.repository.sql.reposiotries;

import org.javers.core.json.JsonConverter;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.repository.sql.ConnectionProvider;
import org.polyjdbc.core.PolyJDBC;
import org.polyjdbc.core.query.InsertQuery;

import java.util.List;

import static org.javers.repository.sql.schema.FixedSchemaFactory.*;

public class CdoSnapshotRepository {

    private PolyJDBC javersPolyJDBC;
    private JsonConverter jsonConverter;
    private ConnectionProvider connectionProvider;
    private GlobalIdRepository globalIdRepository;

    public CdoSnapshotRepository(ConnectionProvider connectionProvider, PolyJDBC javersPolyJDBC, GlobalIdRepository globalIdRepository) {
        this.connectionProvider = connectionProvider;
        this.javersPolyJDBC = javersPolyJDBC;
        this.globalIdRepository = globalIdRepository;
    }

    public void save(long commitIdPk, List<CdoSnapshot> cdoSnapshots) {
        //TODO add batch insert
        for (CdoSnapshot cdoSnapshot : cdoSnapshots) {
            long globalIdPk = globalIdRepository.getOrInsertId(cdoSnapshot.getGlobalId());
            insertSnapshot(globalIdPk, commitIdPk, cdoSnapshot);
        }
    }

    private long insertSnapshot(long globalIdPk, long commitIdPk, CdoSnapshot cdoSnapshot) {
        InsertQuery query = javersPolyJDBC.query().insert().into(SNAPSHOT_TABLE_NAME)
                .value(SNAPSHOT_TYPE, cdoSnapshot.getType().toString())
                .value(SNAPSHOT_GLOBAL_ID_FK, globalIdPk)
                .value(SNAPSHOT_COMMIT_FK, commitIdPk)
                .value(SNAPSHOT_STATE, jsonConverter.toJson(cdoSnapshot.getState()))
                .value(SNAPSHOT_CHANGED, jsonConverter.toJson(cdoSnapshot.getChangedPropertyNames() ))
                .sequence(SNAPSHOT_PK, SNAPSHOT_TABLE_PK_SEQ);

        return javersPolyJDBC.queryRunner().insert(query);
    }

    //TODO dependency injection
    public void setJsonConverter(JsonConverter jsonConverter) {
        this.jsonConverter = jsonConverter;
    }
}
