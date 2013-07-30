package org.javers.core.exceptions;

/**
 * Basic javers exception.
 * @author bartosz walacik
 */
public class JaversException extends RuntimeException {

    private String errorMessage;

    public JaversException(String errorCode, String errorMessage) {
        super(errorCode);
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
