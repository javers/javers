package org.javers.repository.jql;

import org.javers.common.exception.JaversException;
import org.javers.common.exception.JaversExceptionCode;
import org.javers.common.validation.Validate;
import org.javers.core.diff.Change;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.core.metamodel.object.GlobalId;
import org.javers.core.metamodel.object.GlobalIdFactory;
import org.javers.core.metamodel.type.TypeMapper;
import org.javers.repository.api.JaversExtendedRepository;
import org.javers.shadow.Shadow;

import java.util.List;
import java.util.Optional;

/**
 * Adapter from a JqlQuery to JaversRepository API
 *
 * @author bartosz.walacik
 */
public class QueryRunner {
    private final JaversExtendedRepository repository;
    private final GlobalIdFactory globalIdFactory;
    private final TypeMapper typeMapper;
    private final ShadowQueryRunner shadowQueryRunner;

    public QueryRunner(JaversExtendedRepository repository, GlobalIdFactory globalIdFactory, TypeMapper typeMapper, ShadowQueryRunner shadowQueryRunner) {
        this.repository = repository;
        this.globalIdFactory = globalIdFactory;
        this.typeMapper = typeMapper;
        this.shadowQueryRunner = shadowQueryRunner;
    }

    public List<Shadow> queryForShadows(JqlQuery query) {
        compile(query);

        List<CdoSnapshot> snapshots = queryForSnapshots(query);

        return shadowQueryRunner.queryForShadows(query, snapshots);
    }

    public Optional<CdoSnapshot> runQueryForLatestSnapshot(GlobalIdDTO globalId) {
        Validate.argumentIsNotNull(globalId);
        return repository.getLatest(globalIdFactory.createFromDto(globalId));
    }

    public List<CdoSnapshot> queryForSnapshots(JqlQuery query){
        compile(query);

        if (query.isAnyDomainObjectQuery()) {
            return repository.getSnapshots(query.getQueryParams());
        }

        if (query.isIdQuery()){
            return repository.getStateHistory(query.getIdFilter(), query.getQueryParams());
        }

        if (query.isClassQuery()){
            return repository.getStateHistory(query.getClassFilter(), query.getQueryParams());
        }

        if (query.isVoOwnerQuery()) {
            VoOwnerFilter filter = query.getVoOwnerFilter();
            globalIdFactory.touchValueObjectFromPath(filter.getOwnerEntity(), filter.getPath());
            return repository.getValueObjectStateHistory(filter.getOwnerEntity(), filter.getPath(), query.getQueryParams());
        }

        throw new JaversException(JaversExceptionCode.MALFORMED_JQL, "queryForSnapshots: " + query + " is not supported");
    }

    public List<Change> queryForChanges(JqlQuery query) {
        compile(query);

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
            globalIdFactory.touchValueObjectFromPath(filter.getOwnerEntity(), filter.getPath());
            return repository.getValueObjectChangeHistory(
                    filter.getOwnerEntity(), filter.getPath(), query.getQueryParams());
        }

        throw new JaversException(JaversExceptionCode.MALFORMED_JQL, "queryForChanges: " + query + " is not supported");
    }

    private GlobalId fromInstance(Object instance) {
        return globalIdFactory.createInstanceId(instance);
    }

    private void compile(JqlQuery query) {
        Validate.argumentIsNotNull(query);
        query.compile(globalIdFactory, typeMapper);
    }
}
