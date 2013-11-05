package org.javers.model.mapping;

/**
 * @author pawel szymczyk
 */
public abstract class ManagedClassFactory<T extends ManagedClass> {

    protected PropertyScanner propertyScanner;

    protected ManagedClassFactory(PropertyScanner propertyScanner) {
        this.propertyScanner = propertyScanner;
    }

    public abstract <S> T create(Class<S> clazz);
}
