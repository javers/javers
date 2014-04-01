package org.javers.core.metamodel.object;

import org.javers.core.metamodel.property.ValueObject;

/**
 * Identifies Set of ValueObjects, it's a kind of multi-identifier.
 *
 * @author bartosz walacik
 */
public class ValueObjectSetId extends ValueObjectId{

    public ValueObjectSetId(ValueObject valueObject, OwnerContext ownerContext) {
        super(valueObject, ownerContext.getGlobalCdoId(), ownerContext.getPath());
    }

    public ValueObjectSetId(ValueObject valueObject, GlobalCdoId ownerId, String fragment) {
        super(valueObject, ownerId, fragment);
    }

    @Override
    public String toString() {
        return super.toString() + "/Set";
    }
}
