package org.javers.repository.sql.repositories;

import org.javers.core.json.JsonConverter;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.repository.sql.schema.SchemaNameAware;
import org.javers.repository.sql.schema.TableNameProvider;
import org.javers.repository.sql.session.Session;
import org.polyjdbc.core.PolyJDBC;
import org.polyjdbc.core.query.InsertQuery;

import java.util.List;

import static org.javers.repository.sql.schema.FixedSchemaFactory.*;

public class CdoSnapshotRepository extends SchemaNameAware {


    private final PolyJDBC javersPolyJDBC;
    private JsonConverter jsonConverter;
    private final GlobalIdRepository globalIdRepository;

    public CdoSnapshotRepository(PolyJDBC javersPolyJDBC, GlobalIdRepository globalIdRepository, TableNameProvider tableNameProvider) {
        super(tableNameProvider);
        this.javersPolyJDBC = javersPolyJDBC;
        this.globalIdRepository = globalIdRepository;
    }

    public void save(long commitIdPk, List<CdoSnapshot> cdoSnapshots, Session session) {
        for (CdoSnapshot cdoSnapshot : cdoSnapshots) {
            long globalIdPk = globalIdRepository.getOrInsertId(cdoSnapshot.getGlobalId(), session);
            insertSnapshot(globalIdPk, commitIdPk, cdoSnapshot);
        }
    }

    private long insertSnapshot(long globalIdPk, long commitIdPk, CdoSnapshot cdoSnapshot) {
        // TODO HOTSPOT
        System.out.println("--HOTSPOT-1 insertSnapshot() globalIdPk:" + globalIdPk+ ", commitIdPk:"+commitIdPk);

        InsertQuery query = javersPolyJDBC.query().insert().into(getSnapshotTableNameWithSchema())
                .value(SNAPSHOT_TYPE, cdoSnapshot.getType().toString())
                .value(SNAPSHOT_GLOBAL_ID_FK, globalIdPk)
                .value(SNAPSHOT_COMMIT_FK, commitIdPk)
                .value(SNAPSHOT_VERSION, cdoSnapshot.getVersion())
                .value(SNAPSHOT_STATE, jsonConverter.toJson(cdoSnapshot.getState()))
                .value(SNAPSHOT_CHANGED, jsonConverter.toJson(cdoSnapshot.getChanged()))
                .value(SNAPSHOT_MANAGED_TYPE, cdoSnapshot.getManagedType().getName())
                .sequence(SNAPSHOT_PK, getSnapshotTablePkSeqWithSchema());

        return javersPolyJDBC.queryRunner().insert(query);
    }

    public void setJsonConverter(JsonConverter jsonConverter) {
        this.jsonConverter = jsonConverter;
    }
}
