package org.javers.test.assertion;

import org.javers.core.exceptions.JaversException;

/**
 *
 * @author Adam Dubiel <adam.dubiel@allegro.pl>
 */
public class Assertions extends org.fest.assertions.api.Assertions {

    public static JaversExceptionAssert assertThat(JaversException actual) {
        return JaversExceptionAssert.assertThat(actual);
    }

}
