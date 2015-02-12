package org.javers.repository.sql.reposiotries;

import org.javers.common.collections.Optional;
import org.javers.common.exception.JaversException;
import org.javers.common.exception.JaversExceptionCode;
import org.javers.core.commit.Commit;
import org.javers.core.commit.CommitId;
import org.javers.core.json.JsonConverter;
import org.javers.repository.sql.schema.FixedSchemaFactory;
import org.joda.time.LocalDateTime;
import org.polyjdbc.core.PolyJDBC;
import org.polyjdbc.core.query.InsertQuery;
import org.polyjdbc.core.query.SelectQuery;
import org.polyjdbc.core.query.mapper.ObjectMapper;
import org.polyjdbc.core.type.Timestamp;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * @author pawel szymczyk
 */
public class CommitMetadataRepository {

    private final PolyJDBC javersPolyJDBC;
    private JsonConverter jsonConverter;

    public CommitMetadataRepository(PolyJDBC javersPolyjdbc) {
        this.javersPolyJDBC = javersPolyjdbc;
    }

    public long save(String author, LocalDateTime date, CommitId commitId) {
        InsertQuery query = javersPolyJDBC.query().insert().into(FixedSchemaFactory.COMMIT_TABLE_NAME)
                .value(FixedSchemaFactory.COMMIT_TABLE_AUTHOR, author)
                .value(FixedSchemaFactory.COMMIT_TABLE_COMMIT_DATE, toTimestamp(date))
                .value(FixedSchemaFactory.COMMIT_TABLE_COMMIT_ID, commitId.value())
                .sequence(FixedSchemaFactory.COMMIT_TABLE_PK, FixedSchemaFactory.COMMIT_TABLE_PK_SEQ);

        return javersPolyJDBC.queryRunner().insert(query);
    }

    public Optional<Long> getCommitPrimaryKey(Commit commit) {
        SelectQuery selectQuery = javersPolyJDBC.query()
                .select(FixedSchemaFactory.COMMIT_TABLE_PK)
                .from(FixedSchemaFactory.COMMIT_TABLE_NAME)
                .where(FixedSchemaFactory.COMMIT_TABLE_AUTHOR + " = :author " +
                        "AND " + FixedSchemaFactory.COMMIT_TABLE_COMMIT_DATE + " = :date " +
                        "AND " + FixedSchemaFactory.COMMIT_TABLE_COMMIT_ID + " = :id")
                .withArgument("author", commit.getAuthor())
                .withArgument("date", toTimestamp(commit.getCommitDate()))
                .withArgument("id", commit.getId().value());

        return Optional.fromNullable(javersPolyJDBC.queryRunner().queryUnique(selectQuery, new ObjectMapper<Long>() {
            @Override
            public Long createObject(ResultSet resultSet) throws SQLException {
                return resultSet.getLong(FixedSchemaFactory.COMMIT_TABLE_PK);
            }
        }, false));
    }

    private Timestamp toTimestamp(LocalDateTime commitMetadata) {
        return Timestamp.from(commitMetadata.toDate());
    }

    public CommitId getCommitHeadId() {
        Optional<Integer> maxPrimaryKey = selectMaxCommitPrimaryKey();

        return maxPrimaryKey.isEmpty() ? null : selectCommitId(maxPrimaryKey.get());
    }

    private Optional<Integer> selectMaxCommitPrimaryKey() {
        SelectQuery query = javersPolyJDBC.query()
                .select("MAX(" + FixedSchemaFactory.COMMIT_TABLE_NAME + "." + FixedSchemaFactory.COMMIT_TABLE_PK + ") AS " + FixedSchemaFactory.COMMIT_TABLE_PK)
                .from(FixedSchemaFactory.COMMIT_TABLE_NAME);

        List<String> maxPrimaryKey = javersPolyJDBC.queryRunner().queryList(query, new ObjectMapper<String>() {
            @Override
            public String createObject(ResultSet resultSet) throws SQLException {
                return resultSet.getString(FixedSchemaFactory.COMMIT_TABLE_PK);
            }
        });

        if (maxPrimaryKey.size() != 1 || maxPrimaryKey.get(0) == null) {
            return Optional.empty();
        }

        return Optional.of(Integer.valueOf(maxPrimaryKey.get(0)));
    }

    private CommitId selectCommitId(int primaryKey) {
        SelectQuery query = javersPolyJDBC.query()
                .select(FixedSchemaFactory.COMMIT_TABLE_NAME + "." + FixedSchemaFactory.COMMIT_TABLE_COMMIT_ID)
                .from(FixedSchemaFactory.COMMIT_TABLE_NAME)
                .where(FixedSchemaFactory.COMMIT_TABLE_PK + " = :maxPrimaryKey")
                .withArgument("maxPrimaryKey", primaryKey);

        List<CommitId> commitId = javersPolyJDBC.queryRunner().queryList(query, new ObjectMapper<CommitId>() {
            @Override
            public CommitId createObject(ResultSet resultSet) throws SQLException {
                return jsonConverter.fromJson(resultSet.getString(FixedSchemaFactory.COMMIT_TABLE_COMMIT_ID), CommitId.class);
            }
        });

        if (commitId.size() != 1) {
            throw new JaversException(JaversExceptionCode.CANT_FIND_COMMIT_HEAD_ID);
        }

        return commitId.get(0);
    }

    //TODO dependency injection
    public void setJsonConverter(JsonConverter jsonConverter) {
        this.jsonConverter = jsonConverter;
    }
}
