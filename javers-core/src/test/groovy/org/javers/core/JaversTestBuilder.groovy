package org.javers.core

import org.javers.core.metamodel.type.TypeFactory
import org.javers.core.model.DummyUserWithValues
import org.javers.core.metamodel.property.ManagedClassFactory
import org.javers.core.metamodel.type.TypeMapper
import org.javers.model.object.graph.ObjectGraphBuilder
import org.javers.core.model.DummyAddress
import org.javers.core.model.DummyNetworkAddress
import org.javers.core.model.DummyUser
import org.javers.core.model.DummyUserDetails

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
