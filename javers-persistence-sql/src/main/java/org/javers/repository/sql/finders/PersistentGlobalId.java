package org.javers.repository.sql.finders;

import org.javers.common.collections.Optional;
import org.javers.common.validation.Validate;
import org.javers.core.metamodel.object.GlobalId;
import org.javers.core.metamodel.property.Property;

/**
* @author bartosz walacik
*/
class PersistentGlobalId {
    GlobalId instance;
    Optional<Integer> primaryKey;

    PersistentGlobalId(GlobalId instance, Optional<Integer> primaryKey) {
        Validate.argumentsAreNotNull(instance, primaryKey);
        this.instance = instance;
        this.primaryKey = primaryKey;
    }

    boolean found() {
        return primaryKey.isPresent();
    }

    Property getProperty(String name) {
        return instance.getCdoClass().getProperty(name);
    }
}
