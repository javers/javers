package org.javers.model.mapping;

import org.javers.model.mapping.type.TypeMapper;
import org.javers.common.scanner.Scanner;

/**
 * @author pawel szymczyk
 */
public abstract class ManagedClassFactory<T extends ManagedClass> {

    protected Scanner scanner;
    protected TypeMapper typeMapper;

    protected ManagedClassFactory(TypeMapper typeMapper, Scanner scanner) {
        this.typeMapper = typeMapper;
        this.scanner = scanner;
    }

    public abstract <S> T create(Class<S> clazz);
}
