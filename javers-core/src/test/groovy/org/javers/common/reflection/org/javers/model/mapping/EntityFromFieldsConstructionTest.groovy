package org.javers.common.reflection.org.javers.model.mapping

import org.javers.core.model.DummyAddress
import org.javers.core.model.DummyUserDetails
import org.javers.model.mapping.FieldBasedEntityFactory
import org.javers.model.mapping.type.TypeMapper

/**
 * @author Pawel Cierpiatka
 */
class EntityFromFieldsConstructionTest extends EntityConstructionTest {

    def setupSpec() {
        TypeMapper mapper = new TypeMapper();
        mapper.registerValueObjectType(DummyAddress.class);
        mapper.registerValueObjectType(DummyUserDetails.class);
        entityFactory = new FieldBasedEntityFactory(mapper);
    }
}
