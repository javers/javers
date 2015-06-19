package org.javers.core;

import com.google.gson.TypeAdapter;
import org.javers.core.commit.CommitFactoryModule;
import org.javers.core.diff.DiffFactoryModule;
import org.javers.core.diff.ListCompareAlgorithm;
import org.javers.core.diff.appenders.DiffAppendersModule;
import org.javers.core.diff.changetype.PropertyChange;
import org.javers.core.diff.custom.CustomPropertyComparator;
import org.javers.core.diff.custom.CustomToNativeAppenderAdapter;
import org.javers.core.graph.ObjectAccessHook;
import org.javers.core.graph.GraphFactoryModule;
import org.javers.core.json.JsonConverter;
import org.javers.core.json.JsonConverterBuilder;
import org.javers.core.json.JsonTypeAdapter;
import org.javers.core.json.typeadapter.change.ChangeTypeAdaptersModule;
import org.javers.core.json.typeadapter.commit.CommitTypeAdaptersModule;
import org.javers.core.metamodel.annotation.DiffIgnore;
import org.javers.core.metamodel.clazz.*;
import org.javers.core.metamodel.object.GlobalIdFactory;
import org.javers.core.metamodel.type.CustomType;
import org.javers.core.metamodel.type.TypeMapper;
import org.javers.core.metamodel.type.ValueObjectType;
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

import static org.javers.common.validation.Validate.argumentIsNotNull;
import static org.javers.common.validation.Validate.argumentsAreNotNull;

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
 * @see <a href="http://javers.org/documentation/domain-configuration/">http://javers.org/documentation/domain-configuration</a>
 * @author bartosz walacik
 */
public class JaversBuilder extends AbstractJaversBuilder {
    private static final Logger logger = LoggerFactory.getLogger(JaversBuilder.class);

    private final Set<ClientsClassDefinition> clientsClassDefinitions = new HashSet<>();

    private JaversRepository repository;

    public static JaversBuilder javers() {
        return new JaversBuilder();
    }

    /**
     * use static factory method {@link JaversBuilder#javers()}
     */
    protected JaversBuilder() {
        logger.debug("starting up JaVers ...");

        // bootstrap phase 1: core beans
        bootContainer();
        addModule(new CoreJaversModule(getContainer()));
        addModule(new DiffFactoryModule());
        addModule(new CommitFactoryModule(getContainer()));
        addModule(new GraphSnapshotModule(getContainer()));
        addModule(new GraphFactoryModule(getContainer()));
    }

    public Javers build() {

        Javers javers = assembleJaversInstance();
        repository.ensureSchema();

        logger.info("JaVers instance is up & ready");
        return javers;
    }

    protected Javers assembleJaversInstance(){
        bootDiffAppenders();

        // ManagedClassFactory & managed clazz registration
        bootManagedClasses();

        // JSON beans & domain aware typeAdapters
        bootJsonConverter();

        // Repository
        bootRepository();

        Javers javers = getContainerComponent(JaversCore.class);
        logger.info("JaVers instance is up & ready");
        return javers;
    }

    private void bootDiffAppenders() {
        addModule(new DiffAppendersModule(getContainer(), coreConfiguration()));
    }

    /**
     * @see <a href="http://javers.org/documentation/repository-configuration">http://javers.org/documentation/repository-configuration</a>
     */
    public JaversBuilder registerJaversRepository(JaversRepository repository){
        argumentsAreNotNull(repository);
        this.repository = repository;
        return this;
    }

    /**
     * Registers an {@link Entity}.
     * Use @Id annotation to mark exactly one Id-property.
     * <br/><br/>
     *
     * Optionally, use @Transient or @{@link DiffIgnore} to mark ignored properties.
     *
     * @see <a href="http://javers.org/documentation/domain-configuration/#domain-model-mapping">http://javers.org/documentation/domain-configuration/#domain-model-mapping</a>
     */
    public JaversBuilder registerEntity(Class<?> entityClass) {
        argumentIsNotNull(entityClass);
        return registerEntity( new EntityDefinition(entityClass));
    }

    /**
     * Registers an {@link Entity}. <br/>
     * Use this method if you are not willing to use annotations to mark Id-property
     * and ignored properties.
     *
     * @see <a href="http://javers.org/documentation/domain-configuration/#domain-model-mapping">http://javers.org/documentation/domain-configuration/#domain-model-mapping</a>
     */
    public JaversBuilder registerEntity(EntityDefinition entityDefinition){
        argumentIsNotNull(entityDefinition);
        clientsClassDefinitions.add(entityDefinition);
        return this;
    }

    /**
     * Deprecated since 1.0.6,
     * use {@link #registerEntity(Class)}  or
     * {@link #registerEntity(EntityDefinition)}
     */
    @Deprecated
    public JaversBuilder registerEntity(Class<?> entityClass, String idPropertyName){
        return registerEntity(new EntityDefinition(entityClass, idPropertyName));
    }

