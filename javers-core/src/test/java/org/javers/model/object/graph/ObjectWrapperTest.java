package org.javers.model.object.graph;

import org.javers.core.exceptions.JaversException;
import org.javers.core.exceptions.JaversExceptionCode;
import org.javers.core.model.DummyUser;
import org.javers.model.domain.Cdo;
import org.javers.model.domain.InstanceId;
import org.javers.core.metamodel.property.Entity;
import org.javers.core.metamodel.property.ManagedClassFactory;
import org.javers.test.assertion.Assertions;
import org.junit.Test;
import org.mockito.Mockito;

import static org.javers.test.assertion.JaversExceptionAssert.assertThat;
import static org.javers.test.builder.DummyUserBuilder.dummyUser;
import static org.junit.Assert.fail;

/**
 * @author bartosz walacik
 */
public abstract class ObjectWrapperTest {

    protected ManagedClassFactory managedClassFactory;

    @Test
    public void shouldHoldEntityReference() {
        //given
        DummyUser cdo = dummyUser().build();
        Entity entity = managedClassFactory.create(DummyUser.class);

        //when
        ObjectWrapper wrapper = new ObjectWrapper(cdo, entity);

        //then
        Assertions.assertThat(wrapper.getManagedClass()).isSameAs(entity);
    }

    @Test
    public void shouldHoldGlobalCdoId() {
        //given
        DummyUser cdo = dummyUser().withName("Mad Kaz").build();
        Entity entity = managedClassFactory.create(DummyUser.class);

        //when
        ObjectWrapper wrapper = new ObjectWrapper(cdo, entity);

        //then
        Assertions.assertThat(wrapper.getGlobalCdoId())
                  .isEqualTo(new InstanceId(cdo, entity));
    } 
    @Test
    public void shouldHoldCdoReference() {
        //given
        DummyUser cdo = dummyUser().build();
        Entity entity = managedClassFactory.create(DummyUser.class);

        //when
        ObjectWrapper wrapper = new ObjectWrapper(cdo, entity);

        //then
        Assertions.assertThat(wrapper.unwrapCdo()).isSameAs(cdo);
    }

    @Test
    public void shouldThrowExceptinWhenEntityWithoutId() {
        //given
        DummyUser cdo = new DummyUser();
        Entity entity = managedClassFactory.create(DummyUser.class);

        //when
        try {
            new ObjectWrapper(cdo, entity);
            fail();  //no CatchExceptionBdd for constructors :(
        }
        catch (JaversException e)          {
            assertThat(e).hasCode(JaversExceptionCode.ENTITY_INSTANCE_WITH_NULL_ID);
        }
   }

    @Test
    public void shouldBeEqualByIdValueAndEntityClass() {
        //given
        ObjectWrapper first = new ObjectWrapper(new DummyUser("Mad Kax"), managedClassFactory.create(DummyUser.class));
        ObjectWrapper second = new ObjectWrapper(new DummyUser("Mad Kax"), managedClassFactory.create(DummyUser.class));

        //when + then
        Assertions.assertThat(first.hashCode()).isEqualTo(second.hashCode());
        Assertions.assertThat(first).isEqualTo(second);
    }

    @Test
    public void shouldNotBeEqualWithDifferentIdValue() {
        //given
        ObjectWrapper first = new ObjectWrapper(new DummyUser("stach"), managedClassFactory.create(DummyUser.class));
        ObjectWrapper second = new ObjectWrapper(new DummyUser("Mad Kax 1"), managedClassFactory.create(DummyUser.class));

        //when + then
        Assertions.assertThat(first).isNotEqualTo(second);
    }

    @Test
    public void shouldHaveReflexiveEqualsMethod() {
        // given
        ObjectWrapper objectWrapper = new ObjectWrapper(new DummyUser("Mad Kax"), managedClassFactory.create(DummyUser.class));

        //when + then
        Assertions.assertThat(objectWrapper).isEqualTo(objectWrapper);
    }

    @Test
    public void shouldHaveSymmetricAndTransitiveEqualsMethod() {
        //given
        ObjectWrapper first = new ObjectWrapper(new DummyUser("Mad Kax"), managedClassFactory.create(DummyUser.class));
        ObjectWrapper second = new ObjectWrapper(new DummyUser("Mad Kax"), managedClassFactory.create(DummyUser.class));
        ObjectWrapper third = new ObjectWrapper(new DummyUser("Mad Kax"), managedClassFactory.create(DummyUser.class));

        //when + then
        Assertions.assertThat(first).isEqualTo(second);
        Assertions.assertThat(second).isEqualTo(third);
        Assertions.assertThat(first).isEqualTo(third);
    }

    @Test
    public void shouldReturnFalseWhenEqualsMethodHasNullArg() {
        //given
        ObjectWrapper first = new ObjectWrapper(new DummyUser("Mad Kax"), managedClassFactory.create(DummyUser.class));

        //when + then
        Assertions.assertThat(first).isNotEqualTo(null);
    }

    @Test
    public void shouldDelegateEqualsAndHashCodeToCdo() {
        //given
        Cdo mockedCdo = Mockito.mock(Cdo.class);
        ObjectWrapper node1 = new ObjectWrapper(mockedCdo);
        ObjectWrapper node2 = new ObjectWrapper(mockedCdo);

        //when + then
        Assertions.assertThat(node1.equals(node2)).isTrue();
        Assertions.assertThat(node1.hashCode()).isEqualTo(mockedCdo.hashCode());
    }
}
