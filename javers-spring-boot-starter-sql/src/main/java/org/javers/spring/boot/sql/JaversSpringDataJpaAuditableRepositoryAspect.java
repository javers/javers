package org.javers.spring.boot.sql;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.javers.core.Javers;
import org.javers.spring.auditable.AuthorProvider;
import org.javers.spring.auditable.CommitPropertiesProvider;
import org.javers.spring.auditable.aspect.springdata.JaversSpringDataAuditableRepositoryAspect;

/**
 *  Commits all arguments passed to save() and delete() methods
 *  of Spring Data CrudRepositories with (class-level) @JaversSpringDataAuditable.
 */
@Aspect
public class JaversSpringDataJpaAuditableRepositoryAspect extends JaversSpringDataAuditableRepositoryAspect {


    public JaversSpringDataJpaAuditableRepositoryAspect(Javers javers, AuthorProvider authorProvider, CommitPropertiesProvider commitPropertiesProvider) {
        super(javers, authorProvider, commitPropertiesProvider);
    }

    public JaversSpringDataJpaAuditableRepositoryAspect(Javers javers, AuthorProvider authorProvider) {
        super(javers, authorProvider);
    }

    @AfterReturning("this(org.springframework.data.jpa.repository.JpaRepository) "+
                    "&& @target(org.javers.spring.annotation.JaversSpringDataAuditable) "+
                    "&& execution(public * saveAndPublish(..)) ")
    public void onSaveExecutedJpaRepo(JoinPoint pjp) {
        onVersionEvent(pjp, getSaveHandler());
    }

}
