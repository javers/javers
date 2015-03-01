package org.javers.spring.data.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.javers.common.collections.Lists;
import org.javers.common.collections.Optional;
import org.javers.core.Javers;
import org.javers.spring.AuthorProvider;
import org.javers.spring.data.JaversSpringDataAuditable;
import org.javers.spring.data.handler.AuditChangeHandler;
import org.javers.spring.data.handler.OnDeleteAuditChangeHandler;
import org.javers.spring.data.handler.OnSaveAuditChangeHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.DefaultRepositoryMetadata;

import java.util.Collection;
import java.util.Collections;

@Aspect
public class JaversSpringDataRepositoryAspect {

    private static final Logger logger = LoggerFactory.getLogger(JaversSpringDataRepositoryAspect.class);
    private final AuditChangeHandler saveHandler;
    private final AuditChangeHandler deleteHandler;

    public JaversSpringDataRepositoryAspect(Javers javers, AuthorProvider authorProvider) {
        this(new OnSaveAuditChangeHandler(javers, authorProvider), new OnDeleteAuditChangeHandler(javers, authorProvider));
    }

    protected JaversSpringDataRepositoryAspect(AuditChangeHandler saveHandler, AuditChangeHandler deleteHandler) {
        this.saveHandler = saveHandler;
        this.deleteHandler = deleteHandler;
    }


    @Around("execution(public * delete(..)) && this(org.springframework.data.repository.CrudRepository)")
    public Object onDeleteExecuted(ProceedingJoinPoint pjp) throws Throwable {
        return onVersionEvent(pjp, deleteHandler);
    }

    @Around("execution(public * save(..)) && this(org.springframework.data.repository.CrudRepository)")
    public Object onSaveExecuted(ProceedingJoinPoint pjp) throws Throwable {
        return onVersionEvent(pjp, saveHandler);
    }

    private Object onVersionEvent(ProceedingJoinPoint pjp, AuditChangeHandler handler) throws Throwable {
        if (isVersionedRepository(pjp)) {
            RepositoryMetadata metadata = getMetadata(pjp);
            Iterable<Object> domainObjects = getDomainObjectsFromMethodArgumentsOfJoinPoint(pjp);

            Object retVal = pjp.proceed();

            applyVersionChanges(metadata, domainObjects, handler);
            return retVal;
        }
        return pjp.proceed();
    }

    private RepositoryMetadata getMetadata(ProceedingJoinPoint pjp) {
        Optional<Class> repoClass = getRepositoryInterface(pjp);
        if (repoClass.isPresent()) {
            return DefaultRepositoryMetadata.getMetadata(repoClass.get());
        }
        throw new IllegalStateException("Cannot determine repository interface");
    }

    private boolean isVersionedRepository(ProceedingJoinPoint pjp) {
        return getRepositoryInterface(pjp).isPresent();
    }

    private Optional<Class> getRepositoryInterface(ProceedingJoinPoint pjp) {
        for (Class i : pjp.getTarget().getClass().getInterfaces()) {
            if (i.isAnnotationPresent(JaversSpringDataAuditable.class) && CrudRepository.class.isAssignableFrom(i)) {
                return Optional.of(i);
            }
        }
        return Optional.empty();
    }

    private Iterable<Object> getDomainObjectsFromMethodArgumentsOfJoinPoint(ProceedingJoinPoint pjp) {
        if (pjp.getArgs() != null && pjp.getArgs().length > 0) {
            Object arg = pjp.getArgs()[0];
            if (arg instanceof Collection) {
                return (Collection<Object>) arg;
            } else {
                return Lists.asList(arg);
            }
        }
        return Collections.EMPTY_LIST;
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
