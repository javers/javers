package org.javers.repository.sql.repositories;

import org.javers.core.json.JsonConverter;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.repository.sql.schema.SchemaNameAware;
import org.javers.repository.sql.schema.TableNameProvider;
import org.javers.repository.sql.session.Session;
import org.polyjdbc.core.PolyJDBC;

import java.util.List;

import static org.javers.repository.sql.schema.FixedSchemaFactory.*;
import static org.javers.repository.sql.session.ParametersBuilder.parameters;

public class CdoSnapshotRepository extends SchemaNameAware {

    private JsonConverter jsonConverter;
    private final GlobalIdRepository globalIdRepository;

    public CdoSnapshotRepository(GlobalIdRepository globalIdRepository, TableNameProvider tableNameProvider) {
        super(tableNameProvider);
        this.globalIdRepository = globalIdRepository;
    }

    public void save(long commitIdPk, List<CdoSnapshot> cdoSnapshots, Session session) {
        for (CdoSnapshot cdoSnapshot : cdoSnapshots) {
            long globalIdPk = globalIdRepository.getOrInsertId(cdoSnapshot.getGlobalId(), session);
            insertSnapshot(globalIdPk, commitIdPk, cdoSnapshot, session);
        }
    }

    private long insertSnapshot(long globalIdPk, long commitIdPk, CdoSnapshot cdoSnapshot, Session session) {

        //TODO blind

        return session.insert(
                "insert Snapshot",
                parameters()
                        .add(SNAPSHOT_TYPE, cdoSnapshot.getType().toString())
                        .add(SNAPSHOT_GLOBAL_ID_FK, globalIdPk)
                        .add(SNAPSHOT_COMMIT_FK, commitIdPk)
                        .add(SNAPSHOT_VERSION, cdoSnapshot.getVersion())
                        .add(SNAPSHOT_STATE, jsonConverter.toJson(cdoSnapshot.getState()))
                        .add(SNAPSHOT_CHANGED, jsonConverter.toJson(cdoSnapshot.getChanged()))
                        .add(SNAPSHOT_MANAGED_TYPE, cdoSnapshot.getManagedType().getName())
                        .build(),
                getSnapshotTableNameWithSchema(),
                SNAPSHOT_PK,
                getSnapshotTablePkSeqWithSchema());
    }

    public void setJsonConverter(JsonConverter jsonConverter) {
        this.jsonConverter = jsonConverter;
    }
}
