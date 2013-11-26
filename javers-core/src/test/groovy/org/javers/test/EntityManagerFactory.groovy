package org.javers.test

import org.javers.model.mapping.BeanBasedPropertyScanner
import org.javers.model.mapping.EntityFactory
import org.javers.model.mapping.EntityManager
import org.javers.model.mapping.ValueObjectFactory
import org.javers.model.mapping.type.TypeMapper

import static org.javers.test.builder.TypeMapperTestBuilder.typeMapper

class EntityManagerFactory {

    static EntityManager createWithEntities(Class<?>... entityClasses) {
        TypeMapper mapper = typeMapper().registerAllDummyTypes().build();
        BeanBasedPropertyScanner scanner = new BeanBasedPropertyScanner(mapper);
        EntityFactory entityFactory = new EntityFactory(scanner);
        ValueObjectFactory valueObjectFactory = new ValueObjectFactory();
        EntityManager entityManager = new EntityManager(entityFactory, valueObjectFactory, mapper);
        for (Class<?> entity : entityClasses) {
            entityManager.registerEntity(entity);
        }
        entityManager.buildManagedClasses();
        entityManager
    }
}
