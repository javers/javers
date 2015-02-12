package org.javers.repository.sql.finders;

import org.polyjdbc.core.PolyJDBC;
import org.polyjdbc.core.query.SelectQuery;
import org.polyjdbc.core.query.mapper.ObjectMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static org.javers.repository.sql.schema.FixedSchemaFactory.*;

public class PropertiesFinder {

    private final PolyJDBC javersPolyJDBC;

    public PropertiesFinder(PolyJDBC javersPolyJDBC) {
        this.javersPolyJDBC = javersPolyJDBC;
    }

    public List<SnapshotPropertyDto> findProperties(int snapshotPk) {
        SelectQuery query = javersPolyJDBC.query()
                .select(SNAP_PROPERTY_NAME + ", " + SNAP_PROPERTY_VALUE)
                .from(SNAP_PROPERTY_TABLE_NAME)
                .where(SNAP_PROPERTY_SNAPSHOT_FK + " = :snapshot_fk")
                .withArgument("snapshot_fk", snapshotPk);

        return javersPolyJDBC.queryRunner().queryList(query, new ObjectMapper<SnapshotPropertyDto>() {
            @Override
            public SnapshotPropertyDto createObject(ResultSet resultSet) throws SQLException {
                return new SnapshotPropertyDto(resultSet.getString(SNAP_PROPERTY_NAME), resultSet.getString(SNAP_PROPERTY_VALUE));
            }
        });
    }

    public static class SnapshotPropertyDto {
        private String name;
        private String value;

        private SnapshotPropertyDto(String name, String value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public String getValue() {
            return value;
        }
    }
}
