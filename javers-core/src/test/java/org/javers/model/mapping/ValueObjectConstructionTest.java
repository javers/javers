package org.javers.model.mapping;

import org.javers.core.model.DummyAddress;
import org.javers.core.model.DummyNetworkAddress;
import org.javers.core.model.DummyUser;
import org.javers.model.mapping.type.EntityReferenceType;
import org.javers.model.mapping.type.ValueObjectType;
import org.testng.annotations.Test;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * @author pawel szymczyk
 */
public abstract class ValueObjectConstructionTest {

    protected ValueObjectFactory factory;

    @Test
    public void shouldCreateValueObject() throws Throwable {
        //when
        ValueObject<DummyAddress> valueObject = factory.create(DummyAddress.class);

        //then
        assertThat(valueObject).isNotNull();
    }

    @Test
    public void shouldHoldReferenceToSourceClass() {
        //when
        ValueObject<DummyAddress> valueObject = factory.create(DummyAddress.class);

        //then
        assertThat(valueObject.getSourceClass()).isSameAs(DummyAddress.class);
    }

    @Test
    public void shouldScanAllProperties() {
        //when
        ValueObject<DummyAddress> valueObject = factory.create(DummyAddress.class);

        //then
        assertThat(valueObject.getProperties()).hasSize(5);
    }

    @Test
    public void shouldScanReferenceToOtherValueObject() {
        //when
        ValueObject<DummyAddress> valueObject = factory.create(DummyAddress.class);

        //TODO refactor
        //then
        assertThat(valueObject.getProperty("networkAddress"))
                .isNotNull();
        assertThat(valueObject.getProperty("networkAddress").getType())
                .isInstanceOf(ValueObjectType.class);
        assertThat(valueObject.getProperty("networkAddress").getType().getBaseJavaType())
                .isEqualTo(DummyNetworkAddress.class);
    }

    @Test
    public void shouldScanInheritedProperty() {
        //when
        ValueObject<DummyAddress> valueObject = factory.create(DummyAddress.class);

        //then
        assertThat(valueObject.getProperty("inheritedInt")).isNotNull();
    }

    @Test
    public void shouldNotScanTransientProperty() {
        //when
        ValueObject<DummyAddress> valueObject = factory.create(DummyAddress.class);

        //then
        assertThat(valueObject.getProperty("someTransientField")).isNull();
    }
}
