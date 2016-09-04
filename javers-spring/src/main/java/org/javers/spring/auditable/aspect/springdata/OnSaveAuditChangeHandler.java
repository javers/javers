package org.javers.spring.auditable.aspect.springdata;

import org.javers.core.Javers;
import org.javers.spring.auditable.AuthorProvider;
import org.javers.spring.auditable.CommitPropertiesProvider;
import org.springframework.data.repository.core.RepositoryMetadata;

/**
 * Created by gessnerfl on 22.02.15.
 */
class OnSaveAuditChangeHandler extends AbstractAuditChangeHandler {
    OnSaveAuditChangeHandler(Javers javers, AuthorProvider authorProvider, CommitPropertiesProvider commitPropertiesProvider) {
        super(javers, authorProvider, commitPropertiesProvider);
    }

    @Override
    public void handle(RepositoryMetadata repositoryMetadata, Object domainObject) {
        javers.commit(authorProvider.provide(), domainObject, commitPropertiesProvider.provide());
    }
}
