package org.javers.core;

import org.javers.core.metamodel.type.*;
import com.google.gson.TypeAdapter;
import org.javers.common.validation.Validate;
import org.javers.core.commit.CommitFactoryModule;
import org.javers.core.diff.DiffFactoryModule;
import org.javers.core.diff.appenders.DiffAppendersModule;
import org.javers.core.diff.changetype.PropertyChange;
import org.javers.core.diff.custom.CustomPropertyComparator;
import org.javers.core.diff.custom.CustomToNativeAppenderAdapter;
import org.javers.core.json.JsonConverterBuilder;
import org.javers.core.json.JsonTypeAdapter;
import org.javers.core.json.typeadapter.change.ChangeTypeAdaptersModule;
import org.javers.core.json.typeadapter.commit.CommitTypeAdaptersModule;
import org.javers.core.metamodel.clazz.*;
import org.javers.core.metamodel.type.CustomType;
import org.javers.core.metamodel.type.TypeMapper;
import org.javers.core.metamodel.type.ValueType;
import org.javers.core.snapshot.GraphSnapshotModule;
import org.javers.repository.api.InMemoryRepositoryModule;
import org.javers.repository.api.JaversExtendedRepository;
import org.javers.repository.api.JaversRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Creates a JaVers instance based on your domain model metadata and custom configuration.
 * <br/><br/>
 *
 * For example, to build a JaVers instance configured with reasonable defaults:
 * <pre>
 * Javers javers = JaversBuilder.javers().build();
 * </pre>
 *
 * To build a JaVers instance with Entity type registered:
 * <pre>
 * Javers javers = JaversBuilder.javers()
 *                              .registerEntity(MyEntity.class)
 *                              .build();
 * </pre>
 *
 * @see <a href="http://javers.org/documentation/configuration/">http://javers.org/documentation/configuration</a>
 * @author bartosz walacik
 */
public final class JaversBuilder extends AbstractJaversBuilder {
    private static final Logger logger = LoggerFactory.getLogger(JaversBuilder.class);

    private final Set<ClientsClassDefinition> clientsClassDefinitions = new HashSet<>();

    private JaversRepository repository;

    public static JaversBuilder javers() {
        return new JaversBuilder();
    }

    private JaversBuilder() {
        logger.debug("starting up javers ...");

        // bootstrap phase 1: core beans
        bootContainer();
        addModule(new CoreJaversModule(getContainer()));
        addModule(new DiffAppendersModule(getContainer()));
        addModule(new DiffFactoryModule());
        addModule(new CommitFactoryModule(getContainer()));
        addModule(new GraphSnapshotModule(getContainer()));
    }

    public Javers build() {

        // ManagedClassFactory & managed clazz registration
        bootManagedClasses();

        // JSON beans & domain aware typeAdapters
        bootJsonConverter();

        // Repository
        bootRepository();

        Javers javers = getContainerComponent(Javers.class);
        logger.info("javers instance is up & ready");
        return javers;
    }

    /**
     * @see <a href="http://javers.org/documentation/configuration/#repository-setup">http://javers.org/documentation/configuration/#repository-setup</a>
     */
    public JaversBuilder registerJaversRepository(JaversRepository repository){
        Validate.argumentsAreNotNull(repository);
        this.repository = repository;
        return this;
    }

    /**
     * registers an {@link Entity} with id-property pointed by @Id annotation
     *
     * @see <a href="http://javers.org/documentation/configuration/#domain-model-mapping">http://javers.org/documentation/configuration/#domain-model-mapping</a>
     */
    public JaversBuilder registerEntity(Class<?> entityClass) {
        Validate.argumentIsNotNull(entityClass);
        return registerEntity( new EntityDefinition(entityClass));
    }

    /**
     * registers an {@link Entity} with id-property selected explicitly by name
     *
     * @see <a href="http://javers.org/documentation/configuration/#domain-model-mapping">http://javers.org/documentation/configuration/#domain-model-mapping</a>
     */
    public JaversBuilder registerEntity(Class<?> entityClass, String idPropertyName) {
        Validate.argumentsAreNotNull(entityClass, idPropertyName);
        return registerEntity( new EntityDefinition(entityClass, idPropertyName) );
    }

    /**
     * Registers a ValueObject type (see {@link ValueObjectType}). <br/>
     * For example, ValueObjects are: Address, Point
     *
     * @see <a href="http://javers.org/documentation/configuration/#domain-model-mapping">http://javers.org/documentation/configuration/#domain-model-mapping</a>
     */
    public JaversBuilder registerValueObject(Class<?> valueObjectClass) {
        Validate.argumentIsNotNull(valueObjectClass);
        clientsClassDefinitions.add(new ValueObjectDefinition(valueObjectClass));
        return this;
    }

