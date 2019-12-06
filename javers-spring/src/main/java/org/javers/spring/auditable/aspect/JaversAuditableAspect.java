package org.javers.spring.auditable.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.javers.core.Javers;
import org.javers.spring.annotation.JaversAuditable;
import org.javers.spring.auditable.AuthorProvider;
import org.javers.spring.auditable.CommitPropertiesProvider;
import org.javers.spring.auditable.EmptyPropertiesProvider;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Commits all arguments passed to methods annotated with {@link JaversAuditable}
 * (only if a method exits normally, i.e. no Exception has been thrown).
 * <br/><br/>
 *
 * Spring @Transactional attributes (like noRollbackFor or noRollbackForClassName)
 * have no effects on this aspect.
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
        javersCommitAdvice.commitSaveMethodArguments(pjp);
    }

    @AfterReturning("@annotation(org.javers.spring.annotation.JaversAuditableDelete)")
    public void commitDeleteAdvice(JoinPoint pjp) {
        javersCommitAdvice.commitDeleteMethodArguments(pjp);
    }
}