package org.javers.spring.auditable.aspect;

import org.javers.core.commit.Commit;

import java.util.Map;

@FunctionalInterface
public interface JaversCommitHandler {
    /**
     * Persists save or delete method arguments in JaVers repository.
     *
     * @see JaversCommitAdvice
     * @param author current user
     * @param object object to be marked as saved or deleted
     * See {@link org.javers.core.Javers#commit(String, Object, Map)} for commitProperties description.
     */
    Commit commit(String author, Object object, Map<String, String> commitProperties);
}
