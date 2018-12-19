package org.javers.spring.auditable.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.javers.core.Javers;
import org.javers.spring.auditable.AuthorProvider;
import org.javers.spring.auditable.CommitPropertiesProvider;
import org.javers.spring.auditable.EmptyPropertiesProvider;

/**
 * Commits all arguments passed to methods with @JaversAuditableDelete annotation
 * (only if a method exits normally, i.e. no Exception has been thrown).
 */
@Aspect
public class JaversAuditableDeleteAspect {
    private final JaversCommitAdvice javersCommitAdvice;

    public JaversAuditableDeleteAspect(Javers javers, AuthorProvider authorProvider, CommitPropertiesProvider commitPropertiesProvider) {
        this(new JaversCommitAdvice(javers, authorProvider, commitPropertiesProvider) );
    }

    public JaversAuditableDeleteAspect(Javers javers, AuthorProvider authorProvider) {
        this(javers, authorProvider, new EmptyPropertiesProvider());
    }

    JaversAuditableDeleteAspect(JaversCommitAdvice javersCommitAdvice) {
        this.javersCommitAdvice = javersCommitAdvice;
    }

    @AfterReturning("@annotation(org.javers.spring.annotation.JaversAuditableDelete)")
    public void commitAdvice(JoinPoint pjp) {
        javersCommitAdvice.commitDeleteMethodArguments(pjp);
    }
}