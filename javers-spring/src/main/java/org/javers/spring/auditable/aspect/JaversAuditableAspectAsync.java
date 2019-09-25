package org.javers.spring.auditable.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.javers.core.Javers;
import org.javers.spring.auditable.AuthorProvider;
import org.javers.spring.auditable.CommitPropertiesProvider;

import java.util.concurrent.Executor;

/**
 * Commits all arguments passed to methods with @JaversAuditableAsync annotation
 * (only if a method exits normally, i.e. no Exception has been thrown).
 * <br/><br/>
 *
 * Spring @Transactional attributes (like noRollbackFor or noRollbackForClassName)
 * have no effects on this aspect.
 */
@Aspect
public class JaversAuditableAspectAsync {
    private final JaversCommitAdvice javersCommitAdvice;

    public JaversAuditableAspectAsync(Javers javers, AuthorProvider authorProvider, CommitPropertiesProvider commitPropertiesProvider,Executor executor) {
        this(new JaversCommitAdvice(javers, authorProvider, commitPropertiesProvider,executor));
    }

    JaversAuditableAspectAsync(JaversCommitAdvice javersCommitAdviceAsync) {
        this.javersCommitAdvice = javersCommitAdviceAsync;
    }

    @AfterReturning("@annotation(org.javers.spring.annotation.JaversAuditable)")
    public void commitAdvice(JoinPoint pjp) {
		javersCommitAdvice.commitSaveMethodArgumentsAsync(pjp);
    }

    @AfterReturning("@annotation(org.javers.spring.annotation.JaversAuditableDelete)")
    public void commitDeleteAdvice(JoinPoint pjp) {
		javersCommitAdvice.commitDeleteMethodArguments(pjp);
    }
}