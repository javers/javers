package org.javers.test.builder

import org.javers.core.model.DummyAddress
import org.javers.core.model.DummyNetworkAddress
import org.javers.core.model.DummyUser
import org.javers.core.model.DummyUserDetails
import org.javers.core.metamodel.type.TypeMapper

/**
 * @author Pawel Cierpiatka <pawel.cierpiatka@gmail.com>
 */
class TypeMapperTestBuilder {

    private TypeMapper typeMapper

    static TypeMapperTestBuilder typeMapper() {
        new TypeMapperTestBuilder(typeMapper: new TypeMapper())
    }

    TypeMapper build() {
        typeMapper
    }

    TypeMapperTestBuilder registerAllDummyTypes() {
        typeMapper.registerValueType(DummyAddress)
        typeMapper.registerEntityReferenceType(DummyUser)
        typeMapper.registerEntityReferenceType(DummyUserDetails)
        typeMapper.registerValueType(DummyNetworkAddress)
        this
    }
}
