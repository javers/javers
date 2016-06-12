package org.javers.repository.sql.finders;

import org.polyjdbc.core.query.SelectQuery;

public class AnySnapshotFilter extends SnapshotFilter {

    @Override
    String select() {
        return BASE_AND_GLOBAL_ID_FIELDS;
    }

    @Override
    void addFrom(SelectQuery query) {
        query.from(COMMIT_WITH_SNAPSHOT_GLOBAL_ID);
    }

    @Override
    void addWhere(SelectQuery query) {
        query.where("1=1");
    }

}
