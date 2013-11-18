package org.javers.common.validation;

import org.junit.Test;

import static com.googlecode.catchexception.CatchException.caughtException;
import static com.googlecode.catchexception.apis.CatchExceptionBdd.when;
/**
 * @author bartosz walacik
 */
public class ValidateTest {

    @Test(expected = IllegalArgumentException.class)
    public void shouldCheckAllVarargs() {
        Validate.argumentsAreNotNull(1, null);
    }
}
