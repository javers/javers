package org.javers.test.assertion;

import org.javers.core.exceptions.JaversException;
import org.javers.model.Entity;

/**
 *
 * @author Adam Dubiel <adam.dubiel@gmail.com>
 */
public class Assertions extends org.fest.assertions.api.Assertions {

    public static EntityAssert assertThat(Entity actual) {
        return EntityAssert.assertThat(actual);
    }

    public static JaversExceptionAssert assertThat(JaversException actual) {
        return JaversExceptionAssert.assertThat(actual);
    }
}
