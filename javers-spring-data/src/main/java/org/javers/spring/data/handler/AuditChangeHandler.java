package org.javers.spring.data.handler;

import org.springframework.data.repository.core.RepositoryMetadata;

/**
 * Created by gessnerfl on 22.02.15.
 */
public interface AuditChangeHandler {
    void onAfterRepositoryCall(RepositoryMetadata repositoryMetadata, Object changedObject);
}
