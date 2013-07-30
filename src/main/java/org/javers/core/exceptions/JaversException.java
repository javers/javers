package org.javers.core.exceptions;

/**
 * Basic javers exception.
 *
 * @author bartosz walacik
 */
@SuppressWarnings("serial")
public class JaversException extends RuntimeException {

    private JaversExceptionCode code;

    public JaversException(JaversExceptionCode code, Object... argumants) {
        super(code.name() + " " + String.format(code.getMessage(), argumants));
        this.code = code;
    }

    public JaversExceptionCode getCode() {
        return code;
    }
}
