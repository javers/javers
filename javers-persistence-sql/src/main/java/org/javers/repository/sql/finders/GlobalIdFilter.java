package org.javers.repository.sql.finders;

import org.javers.common.collections.Optional;
import org.javers.core.metamodel.object.GlobalId;
import org.javers.repository.sql.reposiotries.PersistentGlobalId;

import static org.javers.repository.sql.schema.FixedSchemaFactory.SNAPSHOT_GLOBAL_ID_FK;

/**
 * @author bartosz.walacik
 */
class GlobalIdFilter extends SnapshotFilter {
    final GlobalId globalId;

    GlobalIdFilter(PersistentGlobalId id, Optional<String> propertyName) {
        super(id.getPrimaryKey(), SNAPSHOT_GLOBAL_ID_FK, propertyName);
        this.globalId = id.getInstance();
    }
}
