package org.javers.model.mapping;

import org.javers.core.model.DummyUser;
import org.testng.annotations.Test;


/**
 * @author bartosz walacik
 */
public abstract class EntityConstructionTest {
    protected EntityFactory entityFactory;

    @Test
    public void shouldHoldReferenceToSourceClass() {
        //when
        Entity entity = entityFactory.createEntity(DummyUser.class);

        //then
        EntityAssert.assertThat(entity).hasSourceClass(DummyUser.class);
    }
}
