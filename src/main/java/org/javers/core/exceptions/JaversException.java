package org.javers.core.exceptions;

/**
 * Basic javers exception.
 * @author bartosz walacik
 */
@SuppressWarnings("serial")
public class JaversException extends RuntimeException {

    private JaversExceptionCode code;

    private String errorMessage;

    public JaversException(JaversExceptionCode code, String errorMessage) {
        super(code + " " + errorMessage);
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
