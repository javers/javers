package org.javers.repository.sql.finders;

import org.javers.common.collections.Optional;
import org.javers.core.metamodel.object.GlobalId;
import org.javers.repository.sql.reposiotries.PersistentGlobalId;

import static org.javers.repository.sql.schema.FixedSchemaFactory.SNAPSHOT_GLOBAL_ID_FK;

/**
 * Created by bartosz.walacik on 2015-04-12.
 */
class GlobalIdFilter extends PropertyNameFilter {
    final GlobalId globalId;

    GlobalIdFilter(PersistentGlobalId id, Optional<String> propertyName) {
        super(id.getPrimaryKey(), SNAPSHOT_GLOBAL_ID_FK, propertyName);
        this.globalId = id.getInstance();
    }
}