    /**
     * Registers a {@link ValueObjectType}. <br/>
     * Use @Transient or @{@link DiffIgnore} to mark ignored properties.
     * <br/><br/>
     *
     * For example, ValueObjects are: Address, Point
     *
     * @see <a href="http://javers.org/documentation/domain-configuration/#domain-model-mapping">http://javers.org/documentation/domain-configuration/#domain-model-mapping</a>
     */
    public JaversBuilder registerValueObject(Class<?> valueObjectClass) {
        argumentIsNotNull(valueObjectClass);
        clientsClassDefinitions.add(new ValueObjectDefinition(valueObjectClass));
        return this;
    }

    /**
     * Registers a {@link ValueObjectType}. <br/>
     * Use this method if you are not willing to use annotations to mark ignored properties.
     *
     * @see <a href="http://javers.org/documentation/domain-configuration/#domain-model-mapping">http://javers.org/documentation/domain-configuration/#domain-model-mapping</a>
     */
    public JaversBuilder registerValueObject(ValueObjectDefinition valueObjectDefinition) {
        argumentIsNotNull(valueObjectDefinition);
        clientsClassDefinitions.add(valueObjectDefinition);
        return this;
    }

    /**
     * Registers a simple value type (see {@link ValueType}).
     * <br/><br/>
     *
     * For example, values are: BigDecimal, LocalDateTime
     *
     * @see <a href="http://javers.org/documentation/domain-configuration/#domain-model-mapping">http://javers.org/documentation/domain-configuration/#domain-model-mapping</a>
     */
    public JaversBuilder registerValue(Class<?> valueClass) {
        argumentIsNotNull(valueClass);
        clientsClassDefinitions.add(new ValueDefinition(valueClass));
        return this;
    }

    /**
     * Registers a {@link ValueType} and its custom JSON adapter.
     * <br><br>
     *
     * Useful for not trivial ValueTypes when Gson's default representation isn't appropriate
     *
     * @see <a href="http://javers.org/documentation/repository-configuration/#json-type-adapters">http://javers.org/documentation/repository-configuration/#json-type-adapters</a>
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
    public JaversBuilder typeSafeValues() {
        jsonConverterBuilder().typeSafeValues(true);
        return this;
    }

    public JaversBuilder registerEntities(Class<?>... entityClasses) {
        for(Class clazz : entityClasses) {
            registerEntity(clazz);
        }
        return this;
    }

    public JaversBuilder registerValueObjects(Class<?>... valueObjectClasses) {
        for(Class clazz : valueObjectClasses) {
            registerValueObject(clazz);
        }
        return this;
    }

    /**
     * Default style is {@link MappingStyle#FIELD}.
     */
    public JaversBuilder withMappingStyle(MappingStyle mappingStyle) {
        argumentIsNotNull(mappingStyle);
        coreConfiguration().withMappingStyle(mappingStyle);
        return this;
    }

    public JaversBuilder withNewObjectsSnapshot(boolean newObjectsSnapshot){
        coreConfiguration().withNewObjectsSnapshot(newObjectsSnapshot);
        return this;
    }

    public JaversBuilder withObjectAccessHook(ObjectAccessHook objectAccessHook) {
        removeComponent(ObjectAccessHook.class);
        bindComponent(ObjectAccessHook.class, objectAccessHook);
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
        bindComponent(comparator, new CustomToNativeAppenderAdapter(comparator, customType));
        return this;
    }

    /**
     * Choose between two algorithms for comparing list: ListCompareAlgorithm.SIMPLE
     * or ListCompareAlgorithm.LEVENSHTEIN_DISTANCE.
     * <br/><br/>
     * Generally, we recommend using LEVENSHTEIN_DISTANCE, because it’s smarter.
     * Hoverer, it can be slow for long lists, so SIMPLE is enabled by default.
     * <br/><br/>
     *
     * Refer to <a href="http://javers.org/documentation/diff-configuration/#list-algorithms">javers.org/documentation/diff-configuration/#list-algorithms</a>
     * for description of both algorithms
     *
     * @param algorithm ListCompareAlgorithm.SIMPLE is used by default
     */
    public JaversBuilder withListCompareAlgorithm(ListCompareAlgorithm algorithm) {
        argumentIsNotNull(algorithm);
        coreConfiguration().withListCompareAlgorithm(algorithm);

        return this;
    }

    private void mapRegisteredClasses() {
        TypeMapper typeMapper = typeMapper();
        for (ClientsClassDefinition def : clientsClassDefinitions) {
            typeMapper.registerClientsClass(def);
        }
    }

    private GlobalIdFactory globalIdFactory(){
        return getContainerComponent(GlobalIdFactory.class);
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
        jsonConverterBuilder
                .typeMapper(typeMapper())
                .globalIdFactory(globalIdFactory())
                .registerJsonTypeAdapters(getComponents(JsonTypeAdapter.class));

        addComponent(jsonConverterBuilder.build());
    }

    private void bootRepository(){
        if (repository == null){
            logger.info("using fake InMemoryRepository, register actual implementation via JaversBuilder.registerJaversRepository()");
            addModule(new InMemoryRepositoryModule(getContainer()));
            repository = getContainerComponent(JaversRepository.class);
        } else {
            repository.setJsonConverter( getContainerComponent(JsonConverter.class));
            addComponent(repository);
        }

       //JaversExtendedRepository can be created after users calls JaversBuilder.registerJaversRepository()
        addComponent(JaversExtendedRepository.class);
    }
}
