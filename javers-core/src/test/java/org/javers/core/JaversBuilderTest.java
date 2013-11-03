package org.javers.core;

import org.javers.core.model.DummyAddress;
import org.javers.core.model.DummyNetworkAddress;
import org.javers.core.model.DummyUser;
import org.javers.core.model.DummyUserDetails;
import org.javers.test.assertion.Assertions;
import org.joda.time.LocalDateTime;
import org.testng.annotations.Test;

import javax.persistence.Id;

import static org.javers.core.JaversBuilder.javers;
import static org.javers.test.assertion.Assertions.assertThat;

/**
 * @author bartosz walacik
 */
public class JaversBuilderTest {

    @Test
    public void shouldManageEntity() {
        //when
        Javers javers = javers().registerEntity(DummyEntity.class).build();

        //then
        assertThat(javers.isManaged(DummyEntity.class)).isTrue();
    }

    @Test
    public void shouldManageValueObject() {
        //when
        Javers javers = javers().registerValueObject(DummyNetworkAddress.class).build();

        //then
        assertThat(javers.isManaged(DummyNetworkAddress.class)).isTrue();
    }

    @Test
    public void shouldCreateJavers() throws Exception {
        //when
        Javers javers = javers().build();

        //then
        assertThat(javers).isNotNull();
    }

    @Test
    public void shouldCreateMultipleJaversInstances() {
        //when
        Javers javers1 = javers().build();
        Javers javers2 = javers().build();

        //then
        assertThat(javers1).isNotSameAs(javers2);
    }


    private class DummyEntity {
        private int id;
        private DummyEntity parent;

        @Id
        private int getId() {
            return id;
        }

        private DummyEntity getParent() {
            return parent;
        }
    }
}
