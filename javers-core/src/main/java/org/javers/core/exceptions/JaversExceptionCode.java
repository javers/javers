package org.javers.core.exceptions;

import static org.javers.core.exceptions.JaversException.*;

/**
 * Enums with all Javers errors codes
 *
 * @author Pawel Cierpiatka <pawel.cierpiatka@gmail.com>
 */
public enum JaversExceptionCode {
    /**
     * Class is not defined in Javers configuration.
     */
    CLASS_NOT_MANAGED(RUNTIME_ERROR + "Class '%s' is not managed. Add this class to your JaVers configuration."),
    TYPE_NOT_MAPPED (BOOTSTRAP_ERROR + "Property Type '%s' is not mapped. Implement UserType and add it to your JaVers configuration.") ,
    ENTITY_WITHOUT_ID (BOOTSTRAP_ERROR + "Class '%s' has no Id property. Use @Id annotation to mark unique Entity identifier"),
    //TODO better exception messages - User Friendly!
    ENTITY_MANAGER_NOT_INITIALIZED(BOOTSTRAP_ERROR + "EntityManager is not initialized properly. You should call buildManagedClasses()"),
    UNDEFINED_PROPERTY(BOOTSTRAP_ERROR + "undefined mandatory property '%s'. Define it in your classpath:javers.properties"),
    MALFORMED_PROPERTY(BOOTSTRAP_ERROR + "value '%s' is invalid for property '%s'. Fix it in your classpath:javers.properties"),
    CLASSPATH_RESOURCE_NOT_FOUND(BOOTSTRAP_ERROR + "classpath resource '%s' could not be found"),
    JAVERS_ALREADY_BUILT(BOOTSTRAP_ERROR + "JaVers instance already built, each JaversBuilder may produce only one JaVers instance");


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
