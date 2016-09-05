package org.javers.spring.auditable.aspect.springdata;

import org.javers.core.Javers;
import org.javers.spring.auditable.AuthorProvider;
import org.javers.spring.auditable.CommitPropertiesProvider;
import org.springframework.data.repository.core.RepositoryMetadata;

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

    boolean isDomainClass(RepositoryMetadata metadata, Object o) {
        return metadata.getDomainType().isAssignableFrom(o.getClass());
    }

    boolean isIdClass(RepositoryMetadata metadata, Object o) {
        return metadata.getIdType().isAssignableFrom(o.getClass());
    }
}
