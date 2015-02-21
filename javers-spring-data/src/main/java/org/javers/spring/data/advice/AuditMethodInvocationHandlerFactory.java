package org.javers.spring.data.advice;

import org.aopalliance.intercept.MethodInvocation;
import org.javers.core.Javers;
import org.javers.core.metamodel.object.InstanceIdDTO;
import org.javers.spring.AuthorProvider;
import org.javers.spring.data.JaversSpringDataAuditEvent;
import org.springframework.data.repository.core.RepositoryMetadata;

public class AuditMethodInvocationHandlerFactory {
    private final Javers javers;
    private final AuthorProvider authorProvider;
    private final RepositoryMetadata repositoryMetadata;

    public AuditMethodInvocationHandlerFactory(Javers javers, AuthorProvider authorProvider, RepositoryMetadata repositoryMetadata) {
        this.javers = javers;
        this.authorProvider = authorProvider;
        this.repositoryMetadata = repositoryMetadata;
    }

    public AuditMethodInvocationHandler createFor(MethodInvocation invocation) {
        if (isSaveMethod(invocation)) {
            return new SaveAuditInvocationHandler();
        } else if (isDeleteMethod(invocation)) {
            return new DeleteAuditInvocationHandler();
        }
        throw new IllegalArgumentException("Save or delete method invoication expected");
    }

    private boolean isDeleteMethod(MethodInvocation invocation) {
        return isGivenAuditMethod(invocation, JaversSpringDataAuditEvent.DELETE);
    }

    private boolean isSaveMethod(MethodInvocation invocation) {
        return isGivenAuditMethod(invocation, JaversSpringDataAuditEvent.SAVE);
    }

    private boolean isGivenAuditMethod(MethodInvocation invocation, JaversSpringDataAuditEvent event) {
        return event.isEventMethod(invocation.getMethod());
    }

    private abstract class AbstractAuditMethodInvocationHandler implements AuditMethodInvocationHandler {
        protected boolean isDomainClass(Object o) {
            return repositoryMetadata.getDomainType().equals(o.getClass());
        }

        protected boolean isIdClass(Object o) {
            return repositoryMetadata.getIdType().equals(o.getClass());
        }
    }

    private class SaveAuditInvocationHandler extends AbstractAuditMethodInvocationHandler {
        @Override
        public void onAfterMethodInvocation(Object o) {
            if(isDomainClass(o)){
                javers.commit(authorProvider.provide(), o);
            }else {
                throw new IllegalArgumentException("Domain object expected");
            }
        }
    }

    private class DeleteAuditInvocationHandler extends AbstractAuditMethodInvocationHandler {
        @Override
        public void onAfterMethodInvocation(Object o) {
            if (isIdClass(o)) {
                javers.commitShallowDeleteById(authorProvider.provide(), new InstanceIdDTO(repositoryMetadata.getDomainType(), o));
            } else if(isDomainClass(o)) {
                javers.commitShallowDelete(authorProvider.provide(), o);
            } else {
                throw new IllegalArgumentException("Domain object or object id expected");
            }
        }
    }
}
