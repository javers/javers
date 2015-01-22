package org.javers.repository.sql.domain;

import org.javers.common.collections.Optional;
import org.javers.core.commit.Commit;
import org.javers.core.commit.CommitId;
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
public class CommitMetadataRepository {

    private final JaversPolyJDBC javersPolyJDBC;

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
}
