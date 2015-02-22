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
    public void onAfterRepositoryCall(RepositoryMetadata repositoryMetadata, Object changedObject) {
        if (isIdClass(repositoryMetadata, changedObject)) {
            javers.commitShallowDeleteById(authorProvider.provide(), new InstanceIdDTO(repositoryMetadata.getDomainType(), changedObject));
        } else if (isDomainClass(repositoryMetadata, changedObject)) {
            javers.commitShallowDelete(authorProvider.provide(), changedObject);
        } else {
            throw new IllegalArgumentException("Domain object or object id expected");
        }
    }
}
