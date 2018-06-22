package org.javers.repository.jql;

import org.javers.common.exception.JaversException;
import org.javers.common.exception.JaversExceptionCode;
import org.javers.core.diff.Change;
import org.javers.repository.api.JaversExtendedRepository;

import java.util.List;

class ChangesQueryRunner {
    private final QueryCompiler queryCompiler;
    private final JaversExtendedRepository repository;

    ChangesQueryRunner(QueryCompiler queryCompiler, JaversExtendedRepository repository) {
        this.queryCompiler = queryCompiler;
        this.repository = repository;
    }

    List<Change> queryForChanges(JqlQuery query) {
        queryCompiler.compile(query);

        if (query.isAnyDomainObjectQuery()) {
            return repository.getChanges(query.isNewObjectChanges(), query.getQueryParams());
        }

        if (query.isIdQuery()){
            return repository.getChangeHistory(query.getIdFilter(), query.getQueryParams());
        }

        if (query.isClassQuery()){
            return repository.getChangeHistory(query.getClassFilter(), query.getQueryParams());
        }

        if (query.isVoOwnerQuery()) {
            VoOwnerFilter filter = query.getVoOwnerFilter();
            return repository.getValueObjectChangeHistory(
                    filter.getOwnerEntity(), filter.getPath(), query.getQueryParams());
        }

        throw new JaversException(JaversExceptionCode.MALFORMED_JQL, "queryForChanges: " + query + " is not supported");
    }
}
