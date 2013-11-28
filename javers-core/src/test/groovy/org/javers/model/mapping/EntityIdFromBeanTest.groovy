package org.javers.model.mapping

import org.javers.model.mapping.type.TypeMapper

import static org.javers.test.builder.TypeMapperTestBuilder.typeMapper

/**
 * @author bartosz walacik
 */
class EntityIdFromBeanTest extends EntityIdTest {

    def setup() {
        TypeMapper mapper = typeMapper().registerAllDummyTypes().build()
        BeanBasedPropertyScanner scanner = new BeanBasedPropertyScanner(mapper)
        entityFactory = new EntityFactory(scanner)
    }
}
