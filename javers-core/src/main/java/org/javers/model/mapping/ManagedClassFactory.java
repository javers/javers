package org.javers.model.mapping;

import org.javers.model.mapping.type.TypeMapper;

/**
 * @author pawel szymczyk
 */
public abstract class ManagedClassFactory<T extends ManagedClass> {

    protected PropertyScanner propertyScanner;
    protected TypeMapper typeMapper;

    protected ManagedClassFactory(TypeMapper typeMapper, PropertyScanner propertyScanner) {
        this.typeMapper = typeMapper;
        this.propertyScanner = propertyScanner;
    }

    public abstract <S> T create(Class<S> clazz);
}
