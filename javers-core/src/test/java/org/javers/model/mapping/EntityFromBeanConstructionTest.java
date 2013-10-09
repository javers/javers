package org.javers.model.mapping;

import org.javers.core.model.DummyAddress;
import org.javers.core.model.DummyUser;
import org.javers.core.model.DummyUserDetails;
import org.javers.model.mapping.type.TypeMapper;
import org.javers.model.mapping.type.ValueObjectType;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.persistence.Id;

import static org.javers.test.builder.TypeMapperTestBuilder.typeMapper;

/**
 * @author bartosz walacik
 */
public class EntityFromBeanConstructionTest extends EntityConstructionTest {

    @BeforeMethod
    public void setUp() {
        TypeMapper typeMapper = typeMapper().registerAllDummyTypes().build();
        entityFactory = new BeanBasedEntityFactory(typeMapper);
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

    @Test
    public void shouldScanValueObjectProperty() {
        //when
        Entity entity = entityFactory.createEntity(DummyUserDetails.class);

        //then
        EntityAssert.assertThat(entity).hasProperty("dummyAddress")
                .hasJaversType(ValueObjectType.class);
    }
}
