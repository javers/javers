package org.javers.test.builder;

import org.javers.core.model.DummyAddress;
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

    public TypeMapperTestBuilder withAllDummyModels() {
        typeMapper.registerObjectValueType(DummyAddress.class);
        return this;
    }

    public TypeMapper build() {
        return typeMapper;
    }
}
