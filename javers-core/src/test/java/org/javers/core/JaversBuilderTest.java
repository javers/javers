package org.javers.core;


import org.fest.assertions.api.Assertions;
import org.javers.core.model.DummyNetworkAddress;
import org.javers.model.mapping.BeanBasedPropertyScanner;
import org.javers.model.mapping.EntityManager;
import org.javers.model.mapping.FieldBasedPropertyScanner;
import org.javers.model.mapping.PropertyScanner;
import org.javers.model.mapping.type.TypeMapper;
import org.junit.Test;

import javax.persistence.Id;

import static org.javers.core.JaversBuilder.javers;
import static org.javers.test.assertion.Assertions.assertThat;

/**
 * [Integration Test]
 *
 * @author bartosz walacik
 */
public class JaversBuilderTest {

    @Test
    public void shouldLoadDefaultPropertiesFile() {
        //given
        JaversBuilder javersBuilder = javers();

        //when
        javersBuilder.build();

        //then
        Assertions.assertThat(javersBuilder.getContainerComponent(PropertyScanner.class)).isInstanceOf(FieldBasedPropertyScanner.class);
    }

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
        @Id
        private int id;
        private DummyEntity parent;

    }
}
