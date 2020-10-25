package org.javers.repository.sql.finders;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.javers.repository.sql.schema.DBNameProvider;
import org.javers.repository.sql.session.Session;

import com.google.common.base.Joiner;

public class CommitPropertyFinder {

    private final DBNameProvider dbNameProvider;

    public CommitPropertyFinder(DBNameProvider dbNameProvider) {
        this.dbNameProvider = dbNameProvider;
    }

    List<CommitPropertyDTO> findCommitPropertiesOfSnaphots(Collection<Long> commitPKs, Session session) {
        if (commitPKs.isEmpty()) {
            return Collections.emptyList();
        }

        return session.select(dbNameProvider.getCommitPropertyCommitFKColumnName() + ", " +  dbNameProvider.getCommitPropertyNameColumnName() + ", " + dbNameProvider.getCommitPropertyValueColumnName())
               .from(dbNameProvider.getCommitPropertyTableNameWithSchema())
               .queryName("commit properties")
               .and(dbNameProvider.getCommitPropertyCommitFKColumnName() + " in (" + Joiner.on(",").join(commitPKs) + ")")
               .executeQuery(resultSet -> new CommitPropertyDTO(
                       resultSet.getLong(dbNameProvider.getCommitPropertyCommitFKColumnName()),
                       resultSet.getString(dbNameProvider.getCommitPropertyNameColumnName()),
                       resultSet.getString(dbNameProvider.getCommitPropertyValueColumnName())));
    }
}
