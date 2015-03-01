package org.javers.spring.data.aspect;

import org.javers.core.Javers;
import org.javers.spring.auditable.AuthorProvider;
import org.springframework.data.repository.core.RepositoryMetadata;

/**
 * Created by gessnerfl on 22.02.15.
 */
abstract class AbstractAuditChangeHandler implements AuditChangeHandler {
    protected final Javers javers;
    protected final AuthorProvider authorProvider;

    public AbstractAuditChangeHandler(Javers javers, AuthorProvider authorProvider) {
        this.javers = javers;
        this.authorProvider = authorProvider;
    }

    protected boolean isDomainClass(RepositoryMetadata metadata, Object o) {
        return metadata.getDomainType().isAssignableFrom(o.getClass());
    }

    protected boolean isIdClass(RepositoryMetadata metadata, Object o) {
        return metadata.getIdType().isAssignableFrom(o.getClass());
    }
}
