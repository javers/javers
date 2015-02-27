package org.javers.spring.data.handler;

import org.javers.core.Javers;
import org.javers.spring.AuthorProvider;
import org.springframework.data.repository.core.RepositoryMetadata;

/**
 * Created by gessnerfl on 22.02.15.
 */
public abstract  class AbstractAuditChangeHandler implements AuditChangeHandler {
    protected final Javers javers;
    protected final AuthorProvider authorProvider;

    public AbstractAuditChangeHandler(Javers javers, AuthorProvider authorProvider) {
        this.javers = javers;
        this.authorProvider = authorProvider;
    }

    protected boolean isDomainClass(RepositoryMetadata metadata, Object o) {
        return metadata.getDomainType().equals(o.getClass());
    }

    protected boolean isIdClass(RepositoryMetadata metadata, Object o) {
        return metadata.getIdType().equals(o.getClass());
    }
}
