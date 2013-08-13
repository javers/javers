package org.javers.model.mapping;

import org.javers.core.model.DummyUser;
import org.javers.model.mapping.type.TypeMapper;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.persistence.Id;

/**
 * @author bartosz walacik
 */
@Test
public class EntityFromBeanConstructionTest extends EntityConstructionTest {


    @BeforeMethod
    public void setUp() {
        entityFactory = new BeanBasedEntityFactory(new TypeMapper());
    }

    @Test
    public void shouldScanPrivateGetters() {
        //when
        Entity entity = entityFactory.create(ManagedClass.class);

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
