package org.javers.repository.jql;

import org.javers.common.collections.Optional;
import org.javers.common.exception.JaversException;
import org.javers.common.exception.JaversExceptionCode;
import org.javers.common.validation.Validate;
import org.javers.core.diff.Change;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.core.metamodel.object.GlobalId;
import org.javers.core.metamodel.object.GlobalIdFactory;
import org.javers.repository.api.JaversExtendedRepository;

import java.util.List;

/**
 * Adapter from a Query to JaversRepository API
 *
 * Created by bartosz.walacik on 2015-03-29.
 */
public class QueryRunner {
    private final JaversExtendedRepository repository;
    private final GlobalIdFactory globalIdFactory;

    public QueryRunner(JaversExtendedRepository repository, GlobalIdFactory globalIdFactory) {
        this.repository = repository;
        this.globalIdFactory = globalIdFactory;
    }

    public Optional<CdoSnapshot> runQueryForLatestSnapshot(SnapshotQuery query) {
        Validate.argumentIsNotNull(query);
        return repository.getLatest(fromDto(query.getIdFilter()));
    }

    public List<CdoSnapshot> runQuery(SnapshotQuery query){
        Validate.argumentIsNotNull(query);

        if (query.isIdOnlyQuery()){
            return repository.getStateHistory(fromDto(query.getIdFilter()), query.getLimit());
        }

        if (query.isPropertyQuery()){
            return repository.getPropertyStateHistory(fromDto(query.getIdFilter()), query.getPropertyName(), query.getLimit());
        }

        throw new JaversException(JaversExceptionCode.RUNTIME_EXCEPTION, "Query " + query + " is not supported");
    }

    public List<Change> runQuery(ChangeQuery query){
        Validate.argumentIsNotNull(query);

        if (query.isIdOnlyQuery()){
            return repository.getChangeHistory(fromDto(query.getIdFilter()), query.getLimit());
        }

        if (query.isPropertyQuery()){
            return repository.getPropertyChangeHistory(fromDto(query.getIdFilter()), query.getPropertyName(), query.getLimit());
        }

        throw new JaversException(JaversExceptionCode.RUNTIME_EXCEPTION, "Query " + query + " is not supported");
    }

    private GlobalId fromDto(GlobalIdDTO globalIdDTO) {
        return globalIdFactory.createFromDto(globalIdDTO);
    }
}
