package org.javers.repository.sql.finders;

import org.javers.common.collections.Lists;
import org.javers.common.collections.Optional;
import org.javers.common.string.ToStringBuilder;
import org.polyjdbc.core.query.SelectQuery;

import java.util.List;

import static org.javers.repository.sql.schema.FixedSchemaFactory.SNAPSHOT_CHANGED;

class PrimaryKeySnapshotFilter extends SnapshotFilter  {
    private final List<Long> primaryKeys;
    private final String pkFieldName;
    private final Optional<String> propertyName;

    public PrimaryKeySnapshotFilter(long primaryKey, String pkFieldName, Optional<String> propertyName) {
        this(Lists.asList(primaryKey), pkFieldName, propertyName);
    }

    public PrimaryKeySnapshotFilter(List<Long> primaryKeys, String pkFieldName, Optional<String> propertyName) {
        this.primaryKeys = primaryKeys;
        this.pkFieldName = pkFieldName;
        this.propertyName = propertyName;
    }

    @Override
    void addWhere(SelectQuery query) {
        query.where(primaryKeysClause() +
                    propertyNameClause());
    }

    private String primaryKeysClause(){
        if (primaryKeys.size() == 1) {
            return pkFieldName + " = "+primaryKeys.get(0);
        }
        else {
            return pkFieldName + " in (" + ToStringBuilder.join(primaryKeys) + ")";
        }
    }

    private String propertyNameClause(){
        if (propertyName.isPresent()) {
            return " AND " + SNAPSHOT_CHANGED + " like '%\"" + propertyName.get() + "\"%'";
        }
        return "";
    }
}
