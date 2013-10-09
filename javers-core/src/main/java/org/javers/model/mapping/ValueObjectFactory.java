package org.javers.model.mapping;

import org.javers.model.mapping.type.TypeMapper;
import org.javers.model.mapping.util.managedClassPropertyScanner.Scanner;

/**
 * @author pawel szymczyk
 */
public abstract class ValueObjectFactory extends  ManagedClassFactory<ValueObject>{

    protected TypeMapper typeMapper;

    protected ValueObjectFactory(TypeMapper typeMapper, Scanner scanner) {
        super(typeMapper, scanner);
    }

    public abstract <T> ValueObject<T> create(Class<T> clazz);
}
