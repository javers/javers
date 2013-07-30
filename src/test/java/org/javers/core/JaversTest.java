package org.javers.core;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.javers.core.exceptions.JaversException;
import static com.googlecode.catchexception.CatchException.*;
import org.javers.core.exceptions.JaversExceptionCode;
import org.javers.model.Entity;
import static org.javers.test.assertion.Assertions.*;


/**
 *
 * @author Adam Dubiel <adam.dubiel@allegro.pl>
 */
public class JaversTest {

    private Javers javers;

    @BeforeMethod
    public void setUp() {
        javers = new Javers();
    }

    @Test
    public void shouldThrowExceptionIfEntityIsNotManagedWhenTryingToGetIt() {
        // given - none
        // when
        catchException(javers).getByClass(NotManagedClass.class);

        // then
        assertThat((JaversException) caughtException()).hasCode(JaversExceptionCode.CLASS_NOT_MANAGED);
    }

    @Test
    public void shouldReturnEntityModelForManagedClassAfterMakingItManaged() {
        // given
        javers.manage(ManagedClass.class);

        // when
        Entity entity = javers.getByClass(ManagedClass.class);

        // then
        assertThat(entity).isNotNull();
    }

    private static class NotManagedClass { };

    private static class ManagedClass { };
}