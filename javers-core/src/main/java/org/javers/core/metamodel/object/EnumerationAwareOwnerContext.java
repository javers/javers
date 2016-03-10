package org.javers.core.metamodel.object;

import org.javers.common.collections.Optional;

/**
 * @author bartosz.walacik
 */
public class EnumerationAwareOwnerContext implements OwnerContext {
    private final Optional<EnumeratorContext> enumeratorContext;
    private final OwnerContext propertyOwner;

    public EnumerationAwareOwnerContext(EnumeratorContext enumeratorContext, OwnerContext ownerContext) {
        this.enumeratorContext = Optional.of(enumeratorContext);
        this.propertyOwner = ownerContext;
    }

    private EnumerationAwareOwnerContext(OwnerContext ownerContext) {
        this.enumeratorContext = Optional.empty();
        this.propertyOwner = ownerContext;
    }

    public static EnumerationAwareOwnerContext just(OwnerContext ownerContext){
        return new EnumerationAwareOwnerContext(ownerContext);
    }

    @Override
    public String getPath() {
        return propertyOwner.getPath()  + getEnumeratorContextPath();
    }

    @Override
    public GlobalId getOwnerId() {
        return propertyOwner.getOwnerId();
    }

    public <T extends EnumeratorContext> T getEnumeratorContext() {
        return (T)enumeratorContext.get();
    }

    private String getEnumeratorContextPath() {
        if (enumeratorContext.isPresent()) {
            return "/" + enumeratorContext.get().getPath();
        }
        return "";
    }
}
