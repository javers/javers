package org.javers.repository.sql.finders;

import com.google.common.base.Joiner;
import org.javers.repository.sql.schema.TableNameProvider;
import org.javers.repository.sql.session.Session;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.javers.repository.sql.schema.FixedSchemaFactory.*;

public class CommitPropertyFinder {

    private final TableNameProvider tableNameProvider;

    public CommitPropertyFinder(TableNameProvider tableNameProvider) {
        this.tableNameProvider = tableNameProvider;
    }

    List<CommitPropertyDTO> findCommitPropertiesOfSnaphots(Collection<Long> commitPKs, Session session) {
        if (commitPKs.isEmpty()) {
            return Collections.emptyList();
        }

        return session.select(COMMIT_PROPERTY_COMMIT_FK + ", " + COMMIT_PROPERTY_NAME + ", " + COMMIT_PROPERTY_VALUE)
               .from(tableNameProvider.getCommitPropertyTableNameWithSchema())
               .queryName("commit properties")
               .and(COMMIT_PROPERTY_COMMIT_FK + " in (" + Joiner.on(",").join(commitPKs) + ")")
               .executeQuery(resultSet -> new CommitPropertyDTO(
                       resultSet.getLong(COMMIT_PROPERTY_COMMIT_FK),
                       resultSet.getString(COMMIT_PROPERTY_NAME),
                       resultSet.getString(COMMIT_PROPERTY_VALUE)));
    }
}
