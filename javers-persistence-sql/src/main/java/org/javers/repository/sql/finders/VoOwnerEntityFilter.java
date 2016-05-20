package org.javers.repository.sql.finders;

import org.polyjdbc.core.query.SelectQuery;

import static org.javers.repository.sql.schema.FixedSchemaFactory.GLOBAL_ID_TYPE_NAME;
import static org.javers.repository.sql.schema.FixedSchemaFactory.GLOBAL_ID_FRAGMENT;

/**
 * @author bartosz.walacik
 */
public class VoOwnerEntityFilter extends SnapshotFilter {
    final String ownerTypeName;
    final String fragment;

    VoOwnerEntityFilter(String ownerTypeName, String fragment) {
        this.ownerTypeName = ownerTypeName;
        this.fragment = fragment;
    }

    @Override
    void addWhere(SelectQuery query) {
        query.where("o." + GLOBAL_ID_TYPE_NAME + " = :ownerTypeName").withArgument("ownerTypeName", ownerTypeName)
             .append(" AND g." + GLOBAL_ID_FRAGMENT + " = :fragment").withArgument("fragment", fragment);
    }

}
