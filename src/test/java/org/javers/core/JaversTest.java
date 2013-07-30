package org.javers.core;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.javers.core.exceptions.JaversException;
import static org.fest.assertions.api.Assertions.*;
import static com.googlecode.catchexception.CatchException.*;


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
        assertThat(caughtException()).isInstanceOf(JaversException.class);

    }

    private static class NotManagedClass { };
}