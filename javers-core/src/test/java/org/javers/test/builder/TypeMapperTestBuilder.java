package org.javers.test.builder;

import org.javers.core.model.DummyAddress;
import org.javers.core.model.DummyNetworkAddress;
import org.javers.core.model.DummyUser;
import org.javers.core.model.DummyUserDetails;
import org.javers.model.mapping.type.TypeMapper;

/**
 * @author Pawel Cierpiatka <pawel.cierpiatka@gmail.com>
 */
public class TypeMapperTestBuilder {

    private TypeMapper typeMapper;

    private TypeMapperTestBuilder() {
        typeMapper = new TypeMapper();
    }

    public static TypeMapperTestBuilder typeMapper() {
        return new TypeMapperTestBuilder();
    }

    public TypeMapper build() {
        return typeMapper;
    }

    public TypeMapperTestBuilder registerEntity(Class<?> entity) {
        typeMapper.registerEntityReferenceType(entity);
        return this;
    }

    public TypeMapperTestBuilder registerValueObject(Class<?> objectValue) {
        typeMapper.registerValueType(objectValue);
        return this;
    }

    public TypeMapperTestBuilder registerAllDummyTypes() {
        typeMapper.registerValueType(DummyAddress.class);
        typeMapper.registerEntityReferenceType(DummyUser.class);
        typeMapper.registerEntityReferenceType(DummyUserDetails.class);
        typeMapper.registerValueType(DummyNetworkAddress.class);
        return this;
    }
}
