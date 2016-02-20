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
        return BASE_AND_GLOBAL_ID_FIELDS;
    }

    @Override
    void addWhere(SelectQuery query) {
        query.where(pkFieldName + " = :pk"+
                    " AND g." + GLOBAL_ID_FRAGMENT + " = :fragment")
             .withArgument("pk", primaryKey)
             .withArgument("fragment", fragment);
    }

}
