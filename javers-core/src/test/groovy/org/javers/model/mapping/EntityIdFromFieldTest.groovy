package org.javers.model.mapping;

import org.javers.model.mapping.type.TypeMapper;
import org.junit.Before;

import static org.javers.test.builder.TypeMapperTestBuilder.typeMapper;

/**
 * @author bartosz walacik
 */
class EntityIdFromFieldTest extends EntityIdTest {

    def setup() {
        TypeMapper mapper = typeMapper().registerAllDummyTypes().build()
        FieldBasedPropertyScanner scanner = new FieldBasedPropertyScanner(mapper)
        entityFactory = new EntityFactory(scanner)
    }
}
