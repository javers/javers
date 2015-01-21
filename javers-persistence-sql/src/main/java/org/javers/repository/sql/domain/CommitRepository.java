package org.javers.repository.sql.domain;

import org.javers.common.collections.Optional;
import org.javers.core.commit.CommitMetadata;
import org.javers.repository.sql.infrastructure.poly.JaversPolyJDBC;
import org.joda.time.LocalDateTime;
import org.polyjdbc.core.query.InsertQuery;
import org.polyjdbc.core.query.SelectQuery;
import org.polyjdbc.core.query.mapper.ObjectMapper;
import org.polyjdbc.core.type.Timestamp;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.javers.repository.sql.domain.FixedSchemaFactory.COMMIT_TABLE_AUTHOR;
import static org.javers.repository.sql.domain.FixedSchemaFactory.COMMIT_TABLE_COMMIT_DATE;
import static org.javers.repository.sql.domain.FixedSchemaFactory.COMMIT_TABLE_COMMIT_ID;
import static org.javers.repository.sql.domain.FixedSchemaFactory.COMMIT_TABLE_NAME;
import static org.javers.repository.sql.domain.FixedSchemaFactory.COMMIT_TABLE_PK;
import static org.javers.repository.sql.domain.FixedSchemaFactory.COMMIT_TABLE_PK_SEQ;

/**
 * @author pawel szymczyk
 */
public class CommitRepository {

    private final JaversPolyJDBC javersPolyjdbc;

    public CommitRepository(JaversPolyJDBC javersPolyjdbc) {
        this.javersPolyjdbc = javersPolyjdbc;
    }

    public long save(CommitMetadata commitMetadata) {
        Optional<Long> primaryKey = findCommitMetadataPrimaryKey(commitMetadata);

        return primaryKey.isPresent() ? primaryKey.get() : insert(commitMetadata);
    }

    private Optional<Long> findCommitMetadataPrimaryKey(CommitMetadata commitMetadata) {
        SelectQuery selectQuery = javersPolyjdbc.query()
                .select(COMMIT_TABLE_PK)
                .from(COMMIT_TABLE_NAME)
                .where(COMMIT_TABLE_AUTHOR + " = :author " +
                        "AND " + COMMIT_TABLE_COMMIT_DATE + " = :date " +
                        "AND " + COMMIT_TABLE_COMMIT_ID + " = :id")
                .withArgument("author", commitMetadata.getAuthor())
                .withArgument("date", toTimestamp(commitMetadata.getCommitDate()))
                .withArgument("id", commitMetadata.getId().value());

        return Optional.fromNullable(javersPolyjdbc.queryRunner().queryUnique(selectQuery, new ObjectMapper<Long>() {
            @Override
            public Long createObject(ResultSet resultSet) throws SQLException {
                return resultSet.getLong(COMMIT_TABLE_PK);
            }
        }, false));
    }

    private Long insert(CommitMetadata commitMetadata) {
        InsertQuery query = javersPolyjdbc.query().insert().into(COMMIT_TABLE_NAME)
                .value(COMMIT_TABLE_AUTHOR, commitMetadata.getAuthor())
                .value(COMMIT_TABLE_COMMIT_DATE, toTimestamp(commitMetadata.getCommitDate()))
                .value(COMMIT_TABLE_COMMIT_ID, commitMetadata.getId().value())
                .sequence(COMMIT_TABLE_PK, COMMIT_TABLE_PK_SEQ);

        return javersPolyjdbc.queryRunner().insert(query);
    }

    private Timestamp toTimestamp(LocalDateTime commitMetadata) {
        return Timestamp.from(commitMetadata.toDate());
    }
}
