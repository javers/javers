package org.javers.spring.auditable.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.javers.core.Javers;
import org.javers.core.commit.Commit;
import org.javers.spring.annotation.JaversAuditableAsync;
import org.javers.spring.auditable.AuthorProvider;
import org.javers.spring.auditable.CommitPropertiesProvider;
import org.springframework.core.annotation.Order;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * <b>INCUBATING - Javers Async API has incubating status.</b>
 * <br/><br/>
 *
 *  Asynchronously commits all arguments passed to methods annotated with {@link JaversAuditableAsync}
 *  by calling {@link Javers#commitAsync(String, Object, Executor)} for each method argument.
 *  <br/><br/>
 *
 *  This is the {@link AfterReturning} aspect, it triggers
 *  only if a method exits normally, i.e. if no Exception has been thrown.
 */
@Aspect
@Order(0)
public class JaversAuditableAspectAsync {
    private final JaversCommitAdvice javersCommitAdvice;
    private Optional<CompletableFuture<Commit>> lastAsyncCommit = Optional.empty();

    public JaversAuditableAspectAsync(Javers javers, AuthorProvider authorProvider, CommitPropertiesProvider commitPropertiesProvider,Executor executor) {
        this(new JaversCommitAdvice(javers, authorProvider, commitPropertiesProvider, executor));
    }

    JaversAuditableAspectAsync(JaversCommitAdvice javersCommitAdvice) {
        this.javersCommitAdvice = javersCommitAdvice;
    }

    @AfterReturning("@annotation(org.javers.spring.annotation.JaversAuditableAsync)")
    public void commitAdvice(JoinPoint pjp) {
        lastAsyncCommit = javersCommitAdvice.commitSaveMethodArgumentsAsync(pjp);
    }

    Optional<CompletableFuture<Commit>> getLastAsyncCommit() {
        return lastAsyncCommit;
    }
}