package org.javers.spring.auditable.aspect;

import org.javers.core.Javers;
import org.javers.repository.jql.InstanceIdDTO;
import org.javers.spring.auditable.AuthorProvider;
import org.springframework.data.repository.core.RepositoryMetadata;

/**
 * Created by gessnerfl on 22.02.15.
 */
class OnDeleteAuditChangeHandler extends AbstractAuditChangeHandler {
    public OnDeleteAuditChangeHandler(Javers javers, AuthorProvider authorProvider) {
        super(javers, authorProvider);
    }

    @Override
    public void handle(RepositoryMetadata repositoryMetadata, Object domainObject) {
        if (isIdClass(repositoryMetadata, domainObject)) {
            javers.commitShallowDeleteById(authorProvider.provide(), InstanceIdDTO.instanceId(domainObject, repositoryMetadata.getDomainType()));
        } else if (isDomainClass(repositoryMetadata, domainObject)) {
            javers.commitShallowDelete(authorProvider.provide(), domainObject);
        } else {
            throw new IllegalArgumentException("Domain object or object id expected");
        }
    }
}
