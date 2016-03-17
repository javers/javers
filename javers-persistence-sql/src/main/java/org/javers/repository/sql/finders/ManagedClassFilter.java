package org.javers.repository.sql.finders;

import org.javers.common.collections.Optional;
import org.polyjdbc.core.query.SelectQuery;

import static org.javers.repository.sql.schema.FixedSchemaFactory.GLOBAL_ID_CLASS_QUALIFIED_NAME;
import static org.javers.repository.sql.schema.FixedSchemaFactory.SNAPSHOT_CHANGED;

/**
 * @author bartosz.walacik
 */
class ManagedClassFilter extends SnapshotFilter {
    final String typeName;
    final Optional<String> propertyName;

    ManagedClassFilter(String typeName, Optional<String> propertyName) {
        this.typeName = typeName;
        this.propertyName = propertyName;
    }

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
        query.where("g." + GLOBAL_ID_CLASS_QUALIFIED_NAME + " = :typeName ").withArgument("typeName", typeName);
        if (propertyName.isPresent()) {
            query.append(" AND " + SNAPSHOT_CHANGED + " like '%\"" + propertyName.get() + "\"%'");
        }
    }
}
