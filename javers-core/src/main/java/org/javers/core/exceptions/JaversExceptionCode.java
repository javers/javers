package org.javers.core.exceptions;

/**
 * Enums with all JaVers errors codes
 *
 * @author Pawel Cierpiatka <pawel.cierpiatka@gmail.com>
 */
public enum JaversExceptionCode {

    /**
     * Class is not defined in JaVers configuration.
     */
    CLASS_NOT_MANAGED("Class %s is not managed! You should add this class to your JaVers configuration!");

    private String message;

    private JaversExceptionCode(String message) {
        this.message = message;
    }

    /**
     * Error description and possibly solution hints.
     */
    public String getMessage() {
        return message;
    }
}
