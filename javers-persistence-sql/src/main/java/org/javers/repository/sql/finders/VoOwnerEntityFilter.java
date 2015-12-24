package org.javers.repository.sql.finders;

import org.polyjdbc.core.query.SelectQuery;

import static org.javers.repository.sql.schema.FixedSchemaFactory.*;

/**
 * @author bartosz.walacik
 */
public class VoOwnerEntityFilter extends ManagedClassFilter {
    final String fragment;
    VoOwnerEntityFilter(long ownerEntityClassPk, String fragment) {
        super(ownerEntityClassPk, "o." + GLOBAL_ID_CLASS_FK);
        this.fragment = fragment;
    }

    @Override
    String select() {
        return BASE_FIELDS + ", " +
                "g." + GLOBAL_ID_LOCAL_ID + ", " +
                "g." + GLOBAL_ID_FRAGMENT + ", " +
                "g." + GLOBAL_ID_OWNER_ID_FK + ", " +
                "g_c." + CDO_CLASS_QUALIFIED_NAME + ", " +
                "o." + GLOBAL_ID_LOCAL_ID + " owner_" + GLOBAL_ID_LOCAL_ID + ", " +
                "o." + GLOBAL_ID_FRAGMENT + " owner_" + GLOBAL_ID_FRAGMENT + ", " +
                "o_c." + CDO_CLASS_QUALIFIED_NAME + " owner_" + CDO_CLASS_QUALIFIED_NAME;
    }

    void addWhere(SelectQuery query) {
        query.where(pkFieldName + " = :pk"+
                    " AND g." + GLOBAL_ID_FRAGMENT + " = :fragment")
             .withArgument("pk", primaryKey)
             .withArgument("fragment", fragment);
    }
}
