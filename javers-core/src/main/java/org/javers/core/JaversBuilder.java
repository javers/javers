package org.javers.core;

import org.javers.common.pico.JaversModule;
import org.javers.common.validation.Validate;
import org.javers.core.configuration.JaversCoreConfiguration;
import org.javers.core.json.JsonConverterBuilder;
import org.javers.core.pico.CoreJaversModule;
import org.javers.model.mapping.EntityDefinition;
import org.javers.model.mapping.EntityManager;
import org.javers.model.mapping.ManagedClassDefinition;
import org.javers.model.mapping.ValueObjectDefinition;
import org.javers.core.pico.ModelJaversModule;
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
    private JaversCoreConfiguration coreConfiguration = new JaversCoreConfiguration();
    private Set<ManagedClassDefinition> managedClassDefinitions = new HashSet<>();
    private List<JaversModule> externalModules = new ArrayList<>();
    private JsonConverterBuilder jsonConverterBuilder = JsonConverterBuilder.jsonConverter();

    private JaversBuilder() {
    }

    public static JaversBuilder javers() {
        return new JaversBuilder();
    }

    public Javers build() {
        logger.info("starting up javers ...");

        bootContainer(getCoreModules(coreConfiguration), jsonConverterBuilder.build());
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
        managedClassDefinitions.add(new EntityDefinition(entityClass));
        return this;
    }

    /**
     * gives you Entity with id-property selected explicitly by name
     */
    public JaversBuilder registerEntity(Class<?> entityClass, String idPropertyName) {
        Validate.argumentsAreNotNull(entityClass, idPropertyName);
        managedClassDefinitions.add( new EntityDefinition(entityClass, idPropertyName) );
        return this;
    }

    public JaversBuilder registerValueObject(Class<?> valueObjectClass) {
        Validate.argumentIsNotNull(valueObjectClass);
        managedClassDefinitions.add(new ValueObjectDefinition(valueObjectClass));
        return this;
    }

    public JaversBuilder registerEntities(Class<?>...entityClasses) {
        for(Class clazz : entityClasses) {
            registerEntity(clazz);
        }
        return this;
    }

    public JaversBuilder registerValueObjects(Class<?>...valueObjectClasses) {
        for(Class clazz : valueObjectClasses) {
            registerValueObject(clazz);
        }
        return this;
    }

    /**
     * {@link MappingStyle#FIELD} by default
     */
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

        for (ManagedClassDefinition def : managedClassDefinitions) {
            entityManager.register(def);
        }
    }

    private void bootEntityManager() {
        EntityManager entityManager = getContainerComponent(EntityManager.class);
        entityManager.buildManagedClasses();
    }
}
