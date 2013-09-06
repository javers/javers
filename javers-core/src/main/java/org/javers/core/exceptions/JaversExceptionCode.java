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
    TYPE_NOT_MAPPED ("Property Type '%s' is not mapped. Implement UserType and add it to your JaVers configuration.") ,
    ENTITY_WITHOUT_ID ("Class '%s' has no Id property. Use @Id annotation to mark unique Entity identifier"),
    //TODO better exception messages - User Friendly!
    ENTITY_MANAGER_NOT_INITIALIZED("EntityManager is not initialized properly. You should call buildManagedClasses()");

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
