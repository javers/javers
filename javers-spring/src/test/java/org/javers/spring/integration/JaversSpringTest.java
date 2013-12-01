package org.javers.spring.integration;

import org.javers.core.Javers;
import org.javers.core.model.DummyUser;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * @author Pawel Cierpiatka
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:applicationContext-Test.xml"})
public class JaversSpringTest {

    @Autowired
    private Javers javers;

    @Test
    public void shouldAutowiredJavers() {

        //then
        assertThat(javers).isNotNull();
        assertThat(javers.isManaged(DummyUser.class)).isTrue();
    }


}
