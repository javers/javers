package org.javers.core.model;

import org.javers.core.Javers;
import org.javers.core.JaversFactory;
import org.javers.model.Entity;
import static org.javers.test.assertion.Assertions.*;
import org.testng.annotations.Test;

/**
 * @author bartosz walacik
 */
public class FirstTest{

    @Test
    public void shouldCreateModelForBasicProperties() {
        //given
        Javers javers = JaversFactory.javers().managingClasses(DummyUser.class).build();

        //when
        Entity entity = javers.getByClass(DummyUser.class);

        //then
        assertThat(entity).hasSourceClass(DummyUser.class);

    }
}
