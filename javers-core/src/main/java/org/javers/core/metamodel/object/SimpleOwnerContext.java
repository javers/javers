package org.javers.core.metamodel.object;

/**
 *
 * @author bartosz walacik
 */
public class SimpleOwnerContext extends OwnerContext{

    public SimpleOwnerContext(GlobalCdoId owner, String propertyName) {
        super(owner, propertyName);
    }
}
