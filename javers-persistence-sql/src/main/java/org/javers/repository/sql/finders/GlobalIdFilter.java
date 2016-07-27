package org.javers.repository.sql.finders;

import org.polyjdbc.core.query.SelectQuery;

import static org.javers.repository.sql.schema.FixedSchemaFactory.GLOBAL_ID_OWNER_ID_FK;
import static org.javers.repository.sql.schema.FixedSchemaFactory.GLOBAL_ID_PK;

class GlobalIdFilter extends SnapshotFilter  {
    private final long globalIdPk;
    private final boolean aggregate;

    public GlobalIdFilter(long globalIdPk, boolean aggregate) {
        this.globalIdPk = globalIdPk;
        this.aggregate = aggregate;
    }

    @Override
    void addWhere(SelectQuery query) {
        if (!aggregate) {
            query.where("g." + GLOBAL_ID_PK + " = " + globalIdPk);
        }
        else {
            query.where( "(    g." + GLOBAL_ID_PK + " = " + globalIdPk +
                         "  OR g." + GLOBAL_ID_OWNER_ID_FK + " = " + globalIdPk + ")");
        }
    }
}
