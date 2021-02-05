package org.javers.repository.sql.finders;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.javers.repository.sql.schema.ColumnNameProvider;
import org.javers.repository.sql.schema.TableNameProvider;
import org.javers.repository.sql.session.Session;

import com.google.common.base.Joiner;

public class CommitPropertyFinder {

    private final TableNameProvider  tableNameProvider;
    private final ColumnNameProvider columnNameProvider;

    public CommitPropertyFinder(TableNameProvider tableNameProvider, ColumnNameProvider columnNameProvider) {
        this.tableNameProvider = tableNameProvider;
        this.columnNameProvider = columnNameProvider;
    }

    List<CommitPropertyDTO> findCommitPropertiesOfSnaphots(Collection<Long> commitPKs, Session session) {
        if (commitPKs.isEmpty()) {
            return Collections.emptyList();
        }

        return session.select(columnNameProvider.getCommitPropertyCommitFKName()+ ", " + columnNameProvider.getCommitPropertyName() + ", " + columnNameProvider.getCommitPropertyValueName())
               .from(tableNameProvider.getCommitPropertyTableNameWithSchema())
               .queryName("commit properties")
               .and(columnNameProvider.getCommitPropertyCommitFKName() + " in (" + Joiner.on(",").join(commitPKs) + ")")
               .executeQuery(resultSet -> new CommitPropertyDTO(
                       resultSet.getLong(columnNameProvider.getCommitPropertyCommitFKName()),
                       resultSet.getString(columnNameProvider.getCommitPropertyName()),
                       resultSet.getString(columnNameProvider.getCommitPropertyValueName())));
    }
}
