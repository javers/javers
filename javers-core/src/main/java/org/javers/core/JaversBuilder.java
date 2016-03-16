package org.javers.core;

import com.google.gson.TypeAdapter;
import org.javers.common.date.DateProvider;
import org.javers.common.date.DefaultDateProvider;
import org.javers.common.reflection.ReflectionUtil;
import org.javers.core.commit.Commit;
import org.javers.core.commit.CommitFactoryModule;
import org.javers.core.diff.DiffFactoryModule;
import org.javers.core.diff.ListCompareAlgorithm;
import org.javers.core.diff.appenders.DiffAppendersModule;
import org.javers.core.diff.changetype.PropertyChange;
import org.javers.core.diff.custom.CustomPropertyComparator;
import org.javers.core.diff.custom.CustomToNativeAppenderAdapter;
import org.javers.core.graph.GraphFactoryModule;
import org.javers.core.graph.ObjectAccessHook;
import org.javers.core.graph.TailoredJaversMemberFactoryModule;
import org.javers.core.json.JsonConverter;
import org.javers.core.json.JsonConverterBuilder;
import org.javers.core.json.JsonTypeAdapter;
import org.javers.core.json.typeadapter.change.ChangeTypeAdaptersModule;
import org.javers.core.json.typeadapter.commit.CommitTypeAdaptersModule;
import org.javers.core.json.typeadapter.util.UtilTypeAdapters;
import org.javers.core.metamodel.annotation.*;
import org.javers.core.metamodel.clazz.*;
import org.javers.core.metamodel.scanner.ScannerModule;
import org.javers.core.metamodel.type.*;
import org.javers.core.snapshot.SnapshotModule;
import org.javers.groovysupport.GroovyAddOns;
import org.javers.java8support.Java8AddOns;
import org.javers.mongosupport.MongoLong64JsonDeserializer;
import org.javers.mongosupport.RequiredMongoSupportPredicate;
import org.javers.repository.api.JaversExtendedRepository;
import org.javers.repository.api.JaversRepository;
import org.javers.repository.inmemory.InMemoryRepositoryModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
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

    private final Set<Class> classesToScan = new HashSet<>();

    private JaversRepository repository;
    private DateProvider dateProvider;

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
        addModule(new SnapshotModule(getContainer()));
        addModule(new GraphFactoryModule(getContainer()));

        // bootstrap phase 2: add-ons
        if (ReflectionUtil.isJava8runtime()){
            new Java8AddOns().beforeAssemble(this);
        }
        new GroovyAddOns().beforeAssemble(this);
        new UtilTypeAdapters().beforeAssemble(this);
    }

    public Javers build() {

        Javers javers = assembleJaversInstance();
        repository.ensureSchema();

        logger.info("JaVers instance is up & ready");
        return javers;
    }

    protected Javers assembleJaversInstance(){
        addModule(new DiffAppendersModule(coreConfiguration(), getContainer()));
        addModule(new TailoredJaversMemberFactoryModule(coreConfiguration(), getContainer()));
        addModule(new ScannerModule(coreConfiguration(), getContainer()));

        bootManagedTypeModule();

        // JSON beans & domain aware typeAdapters
        bootJsonConverter();

        bootDateTimeProvider();
        bootRepository();

        // clases to scan
        for (Class c : classesToScan){
            typeMapper().getJaversType(c);
        }

        return getContainerComponent(JaversCore.class);
    }

    /**
     * @see <a href="http://javers.org/documentation/repository-configuration">http://javers.org/documentation/repository-configuration</a>
     */
    public JaversBuilder registerJaversRepository(JaversRepository repository) {
        argumentsAreNotNull(repository);
        this.repository = repository;
        return this;
    }

    /**
     * Registers an {@link EntityType}. <br/>
     * Use @Id annotation to mark exactly one Id-property.
     * <br/><br/>
     *
     * Optionally, use @Transient or @{@link DiffIgnore} annotations to mark ignored properties.
     * <br/><br/>
     *
     * For example, Entities are: Person, Document
     *
     * @see #registerEntity(EntityDefinition)
     * @see <a href="http://javers.org/documentation/domain-configuration/#domain-model-mapping">http://javers.org/documentation/domain-configuration/#domain-model-mapping</a>
     */
    public JaversBuilder registerEntity(Class<?> entityClass) {
        argumentIsNotNull(entityClass);
        return registerEntity( new EntityDefinition(entityClass));
    }

    /**
     * Registers a {@link ValueObjectType}. <br/>
     * Optionally, use @Transient or @{@link DiffIgnore} annotations to mark ignored properties.
     * <br/><br/>
     *
     * For example, ValueObjects are: Address, Point
     *
     * @see #registerValueObject(ValueObjectDefinition)
     * @see <a href="http://javers.org/documentation/domain-configuration/#domain-model-mapping">http://javers.org/documentation/domain-configuration/#domain-model-mapping</a>
     */
    public JaversBuilder registerValueObject(Class<?> valueObjectClass) {
        argumentIsNotNull(valueObjectClass);
        clientsClassDefinitions.add(new ValueObjectDefinition(valueObjectClass));
        return this;
    }

    /**
     * Registers an {@link EntityType}. <br/>
     * Use this method if you are not willing to use {@link Entity} annotation.
     * <br/></br/>
     *
     * Recommended way to create {@link EntityDefinition} is {@link EntityDefinitionBuilder},
     * for example:
     * <pre>
     * javersBuilder.registerEntity(
     *     EntityDefinitionBuilder.entityDefinition(Person.class)
     *     .withIdPropertyName("id")
     *     .withTypeName("Person")
     *     .withIgnoredProperties("notImportantProperty","transientProperty")
     *     .build());
     * </pre>
     *
     * For simple cases, you can use {@link EntityDefinition} constructors,
     * for example:
     * <pre>
     * javersBuilder.registerEntity( new EntityDefinition(Person.class, "login") );
     * </pre>
     *
     * @see EntityDefinitionBuilder#entityDefinition(Class)
     * @see <a href="http://javers.org/documentation/domain-configuration/#domain-model-mapping">http://javers.org/documentation/domain-configuration/#domain-model-mapping</a>
     */
    public JaversBuilder registerEntity(EntityDefinition entityDefinition){
        argumentIsNotNull(entityDefinition);
        clientsClassDefinitions.add(entityDefinition);
        return this;
    }

    /**
     * Registers a {@link ValueObjectType}. <br/>
     * Use this method if you are not willing to use {@link ValueObject} annotations.
     * <br/></br/>
     *
     * Recommended way to create {@link ValueObjectDefinition} is {@link ValueObjectDefinitionBuilder}.
     * For example:
     * <pre>
     * javersBuilder.registerValueObject(ValueObjectDefinitionBuilder.valueObjectDefinition(Address.class)
     *     .withIgnoredProperties(ignoredProperties)
     *     .withTypeName(typeName)
     *     .build();
     * </pre>
     *
     * For simple cases, you can use {@link ValueObjectDefinition} constructors,
     * for example:
     * <pre>
     * javersBuilder.registerValueObject( new ValueObjectDefinition(Address.class, "ignored") );
     * </pre>
     *
     * @see ValueObjectDefinitionBuilder#valueObjectDefinition(Class)
     * @see <a href="http://javers.org/documentation/domain-configuration/#domain-model-mapping">http://javers.org/documentation/domain-configuration/#domain-model-mapping</a>
     */
    public JaversBuilder registerValueObject(ValueObjectDefinition valueObjectDefinition) {
        argumentIsNotNull(valueObjectDefinition);
        clientsClassDefinitions.add(valueObjectDefinition);
        return this;
    }

    /**
     * <b>Not implemented!</b>
     * <br/><br/>
     *
     * <i>If implemented, allows you to register all your classes with &#64;{@link TypeName} annotation
     * (within given package) in order to use them in all kinds of JQL queries <br/>
     * (without getting TYPE_NAME_NOT_FOUND exception).
     * </i>
     * <br/><br/>
     *
     * If you think that this method should be implemented,
     * vote here: <a href="https://github.com/javers/javers/issues/263">issue/263</a>
     */
    public JaversBuilder scanTypeNames(String packageToScan){
        throw new RuntimeException("JaversBuilder.scanTypeNames(String packageToScan) is not implemented! " +
                "If you think that this method should be implemented, " +
                "vote here: https://github.com/javers/javers/issues/263");
    }

    /**
     * Register your class with &#64;{@link TypeName} annotation
     * in order to use them in all kinds of JQL queries<br/>
     * (without getting TYPE_NAME_NOT_FOUND exception).
     * <br/><br/>
     *
     * If you think that JaVers should be able to scan all your classes
     * in given package, vote for this feature: <a href="https://github.com/javers/javers/issues/263">issue/263</a>
     * <br/><br/>
     *
     * Alias for {@link Javers#getTypeMapping(Type)}
     *
     * @since 1.4
     */
    public JaversBuilder scanTypeName(Class userType){
        classesToScan.add(userType);
        return this;
    }

    /**
     * Registers a simple value type (see {@link ValueType}).
     * <br/><br/>
     *
     * For example, values are: BigDecimal, LocalDateTime.
     * <br/><br/>
     *
     * Use this method if you are not willing to use {@link Value} annotation.
     *
     * @see <a href="http://javers.org/documentation/domain-configuration/#domain-model-mapping">http://javers.org/documentation/domain-configuration/#domain-model-mapping</a>
     */
    public JaversBuilder registerValue(Class<?> valueClass) {
        argumentIsNotNull(valueClass);
        clientsClassDefinitions.add(new ValueDefinition(valueClass));
        return this;
    }

    /**
     * Marks given class as ignored by JaVers.
     * <br/><br/>
     *
     * Use this method if you are not willing to use {@link DiffIgnore} annotation.
     *
     * @see DiffIgnore
     */
    public JaversBuilder registerIgnoredClass(Class<?> ignoredClass) {
        argumentIsNotNull(ignoredClass);
        clientsClassDefinitions.add(new IgnoredTypeDefinition(ignoredClass));
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
     * @param typeSafeValues default false
     */
    public JaversBuilder withTypeSafeValues(boolean typeSafeValues) {
        jsonConverterBuilder().typeSafeValues(typeSafeValues);
        return this;
    }

    /**
     * choose between JSON pretty or concise printing style, i.e. :
     *
     * <ul><li>pretty:
     * <pre>
     * {
     *     "value": 5
     * }
     * </pre>
     * </li><li>concise:
     * <pre>
     * {"value":5}
     * </pre>
     * </li></ul>
     *
     * @param prettyPrint default true
     */
    public JaversBuilder withPrettyPrint(boolean prettyPrint) {
        jsonConverterBuilder().prettyPrint(prettyPrint);
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
     *
     * @see <a href="http://javers.org/documentation/domain-configuration/#property-mapping-style">http://javers.org/documentation/domain-configuration/#property-mapping-style</a>
     */
    public JaversBuilder withMappingStyle(MappingStyle mappingStyle) {
        argumentIsNotNull(mappingStyle);
        coreConfiguration().withMappingStyle(mappingStyle);
        return this;
    }

    /**
     * When enabled, {@link Javers#compare(Object oldVersion, Object currentVersion)}
     * generates additional 'Snapshots' of new objects (objects added in currentVersion graph).
     * <br/>
     * For each new object, state of its properties is captured and returned as a Set of PropertyChanges.
     * These Changes have null at the left side and a current property value at the right side.
     * <br/><br/>
     *
     * Disabled by default.
     */
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
     * Registers a custom comparator for a given Value type
     * (type of a property owned by Entity or ValueObject).
     * <br/><br/>
     *
     * Custom comparators are used by diff algorithm to calculate property-to-property diff.
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
     * Generally, we recommend using LEVENSHTEIN_DISTANCE, because it's smarter.
     * Hoverer, it can be slow for long lists, so SIMPLE is enabled by default.
     * <br/><br/>
     *
     * Refer to <a href="http://javers.org/documentation/diff-configuration/#list-algorithms">http://javers.org/documentation/diff-configuration/#list-algorithms</a>
     * for description of both algorithms
     *
     * @param algorithm ListCompareAlgorithm.SIMPLE is used by default
     */
    public JaversBuilder withListCompareAlgorithm(ListCompareAlgorithm algorithm) {
        argumentIsNotNull(algorithm);
        coreConfiguration().withListCompareAlgorithm(algorithm);
        return this;
    }

  /**
   * DateProvider providers current date for {@link Commit#getCommitDate()}.
   * <br/>
   * By default, now() is used.
   * <br/>
   * Overriding default dateProvider probably makes sense only in test environment.
   */
    public JaversBuilder withDateTimeProvider(DateProvider dateProvider) {
        argumentIsNotNull(dateProvider);
        this.dateProvider = dateProvider;
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

    private void bootManagedTypeModule() {
        addModule(new TypeMapperModule(getContainer()));
        mapRegisteredClasses();
    }

    /**
     * boots JsonConverter and registers domain aware typeAdapters
     */
    private void bootJsonConverter() {
        JsonConverterBuilder jsonConverterBuilder = jsonConverterBuilder();

        addModule(new ChangeTypeAdaptersModule(getContainer()));
        addModule(new CommitTypeAdaptersModule(getContainer()));

        if (new RequiredMongoSupportPredicate().apply(repository)) {
            jsonConverterBuilder.registerNativeGsonDeserializer(Long.class, new MongoLong64JsonDeserializer());
        }

        jsonConverterBuilder
                .registerJsonTypeAdapters(getComponents(JsonTypeAdapter.class));

        addComponent(jsonConverterBuilder.build());
    }

    private void bootDateTimeProvider() {
        if (dateProvider == null) {
            dateProvider = new DefaultDateProvider();
        }
        addComponent(dateProvider);
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
