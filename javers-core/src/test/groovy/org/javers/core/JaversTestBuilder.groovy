package org.javers.core

import org.javers.common.date.DateProvider
import org.javers.core.commit.CommitFactory
import org.javers.core.graph.LiveCdoFactory
import org.javers.core.graph.LiveCdoWrapper
import org.javers.core.graph.LiveGraph
import org.javers.core.graph.LiveGraphFactory
import org.javers.core.json.JsonConverter
import org.javers.core.json.JsonConverterBuilder
import org.javers.core.metamodel.object.LiveCdo
import org.javers.core.metamodel.object.GlobalIdFactory
import org.javers.core.metamodel.object.InstanceId
import org.javers.core.metamodel.object.UnboundedValueObjectId
import org.javers.core.metamodel.object.ValueObjectId
import org.javers.core.metamodel.scanner.ClassScanner
import org.javers.core.metamodel.type.TypeFactory
import org.javers.core.model.DummyAddress
import org.javers.core.snapshot.ObjectHasher
import org.javers.core.snapshot.SnapshotFactory
import org.javers.core.metamodel.property.Property
import org.javers.core.metamodel.type.TypeMapper
import org.javers.repository.api.JaversExtendedRepository
import org.javers.repository.api.JaversRepository
import org.javers.repository.jql.QueryRunner
import org.javers.shadow.ShadowFactory

/**
 * This is just a test builder,
 * don not confuse with unit test - {@link JaversBuilderTest}
 * <br/><br/>
 *
 * Provides default setup with well known Dummy* Entities.
 *
 * @author bartosz walacik
 */
class JaversTestBuilder {
    JaversBuilder javersBuilder

    private JaversTestBuilder (JaversBuilder javersBuilder) {
       this.javersBuilder = javersBuilder
       this.javersBuilder.build()
    }

    private JaversTestBuilder (MappingStyle mappingStyle) {
       javersBuilder = new JaversBuilder()
       javersBuilder.withMappingStyle(mappingStyle).build()
    }

    private JaversTestBuilder (DateProvider dateProvider) {
        javersBuilder = new JaversBuilder()
        javersBuilder.withDateProvider(dateProvider).build()
    }

    private JaversTestBuilder (JaversRepository javersRepository) {
        javersBuilder = new JaversBuilder()
        javersBuilder.registerJaversRepository(javersRepository).build()
    }

    private JaversTestBuilder (Class classToScan) {
        javersBuilder = new JaversBuilder()
        javersBuilder.scanTypeName(classToScan).build()
    }

    private JaversTestBuilder (String packagesToScan) {
        javersBuilder = new JaversBuilder()
        javersBuilder.withPackagesToScan(packagesToScan).build()
    }

    static JaversTestBuilder javersTestAssembly(){
        new JaversTestBuilder(MappingStyle.FIELD)
    }

    static JaversTestBuilder javersTestAssembly(String packagesToScan){
        new JaversTestBuilder(packagesToScan)
    }

    static JaversTestBuilder javersTestAssembly(Class classToScan){
        new JaversTestBuilder(classToScan)
    }

    static JaversTestBuilder javersTestAssembly(JaversRepository javersRepository){
        new JaversTestBuilder(javersRepository)
    }

    static JaversTestBuilder javersTestAssembly(MappingStyle mappingStyle){
        new JaversTestBuilder(mappingStyle)
    }

    static JaversTestBuilder javersTestAssembly(DateProvider dateProvider){
        new JaversTestBuilder(dateProvider)
    }

    static JaversTestBuilder javersTestAssemblyTypeSafe() {
        new JaversTestBuilder(new JaversBuilder().withTypeSafeValues(true))
    }

    static Javers newInstance() {
        javersTestAssembly().javers()
    }

    Javers javers() {
        javersBuilder.getContainerComponent(Javers)
    }

    LiveCdo createCdoWrapper(Object cdo){
        def mType = getTypeMapper().getJaversManagedType(cdo.class)
        def id = instanceId(cdo)

        new LiveCdoWrapper(cdo, id, mType)
    }

    Property getProperty(Class type, String propName) {
        getTypeMapper().getJaversManagedType(type).getProperty(propName)
    }

    SnapshotFactory getSnapshotFactory() {
        javersBuilder.getContainerComponent(SnapshotFactory)
    }

    JaversExtendedRepository getJaversRepository(){
        javersBuilder.getContainerComponent(JaversExtendedRepository)
    }

    TypeMapper getTypeMapper(){
        javersBuilder.getContainerComponent(TypeMapper)
    }

    QueryRunner getQueryRunner(){
        javersBuilder.getContainerComponent(QueryRunner)
    }

    GlobalIdFactory getGlobalIdFactory(){
        javersBuilder.getContainerComponent(GlobalIdFactory)
    }

    LiveCdoFactory getLiveCdoFactory(){
        javersBuilder.getContainerComponent(LiveCdoFactory)
    }

    CommitFactory getCommitFactory(){
        javersBuilder.getContainerComponent(CommitFactory)
    }

    JsonConverter getJsonConverter() {
        javersBuilder.getContainerComponent(JsonConverter)
    }

    ShadowFactory getShadowFactory() {
        javersBuilder.getContainerComponent(ShadowFactory)
    }


    JsonConverter getJsonConverterMinifiedPrint() {
        JaversBuilder.javers().withPrettyPrint(false).build().getJsonConverter()
    }

    JsonConverterBuilder getJsonConverterBuilder() {
        javersBuilder.getContainerComponent(JsonConverterBuilder)
    }

    ObjectHasher getObjectHasher(){
        javersBuilder.getContainerComponent(ObjectHasher)
    }

    String addressHash(String city){
        getObjectHasher().hash(new DummyAddress(city))
    }

    def getContainerComponent(Class type) {
        javersBuilder.getContainerComponent(type)
    }

    LiveGraph createLiveGraph(Object liveCdo) {
        javersBuilder.getContainerComponent(LiveGraphFactory).createLiveGraph(liveCdo)
    }

    InstanceId instanceId(Object instance){
        getGlobalIdFactory().createId(instance)
    }

    InstanceId instanceId(Object localId, Class entity){
        getGlobalIdFactory().createInstanceId(localId, entity)
    }

    ValueObjectId valueObjectId(Object localId, Class owningEntity, fragment) {
        getGlobalIdFactory().createValueObjectIdFromPath(instanceId(localId, owningEntity), fragment)
    }

    UnboundedValueObjectId unboundedValueObjectId(Class valueObject) {
        getGlobalIdFactory().createUnboundedValueObjectId(valueObject)
    }

}
