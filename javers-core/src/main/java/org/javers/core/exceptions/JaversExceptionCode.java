package org.javers.core.exceptions;

import org.javers.core.JaversBuilder;

import static org.javers.core.exceptions.JaversException.*;

/**
 * Enums with all Javers errors codes
 *
 * @author Pawel Cierpiatka <pawel.cierpiatka@gmail.com>
 */
public enum  JaversExceptionCode {

    /**
     * Class is not defined in Javers configuration.
     */
    CLASS_NOT_MANAGED(RUNTIME_ERROR + "Class '%s' is not managed. Add this class to your JaVers configuration."),

    UNEXPECTED_VALUE_OBJECT(RUNTIME_ERROR + "Can't start building graph from Value Object '%s', expected Entity instance."),

    @Deprecated
    TYPE_NOT_MAPPED (BOOTSTRAP_ERROR + "Type '%s' is not mapped and not assignable from any of already mapped types.\n"+
                                       "Register it via JaversBuilder.registerEntity() or JaversBuilder.registerValueObject()") ,

    CLASS_EXTRACTION_ERROR(BOOTSTRAP_ERROR + "Don't know how to extract Class from type '%s'.") ,

    ENTITY_WITHOUT_ID (BOOTSTRAP_ERROR + "Class '%s' has no Id property. Use @Id annotation to mark unique Entity identifier"),
    ENTITY_INSTANCE_WITH_NULL_ID(RUNTIME_ERROR + "Found Entity instance of class '%s' with null id"),

    ENTITY_MANAGER_NOT_INITIALIZED(BOOTSTRAP_ERROR + "EntityManager is not initialized properly. You should call buildManagedClasses()"),

    UNDEFINED_PROPERTY(BOOTSTRAP_ERROR + "undefined mandatory property '%s'. Define it in your classpath:javers.properties"),

    MALFORMED_PROPERTY(BOOTSTRAP_ERROR + "value '%s' is invalid for property '%s'. Fix it in your classpath:javers.properties"),

    CLASSPATH_RESOURCE_NOT_FOUND(BOOTSTRAP_ERROR + "classpath resource '%s' could not be found"),

    ALREADY_BUILT(BOOTSTRAP_ERROR + "instance already built, each AbstractJaversBuilder may produce only one target instance"),

    CONTAINER_NOT_READY(BOOTSTRAP_ERROR +"pico container is not ready"),

    PROPERTY_NOT_FOUND(BOOTSTRAP_ERROR +"property '%s' not found in class '%s'");

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
