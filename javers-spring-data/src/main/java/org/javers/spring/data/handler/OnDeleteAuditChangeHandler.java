package org.javers.spring.data.handler;

import org.javers.core.Javers;
import org.javers.core.metamodel.object.InstanceIdDTO;
import org.javers.spring.AuthorProvider;
import org.springframework.data.repository.core.RepositoryMetadata;

/**
 * Created by gessnerfl on 22.02.15.
 */
public class OnDeleteAuditChangeHandler extends AbstractAuditChangeHandler {
    public OnDeleteAuditChangeHandler(Javers javers, AuthorProvider authorProvider) {
        super(javers, authorProvider);
    }

    @Override
    public void handle(RepositoryMetadata repositoryMetadata, Object domainObject) {
        if (isIdClass(repositoryMetadata, domainObject)) {
            javers.commitShallowDeleteById(authorProvider.provide(), new InstanceIdDTO(repositoryMetadata.getDomainType(), domainObject));
        } else if (isDomainClass(repositoryMetadata, domainObject)) {
            javers.commitShallowDelete(authorProvider.provide(), domainObject);
        } else {
            throw new IllegalArgumentException("Domain object or object id expected");
        }
    }
}
