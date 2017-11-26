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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

/**
 * Adapter from a JqlQuery to JaversRepository API
 *
 * @author bartosz.walacik
 */
public class QueryRunner {
    private static final Logger logger = LoggerFactory.getLogger(JqlQuery.JQL_LOGGER_NAME);

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

        List<Shadow> result = shadowQueryRunner.queryForShadows(query, snapshots);

        query.stats().stop();
        logger.debug("queryForShadows executed: {}", query);
        return result;
    }

    public Optional<CdoSnapshot> runQueryForLatestSnapshot(GlobalIdDTO globalId) {
        Validate.argumentIsNotNull(globalId);
        return repository.getLatest(globalIdFactory.createFromDto(globalId));
    }

    public List<CdoSnapshot> queryForSnapshots(JqlQuery query){
        compile(query);

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
            globalIdFactory.touchValueObjectFromPath(filter.getOwnerEntity(), filter.getPath());
            result = repository.getValueObjectStateHistory(filter.getOwnerEntity(), filter.getPath(), query.getQueryParams());
        } else {
            throw new JaversException(JaversExceptionCode.MALFORMED_JQL, "queryForSnapshots: " + query + " is not supported");
        }

        query.stats().logShallowQuery(result);
        return result;
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
