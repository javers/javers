package org.javers.model.mapping;

import org.javers.core.model.DummyAddress;
import org.junit.Test;

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
}
