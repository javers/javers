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

public class AbstractSpringAuditableRepositoryAspect {
    private final AuditChangeHandler saveHandler;
    private final AuditChangeHandler deleteHandler;

    protected AbstractSpringAuditableRepositoryAspect(Javers javers, AuthorProvider authorProvider, CommitPropertiesProvider commitPropertiesProvider) {
        this.saveHandler = new OnSaveAuditChangeHandler(javers, authorProvider, commitPropertiesProvider);
        this.deleteHandler = new OnDeleteAuditChangeHandler(javers, authorProvider, commitPropertiesProvider);
    }

    protected void onSave(JoinPoint pjp, Object responseEntity) {
        onVersionEvent(pjp, saveHandler, responseEntity);
    }

    private void onVersionEvent(JoinPoint pjp, AuditChangeHandler saveHandler, Object responseEntity) {
        Optional<Class> versionedInterface = getRepositoryInterface(pjp);

        versionedInterface.ifPresent(versioned -> {
            RepositoryMetadata metadata = getMetadata(versioned);
            Iterable<Object> domainObjects = AspectUtil.collectArguments(responseEntity);
            applyVersionChanges(metadata, domainObjects, saveHandler);
        });
    }

    protected void onDelete(JoinPoint pjp) {
        onVersionEvent(pjp, deleteHandler);
    }

    private void onVersionEvent(JoinPoint pjp, AuditChangeHandler handler) {
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
            handler.handle(metadata, domainObject);
        }
    }
}
