package org.javers.spring.auditable.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.javers.core.Javers;
import org.javers.spring.auditable.AuthorProvider;
import org.javers.spring.auditable.CommitPropertiesProvider;
import org.javers.spring.auditable.EmptyPropertiesProvider;

/**
 * Commits all arguments passed to advised methods
 * (only if the method exits normally, i.e. no Exception has been thrown).
 *
 * Spring @Transactional attributes (like noRollbackFor or noRollbackForClassName)
 * have no effects on this aspect.
 * <br/><br/>
 *
 * Creates the following @AfterReturning pointcuts:
 * <ul>
 *    <li/>any method annotated with @JaversAuditable
 *    <li/>all save() and delete() methods of CrudRepositories with (class-level) @JaversSpringDataAuditable
 * </ul>
 */
@Aspect
public class JaversAuditableAspect {
    private final JaversCommitAdvice javersCommitAdvice;

    public JaversAuditableAspect(Javers javers, AuthorProvider authorProvider, CommitPropertiesProvider commitPropertiesProvider) {
        this(new JaversCommitAdvice(javers, authorProvider, commitPropertiesProvider) );
    }

    public JaversAuditableAspect(Javers javers, AuthorProvider authorProvider) {
        this(javers, authorProvider, new EmptyPropertiesProvider());
    }

    JaversAuditableAspect(JaversCommitAdvice javersCommitAdvice) {
        this.javersCommitAdvice = javersCommitAdvice;
    }

    @AfterReturning("@annotation(org.javers.spring.annotation.JaversAuditable)")
    public void commitAdvice(JoinPoint pjp) {
        javersCommitAdvice.commitMethodArguments(pjp);
    }
}
