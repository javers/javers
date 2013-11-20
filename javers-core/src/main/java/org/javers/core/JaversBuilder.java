package org.javers.core;

import org.javers.common.pico.JaversModule;
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

    private JaversConfiguration javersConfiguration;
    private List<Class> entityClasses = new ArrayList<>();
    private List<Class> valueObjectClasses = new ArrayList<>();
    private List<JaversModule> externalModules = new ArrayList<>();
    private PicoContainer container;

    private JaversBuilder() {
        javersConfiguration = new JaversConfiguration();
    }

    public static JaversBuilder javers() {
        return new JaversBuilder();
    }

    public Javers build() {
        if (isBuilt()) {
            throw new JaversException(JaversExceptionCode.JAVERS_ALREADY_BUILT);
        }

        bootPicoContainer();
        registerManagedClasses();
        bootEntityManager();

        return container.getComponent(Javers.class);
    }

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
        javersConfiguration.withMappingStyle(mappingStyle);
        return this;
    }

    public JaversBuilder addModule(JaversModule javersModule) {
        Validate.argumentIsNotNull(javersModule);
        externalModules.add(javersModule);
        return this;
    }

    /**
     * for testing only
     */
    protected PicoContainer getContainer() {
        return container;
    }

    //-- private

    private boolean isBuilt() {
        return container != null;
    }

    private void bootPicoContainer() {
        container = JaversContainerFactory.create(javersConfiguration, externalModules);
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
}
