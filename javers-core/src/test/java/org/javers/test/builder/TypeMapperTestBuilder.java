package org.javers.test.builder;

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

    public TypeMapperTestBuilder registerValueObject(Class<?> objectValue) {
        typeMapper.registerObjectValueType(objectValue);
        return this;
    }
}
