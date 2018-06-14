package org.javers.repository.jql;

import org.javers.common.exception.JaversException;
import org.javers.common.exception.JaversExceptionCode;
import org.javers.common.validation.Validate;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.core.metamodel.object.GlobalIdFactory;
import org.javers.repository.api.JaversExtendedRepository;

import java.util.List;
import java.util.Optional;

class SnapshotQueryRunner {
    private final QueryCompiler queryCompiler;
    private final GlobalIdFactory globalIdFactory;
    private final JaversExtendedRepository repository;

    SnapshotQueryRunner(QueryCompiler queryCompiler, GlobalIdFactory globalIdFactory, JaversExtendedRepository repository) {
        this.queryCompiler = queryCompiler;
        this.globalIdFactory = globalIdFactory;
        this.repository = repository;
    }

    Optional<CdoSnapshot> runQueryForLatestSnapshot(GlobalIdDTO globalId) {
        Validate.argumentIsNotNull(globalId);
        return repository.getLatest(globalIdFactory.createFromDto(globalId));
    }

    List<CdoSnapshot> queryForSnapshots(JqlQuery query){
        queryCompiler.compile(query);

        List<CdoSnapshot> result;
        if (query.isAnyDomainObjectQuery()) {
            result = repository.getSnapshots(query.getQueryParams());
        } else
        if (query.isIdQuery()){
            result = repository.getStateHistory(query.getIdFilter(), query.getQueryParams());
        } else
        if (query.isClassQuery()){
            result = repository.getStateHistory(query.getClassFilter(), query.getQueryParams());
        } else
        if (query.isVoOwnerQuery()) {
            VoOwnerFilter filter = query.getVoOwnerFilter();
            result = repository.getValueObjectStateHistory(filter.getOwnerEntity(), filter.getPath(), query.getQueryParams());
        } else {
            throw new JaversException(JaversExceptionCode.MALFORMED_JQL, "queryForSnapshots: " + query + " is not supported");
        }

        return result;
    }
}
