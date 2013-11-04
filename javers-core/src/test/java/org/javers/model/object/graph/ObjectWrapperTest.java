package org.javers.model.object.graph;

import org.javers.core.model.DummyUser;
import org.javers.model.mapping.Entity;
import org.javers.model.mapping.EntityFactory;
import org.javers.test.assertion.Assertions;

import static org.javers.test.builder.DummyUserBuilder.dummyUser;

import org.javers.test.builder.DummyUserBuilder;
import org.testng.annotations.Test;

/**
 * @author bartosz walacik
 */
public abstract class ObjectWrapperTest {

    protected EntityFactory entityFactory;

    @Test
    public void shouldHoldEntityReference() {
        //given
        DummyUser cdo = dummyUser().build();
        Entity entity = entityFactory.create(DummyUser.class);

        //when
        ObjectWrapper wrapper = new ObjectWrapper(cdo, entity);

        //then
        Assertions.assertThat(wrapper.getEntity()).isSameAs(entity);
    }

    @Test
    public void shouldHoldCdoReference() {
        //given
        DummyUser cdo = dummyUser().build();
        Entity entity = entityFactory.create(DummyUser.class);

        //when
        ObjectWrapper wrapper = new ObjectWrapper(cdo, entity);

        //then
        Assertions.assertThat(wrapper.getCdo()).isSameAs(cdo);
    }

    @Test
    public void shouldReturnCdoId() {
        //given
        DummyUser cdo = dummyUser().withName("Mad Kaz").build();
        Entity entity = entityFactory.create(DummyUser.class);

        //when
        ObjectWrapper wrapper = new ObjectWrapper(cdo, entity);

        //then
        Assertions.assertThat(wrapper.getCdoId()).isEqualTo("Mad Kaz");
    }

    @Test
    public void shouldBeEqualByIdValueAndEntityClass() {
        //given
        ObjectWrapper first = new ObjectWrapper(new DummyUser("Mad Kax"), entityFactory.create(DummyUser.class));
        ObjectWrapper second = new ObjectWrapper(new DummyUser("Mad Kax"), entityFactory.create(DummyUser.class));

        //when + then
        Assertions.assertThat(first.hashCode()).isEqualTo(second.hashCode());
        Assertions.assertThat(first).isEqualTo(second);
    }

    @Test
    public void shouldNotBeEqualWithDifferentIdValue() {
        //given
        ObjectWrapper first = new ObjectWrapper(new DummyUser("stach"), entityFactory.create(DummyUser.class));
        ObjectWrapper second = new ObjectWrapper(new DummyUser("Mad Kax 1"), entityFactory.create(DummyUser.class));

        //when + then
        Assertions.assertThat(first).isNotEqualTo(second);
    }

    @Test
    public void shouldHaveReflexiveEqualsMethod() {
        // given
        ObjectWrapper objectWrapper = new ObjectWrapper(new DummyUser("Mad Kax"), entityFactory.create(DummyUser.class));

        //when + then
        Assertions.assertThat(objectWrapper).isEqualTo(objectWrapper);
    }

    @Test
    public void shouldHaveSymmetricAndTransitiveEqualsMethod() {
        //given
        ObjectWrapper first = new ObjectWrapper(new DummyUser("Mad Kax"), entityFactory.create(DummyUser.class));
        ObjectWrapper second = new ObjectWrapper(new DummyUser("Mad Kax"), entityFactory.create(DummyUser.class));
        ObjectWrapper third = new ObjectWrapper(new DummyUser("Mad Kax"), entityFactory.create(DummyUser.class));

        //when + then
        Assertions.assertThat(first).isEqualTo(second);
        Assertions.assertThat(second).isEqualTo(third);
        Assertions.assertThat(first).isEqualTo(third);
    }

    @Test
    public void shouldReturnFalseWhenEqualsMethodHasNullArg() {
        //given
        ObjectWrapper first = new ObjectWrapper(new DummyUser("Mad Kax"), entityFactory.create(DummyUser.class));

        //when + then
        Assertions.assertThat(first).isNotEqualTo(null);
    }
}
