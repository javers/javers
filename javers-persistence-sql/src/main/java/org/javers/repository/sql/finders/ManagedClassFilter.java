package org.javers.repository.sql.finders;

import org.javers.common.collections.Optional;
import org.polyjdbc.core.query.SelectQuery;

import static org.javers.repository.sql.schema.FixedSchemaFactory.*;

/**
 * @author bartosz.walacik
 */
class ManagedClassFilter extends SnapshotFilter {
    ManagedClassFilter(long classPk, Optional<String> propertyName) {
        super(classPk, "g." + GLOBAL_ID_CLASS_FK, propertyName);
    }

    ManagedClassFilter(long classPk, String pkFieldName) {
        super(classPk, pkFieldName, Optional.<String>empty());
    }

    @Override
    String select() {
        return BASE_FIELDS + ", " +
                "g." + GLOBAL_ID_LOCAL_ID + ", " +
                "g." + GLOBAL_ID_FRAGMENT + ", " +
                "g." + GLOBAL_ID_OWNER_ID_FK + ", " +
                "g_c." + CDO_CLASS_QUALIFIED_NAME + ", " +
                "o." + GLOBAL_ID_LOCAL_ID + " as owner_" + GLOBAL_ID_LOCAL_ID + ", " +
                "o." + GLOBAL_ID_FRAGMENT + " as owner_" + GLOBAL_ID_FRAGMENT + ", " +
                "o_c." + CDO_CLASS_QUALIFIED_NAME + " as owner_" + CDO_CLASS_QUALIFIED_NAME;
    }

    @Override
    void addFrom(SelectQuery query) {
        final String JOIN_GLOBAL_ID_TO_SNAPSHOT
                = " INNER JOIN " + GLOBAL_ID_TABLE_NAME + " as g ON g." + GLOBAL_ID_PK + " = " + SNAPSHOT_GLOBAL_ID_FK +
                " INNER JOIN " + CDO_CLASS_TABLE_NAME + " as g_c ON g_c." + CDO_CLASS_PK + " = g." + GLOBAL_ID_CLASS_FK +
                " LEFT OUTER JOIN " + GLOBAL_ID_TABLE_NAME + " as o ON o." + GLOBAL_ID_PK + " = g." + GLOBAL_ID_OWNER_ID_FK +
                " LEFT OUTER JOIN " + CDO_CLASS_TABLE_NAME + " as o_c ON o_c." + CDO_CLASS_PK + " = o." + GLOBAL_ID_CLASS_FK;

        query.from(COMMIT_WITH_SNAPSHOT + JOIN_GLOBAL_ID_TO_SNAPSHOT);
    }
}
