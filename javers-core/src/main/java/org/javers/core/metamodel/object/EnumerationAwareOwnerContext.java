package org.javers.core.metamodel.object;

/**
 * @author bartosz.walacik
 */
public class EnumerationAwareOwnerContext implements OwnerContext {
    private final OwnerContext propertyOwner;
    private final boolean requiresObjectHasher;

    public EnumerationAwareOwnerContext(OwnerContext ownerContext) {
        this.propertyOwner = ownerContext;
        this.requiresObjectHasher = false;
    }

    public EnumerationAwareOwnerContext(OwnerContext propertyOwner, boolean requiresObjectHasher) {
        this.propertyOwner = propertyOwner;
        this.requiresObjectHasher = requiresObjectHasher;
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

    @Override
    public boolean requiresObjectHasher() {
        return requiresObjectHasher;
    }
}
