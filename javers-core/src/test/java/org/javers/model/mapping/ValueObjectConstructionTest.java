package org.javers.model.mapping;

import org.javers.core.model.DummyAddress;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * @author pawel szymczyk
 */
public class ValueObjectConstructionTest {

    private ValueObjectFactory factory;

    @Before
    public void setUp() throws Throwable {
        factory = new ValueObjectFactory();
    }

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
