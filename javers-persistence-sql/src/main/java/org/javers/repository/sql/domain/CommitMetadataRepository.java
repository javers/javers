package org.javers.repository.sql.domain;

import org.javers.common.collections.Optional;
import org.javers.core.commit.Commit;
import org.javers.core.commit.CommitId;
import org.javers.core.json.JsonConverter;
import org.javers.repository.sql.infrastructure.poly.JaversPolyJDBC;
import org.joda.time.LocalDateTime;
import org.polyjdbc.core.query.InsertQuery;
import org.polyjdbc.core.query.SelectQuery;
import org.polyjdbc.core.query.mapper.ObjectMapper;
import org.polyjdbc.core.type.Timestamp;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static org.javers.repository.sql.domain.FixedSchemaFactory.CDO_CLASS_PK;
import static org.javers.repository.sql.domain.FixedSchemaFactory.CDO_CLASS_QUALIFIED_NAME;
import static org.javers.repository.sql.domain.FixedSchemaFactory.CDO_CLASS_TABLE_NAME;
import static org.javers.repository.sql.domain.FixedSchemaFactory.COMMIT_TABLE_AUTHOR;
import static org.javers.repository.sql.domain.FixedSchemaFactory.COMMIT_TABLE_COMMIT_DATE;
import static org.javers.repository.sql.domain.FixedSchemaFactory.COMMIT_TABLE_COMMIT_ID;
import static org.javers.repository.sql.domain.FixedSchemaFactory.COMMIT_TABLE_NAME;
import static org.javers.repository.sql.domain.FixedSchemaFactory.COMMIT_TABLE_PK;
import static org.javers.repository.sql.domain.FixedSchemaFactory.COMMIT_TABLE_PK_SEQ;
import static org.javers.repository.sql.domain.FixedSchemaFactory.GLOBAL_ID_CLASS_FK;
import static org.javers.repository.sql.domain.FixedSchemaFactory.GLOBAL_ID_LOCAL_ID;
import static org.javers.repository.sql.domain.FixedSchemaFactory.GLOBAL_ID_PK;
import static org.javers.repository.sql.domain.FixedSchemaFactory.GLOBAL_ID_TABLE_NAME;
import static org.javers.repository.sql.domain.FixedSchemaFactory.SNAPSHOT_TABLE_GLOBAL_ID_FK;
import static org.javers.repository.sql.domain.FixedSchemaFactory.SNAPSHOT_TABLE_NAME;
import static org.javers.repository.sql.domain.FixedSchemaFactory.SNAPSHOT_TABLE_PK;

/**
 * @author pawel szymczyk
 */
public class CommitMetadataRepository {

    private final JaversPolyJDBC javersPolyJDBC;
    
    private JsonConverter jsonConverter;

    public CommitMetadataRepository(JaversPolyJDBC javersPolyjdbc) {
        this.javersPolyJDBC = javersPolyjdbc;
    }

    public long save(String author, LocalDateTime date, CommitId commitId) {
        InsertQuery query = javersPolyJDBC.query().insert().into(COMMIT_TABLE_NAME)
                .value(COMMIT_TABLE_AUTHOR, author)
                .value(COMMIT_TABLE_COMMIT_DATE, toTimestamp(date))
                .value(COMMIT_TABLE_COMMIT_ID, commitId.value())
                .sequence(COMMIT_TABLE_PK, COMMIT_TABLE_PK_SEQ);

        return javersPolyJDBC.queryRunner().insert(query);
    }

    private Timestamp toTimestamp(LocalDateTime commitMetadata) {
        return Timestamp.from(commitMetadata.toDate());
    }

    public Optional<Long> find(Commit commit) {
        String author = commit.getAuthor();
        LocalDateTime date = commit.getCommitDate();
        CommitId commitId = commit.getId();
        
        SelectQuery selectQuery = javersPolyJDBC.query()
                .select(COMMIT_TABLE_PK)
                .from(COMMIT_TABLE_NAME)
                .where(COMMIT_TABLE_AUTHOR + " = :author " +
                        "AND " + COMMIT_TABLE_COMMIT_DATE + " = :date " +
                        "AND " + COMMIT_TABLE_COMMIT_ID + " = :id")
                .withArgument("author", author)
                .withArgument("date", toTimestamp(date))
                .withArgument("id", commitId.value());

        return Optional.fromNullable(javersPolyJDBC.queryRunner().queryUnique(selectQuery, new ObjectMapper<Long>() {
            @Override
            public Long createObject(ResultSet resultSet) throws SQLException {
                return resultSet.getLong(COMMIT_TABLE_PK);
            }
        }, false));
    }

    public CommitId getHeadId() {

        SelectQuery selectQuery1 = javersPolyJDBC.query()
                .select("MAX(" + COMMIT_TABLE_NAME + "." + COMMIT_TABLE_PK + ") AS " + COMMIT_TABLE_PK)
                .from(COMMIT_TABLE_NAME);

        List<Integer> maxPrimaryKey = javersPolyJDBC.queryRunner().queryList(selectQuery1, new ObjectMapper<Integer>() {
            @Override
            public Integer createObject(ResultSet resultSet) throws SQLException {
                return resultSet.getInt(COMMIT_TABLE_PK);
            }
        });
        
        if (maxPrimaryKey.size() != 1) {
            return null;
        }
        
        int commitPrimaryKey = maxPrimaryKey.get(0);
        
        SelectQuery selectQuery2 = javersPolyJDBC.query()
                .select(COMMIT_TABLE_NAME + "." + COMMIT_TABLE_COMMIT_ID)
                .from(COMMIT_TABLE_NAME)
                .where(COMMIT_TABLE_PK + " = :maxPrimaryKey")
                .withArgument("maxPrimaryKey", commitPrimaryKey);

        List<CommitId> commitId = javersPolyJDBC.queryRunner().queryList(selectQuery2, new ObjectMapper<CommitId>() {
            @Override
            public CommitId createObject(ResultSet resultSet) throws SQLException {
                return jsonConverter.fromJson(resultSet.getString(COMMIT_TABLE_COMMIT_ID), CommitId.class);
            }
        });
        
        if (commitId.size() != 1) {
            return null;
        }
        
        return commitId.get(0);
    }

    public void setJsonConverter(JsonConverter jsonConverter) {
        this.jsonConverter = jsonConverter;
    }
}
