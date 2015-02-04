package org.javers.repository.sql.domain;

import org.javers.core.json.JsonConverter;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.core.metamodel.property.Property;
import org.javers.repository.sql.infrastructure.poly.JaversPolyJDBC;
import org.polyjdbc.core.query.InsertQuery;

import static org.javers.repository.sql.domain.FixedSchemaFactory.*;

public class CdoSnapshotRepository {

    private JaversPolyJDBC javersPolyJDBC;
    private JsonConverter jsonConverter;

    public CdoSnapshotRepository(JaversPolyJDBC javersPolyJDBC) {
        this.javersPolyJDBC = javersPolyJDBC;
    }

    public long save(long globalIdPk, long commitIdPk, CdoSnapshot cdoSnapshot) {
        InsertQuery query = javersPolyJDBC.query().insert().into(SNAPSHOT_TABLE_NAME)
                .value(SNAPSHOT_TABLE_TYPE, cdoSnapshot.getType().toString())
                .value(SNAPSHOT_TABLE_GLOBAL_ID_FK, globalIdPk)
                .value(SNAPSHOT_TABLE_COMMIT_FK, commitIdPk)
                .sequence(SNAPSHOT_TABLE_PK, SNAPSHOT_TABLE_PK_SEQ);

        long cdoSnapshotPrimaryKey = javersPolyJDBC.queryRunner().insert(query);

        for (Property property : cdoSnapshot.getProperties()) {
            InsertQuery propertyQuery = javersPolyJDBC.query().insert().into(SNAP_PROPERTY_TABLE_NAME)
                    .value(SNAP_PROPERTY_SNAPSHOT_FK, cdoSnapshotPrimaryKey)
                    .value(SNAP_PROPERTY_NAME, property.getName())
                    .value(SNAP_PROPERTY_VALUE, jsonConverter.toJson(cdoSnapshot.getPropertyValue(property)))
                    .value(SNAP_PROPERTY_CLASS, getQualifiedName((property.getType().getName())))
                            .sequence(SNAP_PROPERTY_PK, SNAP_PROPERTY_PK_SEQ);
            
            javersPolyJDBC.queryRunner().insert(propertyQuery);
        }

        return cdoSnapshotPrimaryKey;
    }

    private String getQualifiedName(String name) {
        String result;
        
        switch (name) {
            case "byte": result = Byte.class.getName(); break;
            case "short": result = Short.class.getName(); break;
            case "int": result = Integer.class.getName(); break;
            case "long": result = Long.class.getName(); break;
            case "float": result = Float.class.getName(); break;
            case "double": result = Double.class.getName(); break;
            case "boolean": result = Boolean.class.getName(); break;
            case "char": result = Character.class.getName(); break;
            default: result = name;    
        }
        
        return result;
    }

    public void setJSONConverter(JsonConverter jsonConverter) {
        this.jsonConverter = jsonConverter;
    }
}
