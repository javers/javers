package org.javers.model.mapping;

import org.javers.core.model.DummyAddress;
import org.javers.core.model.DummyUserDetails;
import org.javers.model.mapping.type.TypeMapper;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.persistence.Id;

/**
 * @author bartosz walacik
 */
public class EntityFromBeanConstructionTest extends EntityConstructionTest {

    @BeforeMethod
    public void setUp() {
        TypeMapper mapper = new TypeMapper();
        mapper.registerValueObjectType(DummyAddress.class);
        mapper.registerValueObjectType(DummyUserDetails.class);
        entityFactory = new BeanBasedEntityFactory(mapper);
    }

    @Test
    public void shouldScanPrivateGetters() {
        //when
        Entity entity = entityFactory.createEntity(ManagedClass.class);

        //then
        EntityAssert.assertThat(entity).hasProperty("id").isId();
    }

    private static class ManagedClass {
        @Id
        private int getId() {
            return 0;
        }
    };

}
