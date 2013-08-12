package org.javers.model.object.graph;

import org.javers.core.model.DummyUser;
import org.javers.model.mapping.BeanBasedEntityFactory;
import org.javers.model.mapping.Entity;
import org.javers.model.mapping.EntityFactory;
import org.javers.model.mapping.type.TypeMapper;
import org.javers.test.assertion.Assertions;
import org.mockito.Mockito;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author bartosz walacik
 */
@Test
public class ObjectWrapperTest {

    private EntityFactory entityFactory;
    private ObjectWrapper objectWrapper;

    @BeforeMethod
    public void setUp() {
        entityFactory = new BeanBasedEntityFactory(new TypeMapper());
    }

    @Test
    public void shouldHoldEntityReference() {
        //given
        DummyUser cdo = new DummyUser();
        Entity entity = entityFactory.create(DummyUser.class);

        //when
        ObjectWrapper wrapper = new ObjectWrapper(cdo,entity);

        //then
        Assertions.assertThat(wrapper.getEntity()).isSameAs(entity);
    }


    @Test
    public void shouldReturnObjectId() {

    }
}
