package org.javers.spring.auditable.aspect.springdata;

import org.aspectj.lang.JoinPoint;
import org.javers.core.Javers;
import org.javers.repository.jql.QueryBuilder;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.javers.spring.auditable.AdvancedCommitPropertiesProvider;
import org.javers.spring.auditable.AspectUtil;
import org.javers.spring.auditable.AuditedMethodExecutionContext;
import org.javers.spring.auditable.AuthorProvider;
import org.javers.spring.auditable.CommitPropertiesProvider;
import org.javers.spring.auditable.aspect.JaversCommitAdvice;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.DefaultRepositoryMetadata;

import java.util.Optional;

public class AbstractSpringAuditableRepositoryAspect {
    private final JaversCommitAdvice javersCommitAdvice;
    private final Javers javers;

    protected AbstractSpringAuditableRepositoryAspect(Javers javers, AuthorProvider authorProvider, CommitPropertiesProvider commitPropertiesProvider, AdvancedCommitPropertiesProvider advancedCommitPropertiesProvider) {
        this.javers = javers;
        this.javersCommitAdvice = new JaversCommitAdvice(javers, authorProvider, commitPropertiesProvider, advancedCommitPropertiesProvider);
    }

    protected void onSave(JoinPoint pjp, Object returnedObject) {
        AuditedMethodExecutionContext ctx = AuditedMethodExecutionContext.from(pjp);
        getRepositoryInterface(pjp).ifPresent(i ->
                AspectUtil.collectReturnedObjects(returnedObject).forEach(it -> javersCommitAdvice.commitObject(ctx, it)));
    }

    protected void onDelete(JoinPoint pjp) {
        AuditedMethodExecutionContext ctx = AuditedMethodExecutionContext.from(pjp);
        getRepositoryInterface(pjp).ifPresent( i -> {
            RepositoryMetadata metadata = DefaultRepositoryMetadata.getMetadata(i);
            for (Object deletedObject : AspectUtil.collectArguments(pjp)) {
                handleDelete(ctx, metadata, deletedObject);
            }
        });
    }

    private Optional<Class> getRepositoryInterface(JoinPoint pjp) {
        for (Class i : pjp.getTarget().getClass().getInterfaces()) {
            if (i.isAnnotationPresent(JaversSpringDataAuditable.class) && CrudRepository.class.isAssignableFrom(i)) {
                return Optional.of(i);
            }
        }
        return Optional.empty();
    }


    void handleDelete(AuditedMethodExecutionContext ctx, RepositoryMetadata repositoryMetadata, Object domainObjectOrId) {
            if (isIdClass(repositoryMetadata, domainObjectOrId)) {
                Class<?> domainType = repositoryMetadata.getDomainType();
                if (javers.findSnapshots(QueryBuilder.byInstanceId(domainObjectOrId, domainType).limit(1).build()).size() == 0) {
                    return;
                }
                javersCommitAdvice.commitShallowDeleteById(ctx, domainObjectOrId, domainType);
            } else if (isDomainClass(repositoryMetadata, domainObjectOrId)) {
                if (javers.findSnapshots(QueryBuilder.byInstance(domainObjectOrId).limit(1).build()).size() == 0) {
                    return;
                }
                javersCommitAdvice.commitShallowDelete(ctx, domainObjectOrId);
            } else {
                throw new IllegalArgumentException("Domain object or object id expected");
            }
    }

    private boolean isDomainClass(RepositoryMetadata metadata, Object o) {
        return metadata.getDomainType().isAssignableFrom(o.getClass());
    }

    private boolean isIdClass(RepositoryMetadata metadata, Object o) {
        return metadata.getIdType().isAssignableFrom(o.getClass());
    }

}
