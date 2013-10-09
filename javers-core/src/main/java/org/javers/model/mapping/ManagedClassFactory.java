package org.javers.model.mapping;

import org.javers.model.mapping.type.TypeMapper;

/**
 * @author pawel szymczyk
 */
public abstract class ManagedClassFactory<T extends ManagedClass> {

    protected ManagedClassPropertyScanner scanner;
    protected TypeMapper typeMapper;

    protected ManagedClassFactory(TypeMapper typeMapper, ManagedClassPropertyScanner scanner) {
        this.typeMapper = typeMapper;
        this.scanner = scanner;
    }

    public abstract <S> T create(Class<S> clazz);
}
