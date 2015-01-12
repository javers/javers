package org.javers.core;

import com.google.gson.TypeAdapter;
import org.javers.common.validation.Validate;
import org.javers.core.commit.CommitFactoryModule;
import org.javers.core.diff.appenders.DiffAppendersModule;
import org.javers.core.json.JsonConverter;
import org.javers.core.json.JsonConverterBuilder;
import org.javers.core.json.JsonTypeAdapter;
import org.javers.core.json.typeadapter.change.ChangeTypeAdaptersModule;
import org.javers.core.json.typeadapter.commit.CommitTypeAdaptersModule;
import org.javers.core.metamodel.clazz.*;
import org.javers.core.metamodel.object.GlobalIdFactory;
import org.javers.core.metamodel.type.TypeMapper;
import org.javers.core.metamodel.type.ValueType;
import org.javers.core.pico.CoreJaversModule;
import org.javers.core.pico.JaversModule;
import org.javers.core.snapshot.GraphSnapshotModule;
import org.javers.repository.api.InMemoryRepository;
import org.javers.repository.api.JaversRepository;
import org.picocontainer.PicoContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Creates JaVers instance based on your domain model metadata and custom configuration.
 * <br>
 * Supports two configuring methods:
 * <ul>
 *     <li/>by properties file, see {TBA ...}
 *     <li/>programmatically using builder style methods
 * </ul>
 *
 * @author bartosz walacik
 */
public final class JaversBuilder extends AbstractJaversBuilder {
    private static final Logger logger = LoggerFactory.getLogger(JaversBuilder.class);

    private final Set<ClientsClassDefinition> clientsClassDefinitions = new HashSet<>();

    private JaversRepository repository;

    private JaversBuilder() {
        logger.debug("starting up javers ...");

        // bootstrap phase 1: core beans
        bootContainer(new CoreJaversModule());
        addModule(new DiffAppendersModule(getContainer()));
        addModule(new CommitFactoryModule(getContainer()));
        addModule(new GraphSnapshotModule(getContainer()));
    }

    public static JaversBuilder javers() {
        return new JaversBuilder();
    }

    public Javers build() {

        // bootstrap phase 2:
        // ManagedClassFactory & managed clazz registration
        bootManagedClasses();

        // bootstrap phase 3: JSON beans & domain aware typeAdapters
        bootJsonConverter();

        // bootstrap phase 4: Repository
        bootRepository();

        logger.info("javers instance is up & ready");
        return getContainerComponent(Javers.class);
    }

    public JaversBuilder registerJaversRepository(JaversRepository repository){
        Validate.argumentsAreNotNull(repository);
        this.repository = repository;
        return this;
    }

    /**
     * registers {@link org.javers.core.metamodel.clazz.Entity} with id-property pointed by @Id annotation
     */
    public JaversBuilder registerEntity(Class<?> entityClass) {
        Validate.argumentIsNotNull(entityClass);
        return registerEntity( new EntityDefinition(entityClass));
    }

    /**
     * registers {@link org.javers.core.metamodel.clazz.Entity} with id-property selected explicitly by name
     */
    public JaversBuilder registerEntity(Class<?> entityClass, String idPropertyName) {
        Validate.argumentsAreNotNull(entityClass, idPropertyName);
        return registerEntity( new EntityDefinition(entityClass, idPropertyName) );
    }

    private JaversBuilder registerEntity(EntityDefinition entityDefinition) {
        clientsClassDefinitions.add(entityDefinition);
        return this;
    }

    /**
     * registers {@link org.javers.core.metamodel.clazz.ValueObject}
     */
    public JaversBuilder registerValueObject(Class<?> valueObjectClass) {
        Validate.argumentIsNotNull(valueObjectClass);
        clientsClassDefinitions.add(new ValueObjectDefinition(valueObjectClass));
        return this;
    }

    public JaversBuilder registerValueObjects(Class<?>...valueObjectClasses) {
        for(Class clazz : valueObjectClasses) {
            registerValueObject(clazz);
        }
        return this;
    }

    /**
     * registers {@link ValueType}
     */
    public JaversBuilder registerValue(Class<?> valueClass) {
        Validate.argumentIsNotNull(valueClass);
        clientsClassDefinitions.add(new ValueDefinition(valueClass));
        return this;
    }

    /**
     * Registers {@link ValueType} and its custom JSON adapter.
     * <br><br>
     *
     * Useful for not trivial ValueTypes when Gson's default representation isn't appropriate
     *
     * @see JsonTypeAdapter
     * @see JsonTypeAdapter#getValueTypes()
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
     *  <a href="http://code.google.com/p/google-gson/">Gson</a> adapter.
     * <br><br>
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
     * Switch on when you need type safe {@link org.javers.core.diff.changetype.Atomic}s
     * serialization stored in polymorfic collections like List, List&lt;Object&gt;, Map&lt;Object,Object&gt;, etc.
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

    /**
     * {@link MappingStyle#FIELD} by default
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
        addModule(new ManagedClassFactoryModule( coreConfiguration()) );
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
            repository = new InMemoryRepository(getContainerComponent(GlobalIdFactory.class));
        }
        addComponent(repository);
        repository.setJsonConverter(getContainerComponent(JsonConverter.class));
    }
}
