package org.javers.repository.sql.reposiotries;

import org.javers.common.collections.Optional;
import org.javers.common.exception.JaversException;
import org.javers.common.exception.JaversExceptionCode;
import org.javers.core.commit.Commit;
import org.javers.core.commit.CommitId;
import org.javers.core.json.JsonConverter;
import org.javers.repository.sql.PolyUtil;
import org.joda.time.LocalDateTime;
import org.polyjdbc.core.PolyJDBC;
import org.polyjdbc.core.query.InsertQuery;
import org.polyjdbc.core.query.SelectQuery;
import org.polyjdbc.core.query.mapper.ObjectMapper;
import org.polyjdbc.core.type.Timestamp;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static org.javers.repository.sql.PolyUtil.queryForOptionalLong;
import static org.javers.repository.sql.schema.FixedSchemaFactory.*;

/**
 * @author pawel szymczyk
 */
public class CommitMetadataRepository {

    private final PolyJDBC polyJDBC;
    private JsonConverter jsonConverter;


    public CommitMetadataRepository(PolyJDBC polyjdbc) {
        this.polyJDBC = polyjdbc;
    }

    public long save(String author, LocalDateTime date, CommitId commitId) {
        InsertQuery query = polyJDBC.query().insert().into(COMMIT_TABLE_NAME)
                .value(COMMIT_AUTHOR, author)
                .value(COMMIT_COMMIT_DATE, toTimestamp(date))
                .value(COMMIT_COMMIT_ID, commitId.value())
                .sequence(COMMIT_PK, COMMIT_PK_SEQ);

        return polyJDBC.queryRunner().insert(query);
    }

    public Optional<Long> getCommitPrimaryKey(Commit commit) {
        SelectQuery selectQuery = polyJDBC.query()
                .select(COMMIT_PK)
                .from(COMMIT_TABLE_NAME)
                .where(COMMIT_COMMIT_ID + " = :id")
                .withArgument("id", commit.getId().value());

        return PolyUtil.queryForOptionalLong(selectQuery, polyJDBC);
    }

    private Timestamp toTimestamp(LocalDateTime commitMetadata) {
        return Timestamp.from(commitMetadata.toDate());
    }

    public CommitId getCommitHeadId() {
        Optional<Long> maxPrimaryKey = selectMaxCommitPrimaryKey();

        return maxPrimaryKey.isEmpty() ? null : selectCommitId(maxPrimaryKey.get());
    }

    private Optional<Long> selectMaxCommitPrimaryKey() {
        SelectQuery query = polyJDBC.query()
                .select("MAX(" + COMMIT_PK + ")")
                .from(COMMIT_TABLE_NAME);

        Optional<Long> result = queryForOptionalLong(query, polyJDBC);

        if (result.isPresent() && result.get() == 0){
            return Optional.empty();
        }
        return result;
    }

    private CommitId selectCommitId(long primaryKey) {
        SelectQuery query = polyJDBC.query()
                .select(COMMIT_COMMIT_ID)
                .from(COMMIT_TABLE_NAME)
                .where(COMMIT_PK + " = :maxPrimaryKey")
                .withArgument("maxPrimaryKey", primaryKey);

        List<CommitId> commitId = polyJDBC.queryRunner().queryList(query, new ObjectMapper<CommitId>() {
            @Override
            public CommitId createObject(ResultSet resultSet) throws SQLException {
                return jsonConverter.fromJson(resultSet.getString(COMMIT_COMMIT_ID), CommitId.class);
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
