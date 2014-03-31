package org.javers.core.metamodel.object;

/**
 * @author bartosz walacik
 */
public class SetOwnerContext extends OwnerContext {

    SetOwnerContext(GlobalCdoId owner, String propertyName) {
        super(owner, propertyName);
    }

    public SetOwnerContext(OwnerContext owner) {
        super(owner.getGlobalCdoId(), owner.getPropertyName());
    }

}
