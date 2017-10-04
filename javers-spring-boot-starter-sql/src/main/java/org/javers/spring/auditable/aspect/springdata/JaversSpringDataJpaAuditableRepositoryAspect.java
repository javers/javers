package org.javers.spring.auditable.aspect.springdata;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.javers.core.Javers;
import org.javers.spring.auditable.AuthorProvider;
import org.javers.spring.auditable.CommitPropertiesProvider;

/**
 * Commits all arguments passed to saveAndFlush() method
 * of Spring Data JpaRepository with (class-level) @JaversSpringDataAuditable.
 *
 * @author pawel szymczyk
 */
@Aspect
public class JaversSpringDataJpaAuditableRepositoryAspect extends JaversAuditableRepositoryAspect {


    public JaversSpringDataJpaAuditableRepositoryAspect(Javers javers, AuthorProvider authorProvider, CommitPropertiesProvider commitPropertiesProvider) {
        super(javers, authorProvider, commitPropertiesProvider);
    }

    @AfterReturning("execution(public * saveAndFlush(..)) && this(org.springframework.data.jpa.repository.JpaRepository)")
    public void onSaveAndFlushExecuted(JoinPoint pjp) {
        onVersionEvent(pjp, saveHandler);
    }
}
