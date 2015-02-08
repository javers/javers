package org.javers.core.metamodel.clazz;

import java.util.List;

/**
 * @author bartosz walacik
 */
public class ValueObjectDefinition extends ClientsClassDefinition {

    public ValueObjectDefinition(Class<?> clazz) {
        super(clazz);
    }

    public ValueObjectDefinition(Class<?> clazz, List<String> ignoredProperties) {
        super(clazz, ignoredProperties);
    }


}