    /**
     * Registers a simple value type (see {@link ValueType}). <br/>
     * For example, values are: BigDecimal, LocalDateTime
     *
     * @see <a href="http://javers.org/documentation/configuration/#domain-model-mapping">http://javers.org/documentation/configuration/#domain-model-mapping</a>
     */
    public JaversBuilder registerValue(Class<?> valueClass) {
        Validate.argumentIsNotNull(valueClass);
        clientsClassDefinitions.add(new ValueDefinition(valueClass));
        return this;
    }

    /**
     * Registers a {@link ValueType} and its custom JSON adapter.
     * <br><br>
     *
     * Useful for not trivial ValueTypes when Gson's default representation isn't appropriate
     *
     * @see <a href="http://javers.org/documentation/configuration/#json-type-adapters">http://javers.org/documentation/configuration/#json-type-adapters</a>
     * @see JsonTypeAdapter
     */
    public JaversBuilder registerValueTypeAdapter(JsonTypeAdapter typeAdapter) {
        for (Class c : (List<Class>)typeAdapter.getValueTypes()){
            registerValue(c);
        }

        jsonConverterBuilder().registerJsonTypeAdapter(typeAdapter);
        return this;
    }

    /**
     * Registers {@link ValueType} and its custom native
     * <a href="http://code.google.com/p/google-gson/">Gson</a> adapter.
     * <br/><br/>
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

    /**
     * Switch on when you need a type safe serialization for
     * heterogeneous collections like List, List&lt;Object&gt;.
     * <br/><br/>
     *
     * Heterogeneous collections are collections which contains items of different types
     * (or types unknown at compile time).
     * <br/><br/>
     *
     * This approach is generally discouraged, prefer statically typed collections
     * with exactly one type of items like List&lt;String&gt;.
     *
     * @see org.javers.core.json.JsonConverterBuilder#typeSafeValues(boolean)
     */
    public JaversBuilder typeSafeValues(){
        jsonConverterBuilder().typeSafeValues(true);
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
     * Default style is {@link MappingStyle#FIELD}.
     */
    public JaversBuilder withMappingStyle(MappingStyle mappingStyle) {
        Validate.argumentIsNotNull(mappingStyle);
        coreConfiguration().withMappingStyle(mappingStyle);
        return this;
    }

    public JaversBuilder withNewObjectsSnapshot(boolean newObjectsSnapshot){
        coreConfiguration().withNewObjectsSnapshot(newObjectsSnapshot);
        return this;
    }

    /**
     * Registers a custom comparator for given custom type.
     * <br/><br/>
     *
     * Comparator has to calculate and return a subtype of {@link PropertyChange}.
     * <br/><br/>
     *
     * Internally, given type is mapped as {@link CustomType}.
     * <br/><br/>
     *
     * Custom types are serialized to JSON using Gson defaults.
     *
     * @param <T> custom type
     * @param customType class literal to define a custom type
     * @see CustomPropertyComparator
     */
    public <T> JaversBuilder registerCustomComparator(CustomPropertyComparator<T, ?> comparator, Class<T> customType){
        clientsClassDefinitions.add(new CustomDefinition(customType));
        addComponent(new CustomToNativeAppenderAdapter(comparator, customType));
        return this;
    }

    private void mapRegisteredClasses() {
        TypeMapper typeMapper = typeMapper();
        for (ClientsClassDefinition def : clientsClassDefinitions) {
            typeMapper.registerClientsClass(def);
        }
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

    private void bootManagedClasses() {
        addModule(new ManagedClassFactoryModule(coreConfiguration()));
        mapRegisteredClasses();
    }

    /**
     * boots JsonConverter and registers domain aware typeAdapters
     */
    private void bootJsonConverter() {
        JsonConverterBuilder jsonConverterBuilder = jsonConverterBuilder();

        addModule(new ChangeTypeAdaptersModule(getContainer()));
        addModule(new CommitTypeAdaptersModule(getContainer()));
        jsonConverterBuilder.registerJsonTypeAdapters(getComponents(JsonTypeAdapter.class));

        addComponent(jsonConverterBuilder.build());
    }

    private void bootRepository(){
        if (repository == null){
            logger.info("using fake InMemoryRepository, register actual implementation via JaversBuilder.registerJaversRepository()");
            addModule(new InMemoryRepositoryModule(getContainer()));
        } else {
            addComponent(repository);
        }

       //JaversExtendedRepository can be created after users calls JaversBuilder.registerJaversRepository()
        addComponent(JaversExtendedRepository.class);
    }

    private JaversBuilder registerEntity(EntityDefinition entityDefinition) {
        clientsClassDefinitions.add(entityDefinition);
        return this;
    }

}
