package org.javers.model.mapping

import org.javers.core.model.DummyAddress
import org.javers.core.model.DummyUserDetails
import org.javers.model.mapping.type.TypeMapper
import org.javers.model.mapping.type.ValueObjectType

import static org.javers.test.assertion.EntityAssert.assertThat

/**
 * @author Pawel Cierpiatka
 */
class EntityFromBeanConstructionTest extends EntityConstructionTest {

    def setupSpec() {
        TypeMapper mapper = new TypeMapper();
        mapper.registerValueObjectType(DummyAddress.class);
        mapper.registerValueObjectType(DummyUserDetails.class);
        entityFactory = new BeanBasedEntityFactory(mapper);
    }

    def "should scan value object property"() {
        when:
        Entity entity = entityFactory.createEntity(DummyUserDetails.class);

        then:
        assertThat(entity).hasProperty("dummyAddress")
                .hasJaversType(ValueObjectType.class);
    }

}