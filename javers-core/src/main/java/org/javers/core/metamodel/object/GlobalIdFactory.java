package org.javers.core.metamodel.object;

import org.javers.common.exception.exceptions.JaversException;
import org.javers.common.exception.exceptions.JaversExceptionCode;
import org.javers.common.validation.Validate;
import org.javers.core.metamodel.property.Entity;
import org.javers.core.metamodel.property.ManagedClass;
import org.javers.core.metamodel.property.ValueObject;

import static org.javers.core.metamodel.object.InstanceId.createFromInstance;

/**
 * @author bartosz walacik
 */
public class GlobalIdFactory {

    /**
     * @param owner for ValueObjects, optional
     */
    public static GlobalCdoId createId(Object targetCdo, ManagedClass targetManagedClass, OwnerContext owner) {
        Validate.argumentsAreNotNull(targetCdo,targetManagedClass);

        if (targetManagedClass instanceof Entity) {
            return createFromInstance(targetCdo, (Entity) targetManagedClass);
        }

        if (targetManagedClass instanceof ValueObject && hasNoOwner(owner)) {
            return new UnboundedValueObjectId((ValueObject)targetManagedClass);
        }

        if (targetManagedClass instanceof ValueObject && hasOwner(owner)) {
            return new ValueObjectId((ValueObject) targetManagedClass, owner);
        }

        throw new JaversException(JaversExceptionCode.NOT_IMPLEMENTED);
    }

    private static boolean hasOwner(OwnerContext context) {
        return (context != null && context.getGlobalCdoId() != null);
    }

    private static boolean hasNoOwner(OwnerContext context) {
        return (context == null || context.getGlobalCdoId() == null);
    }
}
