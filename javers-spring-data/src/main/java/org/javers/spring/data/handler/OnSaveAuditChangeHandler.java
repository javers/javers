package org.javers.spring.data.handler;

import org.javers.core.Javers;
import org.javers.spring.AuthorProvider;
import org.springframework.data.repository.core.RepositoryMetadata;

/**
 * Created by gessnerfl on 22.02.15.
 */
public class OnSaveAuditChangeHandler extends AbstractAuditChangeHandler{
    public OnSaveAuditChangeHandler(Javers javers, AuthorProvider authorProvider) {
        super(javers, authorProvider);
    }

    @Override
    public void handle(RepositoryMetadata repositoryMetadata, Object domainObject) {
        if(isDomainClass(repositoryMetadata, domainObject)){
            javers.commit(authorProvider.provide(), domainObject);
        }else {
            throw new IllegalArgumentException("Domain object expected");
        }
    }
}
