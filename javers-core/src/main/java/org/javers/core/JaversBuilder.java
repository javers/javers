package org.javers.core;

import com.google.gson.TypeAdapter;
import org.javers.common.pico.JaversModule;
import org.javers.common.validation.Validate;
import org.javers.core.configuration.JaversCoreConfiguration;
import org.javers.core.json.JsonConverterBuilder;
import org.javers.core.json.JsonTypeAdapter;
import org.javers.core.pico.CoreJaversModule;
import org.javers.core.pico.ModelJaversModule;
import org.javers.model.mapping.*;
import org.javers.model.mapping.type.TypeMapper;
import org.javers.model.mapping.type.ValueType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    private Set<ManagedClassDefinition> managedClassDefinitions = new HashSet<>();
    private List<JaversModule> externalModules = new ArrayList<>();

    private JaversBuilder() {
        logger.debug("starting up javers ...");
        bootContainer(new CoreJaversModule());
    }

    public static JaversBuilder javers() {
        return new JaversBuilder();
    }

    public Javers build() {

        bootJsonConverter();

        //bootstrap phase 2
        addModule(new ModelJaversModule(coreConfiguration()));
        registerManagedClasses();
        bootEntityManager();

        logger.info("javers instance is up & ready");
        return getContainerComponent(Javers.class);
    }

    /**
     * registers {@link Entity} with id-property selected on the basis of @Id annotation
     */
    public JaversBuilder registerEntity(Class<?> entityClass) {
        Validate.argumentIsNotNull(entityClass);
        managedClassDefinitions.add(new EntityDefinition(entityClass));
        return this;
    }

    /**
     * registers {@link Entity} with id-property selected explicitly by name
     */
    public JaversBuilder registerEntity(Class<?> entityClass, String idPropertyName) {
        Validate.argumentsAreNotNull(entityClass, idPropertyName);
        managedClassDefinitions.add( new EntityDefinition(entityClass, idPropertyName) );
        return this;
    }

    /**
     * registers {@link ValueObject}
     */
    public JaversBuilder registerValueObject(Class<?> valueObjectClass) {
        Validate.argumentIsNotNull(valueObjectClass);
        managedClassDefinitions.add(new ValueObjectDefinition(valueObjectClass));
        return this;
    }

    /**
     * registers {@link ValueType}
     */
    public JaversBuilder registerValue(Class<?> valueClass) {
        Validate.argumentIsNotNull(valueClass);
        typeMapper().registerValueType(valueClass);
        return this;
    }

    /**
     * Registers {@link ValueType} and its custom JSON adapter.
     * <p/>
     *
     * Useful for not trivial ValueTypes when Gson's default representation isn't appropriate
     *
     * @see JsonTypeAdapter
     * @see JsonTypeAdapter#getValueType()
     */
    public JaversBuilder registerValueTypeAdapter(JsonTypeAdapter typeAdapter) {
        registerValue(typeAdapter.getValueType());
        jsonConverterBuilder().registerJsonTypeAdapter(typeAdapter);
        return this;
    }

    /**
     * Registers {@link ValueType} and its custom native
     *  <a href="http://code.google.com/p/google-gson/">Gson</a> adapter.
     * <p/>
     *
     * Useful when you already have Gson {@link TypeAdapter}s implemented.
     *
     * @see TypeAdapter
     */
    public JaversBuilder registerValueGsonTypeAdapter(Class valueType, TypeAdapter nativeAdapter) {
        registerValue(valueType);
        jsonConverterBuilder().registerNativeTypeAdapter(valueType, nativeAdapter);
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
        coreConfiguration().withMappingStyle(mappingStyle);
        return this;
    }

   /* @Deprecated
    public JaversBuilder addModule(JaversModule javersModule) {
        Validate.argumentIsNotNull(javersModule);
        externalModules.add(javersModule);
        return this;
    }*/

    private void registerManagedClasses() {
        EntityManager entityManager = entityManager();
        for (ManagedClassDefinition def : managedClassDefinitions) {
            entityManager.register(def);
        }
    }

    private EntityManager entityManager() {
        return getContainerComponent(EntityManager.class);
    }

    private TypeMapper typeMapper() {
        return getContainerComponent(TypeMapper.class);
    }

    private JaversCoreConfiguration coreConfiguration() {
        return getContainerComponent(JaversCoreConfiguration.class);
    }

    private JsonConverterBuilder jsonConverterBuilder(){
        return getContainerComponent(JsonConverterBuilder.class);
    }

    private void bootEntityManager() {
        entityManager().buildManagedClasses();
    }

    private void bootJsonConverter() {
        addComponent(jsonConverterBuilder().build());
    }
}
