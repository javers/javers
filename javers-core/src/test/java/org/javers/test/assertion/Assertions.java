package org.javers.test.assertion;

import org.javers.core.exceptions.JaversException;
import org.javers.model.object.graph.ObjectNode;

/**
 *
 * @author Adam Dubiel <adam.dubiel@gmail.com>
 */
public class Assertions extends org.fest.assertions.api.Assertions {

    public static JaversExceptionAssert assertThat(JaversException actual) {
        return JaversExceptionAssert.assertThat(actual);
    }

    public static NodeAssert assertThat(ObjectNode actual) {
        return NodeAssert.assertThat(actual);
    }
}
