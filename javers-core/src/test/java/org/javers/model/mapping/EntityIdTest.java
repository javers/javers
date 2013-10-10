package org.javers.model.mapping;

import org.javers.core.exceptions.JaversException;
import org.javers.core.exceptions.JaversExceptionCode;
import org.javers.core.model.DummyAddress;
import org.javers.core.model.DummyUser;
import org.testng.annotations.Test;

import static com.googlecode.catchexception.CatchException.caughtException;
import static com.googlecode.catchexception.apis.CatchExceptionBdd.when;
import static org.fest.assertions.api.Assertions.assertThat;

/**
 * @author bartosz walacik
 */
public abstract class EntityIdTest {
    protected EntityFactory entityFactory;

    @Test
    public void shouldScanIdProperty() {
        //when
        Entity entity = entityFactory.createEntity(DummyUser.class);

        //then
        EntityAssert.assertThat(entity).hasProperty("name").isId();
    }

    @Test
    public void shouldThrowExceptionWhenEntityWithoutId() {
        when(entityFactory).createEntity(DummyAddress.class);

        //then
        assertThat(caughtException()).isInstanceOf(JaversException.class)
                                     .hasMessageContaining(JaversExceptionCode.ENTITY_WITHOUT_ID.name());
    }
}
