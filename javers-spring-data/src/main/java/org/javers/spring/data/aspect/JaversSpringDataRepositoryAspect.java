package org.javers.spring.data.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.javers.common.collections.Lists;
import org.javers.common.collections.Optional;
import org.javers.core.Javers;
import org.javers.spring.AuthorProvider;
import org.javers.spring.DefaultAuthorProvider;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(JaversSpringDataRepositoryAspect.class);
    private final Javers javers;
    private final AuthorProvider authorProvider;
    private final AuditChangeHandler saveHandler;
    private final AuditChangeHandler deleteHandler;

    public JaversSpringDataRepositoryAspect(Javers javers) {
        this(javers, new DefaultAuthorProvider());
    }

    public JaversSpringDataRepositoryAspect(Javers javers, AuthorProvider authorProvider) {
        this(javers, authorProvider, new OnSaveAuditChangeHandler(javers, authorProvider), new OnDeleteAuditChangeHandler(javers, authorProvider));
    }

    protected JaversSpringDataRepositoryAspect(Javers javers, AuthorProvider authorProvider, AuditChangeHandler saveHandler, AuditChangeHandler deleteHandler) {
        this.javers = javers;
        this.authorProvider = authorProvider;
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
            Iterable<Object> toBeProcessed = getObjectList(pjp);

            Object retVal = pjp.proceed();

            applyVersionChanges(metadata, toBeProcessed, handler);
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

    private Iterable<Object> getObjectList(ProceedingJoinPoint pjp) {
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

    private void applyVersionChanges(RepositoryMetadata metadata, Iterable<Object> obj, AuditChangeHandler handler) {
        for (Object o : obj) {
            applyVersionChange(metadata, o, handler);
        }
    }

    private void applyVersionChange(RepositoryMetadata metadata, Object changeObject, AuditChangeHandler handler) {
        LOGGER.info("Commit new version " + changeObject.toString());
        handler.onAfterRepositoryCall(metadata, changeObject);
    }

}
