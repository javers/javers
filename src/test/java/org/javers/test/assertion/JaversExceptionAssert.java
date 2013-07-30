package org.javers.test.assertion;

import org.fest.assertions.api.AbstractAssert;
import org.javers.core.exceptions.JaversException;
import org.javers.core.exceptions.JaversExceptionCode;

/**
 *
 * @author Adam Dubiel <adam.dubiel@allegro.pl>
 */
public class JaversExceptionAssert extends AbstractAssert<JaversExceptionAssert, JaversException> {

    private JaversExceptionAssert(JaversException actual) {
        super(actual, JaversExceptionAssert.class);
    }

    public static JaversExceptionAssert assertThat(JaversException actual) {
        return new JaversExceptionAssert(actual);
    }

    public JaversExceptionAssert hasCode(JaversExceptionCode code) {
        Assertions.assertThat(actual.getCode()).isEqualTo(code);
        return this;
    }
}
