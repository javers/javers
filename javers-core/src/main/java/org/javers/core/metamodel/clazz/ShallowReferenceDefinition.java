package org.javers.core.metamodel.clazz;

import org.javers.core.metamodel.type.EntityType;

/**
 * Recipe for {@link EntityType}.
 *
 * @author akrystian
 */
public class ShallowReferenceDefinition extends EntityDefinition {
    public ShallowReferenceDefinition(Class<?> clazz) {
        super(clazz);
    }
}
