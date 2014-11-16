package org.javers.common.exception;

/**
 * Enums with all Javers errors codes
 *
 * @author Pawel Cierpiatka <pawel.cierpiatka@gmail.com>
 */
public enum  JaversExceptionCode {

    CLASS_EXTRACTION_ERROR(JaversException.BOOTSTRAP_ERROR + "Don't know how to extract Class from type '%s'.") ,

    ENTITY_WITHOUT_ID (JaversException.BOOTSTRAP_ERROR + "Class '%s' has no Id property. Use @Id annotation to mark unique Entity identifier"),
    ENTITY_INSTANCE_WITH_NULL_ID(JaversException.RUNTIME_ERROR + "Found Entity instance of class '%s' with null id"),
    NOT_INSTANCE_OF(JaversException.BOOTSTRAP_ERROR + "expected instance of '%s', got instance of '%s'"),

    UNDEFINED_PROPERTY(JaversException.BOOTSTRAP_ERROR + "undefined mandatory property '%s'. Define it in your classpath:javers.properties"),

    MALFORMED_PROPERTY(JaversException.BOOTSTRAP_ERROR + "unwrap '%s' is invalid for property '%s'. Fix it in your classpath:javers.properties"),

    CLASSPATH_RESOURCE_NOT_FOUND(JaversException.BOOTSTRAP_ERROR + "classpath resource '%s' could not be found"),

    ALREADY_BUILT(JaversException.BOOTSTRAP_ERROR + "instance already built, each AbstractJaversBuilder may produce only one target instance"),

    CONTAINER_NOT_READY(JaversException.BOOTSTRAP_ERROR +"pico container is not ready"),

    AFFECTED_CDO_IS_NOT_AVAILABLE(JaversException.RUNTIME_ERROR +"affected cdo is not available, you can access it only for freshly generated diffs"),

    NOT_IMPLEMENTED(JaversException.RUNTIME_ERROR + "not implemented"),

    SNAPSHOT_NOT_FOUND(JaversException.RUNTIME_ERROR + "snapshot '%s' not found in JaversRepository"),

    SET_OF_VO_DIFF_NOT_IMPLEMENTED(JaversException.RUNTIME_ERROR + "diff for Set of ValueObjects is not supported"),

    GENERIC_TYPE_NOT_PARAMETRIZED(JaversException.RUNTIME_ERROR + "expected actual Class arguments in type '%s'. Javers needs to know what kind of content is stored in your collections. Try at least <Object>"),

    //graph & snapshot
    VALUE_OBJECT_IS_NOT_SUPPORTED_AS_MAP_KEY(JaversException.RUNTIME_ERROR + "found Value Object '%s' on KEY position in Map property of '%s'. Please change this class mapping to Value or Entity"),

    SNAPSHOT_STATE_VIOLATION(JaversException.RUNTIME_ERROR + "snapshots are immutable"),

    PROPERTY_NOT_FOUND(JaversException.RUNTIME_ERROR +"property '%s' not found in class '%s'"),

    MANAGED_CLASS_MAPPING_ERROR(JaversException.RUNTIME_ERROR+"given javaClass '%s' is mapped to %s, expected %s"),

    MALFORMED_CHANGE_TYPE_FIELD(JaversException.RUNTIME_ERROR+"no such Change type - '%s'"),

    MALFORMED_ENTRY_CHANGE_TYPE_FIELD(JaversException.RUNTIME_ERROR+"no such EntryChange type - '%s'"),

    CLASS_NOT_MANAGED(JaversException.RUNTIME_ERROR+"given javaClass '%s' is mapped to %s, ManagedType expected"),

    CLASS_NOT_FOUND(JaversException.RUNTIME_ERROR+"class not found - '%s'") ,

    CANNOT_EXTRACT_CHILD_VALUE_OBJECT (JaversException.RUNTIME_ERROR+"error while extracting child ValueObject from '%s'" +
            ", invalid property type, expected ValueObjectType or ContainerType<ValueObjectType>, got '%s'"),

    CANNOT_PARSE_COMMIT_ID(JaversException.RUNTIME_ERROR+"cannot parse given value {'$s'} to CommitId. " +
            "CommitId should consists of two parts : majorId.minorId e.g. 1.0")

    ;

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
