package org.javers.core.exceptions;

/**
 * Enums with all Javers errors codes
 *
 * @author Pawel Cierpiatka <pawel.cierpiatka@gmail.com>
 */
public enum JaversExceptionCode {

    /**
     * Class is not defined in Javers configuration.
     */
    CLASS_NOT_MANAGED("Class '%s' is not managed. Add this class to your JaVers configuration."),
    TYPE_NOT_MAPPED ("Property Type '%s' is not mapped. Implement UserType and add it to your JaVers configuration.")
    ;

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
