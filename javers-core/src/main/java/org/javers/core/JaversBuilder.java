package org.javers.core;

import org.javers.common.validation.Validate;
import org.javers.core.exceptions.JaversException;
import org.javers.core.exceptions.JaversExceptionCode;
import org.javers.core.pico.JaversContainerFactory;
import org.javers.model.mapping.EntityManager;
import org.picocontainer.PicoContainer;

import java.util.ArrayList;
import java.util.List;

/**
 * Creates JaVers instance based on your domain model metadata and custom configuration.
 *
 * @author bartosz walacik
 */
public class JaversBuilder {

    private static final MappingStyle DEFAULT_MAPPING_STYLE = MappingStyle.BEAN;

    private MappingStyle mappingStyle;
    private List<Class> entityClasses;
    private List<Class> valueObjectClasses;
    private boolean built;
    private PicoContainer container;

    private JaversBuilder(){
       built = false;
       entityClasses = new ArrayList<>();
       valueObjectClasses = new ArrayList<>();
    }

    public static JaversBuilder javers() {
        return new JaversBuilder();
    }

    public Javers build() {
        if (built) {
            throw new JaversException(JaversExceptionCode.JAVERS_ALREADY_BUILT);
        }

        bootPicoContainer();
        registerManagedClasses();
        bootEntityManager();

        built = true;
        return container.getComponent(Javers.class);
    }
    private void bootPicoContainer() {
        container = JaversContainerFactory.create(usedMappingStyle());
    }

    private void registerManagedClasses() {
        EntityManager entityManager = container.getComponent(EntityManager.class);

        for (Class<?> clazz : entityClasses) {
            entityManager.registerEntity(clazz);
        }

        for (Class<?> clazz : valueObjectClasses) {
            entityManager.registerValueObject(clazz);
        }
    }

    private void bootEntityManager() {
        EntityManager entityManager = container.getComponent(EntityManager.class);
        entityManager.buildManagedClasses();
    }

    private MappingStyle usedMappingStyle() {
        if (mappingStyle == null) {
            return DEFAULT_MAPPING_STYLE;
        }
        return mappingStyle;
    }

    /*
    public JaversBuilder addManagedClasses(Class<?>... managedClasses) {
        for (Class<?> managedClass : managedClasses) {
            addManagedClass(managedClass);
        }
        return this;
    }*/

    public JaversBuilder registerEntity(Class<?> entityClass) {
        Validate.argumentIsNotNull(entityClass);
        entityClasses.add(entityClass);
        return this;
    }

    public JaversBuilder registerValueObject(Class<?> valueObjectClass) {
        Validate.argumentIsNotNull(valueObjectClass);
        valueObjectClasses.add(valueObjectClass);
        return this;
    }

    public JaversBuilder withMappingStyle(MappingStyle mappingStyle) {
        Validate.argumentIsNotNull(mappingStyle);

        this.mappingStyle = mappingStyle;
        return this;
    }
}
