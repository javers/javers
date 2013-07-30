package org.javers.core.exceptions;

/**
 * Enums contains all errors.
 * @author bartosz walacik
 */
public enum JaversExceptionCode {

    /**
     * Class is not defined in javers configuration.
     */
    CLASS_NOT_MANAGED("Class %s is not managed! You should add this class Javers bla bla bla full description here!");
    private String message;

    private JaversExceptionCode(String message) {
        this.message = message;
    }

    /**
     * Returns definitions and examples of errors.
     */
    public String getMessage() {
        return message;
    }
}
