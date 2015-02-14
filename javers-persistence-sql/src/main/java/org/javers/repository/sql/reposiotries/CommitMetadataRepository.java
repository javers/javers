package org.javers.repository.sql.reposiotries;

import org.javers.common.collections.Optional;
import org.javers.common.exception.JaversException;
import org.javers.common.exception.JaversExceptionCode;
import org.javers.core.commit.Commit;
import org.javers.core.commit.CommitId;
import org.javers.core.json.JsonConverter;
import org.joda.time.LocalDateTime;
import org.polyjdbc.core.PolyJDBC;
import org.polyjdbc.core.query.InsertQuery;
import org.polyjdbc.core.query.SelectQuery;
import org.polyjdbc.core.query.mapper.ObjectMapper;
import org.polyjdbc.core.type.Timestamp;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static org.javers.repository.sql.schema.FixedSchemaFactory.*;

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
        InsertQuery query = javersPolyJDBC.query().insert().into(COMMIT_TABLE_NAME)
                .value(COMMIT_TABLE_AUTHOR, author)
                .value(COMMIT_TABLE_COMMIT_DATE, toTimestamp(date))
                .value(COMMIT_TABLE_COMMIT_ID, commitId.value())
                .sequence(COMMIT_TABLE_PK, COMMIT_TABLE_PK_SEQ);

        return javersPolyJDBC.queryRunner().insert(query);
    }

    public Optional<Long> getCommitPrimaryKey(Commit commit) {
        SelectQuery selectQuery = javersPolyJDBC.query()
                .select(COMMIT_TABLE_PK)
                .from(COMMIT_TABLE_NAME)
                .where(COMMIT_TABLE_AUTHOR + " = :author " +
                        "AND " + COMMIT_TABLE_COMMIT_DATE + " = :date " +
                        "AND " + COMMIT_TABLE_COMMIT_ID + " = :id")
                .withArgument("author", commit.getAuthor())
                .withArgument("date", toTimestamp(commit.getCommitDate()))
                .withArgument("id", commit.getId().value());

        return Optional.fromNullable(javersPolyJDBC.queryRunner().queryUnique(selectQuery, new ObjectMapper<Long>() {
            @Override
            public Long createObject(ResultSet resultSet) throws SQLException {
                return resultSet.getLong(COMMIT_TABLE_PK);
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
                .select("MAX(" + COMMIT_TABLE_NAME + "." + COMMIT_TABLE_PK + ") AS " + COMMIT_TABLE_PK)
                .from(COMMIT_TABLE_NAME);

        List<String> maxPrimaryKey = javersPolyJDBC.queryRunner().queryList(query, new ObjectMapper<String>() {
            @Override
            public String createObject(ResultSet resultSet) throws SQLException {
                return resultSet.getString(COMMIT_TABLE_PK);
            }
        });

        if (maxPrimaryKey.size() != 1 || maxPrimaryKey.get(0) == null) {
            return Optional.empty();
        }

        return Optional.of(Integer.valueOf(maxPrimaryKey.get(0)));
    }

    private CommitId selectCommitId(int primaryKey) {
        SelectQuery query = javersPolyJDBC.query()
                .select(COMMIT_TABLE_NAME + "." + COMMIT_TABLE_COMMIT_ID)
                .from(COMMIT_TABLE_NAME)
                .where(COMMIT_TABLE_PK + " = :maxPrimaryKey")
                .withArgument("maxPrimaryKey", primaryKey);

        List<CommitId> commitId = javersPolyJDBC.queryRunner().queryList(query, new ObjectMapper<CommitId>() {
            @Override
            public CommitId createObject(ResultSet resultSet) throws SQLException {
                return jsonConverter.fromJson(resultSet.getString(COMMIT_TABLE_COMMIT_ID), CommitId.class);
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
