package org.javers.repository.sql.repositories;

import org.javers.common.collections.Optional;
import org.javers.core.commit.Commit;
import org.javers.core.commit.CommitId;
import org.javers.repository.sql.PolyUtil;
import org.joda.time.LocalDateTime;
import org.polyjdbc.core.PolyJDBC;
import org.polyjdbc.core.query.InsertQuery;
import org.polyjdbc.core.query.SelectQuery;
import org.polyjdbc.core.type.Timestamp;

import java.math.BigDecimal;
import java.util.TimeZone;

import static org.javers.repository.sql.PolyUtil.queryForOptionalBigDecimal;
import static org.javers.repository.sql.schema.FixedSchemaFactory.*;

/**
 * @author pawel szymczyk
 */
public class CommitMetadataRepository {
    private final PolyJDBC polyJDBC;

    public CommitMetadataRepository(PolyJDBC polyjdbc) {
        this.polyJDBC = polyjdbc;
    }

    public long save(String author, LocalDateTime date, CommitId commitId) {
        InsertQuery query = polyJDBC.query().insert().into(COMMIT_TABLE_NAME)
                .value(COMMIT_AUTHOR, author)
                .value(COMMIT_COMMIT_DATE, toTimestamp(date))
                .value(COMMIT_COMMIT_ID, commitId.valueAsNumber())
                .sequence(COMMIT_PK, COMMIT_PK_SEQ);

        return polyJDBC.queryRunner().insert(query);
    }

    public boolean isPersisted(Commit commit) {
        SelectQuery selectQuery = polyJDBC.query()
                .select("count(*)")
                .from(COMMIT_TABLE_NAME)
                .where(COMMIT_COMMIT_ID + " = :id")
                .withArgument("id", commit.getId().valueAsNumber());

        return PolyUtil.queryForOptionalLong(selectQuery, polyJDBC).get() > 0;
    }

    private Timestamp toTimestamp(LocalDateTime commitMetadata) {
        return new Timestamp(commitMetadata.toDate());
    }

    public CommitId getCommitHeadId() {
        Optional<BigDecimal> maxCommitId = selectMaxCommitId();

        return maxCommitId.isEmpty() ? null : CommitId.valueOf(maxCommitId.get());
    }

    private Optional<BigDecimal> selectMaxCommitId() {
        SelectQuery query = polyJDBC.query()
                .select("MAX(" + COMMIT_COMMIT_ID + ")")
                .from(COMMIT_TABLE_NAME);

        return queryForOptionalBigDecimal(query, polyJDBC);
    }
}
