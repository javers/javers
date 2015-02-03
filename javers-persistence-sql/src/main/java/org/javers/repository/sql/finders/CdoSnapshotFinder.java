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

import static org.javers.repository.sql.domain.FixedSchemaFactory.*;

public class CdoSnapshotFinder {

    private final JaversPolyJDBC javersPolyJDBC;

    public CdoSnapshotFinder(JaversPolyJDBC javersPolyJDBC) {
        this.javersPolyJDBC = javersPolyJDBC;
    }

    public Optional<CdoSnapshot> getLatest(GlobalId globalId) {

        SelectQuery selectQuery = javersPolyJDBC.query()
                .select("MAX(" + SNAPSHOT_TABLE_NAME + "." + SNAPSHOT_TABLE_PK + ") AS " + SNAPSHOT_TABLE_PK)
                .from(SNAPSHOT_TABLE_NAME +
                        " INNER JOIN " + GLOBAL_ID_TABLE_NAME + " ON " + SNAPSHOT_TABLE_NAME + "." + SNAPSHOT_TABLE_GLOBAL_ID_FK + "=" + GLOBAL_ID_TABLE_NAME + "." + GLOBAL_ID_PK +
                        " INNER JOIN " + CDO_CLASS_TABLE_NAME + " ON " + GLOBAL_ID_TABLE_NAME + "." + GLOBAL_ID_CLASS_FK + "=" + CDO_CLASS_TABLE_NAME + "." + CDO_CLASS_PK)
                .where(CDO_CLASS_TABLE_NAME + "." + CDO_CLASS_QUALIFIED_NAME + " = :qualifiedName")
                .withArgument("qualifiedName", globalId.getCdoClass().getName());


        List<Integer> snapshotPk = javersPolyJDBC.queryRunner().queryList(selectQuery, new ObjectMapper<Integer>() {
            @Override
            public Integer createObject(ResultSet resultSet) throws SQLException {
                return resultSet.getInt(SNAPSHOT_TABLE_PK);
            }
        });
        
        if (snapshotPk.size() != 1 ) {
            return Optional.empty();
        }

        SelectQuery selectQuery2 = javersPolyJDBC.query()
                .select(SNAPSHOT_TABLE_NAME + "." + SNAPSHOT_TABLE_TYPE + ", " + COMMIT_TABLE_NAME + "." + COMMIT_TABLE_AUTHOR + ", " + COMMIT_TABLE_NAME + "." + COMMIT_TABLE_COMMIT_DATE + ", " + COMMIT_TABLE_COMMIT_ID)
                .from(SNAPSHOT_TABLE_NAME +
                        " INNER JOIN " + COMMIT_TABLE_NAME + " ON " + COMMIT_TABLE_NAME + "." + COMMIT_TABLE_PK + "=" + SNAPSHOT_TABLE_NAME + "." + SNAPSHOT_TABLE_COMMIT_FK)
                .where(SNAPSHOT_TABLE_NAME + "." + SNAPSHOT_TABLE_PK + " = :snapshotPk")
                .withArgument("snapshotPk", snapshotPk.get(0));

        JvCommitDto jvCommitDto = javersPolyJDBC.queryRunner().queryUnique(selectQuery2, new ObjectMapper<JvCommitDto>() {
            @Override
            public JvCommitDto createObject(ResultSet resultSet) throws SQLException {
                return new JvCommitDto(resultSet.getString(SNAPSHOT_TABLE_TYPE), resultSet.getString(COMMIT_TABLE_AUTHOR), resultSet.getTimestamp(COMMIT_TABLE_COMMIT_DATE), resultSet.getString(COMMIT_TABLE_COMMIT_ID));
            }
        }, false);

        SelectQuery selectQuery3 = javersPolyJDBC.query()
                .select(SNAP_PROPERTY_NAME + ", " + SNAP_PROPERTY_VALUE)
                        .from(SNAP_PROPERTY_TABLE_NAME)
                        .where(SNAP_PROPERTY_SNAPSHOT_FK + " = :snapshot_fk")
                        .withArgument("snapshot_fk", snapshotPk);

        List<JvSnapshotProperty> properties = javersPolyJDBC.queryRunner().queryList(selectQuery3, new ObjectMapper<JvSnapshotProperty>() {
            @Override
            public JvSnapshotProperty createObject(ResultSet resultSet) throws SQLException {
                return new JvSnapshotProperty(resultSet.getString(SNAP_PROPERTY_NAME), resultSet.getObject(SNAP_PROPERTY_VALUE));
            }
        });

        CommitMetadata commitMetadata = new CommitMetadata(jvCommitDto.author, jvCommitDto.date, CommitId.valueOf(jvCommitDto.commitId));

        CdoSnapshotBuilder snapshotBuilder = CdoSnapshotBuilder.cdoSnapshot(globalId, commitMetadata).withType(SnapshotType.TERMINAL);
        
        for (JvSnapshotProperty property: properties) {
            snapshotBuilder.withPropertyValue(globalId.getCdoClass().getProperty(property.name), property.value);
        }
        
        return Optional.of(snapshotBuilder.build());
    }

    private static class JvCommitDto {
        SnapshotType snapshotType;
        String author;
        LocalDateTime date;
        String commitId;

        private JvCommitDto(String snapshotType, String author, Timestamp date, String commmitId) {
            this.snapshotType = SnapshotType.valueOf(snapshotType);
            this.author = author;
            this.date = new LocalDateTime(date.getTime());
            this.commitId = commmitId;
        }
    }
    
    private static class JvSnapshotProperty {
        String name;
        Object value;

        private JvSnapshotProperty(String name, Object value) {
            this.name = name;
            this.value = value;
        }
    }
}
