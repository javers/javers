package org.javers.repository.sql.reposiotries;

import org.javers.common.collections.Optional;
import org.javers.common.validation.Validate;
import org.javers.core.metamodel.object.GlobalId;
import org.javers.core.metamodel.property.Property;

/**
* @author bartosz walacik
*/
public class PersistentGlobalId {
    private GlobalId instance;
    private Optional<Long> primaryKey;

    PersistentGlobalId(GlobalId instance, Optional<Long> primaryKey) {
        Validate.argumentsAreNotNull(instance, primaryKey);
        this.instance = instance;
        this.primaryKey = primaryKey;
    }

    public boolean found() {
        return primaryKey.isPresent();
    }

    public Property getProperty(String name) {
        return instance.getCdoClass().getProperty(name);
    }

    public GlobalId getInstance() {
        return instance;
    }

    public long getPrimaryKey() {
        return primaryKey.get();
    }
}
