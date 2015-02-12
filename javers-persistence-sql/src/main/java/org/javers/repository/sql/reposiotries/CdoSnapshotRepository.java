package org.javers.repository.sql.reposiotries;

import org.javers.core.json.JsonConverter;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.core.metamodel.property.Property;
import org.polyjdbc.core.PolyJDBC;
import org.polyjdbc.core.query.InsertQuery;

import static org.javers.repository.sql.schema.FixedSchemaFactory.*;

public class CdoSnapshotRepository {

    private PolyJDBC javersPolyJDBC;
    private JsonConverter jsonConverter;

    public CdoSnapshotRepository(PolyJDBC javersPolyJDBC) {
        this.javersPolyJDBC = javersPolyJDBC;
    }

    public long save(long globalIdPk, long commitIdPk, CdoSnapshot cdoSnapshot) {
        long cdoSnapshotPk = selectSnapshotPrimaryKey(globalIdPk, commitIdPk, cdoSnapshot);

        for (Property property : cdoSnapshot.getProperties()) {
            saveProperty(cdoSnapshot, cdoSnapshotPk, property);
        }

        return cdoSnapshotPk;
    }

    private void saveProperty(CdoSnapshot cdoSnapshot, long cdoSnapshotPrimaryKey, Property property) {
        InsertQuery propertyQuery = javersPolyJDBC.query().insert().into(SNAP_PROPERTY_TABLE_NAME)
                .value(SNAP_PROPERTY_SNAPSHOT_FK, cdoSnapshotPrimaryKey)
                .value(SNAP_PROPERTY_NAME, property.getName())
                .value(SNAP_PROPERTY_VALUE, jsonConverter.toJson(cdoSnapshot.getPropertyValue(property)))
                        .sequence(SNAP_PROPERTY_PK, SNAP_PROPERTY_PK_SEQ);

        javersPolyJDBC.queryRunner().insert(propertyQuery);
    }

    private long selectSnapshotPrimaryKey(long globalIdPk, long commitIdPk, CdoSnapshot cdoSnapshot) {
        InsertQuery query = javersPolyJDBC.query().insert().into(SNAPSHOT_TABLE_NAME)
                .value(SNAPSHOT_TABLE_TYPE, cdoSnapshot.getType().toString())
                .value(SNAPSHOT_TABLE_GLOBAL_ID_FK, globalIdPk)
                .value(SNAPSHOT_TABLE_COMMIT_FK, commitIdPk)
                .sequence(SNAPSHOT_TABLE_PK, SNAPSHOT_TABLE_PK_SEQ);

        return javersPolyJDBC.queryRunner().insert(query);
    }

    //TODO dependency injection
    public void setJsonConverter(JsonConverter jsonConverter) {
        this.jsonConverter = jsonConverter;
    }
}
