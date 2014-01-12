package org.javers.model.mapping;

/**
 * @author bartosz walacik
 */
@Deprecated
public class ImmutableValueDefinition extends ManagedClassDefinition {
    public ImmutableValueDefinition(Class<?> clazz) {
        super(clazz);
    }
}
