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

    @Test
    public void shouldHoldReferenceToSourceClass() {
        //when
        ValueObject<DummyAddress> valueObject = new ValueObject(DummyAddress.class);

        //then
        assertThat(valueObject.getSourceClass()).isSameAs(DummyAddress.class);
    }
}
