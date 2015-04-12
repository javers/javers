package org.javers.repository.sql.reposiotries;

import org.javers.common.collections.Optional;
import org.javers.common.validation.Validate;
import org.javers.core.metamodel.clazz.ManagedClass;
import org.javers.core.metamodel.object.GlobalId;

/**
* @author bartosz walacik
*/
public class PersistentGlobalId extends GlobalId{
    private final GlobalId instance;
    private final Optional<Long> primaryKey;

    PersistentGlobalId(GlobalId instance, Optional<Long> primaryKey) {
        Validate.argumentsAreNotNull(instance, primaryKey);
        this.instance = instance;
        this.primaryKey = primaryKey;
    }

    public boolean persisted() {
        return primaryKey.isPresent();
    }

    public long getPrimaryKey() {
        return primaryKey.get();
    }

    @Override
    public ManagedClass getCdoClass() {
        return instance.getCdoClass();
    }

    @Override
    public Object getCdoId() {
        return instance.getCdoId();
    }

    @Override
    public String value() {
        return instance.value();
    }

    @Override
    public int hashCode() {
        return instance.hashCode();
    }

    public GlobalId getInstance() {
        return instance;
    }
}
