package org.javers.repository.sql.repositories;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import org.javers.core.commit.CommitId;
import org.javers.core.json.typeadapter.util.UtilTypeCoreAdapters;
import org.javers.repository.sql.schema.ColumnNameProvider;
import org.javers.repository.sql.schema.SchemaNameAware;
import org.javers.repository.sql.schema.TableNameProvider;
import org.javers.repository.sql.session.Session;
import org.polyjdbc.core.type.Timestamp;

/**
 * @author pawel szymczyk
 */
public class CommitMetadataRepository extends SchemaNameAware {

    public CommitMetadataRepository(TableNameProvider tableNameProvider, ColumnNameProvider columnNameProvider) {
        super(tableNameProvider, columnNameProvider);
    }

    public long save(String author, Map<String, String> properties, LocalDateTime date, Instant dateInstant, CommitId commitId, Session session) {
        long commitPk = session.insert("Commit")
                .into(getCommitTableNameWithSchema())
                .value(getCommitAuthorName(), author)
                .value(getCommitDateName(), date)
                .value(getCommitInstantName(), UtilTypeCoreAdapters.serialize(dateInstant))
                .value(getCommitIdName(), commitId.valueAsNumber())
                .sequence(getCommitPKName(), getCommitPkSeqName().nameWithSchema())
                .executeAndGetSequence();

        insertCommitProperties(commitPk, properties, session);
        return commitPk;
    }

    private void insertCommitProperties(long commitPk, Map<String, String> properties, Session session) {
        for (Map.Entry<String, String> property : properties.entrySet()) {
            session.insert("CommitProperty")
                   .into(getCommitPropertyTableNameWithSchema())
                   .value(getCommitPropertyCommitFKName(), commitPk)
                   .value(getCommitPropertyName(), property.getKey())
                   .value(getCommitPropertyValueName(), property.getValue())
                   .execute();
        }
    }

    boolean isCommitPersisted(CommitId commitId, Session session) {
        long count = session.select("count(*)")
               .from(getCommitTableNameWithSchema())
               .and(getCommitIdName(), commitId.valueAsNumber())
               .queryForLong("isCommitPersisted");

        return count > 0;
    }

    private Timestamp toTimestamp(LocalDateTime commitMetadata) {
        return new Timestamp(UtilTypeCoreAdapters.toUtilDate(commitMetadata));
    }

    public CommitId getCommitHeadId(Session session) {
        Optional<BigDecimal> maxCommitId = selectMaxCommitId(session);

        return maxCommitId.map(max -> CommitId.valueOf(maxCommitId.get()))
                .orElse(null);
    }

    private Optional<BigDecimal> selectMaxCommitId(Session session) {
        return session.select("MAX(" + getCommitIdName() + ")")
                .from(getCommitTableNameWithSchema())
                .queryForOptionalBigDecimal("max CommitId");
    }
}
