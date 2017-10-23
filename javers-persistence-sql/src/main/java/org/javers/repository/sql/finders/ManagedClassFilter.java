package org.javers.repository.sql.finders;

import org.javers.common.collections.Sets;
import org.javers.common.string.ToStringBuilder;
import org.javers.repository.sql.schema.TableNameProvider;
import org.polyjdbc.core.query.SelectQuery;

import java.util.ArrayList;
import java.util.Set;

import static org.javers.repository.sql.schema.FixedSchemaFactory.*;

/**
 * @author bartosz.walacik
 */
class ManagedClassFilter extends SnapshotFilter {
    private final Set<String> managedTypes;
    private final boolean aggregate;

    ManagedClassFilter(TableNameProvider tableNameProvider, Set<String> managedTypes, boolean aggregate) {
        super(tableNameProvider);
        this.managedTypes = managedTypes;
        this.aggregate = aggregate;
    }

    @Override
    void addWhere(SelectQuery query) {
        String condition = getCondition();

        if (!aggregate) {
            query.where(condition);
        }
        else {
            query.where(
            "(    " + condition +
            "  OR g.owner_id_fk in ( "+
            "     select g1." + GLOBAL_ID_PK + " from " + getSnapshotTableNameWithSchema() + " s1 "+
            "     INNER JOIN " + getGlobalIdTableNameWithSchema() + " g1 ON g1." + GLOBAL_ID_PK + "= s1."+ SNAPSHOT_GLOBAL_ID_FK +
            "     and  s1." + condition + ")"+
            ")");
        }
    }

    private String getCondition() {
        return SNAPSHOT_MANAGED_TYPE + " in (" + ToStringBuilder.join(managedTypes) + ")";
    }
}
