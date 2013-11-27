package org.javers.core;

import org.javers.common.pico.JaversModule;
import org.javers.common.validation.Validate;
import org.javers.core.pico.CoreJaversModule;
import org.javers.model.mapping.EntityManager;
import org.javers.model.pico.ModelJaversModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Creates JaVers instance based on your domain model metadata and custom configuration.
 * <br/>
 * Supports two configuring methods:
 * <ul>
 *     <li/>by properties file, see {TBA ...}
 *     <li/>programmatically using builder style methods
 * </ul>
 *
 * @author bartosz walacik
 */
public class JaversBuilder extends AbstractJaversBuilder {
    private static final Logger logger = LoggerFactory.getLogger(JaversBuilder.class);
    //
    private JaversCoreConfiguration coreConfiguration;
    private List<Class> entityClasses = new ArrayList<>();
    private List<Class> valueObjectClasses = new ArrayList<>();
    private List<JaversModule> externalModules = new ArrayList<>();

    private JaversBuilder() {
        coreConfiguration = new JaversCoreConfiguration();
    }

    public static JaversBuilder javers() {
        return new JaversBuilder();
    }

    public Javers build() {
        logger.info("starting up javers ...");

        bootContainer(getCoreModules(coreConfiguration), null);
        registerManagedClasses();
        bootEntityManager();

        logger.info("javers instance is up & ready");
        return getContainerComponent(Javers.class);
    }

    public JaversBuilder registerEntity(Class<?>...entityClasses) {
        for(Class clazz : entityClasses) {
            registerEntity(clazz);
        }
        return this;
    }

    private JaversBuilder registerEntity(Class<?> entityClass) {
        Validate.argumentIsNotNull(entityClass);
        entityClasses.add(entityClass);
        return this;
    }

    public JaversBuilder registerValueObject(Class<?>...valueObjectClasses) {
        for(Class clazz : valueObjectClasses) {
            registerValueObject(clazz);
        }
        return this;
    }

    public JaversBuilder registerValueObject(Class<?> valueObjectClass) {
        Validate.argumentIsNotNull(valueObjectClass);
        valueObjectClasses.add(valueObjectClass);
        return this;
    }

    public JaversBuilder withMappingStyle(MappingStyle mappingStyle) {
        coreConfiguration.withMappingStyle(mappingStyle);
        return this;
    }

    @Deprecated
    public JaversBuilder addModule(JaversModule javersModule) {
        Validate.argumentIsNotNull(javersModule);
        externalModules.add(javersModule);
        return this;
    }

    private List<JaversModule> getCoreModules(JaversCoreConfiguration configuration) {
        return Arrays.asList(new CoreJaversModule(), new ModelJaversModule(configuration));
    }

    private void registerManagedClasses() {
        EntityManager entityManager = getContainerComponent(EntityManager.class);

        for (Class<?> clazz : entityClasses) {
            entityManager.registerEntity(clazz);
        }

        for (Class<?> clazz : valueObjectClasses) {
            entityManager.registerValueObject(clazz);
        }
    }

    private void bootEntityManager() {
        EntityManager entityManager = getContainerComponent(EntityManager.class);
        entityManager.buildManagedClasses();
    }
}
