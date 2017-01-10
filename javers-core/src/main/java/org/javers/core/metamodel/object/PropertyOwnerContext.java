package org.javers.core.metamodel.object;

/**
 * @author bartosz.walacik
 */
public class PropertyOwnerContext implements OwnerContext {

    private final GlobalId owner;
    private final String propertyName;

    public PropertyOwnerContext(GlobalId owner, String propertyName) {
        this.owner = owner;
        this.propertyName = propertyName;
    }

    @Override
    public GlobalId getOwnerId() {
        return owner;
    }

    @Override
    public String getPath() {
        return propertyName;
    }

    public String getPropertyName() {
        return propertyName;
    }

    @Override
    public boolean requiresObjectHasher() {
        return false;
    }
}
