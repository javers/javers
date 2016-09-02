package org.javers.repository.sql.finders;

import org.javers.repository.sql.pico.TableNameManager;
import org.polyjdbc.core.query.SelectQuery;

public class AnySnapshotFilter extends SnapshotFilter {

    public AnySnapshotFilter(TableNameManager tableNameManager) {
        super(tableNameManager);
    }

    @Override
    String select() {
        return BASE_AND_GLOBAL_ID_FIELDS;
    }

    @Override
    void addFrom(SelectQuery query) {
        query.from(getFromCommitWithSnapshot());
    }

    @Override
    void addWhere(SelectQuery query) {
        query.where("1=1");
    }

}
