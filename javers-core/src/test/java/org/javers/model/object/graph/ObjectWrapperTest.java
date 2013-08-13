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
public abstract class ObjectWrapperTest {

    protected EntityFactory entityFactory;

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
    public void shouldHoldCdoReference() {
        //given
        DummyUser cdo = new DummyUser();
        Entity entity = entityFactory.create(DummyUser.class);

        //when
        ObjectWrapper wrapper = new ObjectWrapper(cdo,entity);

        //then
        Assertions.assertThat(wrapper.getCdo()).isSameAs(cdo);
    }


    @Test
    public void shouldReturnCdoId() {
        //given
        DummyUser cdo = new DummyUser("Mad Kaz");
        Entity entity = entityFactory.create(DummyUser.class);

        //when
        ObjectWrapper wrapper = new ObjectWrapper(cdo,entity);

        //then
        Assertions.assertThat(wrapper.getCdoId()).isEqualTo("Mad Kaz");
    }
}
