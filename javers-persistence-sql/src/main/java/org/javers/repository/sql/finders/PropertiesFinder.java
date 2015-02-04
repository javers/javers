package org.javers.repository.sql.finders;

import org.javers.core.json.JsonConverter;
import org.javers.repository.sql.infrastructure.poly.JaversPolyJDBC;
import org.polyjdbc.core.query.SelectQuery;
import org.polyjdbc.core.query.mapper.ObjectMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static org.javers.repository.sql.domain.FixedSchemaFactory.SNAP_PROPERTY_CLASS;
import static org.javers.repository.sql.domain.FixedSchemaFactory.SNAP_PROPERTY_NAME;
import static org.javers.repository.sql.domain.FixedSchemaFactory.SNAP_PROPERTY_SNAPSHOT_FK;
import static org.javers.repository.sql.domain.FixedSchemaFactory.SNAP_PROPERTY_TABLE_NAME;
import static org.javers.repository.sql.domain.FixedSchemaFactory.SNAP_PROPERTY_VALUE;

public class PropertiesFinder {

    private final JaversPolyJDBC javersPolyJDBC;
    private JsonConverter JSONConverter;

    public PropertiesFinder(JaversPolyJDBC javersPolyJDBC) {
        this.javersPolyJDBC = javersPolyJDBC;
    }

    public List<JvSnapshotProperty> findProperties(int snapshotPk) {
        SelectQuery query = javersPolyJDBC.query()
                .select(SNAP_PROPERTY_NAME + ", " + SNAP_PROPERTY_VALUE + ", " + SNAP_PROPERTY_CLASS)
                .from(SNAP_PROPERTY_TABLE_NAME)
                .where(SNAP_PROPERTY_SNAPSHOT_FK + " = :snapshot_fk")
                .withArgument("snapshot_fk", snapshotPk);

        return javersPolyJDBC.queryRunner().queryList(query, new ObjectMapper<JvSnapshotProperty>() {
            @Override
            public JvSnapshotProperty createObject(ResultSet resultSet) throws SQLException {
                try {
                    return new JvSnapshotProperty(resultSet.getString(SNAP_PROPERTY_NAME), JSONConverter.fromJson(resultSet.getString(SNAP_PROPERTY_VALUE), Class.forName(resultSet.getString(SNAP_PROPERTY_CLASS))));
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        });
    }

    public void setJSONConverter(JsonConverter JSONConverter) {
        this.JSONConverter = JSONConverter;
    }

    public static class JvSnapshotProperty<T> {
        String name;
        T value;

        private JvSnapshotProperty(String name, T value) {
            this.name = name;
            this.value = value;
        }
    }
}
