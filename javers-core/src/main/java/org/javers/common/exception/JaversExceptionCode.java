package org.javers.common.exception;

/**
 * Enums with all Javers errors codes
 *
 * @author Pawel Cierpiatka <pawel.cierpiatka@gmail.com>
 */
public enum JaversExceptionCode {
    CLASS_EXTRACTION_ERROR(JaversException.BOOTSTRAP_ERROR + "Don't know how to extract Class from type '%s'.") ,

    COMMITTING_TOP_LEVEL_VALUES_NOT_SUPPORTED("Committing top-level %ss like '%s' is not supported. You can commit only Entity or ValueObject instance."),

    COMPARING_TOP_LEVEL_VALUES_NOT_SUPPORTED("Comparing top-level %ss like '%s' is not supported. Javers.compare() is designed to deeply compare two arbitrary complex object graphs. For simple values, equals() does the job."),

    ENTITY_WITHOUT_ID ("Class '%s' mapped as Entity has no Id property. Use @Id annotation to mark unique and not-null Entity identifier"),

    ENTITY_INSTANCE_WITH_NULL_ID("Found Entity instance of class '%s' with null id"),

    NOT_INSTANCE_OF(JaversException.BOOTSTRAP_ERROR + "expected instance of '%s', got instance of '%s'"),

    UNDEFINED_PROPERTY(JaversException.BOOTSTRAP_ERROR + "undefined mandatory property '%s'. Define it in your classpath:javers.properties"),

    MALFORMED_PROPERTY(JaversException.BOOTSTRAP_ERROR + "unwrap '%s' is invalid for property '%s'. Fix it in your classpath:javers.properties"),

    CLASSPATH_RESOURCE_NOT_FOUND(JaversException.BOOTSTRAP_ERROR + "classpath resource '%s' could not be found"),

    ALREADY_BUILT(JaversException.BOOTSTRAP_ERROR + "instance already built, each AbstractJaversBuilder may produce only one target instance"),

    PROPERTY_ACCESS_ERROR("error getting value from property '%s' on target object of type '%s', cause: %s"),

    CONTAINER_NOT_READY(JaversException.BOOTSTRAP_ERROR +"pico container is not ready"),

    AFFECTED_CDO_IS_NOT_AVAILABLE("affected cdo is not available, you can access it only for freshly generated diffs"),

    MISSING_PROPERTY("There is no property '%s' in type '%s'."),

    NOT_IMPLEMENTED("not implemented"),

    SNAPSHOT_NOT_FOUND("snapshot '%s' not found in JaversRepository"),

    //graph & snapshot
    VALUE_OBJECT_IS_NOT_SUPPORTED_AS_MAP_KEY("found ValueObject on KEY position in Map property '%s'. Please change the key class mapping to Value or Entity"),

    SNAPSHOT_STATE_VIOLATION("snapshots are immutable"),

    PROPERTY_NOT_FOUND("property '%s' not found in class '%s'"),

    /** @since 1.4 */
    TYPE_NAME_NOT_FOUND(
            "type name '%s' not found. " +
            "If you are using @TypeName annotation, " +
            "remember to register this class " +
            "using JaversBuilder.scanTypeName(Class). " +
            "See also https://github.com/javers/javers/issues/263"),

    MANAGED_CLASS_MAPPING_ERROR("given javaClass '%s' is mapped to %s, expected %s"),

    MALFORMED_CHANGE_TYPE_FIELD("no such Change type - '%s'"),

    MALFORMED_ENTRY_CHANGE_TYPE_FIELD("no such EntryChange type - '%s'"),

    CLASS_NOT_MANAGED("given javaClass '%s' is mapped to %s, ManagedType expected"),

    COMPONENT_NOT_FOUND(JaversException.BOOTSTRAP_ERROR+"component of type '%s' not found in container") ,

    NO_PUBLIC_CONSTRUCTOR(JaversException.BOOTSTRAP_ERROR+"no public constructor in class '%s'"),

    CLASS_NOT_FOUND("class not found - '%s'") ,

    CANT_EXTRACT_CHILD_VALUE_OBJECT(
            "error while extracting child ValueObject from path '%s'" +
            ", invalid property type, expected ValueObjectType, ContainerType<ValueObjectType> or MapType<?,ValueObjectType>, got '%s'"),

    CANT_PARSE_COMMIT_ID("can't parse given value {'%s'} to CommitId. " +
            "CommitId should consists of two parts : majorId.minorId e.g. 1.0"),

    CANT_DELETE_OBJECT_NOT_FOUND("failed to delete object {'%s'}, "+
            "it doesn't exists in JaversRepository"),

    CANT_FIND_COMMIT_HEAD_ID("can't find commit head id in JaversRepository"),
    CANT_SAVE_ALREADY_PERSISTED_COMMIT("can't save already persisted commit '%s'"),

    SQL_EXCEPTION("SqlException: %s"),

    UNSUPPORTED_SQL_DIALECT("dialect '%s' is not supported by JaVers"),

    MALFORMED_JQL("Invalid JQL query, %s"),

    UNSUPPORTED_OPTIONAL_CONTENT_TYPE("%s is not supported as Optional<> content type"),

    RUNTIME_EXCEPTION("uncategorized runtime exception. %s")
    ;

    private final String message;

    JaversExceptionCode(String message) {
        this.message = message;
    }

    /**
     * Error description and possibly solution hints.
     */
    public String getMessage() {
        return message;
    }
}
