package org.javers.spring.auditable.aspect;

/**
 * Created by gessnerfl on 22.02.15.
 */
interface AuditChangeHandler {
    /**
     * Creates a new audit log entry for the given domainObject. The handler is executed after
     * the live data was created, updated or deleted by calling the corresponding method (save
     * or delete) on the spring data repository implementation of the given domainObject.
     *
     * @param repositoryMetadata the metadata of the spring data repository
     * @param domainObject the domain object
     */
    void handle(SpringDataRepositoryMetadata repositoryMetadata, Object domainObject);
}
