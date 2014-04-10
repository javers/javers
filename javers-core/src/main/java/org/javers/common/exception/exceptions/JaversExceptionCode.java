package org.javers.common.exception.exceptions;

import static org.javers.common.exception.exceptions.JaversException.BOOTSTRAP_ERROR;
import static org.javers.common.exception.exceptions.JaversException.RUNTIME_ERROR;

/**
 * Enums with all Javers errors codes
 *
 * @author Pawel Cierpiatka <pawel.cierpiatka@gmail.com>
 */
public enum  JaversExceptionCode {

    CLASS_EXTRACTION_ERROR(BOOTSTRAP_ERROR + "Don't know how to extract Class from type '%s'.") ,

    ENTITY_WITHOUT_ID (BOOTSTRAP_ERROR + "Class '%s' has no Id property. Use @Id annotation to mark unique Entity identifier"),
    ENTITY_INSTANCE_WITH_NULL_ID(RUNTIME_ERROR + "Found Entity instance of class '%s' with null id"),
    NOT_INSTANCE_OF(BOOTSTRAP_ERROR + "expected instance of '%s', got instance of '%s'"),

    UNDEFINED_PROPERTY(BOOTSTRAP_ERROR + "undefined mandatory property '%s'. Define it in your classpath:javers.properties"),

    MALFORMED_PROPERTY(BOOTSTRAP_ERROR + "unwrap '%s' is invalid for property '%s'. Fix it in your classpath:javers.properties"),

    CLASSPATH_RESOURCE_NOT_FOUND(BOOTSTRAP_ERROR + "classpath resource '%s' could not be found"),

    ALREADY_BUILT(BOOTSTRAP_ERROR + "instance already built, each AbstractJaversBuilder may produce only one target instance"),

    CONTAINER_NOT_READY(BOOTSTRAP_ERROR +"pico container is not ready"),

    AFFECTED_CDO_IS_NOT_AVAILABLE(RUNTIME_ERROR +"affected cdo is not available, you can access it only for freshly generated diffs"),

    NOT_IMPLEMENTED(RUNTIME_ERROR + "not implemented"),

    SNAPSHOT_NOT_FOUND(RUNTIME_ERROR + "snapshot '%s' not found in JaversRepository"),

    DIFF_NOT_IMPLEMENTED(RUNTIME_ERROR + "not implemented Enumerable content type '%s'"),

    GENERIC_TYPE_NOT_PARAMETRIZED(RUNTIME_ERROR + "expected actual Class arguments in type '%s'. Javers needs to know what kind of content is stored in your collections. Try at least <Object>"),

    //graph & snapshot
    VALUE_OBJECT_IS_NOT_SUPPORTED_AS_MAP_KEY(RUNTIME_ERROR + "found Value Object '%s' on KEY position in Map property of '%s'. Please change this class mapping to Value or Entity"),

    SNAPSHOT_STATE_VIOLATION(RUNTIME_ERROR + "snapshots are immutable"),

    PROPERTY_NOT_FOUND(RUNTIME_ERROR +"property '%s' not found in class '%s'");

    private final String message;

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
