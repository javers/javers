package org.javers.core.metamodel.object;

/**
 * @author bartosz.walacik
 */
public class EnumerationAwareOwnerContext implements OwnerContext {
    private final OwnerContext propertyOwner;

    public EnumerationAwareOwnerContext(OwnerContext ownerContext) {
        this.propertyOwner = ownerContext;
    }

    @Override
    public String getPath() {
        String enumeratorContextPath = getEnumeratorContextPath();
        if (enumeratorContextPath.isEmpty()) {
            return propertyOwner.getPath();
        }
        return propertyOwner.getPath() + "/" + enumeratorContextPath;
    }

    @Override
    public GlobalId getOwnerId() {
        return propertyOwner.getOwnerId();
    }

    protected String getEnumeratorContextPath() {
        return "";
    }
}
