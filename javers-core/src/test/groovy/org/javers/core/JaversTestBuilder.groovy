package org.javers.core

import org.javers.core.commit.CommitFactory
import org.javers.core.graph.LiveCdoFactory
import org.javers.core.graph.LiveGraph
import org.javers.core.graph.ObjectGraphBuilder
import org.javers.core.graph.ObjectNode
import org.javers.core.metamodel.object.InstanceId
import org.javers.core.metamodel.property.ManagedClassFactory
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

    private JaversTestBuilder (MappingStyle mappingStyle) {
       javersBuilder = new JaversBuilder()

       javersBuilder.withMappingStyle(mappingStyle).build()
    }

    static JaversTestBuilder javersTestAssembly(){
        new JaversTestBuilder(MappingStyle.FIELD)
    }

    static JaversTestBuilder javersTestAssembly(MappingStyle mappingStyle){
        new JaversTestBuilder(mappingStyle)
    }

    static Javers newInstance() {
        javersTestAssembly().javers()
    }

    Javers javers() {
        javersBuilder.getContainerComponent(Javers)
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

    LiveCdoFactory getLiveCdoFactory(){
        javersBuilder.getContainerComponent(LiveCdoFactory)
    }

    CommitFactory getCommitFactory(){
        javersBuilder.getContainerComponent(CommitFactory)
    }

    ObjectGraphBuilder createObjectGraphBuilder() {
        new ObjectGraphBuilder(getTypeMapper(), getLiveCdoFactory())
    }

    LiveGraph createLiveGraph(Object liveCdo) {
        new LiveGraph( createObjectGraphBuilder().buildGraph(liveCdo) )
    }

    IdBuilder idBuilder(){
        javers().idBuilder()
    }

    @Deprecated
    IdBuilder voBuilder(Object localId, Class entityClass){
        javers().idBuilder().withOwner(localId, entityClass)
    }

    @Deprecated
    InstanceId instanceId(Object localId, Class entityClass){
        idBuilder().instanceId(localId, entityClass)
    }

    InstanceId instanceId(Object instance){
        idBuilder().instanceId(instance)
    }
}
