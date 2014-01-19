package org.javers.model.mapping

import org.javers.model.mapping.type.TypeMapper

import static org.javers.test.builder.TypeMapperTestBuilder.typeMapper

/**
 * @author Pawel Cierpiatka
 */
class EntityFromFieldsConstructionTest extends EntityConstructionTest {

    def setupSpec() {
        TypeMapper typeMapper = typeMapper().registerAllDummyTypes().build();
        FieldBasedPropertyScanner scanner = new FieldBasedPropertyScanner(typeMapper);
        entityFactory = new ManagedClassFactory(scanner, typeMapper);
    }
}
