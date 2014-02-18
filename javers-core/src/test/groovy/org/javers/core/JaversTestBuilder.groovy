package org.javers.core

import org.javers.core.metamodel.type.TypeSpawningFactory
import org.javers.core.model.DummyUserWithValues
import org.javers.core.metamodel.property.ManagedClassFactory
import org.javers.core.metamodel.type.TypeMapper
import org.javers.model.object.graph.ObjectGraphBuilder
import org.javers.core.model.DummyAddress
import org.javers.core.model.DummyNetworkAddress
import org.javers.core.model.DummyUser
import org.javers.core.model.DummyUserDetails

import javax.persistence.EntityManager;

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

       javersBuilder.withMappingStyle(mappingStyle)
                    .registerEntity(DummyUser)
                    .registerEntity(DummyUserDetails)
                    .registerEntity(DummyUserWithValues)
                    .registerValueObject(DummyAddress)
                    .registerValueObject(DummyNetworkAddress)
                    .build()
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

    TypeSpawningFactory getTypeSpawningFactory() {
        javersBuilder.getContainerComponent(TypeSpawningFactory)
    }

    TypeMapper getTypeMapper(){
        javersBuilder.getContainerComponent(TypeMapper)
    }

    ObjectGraphBuilder createObjectGraphBuilder() {
        new ObjectGraphBuilder(getTypeMapper())
    }

}
