package org.javers.model.mapping;

import org.javers.model.mapping.type.TypeMapper;
import org.javers.common.scanner.Scanner;

/**
 * @author bartosz walacik
 */
public abstract class EntityFactory extends ManagedClassFactory<Entity>{

    protected EntityFactory(TypeMapper typeMapper, Scanner scanner) {
        super(typeMapper, scanner);
    }

    public <S> Entity<S> createEntity(Class<S> entityClass){
        return create(entityClass);
    }
}
