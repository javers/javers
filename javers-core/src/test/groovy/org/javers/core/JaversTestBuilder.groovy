package org.javers.core

import org.javers.common.date.DateProvider
import org.javers.core.commit.CommitFactory
import org.javers.core.graph.LiveCdoFactory
import org.javers.core.graph.LiveGraph
import org.javers.core.graph.ObjectGraphBuilder
import org.javers.core.json.JsonConverter
import org.javers.core.metamodel.clazz.ClassAnnotationsScanner
import org.javers.core.metamodel.object.GlobalIdFactory
import org.javers.core.metamodel.object.InstanceId
import org.javers.core.metamodel.clazz.ManagedClassFactory
import org.javers.core.metamodel.type.TypeFactory
import org.javers.core.metamodel.type.TypeMapper
import org.javers.core.snapshot.GraphShadowFactory
import org.javers.core.snapshot.GraphSnapshotFactory
import org.javers.core.snapshot.SnapshotFactory
import org.javers.repository.api.JaversExtendedRepository
import org.javers.repository.api.JaversRepository

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

    static JaversTestBuilder javersTestAssembly(){
        new JaversTestBuilder(MappingStyle.FIELD)
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

    static JaversTestBuilder javersTestAssemblyTypeSafe(){
        new JaversTestBuilder(new JaversBuilder().typeSafeValues())
    }

    static Javers newInstance() {
        javersTestAssembly().javers()
    }

    Javers javers() {
        javersBuilder.getContainerComponent(Javers)
    }

    ClassAnnotationsScanner getClassAnnotationsScanner(){
        javersBuilder.getContainerComponent(ClassAnnotationsScanner)
    }

    ManagedClassFactory getManagedClassFactory() {
        javersBuilder.getContainerComponent(ManagedClassFactory)
    }

    SnapshotFactory getSnapshotFactory() {
        javersBuilder.getContainerComponent(SnapshotFactory)
    }

    GraphSnapshotFactory getGraphSnapshotFactory() {
        javersBuilder.getContainerComponent(GraphSnapshotFactory)
    }

    GraphShadowFactory getGraphShadowFactory() {
        javersBuilder.getContainerComponent(GraphShadowFactory)
    }

    TypeFactory getTypeSpawningFactory() {
        javersBuilder.getContainerComponent(TypeFactory)
    }

    JaversExtendedRepository getJaversRepository(){
        javersBuilder.getContainerComponent(JaversExtendedRepository)
    }

    TypeMapper getTypeMapper(){
        javersBuilder.getContainerComponent(TypeMapper)
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

    ObjectGraphBuilder createObjectGraphBuilder() {
        new ObjectGraphBuilder(getTypeMapper(), getLiveCdoFactory())
    }


    LiveGraph createLiveGraph(Object liveCdo) {
        createObjectGraphBuilder().buildGraph(liveCdo)
    }

    IdBuilder idBuilder(){
        javers().idBuilder()
    }

    InstanceId instanceId(Object instance){
        idBuilder().instanceId(instance)
    }
}
