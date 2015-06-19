package org.javers.spring.auditable.aspect;

import org.javers.core.Javers;
import org.javers.spring.auditable.AuthorProvider;
import org.springframework.data.repository.core.RepositoryMetadata;

/**
 * Created by gessnerfl on 22.02.15.
 */
class OnSaveAuditChangeHandler extends AbstractAuditChangeHandler {
    public OnSaveAuditChangeHandler(Javers javers, AuthorProvider authorProvider) {
        super(javers, authorProvider);
    }

    @Override
    public void handle(RepositoryMetadata repositoryMetadata, Object domainObject) {
        javers.commit(authorProvider.provide(), domainObject);
    }
}
