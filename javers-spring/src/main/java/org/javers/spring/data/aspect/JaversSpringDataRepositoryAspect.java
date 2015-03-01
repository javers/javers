package org.javers.spring.data.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.javers.common.collections.Optional;
import org.javers.core.Javers;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.javers.spring.auditable.AspectUtil;
import org.javers.spring.auditable.AuthorProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.DefaultRepositoryMetadata;

@Aspect
public class JaversSpringDataRepositoryAspect {

    private static final Logger logger = LoggerFactory.getLogger(JaversSpringDataRepositoryAspect.class);
    private final AuditChangeHandler saveHandler;
    private final AuditChangeHandler deleteHandler;

    public JaversSpringDataRepositoryAspect(Javers javers, AuthorProvider authorProvider) {
        this(new OnSaveAuditChangeHandler(javers, authorProvider), new OnDeleteAuditChangeHandler(javers, authorProvider));
    }

    JaversSpringDataRepositoryAspect(AuditChangeHandler saveHandler, AuditChangeHandler deleteHandler) {
        this.saveHandler = saveHandler;
        this.deleteHandler = deleteHandler;
    }


    @After("execution(public * delete(..)) && this(org.springframework.data.repository.CrudRepository)")
    public void onDeleteExecuted(JoinPoint pjp)  {
        onVersionEvent(pjp, deleteHandler);
    }

    @After("execution(public * save(..)) && this(org.springframework.data.repository.CrudRepository)")
    public void onSaveExecuted(JoinPoint pjp) {
        onVersionEvent(pjp, saveHandler);
    }

    private void onVersionEvent(JoinPoint pjp, AuditChangeHandler handler) {
        Optional<Class> versionedInterface = getRepositoryInterface(pjp);
        if (versionedInterface.isEmpty()){
            return;
        }

        RepositoryMetadata metadata = getMetadata(versionedInterface.get());
        Iterable<Object> domainObjects = AspectUtil.collectArguments(pjp);

        applyVersionChanges(metadata, domainObjects, handler);
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
        //logger.debug("Committing: " + domainObject.toString());
        handler.handle(metadata, domainObject);
    }

}
