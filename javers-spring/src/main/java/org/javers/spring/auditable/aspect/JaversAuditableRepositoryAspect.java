package org.javers.spring.auditable.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.javers.common.collections.Optional;
import org.javers.core.Javers;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.javers.spring.auditable.AspectUtil;
import org.javers.spring.auditable.AuthorProvider;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.DefaultRepositoryMetadata;
import org.springframework.transaction.annotation.Transactional;

import java.lang.annotation.Annotation;

/**
 * Creates three @After advices.
 * <br/><br/>
 *
 * Commits all arguments passed to methods with @JaversAuditable annotation.
 * <br/><br/>
 *
 * For spring-data Repositories with @JaversSpringDataAuditable annotation: <br/>
 * - commits all arguments passed to save() methods,  <br/>
 * - commits delete of arguments passed to delete() methods.  <br/>
 */
@Aspect
public class JaversAuditableRepositoryAspect {
    private final AuditChangeHandler saveHandler;
    private final AuditChangeHandler deleteHandler;
    private final JaversCommitAdvice javersCommitAdvice;

    public JaversAuditableRepositoryAspect(Javers javers, AuthorProvider authorProvider) {
        this(new OnSaveAuditChangeHandler(javers, authorProvider),
             new OnDeleteAuditChangeHandler(javers, authorProvider),
             new JaversCommitAdvice(javers,authorProvider) );
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

    @AfterThrowing(value = "@annotation(org.javers.spring.annotation.JaversAuditable)", throwing = "ex")
    public void commitAdvice(JoinPoint pjp, Throwable ex) {
        //If we are supposed to still execute, despite the exception, do so
        if( isStillExecute(pjp, ex) ) {
            commitAdvice(pjp);
        }
    }

    @AfterReturning("execution(public * delete(..)) && this(org.springframework.data.repository.CrudRepository)")
    public void onDeleteExecuted(JoinPoint pjp)  {
        onVersionEvent(pjp, deleteHandler);
    }

    @AfterThrowing(value = "execution(public * delete(..)) && this(org.springframework.data.repository.CrudRepository)", throwing = "ex")
    public void onDeleteExecuted(JoinPoint pjp, Throwable ex)  {
        //If we are supposed to still execute, despite the exception, do so
        if( isStillExecute(pjp, ex) ) {
            onDeleteExecuted(pjp);
        }
    }

    @AfterReturning("execution(public * save(..)) && this(org.springframework.data.repository.CrudRepository)")
    public void onSaveExecuted(JoinPoint pjp) {
        onVersionEvent(pjp, saveHandler);
    }

    @AfterThrowing(value = "execution(public * save(..)) && this(org.springframework.data.repository.CrudRepository)", throwing = "ex")
    public void onSaveExecuted(JoinPoint pjp, Throwable ex) {
        //If we are supposed to still execute, despite the exception, do so
        if( isStillExecute(pjp, ex) ) {
            onSaveExecuted(pjp);
        }
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
        handler.handle(metadata, domainObject);
    }

    /**
     * Returns TRUE if the {@link Throwable} matches against the current {@link JoinPoint}s method with Spring's @{@link Transactional}
     * noRollbackFor or noRoolbackForClassName values.
     *
     * @param ex the {@link Throwable} which was thrown
     * @param pjp the {@link JoinPoint} we are currently at
     * @return TRUE if the advised method has @{@link Transactional} and will commit despite this @{@link Throwable}
     */
    protected boolean isStillExecute(JoinPoint pjp, Throwable ex) {
        //Grab the current method signature we are operating on, and the Spring Transactional Annotation, if any
        MethodSignature signature = (MethodSignature)pjp.getSignature();
        Transactional transactionalAnnotation = signature.getMethod().getAnnotation(Transactional.class);

        // If there is no Spring @Transactional annotation on the method, then we don't want to execute the normal
        // versioning code
        if( transactionalAnnotation == null ) {
            return false;
        }

        boolean executeCommit = false;
        Class<? extends Throwable> exceptionClass = ex.getClass();
        //grab the @Transactional noRollbackFor classes
        Class<? extends Throwable>[] noRollbackForClasses = transactionalAnnotation.noRollbackFor();
        if( noRollbackForClasses != null && noRollbackForClasses.length > 0 ) {
            for( Class<? extends Throwable> aClass : noRollbackForClasses ) {
                //if the exception class matches a class to NOT rollback for, match and break
                if( aClass.equals(exceptionClass) ) {
                    executeCommit = true;
                    break;
                }
            }
        }

        //if we didn't match on a noRollbackFor classes, we need to check the noRollbackForClassName
        if( !executeCommit ) {
            String[] noRollbackForClassNames = transactionalAnnotation.noRollbackForClassName();
            String exceptionClassName = exceptionClass.getName();
            for( String className : noRollbackForClassNames ) {
                //if the exception class name (will be fully qualified class + package) ends with the specified class
                //name, it'll either match the base class name, or the fully qualified class + package, whichever was
                //specified on the @Transactional. I also tested, @Transactional will commit with just the class name
                //without the full class + package, thus doing endsWith()
                if( exceptionClassName.endsWith(className) ) {
                    executeCommit = true;
                    break;
                }
            }
        }

        return executeCommit;
    }
}
