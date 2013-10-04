package org.javers.core;

import org.testng.annotations.Test;

import static org.javers.test.assertion.Assertions.assertThat;

/**
 * @author bartosz walacik
 */
public class JaversBuilderTest {

    @Test
    public void shouldCreateJavers() throws Exception {
        //when
        Javers javers = new JaversBuilder().build();

        //then
        assertThat(javers).isNotNull();
    }

    @Test
    public void shouldCreateMultipleJaversInstances() {
        //when
        Javers javers1 = new JaversBuilder().build();
        Javers javers2 = new JaversBuilder().build();

        //then
        assertThat(javers1).isNotSameAs(javers2);
    }

}
