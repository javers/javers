package org.javers.spring.auditable.aspect.springdata;

import org.aspectj.lang.JoinPoint;
import org.javers.core.Javers;
import org.javers.repository.jql.QueryBuilder;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.javers.spring.auditable.AspectUtil;
import org.javers.spring.auditable.AuthorProvider;
import org.javers.spring.auditable.CommitPropertiesProvider;
import org.javers.spring.auditable.CommitPropertiesProviderContext;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.DefaultRepositoryMetadata;

import java.util.Optional;
import java.util.function.Supplier;

import static org.javers.repository.jql.InstanceIdDTO.instanceId;

public class AbstractSpringAuditableRepositoryAspect {
    private final AuditChangeHandler saveHandler;
    private final AuditChangeHandler deleteHandler;

    private final Javers javers;
    private final AuthorProvider authorProvider;
    private final CommitPropertiesProvider commitPropertiesProvider;

    protected AbstractSpringAuditableRepositoryAspect(Javers javers, AuthorProvider authorProvider, CommitPropertiesProvider commitPropertiesProvider) {
        this.javers = javers;
        this.authorProvider = authorProvider;
        this.commitPropertiesProvider = commitPropertiesProvider;

        this.saveHandler = (repositoryMetadata, domainObject) -> javers.commit(authorProvider.provide(), domainObject,
                commitPropertiesProvider.provide(CommitPropertiesProviderContext.SAVE_UPDATE, domainObject));
        this.deleteHandler = new OnDeleteAuditChangeHandler();
    }

    protected void onSave(JoinPoint pjp, Object returnedObject) {
        onVersionEvent(pjp, saveHandler, () -> AspectUtil.collectReturnedObjects(returnedObject));
    }

    protected void onDelete(JoinPoint pjp) {
        onVersionEvent(pjp, deleteHandler, () -> AspectUtil.collectArguments(pjp));
    }

    private void onVersionEvent(JoinPoint pjp, AuditChangeHandler changeHandler, Supplier<Iterable<Object>> domainObjectsExtractor) {
        Optional<Class> versionedInterface = getRepositoryInterface(pjp);

        versionedInterface.ifPresent(versioned -> {
            RepositoryMetadata metadata = getMetadata(versioned);
            Iterable<Object> domainObjects = domainObjectsExtractor.get();
            for (Object domainObject : domainObjects) {
                changeHandler.handle(metadata, domainObject);
            }
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

    private class OnDeleteAuditChangeHandler implements AuditChangeHandler {

        @Override
        public void handle(RepositoryMetadata repositoryMetadata, Object domainObjectOrId) {
            String author = authorProvider.provide();

            if (isIdClass(repositoryMetadata, domainObjectOrId)) {
                Class<?> domainType = repositoryMetadata.getDomainType();
                if (javers.findSnapshots(QueryBuilder.byInstanceId(domainObjectOrId, domainType).build()).size() == 0) {
                    return;
                }

                javers.commitShallowDeleteById(author, instanceId(domainObjectOrId, domainType), commitPropertiesProvider.provideForDeleteById(domainType, domainObjectOrId));
            } else if (isDomainClass(repositoryMetadata, domainObjectOrId)) {
                if (javers.findSnapshots(QueryBuilder.byInstance(domainObjectOrId).build()).size() == 0) {
                    return;
                }

                javers.commitShallowDelete(author, domainObjectOrId, commitPropertiesProvider.provide(CommitPropertiesProviderContext.DELETE, domainObjectOrId));
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
}
