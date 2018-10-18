package org.javers.repository.sql.finders;

import org.javers.repository.sql.schema.TableNameProvider;
import org.polyjdbc.core.query.SelectQuery;

class AnySnapshotFilter extends SnapshotFilter {

    AnySnapshotFilter(TableNameProvider tableNameProvider) {
        super(tableNameProvider);
    }

    @Override
    void addWhere(SelectQuery query) {
        query.where("1=1");
    }

}
