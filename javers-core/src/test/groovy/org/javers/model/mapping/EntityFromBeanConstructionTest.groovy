package org.javers.model.mapping

import org.javers.core.metamodel.property.BeanBasedPropertyScanner
import org.javers.core.model.DummyUserDetails
import org.javers.model.mapping.type.TypeMapper
import org.javers.model.mapping.type.ValueType

import static org.javers.test.assertion.EntityAssert.assertThat
import static org.javers.test.builder.TypeMapperTestBuilder.typeMapper

/**
 * @author Pawel Cierpiatka
 */
class EntityFromBeanConstructionTest extends EntityConstructionTest {

    def setupSpec() {
        TypeMapper typeMapper = typeMapper().registerAllDummyTypes().build();
        BeanBasedPropertyScanner scanner = new BeanBasedPropertyScanner(typeMapper);
        entityFactory = new ManagedClassFactory(scanner,typeMapper);
    }

}