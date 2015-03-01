package org.javers.spring.auditable.aspect;

import org.aspectj.lang.JoinPoint;
import org.javers.core.Javers;
import org.javers.spring.auditable.AuthorProvider;

/**
 * @author Pawel Szymczyk
 */
public class JaversCommitAdvice {

    private final Javers javers;
    private final AuthorProvider authorProvider;

    public JaversCommitAdvice(Javers javers, AuthorProvider authorProvider) {
        this.javers = javers;
        this.authorProvider = authorProvider;
    }

    public void commitMethodArguments(JoinPoint pjp) {
        String author = authorProvider.provide();
        for (Object arg: pjp.getArgs()) {
            if (arg instanceof Iterable) {
                commitAllEntities((Iterable) arg, author);
            } else {
                javers.commit(author, arg);
            }
        }
    }

    private void commitAllEntities(Iterable iterable, String author) {
        for (Object entity: iterable) {
            javers.commit(author, entity);
        }
    }
}
