package org.javers.spring.auditable.aspect;

import org.javers.core.Javers;
import org.javers.spring.auditable.AuthorProvider;
import org.javers.spring.auditable.CommitPropertiesProvider;

/**
 * Created by gessnerfl on 22.02.15.
 */
class OnSaveAuditChangeHandler extends AbstractAuditChangeHandler {
    OnSaveAuditChangeHandler(Javers javers, AuthorProvider authorProvider, CommitPropertiesProvider commitPropertiesProvider) {
        super(javers, authorProvider, commitPropertiesProvider);
    }

    @Override
    public void handle(SpringDataRepositoryMetadata repositoryMetadata, Object domainObject) {
        javers.commit(authorProvider.provide(), domainObject, commitPropertiesProvider.provide());
    }
}
