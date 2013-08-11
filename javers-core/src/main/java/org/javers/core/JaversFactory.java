package org.javers.core;

import org.javers.model.mapping.BeanBasedEntityFactory;
import org.javers.model.mapping.EntityFactory;
import org.javers.model.mapping.EntityManager;
import org.javers.model.mapping.FieldBasedEntityFactory;
import org.javers.model.mapping.type.TypeMapper;

/**
 * Creates JaVers instance based on your domain model metadata and custom configuration.
 *
 * @author bartosz walacik
 */
public class JaversFactory {

    private Javers javers;

    public static JaversFactory beanStyleFactory() {
        TypeMapper mapper = new TypeMapper();
        return new JaversFactory( new BeanBasedEntityFactory(mapper));
    }

    public static JaversFactory fieldStyleFactory() {
        TypeMapper mapper = new TypeMapper();
        return new JaversFactory( new FieldBasedEntityFactory(mapper));
    }

    private JaversFactory(EntityFactory entityFactory) {
        EntityManager em = new EntityManager(entityFactory);
        javers = new Javers(em);
    }

    public Javers build() {
        return javers;
    }

    public JaversFactory manageClasses(Class<?>... managedClasses) {
        for (Class<?> managedClass : managedClasses) {
            javers.manage(managedClass);
        }
        return this;
    }
}
