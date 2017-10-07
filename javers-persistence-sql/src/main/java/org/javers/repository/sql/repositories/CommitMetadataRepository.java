package org.javers.repository.sql.repositories;

import java.util.Optional;
import org.javers.core.commit.Commit;
import org.javers.core.commit.CommitId;
import org.javers.core.json.typeadapter.util.UtilTypeCoreAdapters;
import org.javers.repository.sql.poly.PolyUtil;
import org.javers.repository.sql.schema.SchemaNameAware;
import org.javers.repository.sql.schema.TableNameProvider;
import java.time.LocalDateTime;
import org.polyjdbc.core.PolyJDBC;
import org.polyjdbc.core.query.InsertQuery;
import org.polyjdbc.core.query.SelectQuery;
import org.polyjdbc.core.type.Timestamp;

import java.math.BigDecimal;
import java.util.Map;

import static org.javers.repository.sql.poly.PolyUtil.queryForOptionalBigDecimal;
import static org.javers.repository.sql.schema.FixedSchemaFactory.*;

/**
 * @author pawel szymczyk
 */
public class CommitMetadataRepository extends SchemaNameAware {

    private final PolyJDBC polyJDBC;

    public CommitMetadataRepository(PolyJDBC polyjdbc, TableNameProvider tableNameProvider) {
        super(tableNameProvider);
        this.polyJDBC = polyjdbc;
    }

    public long save(String author, Map<String, String> properties, LocalDateTime date, CommitId commitId) {
        long commitPk = insertCommit(author, date, commitId);
        insertCommitProperties(commitPk, properties);
        return commitPk;
    }

    private long insertCommit(String author, LocalDateTime date, CommitId commitId) {
        InsertQuery query = polyJDBC.query().insert().into(getCommitTableNameWithSchema())
                .value(COMMIT_AUTHOR, author)
                .value(COMMIT_COMMIT_DATE, toTimestamp(date))
                .value(COMMIT_COMMIT_ID, commitId.valueAsNumber())
                .sequence(COMMIT_PK, getCommitPkSeqWithSchema());

        return polyJDBC.queryRunner().insert(query);
    }

    private void insertCommitProperties(long commitPk, Map<String, String> properties) {
        for (Map.Entry<String, String> property : properties.entrySet()) {
            InsertQuery query = polyJDBC.query().insert().into(getCommitPropertyTableNameWithSchema())
                .value(COMMIT_PROPERTY_COMMIT_FK, commitPk)
                .value(COMMIT_PROPERTY_NAME, property.getKey())
                .value(COMMIT_PROPERTY_VALUE, property.getValue());
            polyJDBC.queryRunner().insert(query);
        }
    }

    public boolean isPersisted(Commit commit) {
        SelectQuery selectQuery = polyJDBC.query()
                .select("count(*)")
                .from(getCommitTableNameWithSchema())
                .where(COMMIT_COMMIT_ID + " = :id")
                .withArgument("id", commit.getId().valueAsNumber());

        return PolyUtil.queryForOptionalLong(selectQuery, polyJDBC).get() > 0;
    }

    private Timestamp toTimestamp(LocalDateTime commitMetadata) {
        return new Timestamp(UtilTypeCoreAdapters.toUtilDate(commitMetadata));
    }

    public CommitId getCommitHeadId() {
        Optional<BigDecimal> maxCommitId = selectMaxCommitId();

        return maxCommitId.map(max -> CommitId.valueOf(maxCommitId.get()))
                          .orElse(null);
    }

    private Optional<BigDecimal> selectMaxCommitId() {
        SelectQuery query = polyJDBC.query()
                .select("MAX(" + COMMIT_COMMIT_ID + ")")
                .from(getCommitTableNameWithSchema());

        return queryForOptionalBigDecimal(query, polyJDBC);
    }

}
