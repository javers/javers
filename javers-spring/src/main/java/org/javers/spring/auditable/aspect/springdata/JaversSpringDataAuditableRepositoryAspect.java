package org.javers.spring.auditable.aspect.springdata;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.javers.core.Javers;
import org.javers.spring.auditable.AuthorProvider;
import org.javers.spring.auditable.CommitPropertiesProvider;

/**
 * Commits all arguments passed to save() and delete() methods
 * in Spring Data CrudRepository
 * when repositories are annotated with (class-level) @JaversSpringDataAuditable.
 */
@Aspect
public class JaversSpringDataAuditableRepositoryAspect extends AbstractSpringAuditableRepositoryAspect {
    public JaversSpringDataAuditableRepositoryAspect(Javers javers, AuthorProvider authorProvider, CommitPropertiesProvider commitPropertiesProvider) {
        super(javers, authorProvider, commitPropertiesProvider);
    }

    @AfterReturning("execution(public * delete(..)) && this(org.springframework.data.repository.CrudRepository)")
    public void onDeleteExecuted(JoinPoint pjp) {
        onDelete(pjp);
    }

    @AfterReturning("execution(public * deleteAll(..)) && this(org.springframework.data.repository.CrudRepository)")
    public void onDeleteAllExecuted(JoinPoint pjp) {
        onDelete(pjp);
    }

    @AfterReturning(value = "execution(public * save(..)) && this(org.springframework.data.repository.CrudRepository)", returning = "responseEntity")
    public void onSaveExecuted(JoinPoint pjp, Object responseEntity) {
        onSave(pjp, responseEntity);
    }

    @AfterReturning(value = "execution(public * saveAll(..)) && this(org.springframework.data.repository.CrudRepository)", returning = "responseEntity")
    public void onSaveAllExecuted(JoinPoint pjp, Object responseEntity) {
        onSave(pjp, responseEntity);
    }
}
