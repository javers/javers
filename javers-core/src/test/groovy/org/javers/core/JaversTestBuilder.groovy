package org.javers.core

import org.javers.core.metamodel.type.TypeFactory
import org.javers.core.metamodel.property.ManagedClassFactory
import org.javers.core.metamodel.type.TypeMapper
import org.javers.core.graph.ObjectGraphBuilder
import org.javers.core.snapshot.SnapshotFactory

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

    static Javers javers() {
        new JaversTestBuilder(MappingStyle.FIELD).javersBuilder.getContainerComponent(Javers)
    }

    ManagedClassFactory getManagedClassFactory() {
        javersBuilder.getContainerComponent(ManagedClassFactory)
    }

    SnapshotFactory getSnapshotFactory() {
        javersBuilder.getContainerComponent(SnapshotFactory)
    }

    TypeFactory getTypeSpawningFactory() {
        javersBuilder.getContainerComponent(TypeFactory)
    }

    TypeMapper getTypeMapper(){
        javersBuilder.getContainerComponent(TypeMapper)
    }

    ObjectGraphBuilder createObjectGraphBuilder() {
        new ObjectGraphBuilder(getTypeMapper())
    }

}
