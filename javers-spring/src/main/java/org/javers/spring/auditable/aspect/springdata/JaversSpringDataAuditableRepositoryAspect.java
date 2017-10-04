package org.javers.spring.auditable.aspect.springdata;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.javers.core.Javers;
import org.javers.spring.auditable.AuthorProvider;
import org.javers.spring.auditable.CommitPropertiesProvider;

/**
 * Commits all arguments passed to save() and delete() methods
 * of Spring Data CrudRepositories with (class-level) @JaversSpringDataAuditable.
 */
@Aspect
public class JaversSpringDataAuditableRepositoryAspect extends JaversAuditableRepositoryAspect {

    public JaversSpringDataAuditableRepositoryAspect(Javers javers, AuthorProvider authorProvider, CommitPropertiesProvider commitPropertiesProvider) {
        super(javers, authorProvider, commitPropertiesProvider);
    }

    @AfterReturning("execution(public * delete(..)) && this(org.springframework.data.repository.CrudRepository)")
    public void onDeleteExecuted(JoinPoint pjp) {
        onVersionEvent(pjp, deleteHandler);
    }

    @AfterReturning("execution(public * save(..)) && this(org.springframework.data.repository.CrudRepository)")
    public void onSaveExecuted(JoinPoint pjp) {
        onVersionEvent(pjp, saveHandler);
    }
}
