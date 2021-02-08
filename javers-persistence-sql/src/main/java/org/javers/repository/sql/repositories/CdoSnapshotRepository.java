package org.javers.repository.sql.repositories;

import java.util.List;

import org.javers.core.json.JsonConverter;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.repository.sql.schema.DBNameProvider;
import org.javers.repository.sql.schema.SchemaNameAware;
import org.javers.repository.sql.session.Session;

public class CdoSnapshotRepository extends SchemaNameAware {

    private JsonConverter jsonConverter;
    private final GlobalIdRepository globalIdRepository;

    public CdoSnapshotRepository(GlobalIdRepository globalIdRepository, DBNameProvider tableNameProvider) {
        super(tableNameProvider);
        this.globalIdRepository = globalIdRepository;
    }

    public void save(long commitIdPk, List<CdoSnapshot> cdoSnapshots, Session session) {
        for (CdoSnapshot cdoSnapshot : cdoSnapshots) {
            long globalIdPk = globalIdRepository.getOrInsertId(cdoSnapshot.getGlobalId(), session);

            session.insert("Snapshot")
                    .into(getSnapshotTableNameWithSchema())
                    .value(getSnapshotTypeColumnName(), cdoSnapshot.getType().toString())
                    .value(getSnapshotGlobalIdFKColumnName(), globalIdPk)
                    .value(getSnapshotCommitFKColumnName(), commitIdPk)
                    .value(getSnapshotVersionColumnName(), cdoSnapshot.getVersion())
                    .value(getSnapshotStateColumnName(), jsonConverter.toJson(cdoSnapshot.getState()))
                    .value(getSnapshotChangedColumnName(), jsonConverter.toJson(cdoSnapshot.getChanged()))
                    .value(getSnapshotManagedTypeColumnName(), cdoSnapshot.getManagedType().getName())
                    .sequence(getSnapshotPKColumnName(), getSnapshotTablePkSeqName().nameWithSchema())
                    .execute();
        }
    }

    public void setJsonConverter(JsonConverter jsonConverter) {
        this.jsonConverter = jsonConverter;
    }
}
