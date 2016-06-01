package org.javers.spring.auditable.aspect;

import org.aspectj.lang.JoinPoint;
import org.javers.core.Javers;
import org.javers.spring.auditable.AspectUtil;
import org.javers.spring.auditable.AuthorProvider;
import org.javers.spring.auditable.CommitPropertiesProvider;

import java.util.Map;

/**
 * @author Pawel Szymczyk
 */
class JaversCommitAdvice {

    private final Javers javers;
    private final AuthorProvider authorProvider;
    private final CommitPropertiesProvider commitPropertiesProvider;

    JaversCommitAdvice(Javers javers, AuthorProvider authorProvider, CommitPropertiesProvider commitPropertiesProvider) {
        this.javers = javers;
        this.authorProvider = authorProvider;
        this.commitPropertiesProvider = commitPropertiesProvider;
    }

    void commitMethodArguments(JoinPoint pjp) {
        String author = authorProvider.provide();
        Map<String, String> props = commitPropertiesProvider.provide();

        for (Object arg : AspectUtil.collectArguments(pjp)) {
            javers.commit(author, arg, props);
        }
    }
}
