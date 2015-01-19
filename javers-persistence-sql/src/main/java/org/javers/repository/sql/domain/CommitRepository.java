package org.javers.repository.sql.domain;

import org.javers.core.commit.CommitMetadata;
import org.javers.repository.sql.poly.JaversPolyJDBC;
import org.javers.repository.sql.schema.FixedSchemaFactory;
import org.joda.time.LocalDateTime;
import org.polyjdbc.core.query.InsertQuery;
import org.polyjdbc.core.query.SelectQuery;
import org.polyjdbc.core.query.mapper.ObjectMapper;
import org.polyjdbc.core.type.Timestamp;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.javers.repository.sql.schema.FixedSchemaFactory.COMMIT_TABLE_AUTHOR;

/**
 * @author pawel szymczyk
 */
public class CommitRepository {

    private JaversPolyJDBC javersPolyjdbc;

    public CommitRepository(JaversPolyJDBC javersPolyjdbc) {
        this.javersPolyjdbc = javersPolyjdbc;
    }

    public long save(CommitMetadata commitMetadata) {
        Long primaryKey = findCommitMetadata(commitMetadata);
        
        return primaryKey != null ? primaryKey : insert(commitMetadata);
    }

    private Long findCommitMetadata(CommitMetadata commitMetadata) {
        SelectQuery selectQuery = javersPolyjdbc.query()
                .select(FixedSchemaFactory.COMMIT_TABLE_PK)
                .from(FixedSchemaFactory.COMMIT_TABLE_NAME)
                .where(FixedSchemaFactory.COMMIT_TABLE_AUTHOR + " = :author " +
                        "AND " + FixedSchemaFactory.COMMIT_TABLE_COMMIT_DATE + " = :date " +
                        "AND " + FixedSchemaFactory.COMMIT_TABLE_COMMIT_ID + " = :id")
                .withArgument("author", commitMetadata.getAuthor())
                .withArgument("date", toTimestamp(commitMetadata.getCommitDate()))
                .withArgument("id", commitMetadata.getId().value());

        return javersPolyjdbc.queryRunner().queryUnique(selectQuery, new ObjectMapper<Long>() {
            @Override
            public Long createObject(ResultSet resultSet) throws SQLException {
                return resultSet.getLong(FixedSchemaFactory.COMMIT_TABLE_PK);
            }
        }, false);
    }

    private Long insert(CommitMetadata commitMetadata) {
        InsertQuery query = javersPolyjdbc.query().insert().into(FixedSchemaFactory.COMMIT_TABLE_NAME)
                .value(COMMIT_TABLE_AUTHOR, commitMetadata.getAuthor())
                .value(FixedSchemaFactory.COMMIT_TABLE_COMMIT_DATE, toTimestamp(commitMetadata.getCommitDate()))
                .value(FixedSchemaFactory.COMMIT_TABLE_COMMIT_ID, commitMetadata.getId().value())
                .sequence(FixedSchemaFactory.COMMIT_TABLE_PK, FixedSchemaFactory.COMMIT_TABLE_PK_SEQ);

        return javersPolyjdbc.queryRunner().insert(query);
    }

    private Timestamp toTimestamp(LocalDateTime commitMetadata) {
        return Timestamp.from(commitMetadata.toDate());
    }
}
