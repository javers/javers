package org.javers.spring.auditable.aspect.springdata;

import org.aspectj.lang.JoinPoint;
import org.javers.core.Javers;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.javers.spring.auditable.AspectUtil;
import org.javers.spring.auditable.AuthorProvider;
import org.javers.spring.auditable.CommitPropertiesProvider;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.DefaultRepositoryMetadata;

import java.util.Optional;

/**
 * @author pawel szymczyk
 */
abstract class JaversAuditableRepositoryAspect {
    protected final AuditChangeHandler saveHandler;
    protected final AuditChangeHandler deleteHandler;

    public JaversAuditableRepositoryAspect(Javers javers, AuthorProvider authorProvider, CommitPropertiesProvider commitPropertiesProvider) {
        this(new OnSaveAuditChangeHandler(javers, authorProvider, commitPropertiesProvider),
                new OnDeleteAuditChangeHandler(javers, authorProvider, commitPropertiesProvider));
    }

    JaversAuditableRepositoryAspect(AuditChangeHandler saveHandler, AuditChangeHandler deleteHandler) {
        this.saveHandler = saveHandler;
        this.deleteHandler = deleteHandler;
    }

    protected void onVersionEvent(JoinPoint pjp, AuditChangeHandler handler) {
        Optional<Class> versionedInterface = getRepositoryInterface(pjp);

        versionedInterface.ifPresent(versioned -> {
            RepositoryMetadata metadata = getMetadata(versioned);
            Iterable<Object> domainObjects = AspectUtil.collectArguments(pjp);
            applyVersionChanges(metadata, domainObjects, handler);
        });
    }

    private RepositoryMetadata getMetadata(Class versionedInterface) {
        return DefaultRepositoryMetadata.getMetadata(versionedInterface);
    }

    private Optional<Class> getRepositoryInterface(JoinPoint pjp) {
        for (Class i : pjp.getTarget().getClass().getInterfaces()) {
            if (i.isAnnotationPresent(JaversSpringDataAuditable.class) && CrudRepository.class.isAssignableFrom(i)) {
                return Optional.of(i);
            }
        }
        return Optional.empty();
    }

    private void applyVersionChanges(RepositoryMetadata metadata, Iterable<Object> domainObjects, AuditChangeHandler handler) {
        for (Object domainObject : domainObjects) {
            applyVersionChange(metadata, domainObject, handler);
        }
    }

    private void applyVersionChange(RepositoryMetadata metadata, Object domainObject, AuditChangeHandler handler) {
        handler.handle(metadata, domainObject);
    }
}
