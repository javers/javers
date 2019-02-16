package org.javers.spring.auditable.aspect;

import org.aspectj.lang.JoinPoint;
import org.javers.core.Javers;
import org.javers.core.commit.Commit;
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

    void commitSaveMethodArguments(JoinPoint pjp) {
        commitMethodArguments(pjp, javers::commit);
    }

    void commitDeleteMethodArguments(JoinPoint pjp) {
        commitMethodArguments(pjp, javers::commitShallowDelete);
    }

    private void commitMethodArguments(JoinPoint pjp, JaversCommitHandler commitHandler) {
        String author = authorProvider.provide();
        Map<String, String> props = commitPropertiesProvider.provide();

        for (Object arg : AspectUtil.collectArguments(pjp)) {
            commitHandler.commit(author, arg, props);
        }
    }

    @FunctionalInterface
    private interface JaversCommitHandler {
        Commit commit(String author, Object object, Map<String, String> commitProperties);
    }
}
