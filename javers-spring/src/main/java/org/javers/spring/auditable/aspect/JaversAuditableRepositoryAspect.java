package org.javers.spring.auditable.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.javers.common.collections.Lists;
import org.javers.common.collections.Optional;
import org.javers.common.exception.JaversException;
import org.javers.common.exception.JaversExceptionCode;
import org.javers.common.reflection.ReflectionUtil;
import org.javers.core.Javers;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.javers.spring.auditable.AspectUtil;
import org.javers.spring.auditable.AuthorProvider;
import org.javers.spring.auditable.CommitPropertiesProvider;
import org.javers.spring.auditable.EmptyPropertiesProvider;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Commits all arguments passed to advised methods
 * (only if the method exits normally, i.e. no Exception has been thrown).
 * <p>
 * Spring @Transactional attributes (like noRollbackFor or noRollbackForClassName)
 * have no effects on this aspect.
 * <br/><br/>
 * <p>
 * Creates the following @AfterReturning pointcuts:
 * <ul>
 * <li/>any method annotated with @JaversAuditable
 * <li/>all save() and delete() methods of CrudRepositories with (class-level) @JaversSpringDataAuditable
 * </ul>
 */
@Aspect
public class JaversAuditableRepositoryAspect {
    private final AuditChangeHandler saveHandler;
    private final AuditChangeHandler deleteHandler;
    private final JaversCommitAdvice javersCommitAdvice;

    public JaversAuditableRepositoryAspect(Javers javers, AuthorProvider authorProvider, CommitPropertiesProvider commitPropertiesProvider) {
        this(new OnSaveAuditChangeHandler(javers, authorProvider, commitPropertiesProvider),
                new OnDeleteAuditChangeHandler(javers, authorProvider, commitPropertiesProvider),
                new JaversCommitAdvice(javers, authorProvider, commitPropertiesProvider));
    }

    public JaversAuditableRepositoryAspect(Javers javers, AuthorProvider authorProvider) {
        this(javers, authorProvider, new EmptyPropertiesProvider());
    }

    JaversAuditableRepositoryAspect(AuditChangeHandler saveHandler, AuditChangeHandler deleteHandler, JaversCommitAdvice javersCommitAdvice) {
        this.saveHandler = saveHandler;
        this.deleteHandler = deleteHandler;
        this.javersCommitAdvice = javersCommitAdvice;
    }

    @AfterReturning("@annotation(org.javers.spring.annotation.JaversAuditable)")
    public void commitAdvice(JoinPoint pjp) {
        javersCommitAdvice.commitMethodArguments(pjp);
    }

    @AfterReturning("execution(public * delete(..)) && this(org.springframework.data.repository.CrudRepository)")
    public void onDeleteExecuted(JoinPoint pjp) {
        onVersionEvent(pjp, deleteHandler);
    }

    @AfterReturning("execution(public * save(..)) && this(org.springframework.data.repository.CrudRepository)")
    public void onSaveExecuted(JoinPoint pjp) {
        onVersionEvent(pjp, saveHandler);
    }

    private void onVersionEvent(JoinPoint pjp, AuditChangeHandler handler) {
        Optional<Class> versionedInterface = getRepositoryInterface(pjp);
        if (versionedInterface.isEmpty()) {
            return;
        }

        SpringDataRepositoryMetadata metadata = getMetadata(versionedInterface.get());
        Iterable<Object> domainObjects = AspectUtil.collectArguments(pjp);

        applyVersionChanges(metadata, domainObjects, handler);
    }

    private SpringDataRepositoryMetadata getMetadata(Class versionedInterface) {
        Type genericInterface = getSpringDataRepositoryInterface(versionedInterface);
        List<Type> genericArguments = getGenericArguments(versionedInterface, genericInterface);

        Class domainClass = ReflectionUtil.extractClass(genericArguments.get(0));
        Class idClass = ReflectionUtil.extractClass(genericArguments.get(1));

        return new SpringDataRepositoryMetadata(domainClass, idClass);
    }

    private Type getSpringDataRepositoryInterface(Class versionedInterface) {
        List<Type> genericInterfaces = ReflectionUtil.getGenericInterfaces(versionedInterface);

        if (genericInterfaces.size() != 1) {
            throw new JaversException(JaversExceptionCode.COULD_NOT_EXTRACT_SPRING_DATA_REPOSITORY_INTERFACE_FROM_GIVEN_CLASS,
                    versionedInterface,
                    Lists.asString(genericInterfaces));
        }
        return genericInterfaces.get(0);
    }

    private List<Type> getGenericArguments(Class versionedInterface, Type genericInterface) {
        List<Type> genericArguments = ReflectionUtil.getAllTypeArguments(genericInterface);

        if (genericArguments.size() != 2) {
            throw new JaversException(JaversExceptionCode.COULD_NOT_EXTRACT_TYPE_ARGUMENTS_FROM_SPRING_DATA_REPOSITORY_INTERFACE,
                    versionedInterface,
                    Lists.asString(genericArguments));
        }
        return genericArguments;
    }

    private Optional<Class> getRepositoryInterface(JoinPoint pjp) {
        for (Class i : pjp.getTarget().getClass().getInterfaces()) {
            if (i.isAnnotationPresent(JaversSpringDataAuditable.class)) {
                return Optional.of(i);
            }
        }
        return Optional.empty();
    }

    private void applyVersionChanges(SpringDataRepositoryMetadata metadata, Iterable<Object> domainObjects, AuditChangeHandler handler) {
        for (Object domainObject : domainObjects) {
            applyVersionChange(metadata, domainObject, handler);
        }
    }

    private void applyVersionChange(SpringDataRepositoryMetadata metadata, Object domainObject, AuditChangeHandler handler) {
        handler.handle(metadata, domainObject);
    }
}
