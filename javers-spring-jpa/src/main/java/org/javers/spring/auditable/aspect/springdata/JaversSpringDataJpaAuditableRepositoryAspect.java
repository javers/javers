package org.javers.spring.auditable.aspect.springdata;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.javers.core.Javers;
import org.javers.spring.auditable.AuthorProvider;
import org.javers.spring.auditable.CommitPropertiesProvider;

/**
 * Commits all arguments passed to save(), delete() and saveAndFlush() methods
 * in Spring Data JpaRepository
 * when repositories are annotated with (class-level) @JaversSpringDataAuditable.
 */
@Aspect
public class JaversSpringDataJpaAuditableRepositoryAspect extends AbstractSpringAuditableRepositoryAspect {
    public JaversSpringDataJpaAuditableRepositoryAspect(Javers javers, AuthorProvider authorProvider, CommitPropertiesProvider commitPropertiesProvider) {
        super(javers, authorProvider, commitPropertiesProvider);
    }

    @AfterReturning("execution(public * delete(..)) && this(org.springframework.data.repository.CrudRepository)")
    public void onDeleteExecuted(JoinPoint pjp) {
        onDelete(pjp);
    }

    @AfterReturning("execution(public * save(..)) && this(org.springframework.data.repository.CrudRepository)")
    public void onSaveExecuted(JoinPoint pjp) {
        onSave(pjp);
    }

    @AfterReturning("execution(public * saveAndFlush(..)) && this(org.springframework.data.jpa.repository.JpaRepository)")
    public void onSaveAndFlushExecuted(JoinPoint pjp) {
       onSave(pjp);
    }
}
