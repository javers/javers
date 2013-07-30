package org.javers.core.model;

import com.sun.corba.se.impl.orb.ParserTable;
import org.javers.core.Javers;
import org.javers.core.JaversFactory;
import org.javers.model.Entity;
import org.javers.test.assertion.Assertions;
import org.junit.Test;

import java.util.ArrayList;

/**
 * @author bartosz walacik
 */
public class FirstTest{

    @Test
    public void shouldCreateModelForBasicProperties() {
        //given
        Javers javers = JaversFactory.create(Lists.newArrayList(DummyUser.class));

        //when
        Entity entity = javers.getByClass(DummyUser.class);

        //then
        Assertions.assertThat(entity.getSourceClass()).isEqualTo(DummyUser.class);

    }
}
