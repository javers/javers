package org.javers.repository.sql.finders;

import org.javers.common.collections.Optional;
import org.javers.core.commit.CommitId;
import org.javers.core.commit.CommitMetadata;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.core.metamodel.object.CdoSnapshotBuilder;
import org.javers.core.metamodel.object.GlobalId;
import org.javers.core.metamodel.object.SnapshotType;
import org.javers.repository.sql.infrastructure.poly.JaversPolyJDBC;
import org.joda.time.LocalDateTime;
import org.polyjdbc.core.query.SelectQuery;
import org.polyjdbc.core.query.mapper.ObjectMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import static org.javers.repository.sql.domain.FixedSchemaFactory.CDO_CLASS_QUALIFIED_NAME;
import static org.javers.repository.sql.domain.FixedSchemaFactory.CDO_CLASS_TABLE_NAME;
import static org.javers.repository.sql.domain.FixedSchemaFactory.COMMIT_TABLE_NAME;
import static org.javers.repository.sql.domain.FixedSchemaFactory.GLOBAL_ID_TABLE_NAME;
import static org.javers.repository.sql.domain.FixedSchemaFactory.SNAPSHOT_TABLE_NAME;
import static org.javers.repository.sql.domain.FixedSchemaFactory.SNAPSHOT_TABLE_PK;
import static org.javers.repository.sql.domain.FixedSchemaFactory.SNAP_PROPERTY_SNAPSHOT_FK;
import static org.javers.repository.sql.domain.FixedSchemaFactory.SNAP_PROPERTY_TABLE_NAME;

public class CdoSnapshotFinder {

    private final JaversPolyJDBC javersPolyJDBC;

    public CdoSnapshotFinder(JaversPolyJDBC javersPolyJDBC) {
        this.javersPolyJDBC = javersPolyJDBC;
    }


    public Optional<CdoSnapshot> getLatest(GlobalId globalId) {

        SelectQuery selectQuery = javersPolyJDBC.query()
                .select("MAX(" + SNAPSHOT_TABLE_PK + ")")
                .from(SNAPSHOT_TABLE_NAME + ", " + GLOBAL_ID_TABLE_NAME + ", " + CDO_CLASS_TABLE_NAME)
                .where(CDO_CLASS_QUALIFIED_NAME + " = :qualifiedName")
                .withArgument("qualifiedName", globalId.getCdoClass().getName());

        Integer jvSnaphotDtoPk = javersPolyJDBC.queryRunner().queryUnique(selectQuery, new ObjectMapper<Integer>() {
            @Override
            public Integer createObject(ResultSet resultSet) throws SQLException {
                return Integer.valueOf(resultSet.getString("MAX(" + SNAPSHOT_TABLE_PK + ")"));

            }
        }, false);
        
        if (jvSnaphotDtoPk == null) {
            return Optional.empty();
        }

        SelectQuery selectQuery2 = javersPolyJDBC.query()
                .select("author, commit_date, commit_id")
                .from(SNAPSHOT_TABLE_NAME + ", " + COMMIT_TABLE_NAME)
                .where("\"commit_pk\" = \"commit_fk\" AND \"snapshot_pk\" = :snapshotPk")
                .withArgument("snapshotPk", jvSnaphotDtoPk);

        JvCommitDto jvCommitDto = javersPolyJDBC.queryRunner().queryUnique(selectQuery2, new ObjectMapper<JvCommitDto>() {
            @Override
            public JvCommitDto createObject(ResultSet resultSet) throws SQLException {
                return new JvCommitDto(resultSet.getString("author"), resultSet.getTimestamp("commit_date"), resultSet.getString("commit_id"));
            }
        }, false);

        SelectQuery selectQuery3 = javersPolyJDBC.query()
                .select("name, value")
                .from(SNAP_PROPERTY_TABLE_NAME)
                .where(SNAP_PROPERTY_SNAPSHOT_FK + " = :snapshot_fk")
                .withArgument("snapshot_fk", jvSnaphotDtoPk);

        List<JvSnapshotProperty> properties = javersPolyJDBC.queryRunner().queryList(selectQuery3, new ObjectMapper<JvSnapshotProperty>() {
            @Override
            public JvSnapshotProperty createObject(ResultSet resultSet) throws SQLException {
                return new JvSnapshotProperty(resultSet.getString("name"), resultSet.getObject("value"));
            }
        });

        CommitMetadata commitMetadata = 
                new CommitMetadata(jvCommitDto.author, jvCommitDto.date, CommitId.valueOf(jvCommitDto.commmitId));

        CdoSnapshotBuilder snapshotBuilder = CdoSnapshotBuilder.cdoSnapshot(globalId, commitMetadata).withType(SnapshotType.TERMINAL);
        
        for (JvSnapshotProperty property: properties) {
            snapshotBuilder.withPropertyValue(globalId.getCdoClass().getProperty(property.name), property.value);
        }
        
        return Optional.of(snapshotBuilder.build());
    }

    private static class JvSnapshotDto {
        int pk;
        SnapshotType type;

        public JvSnapshotDto(Integer pk, SnapshotType type) {
            this.pk = pk;
            this.type = type;
        }
    }
    
    private static class JvCommitDto {
        String author;
        LocalDateTime date;
        String commmitId;

        public JvCommitDto(String author, Timestamp date, String commmitId) {
            this.author = author;
            this.date = new LocalDateTime(date.getTime());
            this.commmitId = commmitId;
        }
    }
    
    private static class JvSnapshotProperty {
        String name;
        Object value;

        public JvSnapshotProperty(String name, Object value) {
            this.name = name;
            this.value = value;
        }
    }
}
