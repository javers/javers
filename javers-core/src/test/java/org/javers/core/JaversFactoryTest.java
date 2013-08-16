package org.javers.core;

import org.javers.test.assertion.Assertions;
import org.testng.annotations.Test;

import static org.javers.test.assertion.Assertions.*;

/**
 * @author bartosz walacik
 */
public class JaversFactoryTest {
    @Test
    public void shouldCreateJavers() throws Exception {
        //given
        JaversFactory factory = new JaversFactory();

        //when
        Javers javers = factory.build();

        assertThat(javers).isNotNull();
    }
}
