package org.javers.core

import org.javers.core.model.DummyUserWithValues
import org.javers.model.mapping.ManagedClassFactory
import org.javers.model.mapping.type.TypeMapper
import org.javers.model.object.graph.ObjectGraphBuilder
import org.javers.core.model.DummyAddress
import org.javers.core.model.DummyNetworkAddress
import org.javers.core.model.DummyUser
import org.javers.core.model.DummyUserDetails;
import org.javers.model.mapping.EntityManager

/**
 * This is just a test builder,
 * don not confuse with unit test - {@link JaversBuilderTest}
 * <br/><br/>
 *
 * Provides default setup with well known Dummy* Entities.
 * Provides access to protected components like {@link EntityManager}
 *
 * @author bartosz walacik
 */
class JaversTestBuilder {
    JaversBuilder javersBuilder

    private JaversTestBuilder (){
       javersBuilder = new JaversBuilder()

       javersBuilder.registerEntity(DummyUser)
       .registerEntity(DummyUserDetails)
       .registerEntity(DummyUserWithValues)
       .registerValueObject(DummyAddress)
       .registerValueObject(DummyNetworkAddress)
       .build()
    }

    static JaversTestBuilder javersTestAssembly(){
        new JaversTestBuilder()
    }

    static Javers javers() {
        new JaversTestBuilder().javersBuilder.getContainerComponent(Javers)
    }

    ManagedClassFactory getEntityFactory() {
        javersBuilder.getContainerComponent(ManagedClassFactory)
    }

    EntityManager getEntityManager() {
        javersBuilder.getContainerComponent(EntityManager)
    }

    TypeMapper getTypeMapper(){
        javersBuilder.getContainerComponent(TypeMapper)
    }

    ObjectGraphBuilder createObjectGraphBuilder() {
        new ObjectGraphBuilder(getEntityManager(), getTypeMapper())
    }

}
