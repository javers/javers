package org.javers.core.metamodel.clazz;

import org.javers.core.metamodel.type.CustomType;

/**
 *  Recipe for {@link CustomType}
 *
 * @author bartosz walacik
 */
public class CustomDefinition extends ClientsClassDefinition {

    public CustomDefinition(Class<?> clazz) {
        super(clazz);
    }
}
