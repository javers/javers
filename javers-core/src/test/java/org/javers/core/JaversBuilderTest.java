package org.javers.core;

import org.testng.annotations.Test;

import static org.javers.test.assertion.Assertions.assertThat;

/**
 * @author bartosz walacik
 */
public class JaversBuilderTest {

    @Test
    public void shouldCreateJavers() throws Exception {
        //given
        JaversBuilder builder = new JaversBuilder();

        //when
        Javers javers = builder.build();

        assertThat(javers).isNotNull();
    }

}
