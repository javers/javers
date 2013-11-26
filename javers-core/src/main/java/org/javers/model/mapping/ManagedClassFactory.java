package org.javers.model.mapping;

/**
 * @author pawel szymczyk
 */
public abstract class ManagedClassFactory<T extends ManagedClass> {

    protected ManagedClassFactory() {
    }

    public abstract <S> T create(Class<S> clazz);
}
