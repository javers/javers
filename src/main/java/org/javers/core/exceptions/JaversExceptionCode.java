package org.javers.core.exceptions;

/**
 * Enums contains all errors.
 * @author bartosz walacik
 */
public enum JaversExceptionCode {

    /**
     * Class is not defined in javers configuration.
     */
    CLASS_NOT_MANAGED("Class %s is not managed!","You should add this class Javers bla bla bla full description here!");
    private String errorCode;
    private String message;

    private JaversExceptionCode(String errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }

    /**
     * Returns exception code.
     */
    public String getErrorCode(Class<?> clazz) {
        return String.format(errorCode, clazz.getSimpleName());
    }

    /**
     * Returns definitions and examples of errors.
     */
    public String getMessage() {
        return message;
    }
}
