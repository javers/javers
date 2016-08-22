package org.javers.spring.auditable.aspect;

import org.javers.core.Javers;
import org.javers.spring.auditable.AuthorProvider;
import org.javers.spring.auditable.CommitPropertiesProvider;

/**
 * Created by gessnerfl on 22.02.15.
 */
abstract class AbstractAuditChangeHandler implements AuditChangeHandler {
    protected final Javers javers;
    protected final AuthorProvider authorProvider;
    protected final CommitPropertiesProvider commitPropertiesProvider;

    AbstractAuditChangeHandler(Javers javers, AuthorProvider authorProvider, CommitPropertiesProvider commitPropertiesProvider) {
        this.javers = javers;
        this.authorProvider = authorProvider;
        this.commitPropertiesProvider = commitPropertiesProvider;
    }

    boolean isDomainClass(SpringDataRepositoryMetadata metadata, Object o) {
        return metadata.getDomainType().isAssignableFrom(o.getClass());
    }

    boolean isIdClass(SpringDataRepositoryMetadata metadata, Object o) {
        return metadata.getIdType().isAssignableFrom(o.getClass());
    }
}
