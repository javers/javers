package org.javers.repository.sql.repositories;

import org.javers.core.json.JsonConverter;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.polyjdbc.core.PolyJDBC;
import org.polyjdbc.core.query.InsertQuery;

import java.util.List;

import static org.javers.repository.sql.schema.FixedSchemaFactory.*;

public class CdoSnapshotRepository {

    private final PolyJDBC javersPolyJDBC;
    private JsonConverter jsonConverter;
    private final GlobalIdRepository globalIdRepository;

    public CdoSnapshotRepository(PolyJDBC javersPolyJDBC, GlobalIdRepository globalIdRepository) {
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
                .value(SNAPSHOT_VERSION, cdoSnapshot.getVersion())
                .value(SNAPSHOT_STATE, jsonConverter.toJson(cdoSnapshot.getState()))
                .value(SNAPSHOT_CHANGED, jsonConverter.toJson(cdoSnapshot.getChanged() ))
                .value(SNAPSHOT_MANAGED_TYPE, cdoSnapshot.getManagedType().getName())
                .sequence(SNAPSHOT_PK, SNAPSHOT_TABLE_PK_SEQ);

        return javersPolyJDBC.queryRunner().insert(query);
    }

    public void setJsonConverter(JsonConverter jsonConverter) {
        this.jsonConverter = jsonConverter;
    }
}
