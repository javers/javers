package org.javers.core;


import org.junit.Test;

import static org.javers.test.assertion.Assertions.assertThat;

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
