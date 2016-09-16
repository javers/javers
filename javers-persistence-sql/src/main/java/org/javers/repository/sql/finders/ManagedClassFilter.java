package org.javers.repository.sql.finders;

import org.javers.common.collections.Optional;
import org.polyjdbc.core.query.SelectQuery;

import static org.javers.repository.sql.schema.FixedSchemaFactory.*;

/**
 * @author bartosz.walacik
 */
class ManagedClassFilter extends SnapshotFilter {
    private final String managedType;
    private final boolean aggregate;

    ManagedClassFilter(String managedType, boolean aggregate) {
        this.managedType = managedType;
        this.aggregate = aggregate;
    }

    @Override
    void addWhere(SelectQuery query) {
        if (!aggregate) {
            query.where(SNAPSHOT_MANAGED_TYPE + " = :managedType");
        }
        else {
            query.where(
            "(    " + SNAPSHOT_MANAGED_TYPE + " = :managedType "+
            "  OR g.owner_id_fk in ( "+
            "     select g1." + GLOBAL_ID_PK + " from " + getSnapshotTableName() + " s1 "+
            "     INNER JOIN " + getGlobalIdTableName() + " g1 ON g1." + GLOBAL_ID_PK + "= s1."+ SNAPSHOT_GLOBAL_ID_FK +
            "     and  s1.managed_type = :managedType)"+
            ")");
        }
        query.withArgument("managedType", managedType);
    }
}

