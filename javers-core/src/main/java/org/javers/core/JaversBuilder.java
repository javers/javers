package org.javers.core;

import org.javers.common.pico.JaversModule;
import org.javers.common.validation.Validate;
import org.javers.core.pico.CoreJaversModule;
import org.javers.model.mapping.EntityDefinition;
import org.javers.model.mapping.EntityManager;
import org.javers.model.pico.ModelJaversModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

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
    private Set<EntityDefinition> entityDefinitions = new HashSet<>();
    private Set<Class> valueObjectClasses = new HashSet<>();
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

    /**
     * gives you Entity with id-property selected on the basis of @Id annotation
     */
    public JaversBuilder registerEntity(Class<?> entityClass) {
        Validate.argumentIsNotNull(entityClass);
        entityDefinitions.add(new EntityDefinition(entityClass));
        return this;
    }

    /**
     * gives you Entity with id-property selected explicitly by name
     */
    public JaversBuilder registerEntity(Class<?> entityClass, String idPropertyName) {
        Validate.argumentsAreNotNull(entityClass, idPropertyName);
        entityDefinitions.add( new EntityDefinition(entityClass, idPropertyName) );
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

        for (EntityDefinition def : entityDefinitions) {
            entityManager.registerEntity(def);
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
