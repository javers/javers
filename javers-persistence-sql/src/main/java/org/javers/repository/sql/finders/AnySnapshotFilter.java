package org.javers.repository.sql.finders;

import org.javers.core.json.JsonConverter;
import org.javers.repository.sql.schema.TableNameProvider;
import org.polyjdbc.core.query.SelectQuery;

class AnySnapshotFilter extends SnapshotFilter {

    AnySnapshotFilter(TableNameProvider tableNameProvider, JsonConverter converter) {
        super(tableNameProvider, converter);
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
