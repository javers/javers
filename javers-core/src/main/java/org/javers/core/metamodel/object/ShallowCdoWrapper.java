package org.javers.core.metamodel.object;

import org.javers.common.validation.Validate;
import org.javers.core.metamodel.property.Property;
import org.javers.core.metamodel.type.EntityType;
import org.javers.core.metamodel.type.ManagedType;

import static org.javers.common.validation.Validate.argumentIsNotNull;

/**
 * @author bartosz.walacik
 */
public class ShallowCdoWrapper extends CdoWrapper {
    private final EntityType entityType;

    public ShallowCdoWrapper(Object wrappedCdo, GlobalId globalId, ManagedType managedType) {
        super(wrappedCdo, globalId, managedType);
        Validate.argumentCheck(managedType instanceof EntityType, "expected EntityType, got "+managedType.getClass().getSimpleName());
        entityType = (EntityType) managedType;
    }

    @Override
    public Object getPropertyValue(Property property) {
        argumentIsNotNull(property);

        if (entityType.getIdProperty().equals(property)){
            return super.getPropertyValue(property);
        }
        return null;
    }

    @Override
    public boolean isNull(Property property) {
        argumentIsNotNull(property);

        if (entityType.getIdProperty().equals(property)){
            return super.isNull(property);
        }
        return true;
    }
}
