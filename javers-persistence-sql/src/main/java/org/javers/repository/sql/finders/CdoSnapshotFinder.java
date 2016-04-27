package org.javers.repository.sql.finders;

import org.javers.common.collections.Optional;
import org.javers.core.json.JsonConverter;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.core.metamodel.object.GlobalId;
import org.javers.core.metamodel.type.EntityType;
import org.javers.core.metamodel.type.ManagedType;
import org.javers.repository.api.QueryParams;
import org.javers.repository.api.QueryParamsBuilder;
import org.javers.repository.api.SnapshotIdentifier;
import org.javers.repository.sql.repositories.GlobalIdRepository;
import org.polyjdbc.core.PolyJDBC;
import org.polyjdbc.core.query.Order;
import org.polyjdbc.core.query.SelectQuery;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.javers.repository.sql.PolyUtil.queryForOptionalLong;
import static org.javers.repository.sql.schema.FixedSchemaFactory.*;

public class CdoSnapshotFinder {

    private final PolyJDBC polyJDBC;
    private JsonConverter jsonConverter;
    private GlobalIdRepository globalIdRepository;

    public CdoSnapshotFinder(GlobalIdRepository globalIdRepository, PolyJDBC polyJDBC) {
        this.globalIdRepository = globalIdRepository;
        this.polyJDBC = polyJDBC;
    }

    public Optional<CdoSnapshot> getLatest(GlobalId globalId) {
        Optional<Long> globalIdPk = globalIdRepository.findGlobalIdPk(globalId);
        if (globalIdPk.isEmpty()){
            return Optional.empty();
        }

        Optional<Long> maxSnapshot = selectMaxSnapshotPrimaryKey(globalIdPk.get());

        if (maxSnapshot.isEmpty()) {
            return Optional.empty();
        }

        QueryParams oneItemLimit = QueryParamsBuilder.withLimit(1).build();
        return Optional.of(queryForCdoSnapshots(new SnapshotIdFilter(maxSnapshot.get()), Optional.of(globalId), Optional.of(oneItemLimit)).get(0));
    }

    public List<CdoSnapshot> getSnapshots(QueryParams queryParams) {
        return queryForCdoSnapshots(new AnySnapshotFilter(), Optional.<GlobalId>empty(), Optional.of(queryParams));
    }

    public List<CdoSnapshot> getSnapshots(Collection<SnapshotIdentifier> snapshotIdentifiers) {
        return queryForCdoSnapshots(new SnapshotIdentifiersFilter(globalIdRepository, snapshotIdentifiers), Optional.<GlobalId>empty(), Optional.<QueryParams>empty());
    }

    public List<CdoSnapshot> getStateHistory(ManagedType managedType, Optional<String> propertyName, QueryParams queryParams) {
        Optional<Long> classPk = globalIdRepository.findClassPk(managedType.getName());
        if (classPk.isEmpty()){
            return Collections.emptyList();
        }

        ManagedClassFilter classFilter = new ManagedClassFilter(classPk.get(), propertyName);

        return queryForCdoSnapshots(classFilter, Optional.<GlobalId>empty(), Optional.of(queryParams));
    }

    public List<CdoSnapshot> getVOStateHistory(EntityType ownerEntity, String fragment, QueryParams queryParams) {
        Optional<Long> ownerEntityClassPk = globalIdRepository.findClassPk(ownerEntity.getName());
        if (ownerEntityClassPk.isEmpty()){
            return Collections.emptyList();
        }

        VoOwnerEntityFilter voOwnerFilter = new VoOwnerEntityFilter(ownerEntityClassPk.get(), fragment);

        return queryForCdoSnapshots(voOwnerFilter, Optional.<GlobalId>empty(), Optional.of(queryParams));
    }

    public List<CdoSnapshot> getStateHistory(GlobalId globalId, Optional<String> propertyName, QueryParams queryParams) {
        Optional<Long> globalIdPk = globalIdRepository.findGlobalIdPk(globalId);

        if (globalIdPk.isEmpty()){
            return Collections.emptyList();
        }

        return queryForCdoSnapshots(new GlobalIdFilter(globalIdPk.get(), propertyName), Optional.of(globalId), Optional.of(queryParams));
    }

    public void setJsonConverter(JsonConverter jsonConverter) {
        this.jsonConverter = jsonConverter;
    }

    private List<CdoSnapshot> queryForCdoSnapshots(SnapshotFilter snapshotFilter, Optional<GlobalId> providedGlobalId, Optional<QueryParams> queryParams){

        SelectQuery query =  polyJDBC.query().select(snapshotFilter.select());
        snapshotFilter.addFrom(query);
        snapshotFilter.addWhere(query);
        if (queryParams.isPresent()) {
            applyQueryParams(snapshotFilter, queryParams.get(), query);
        }
        query.orderBy(SNAPSHOT_PK, Order.DESC);

        return polyJDBC.queryRunner().queryList(query, new CdoSnapshotObjectMapper(jsonConverter, providedGlobalId));
    }

    private void applyQueryParams(SnapshotFilter snapshotFilter, QueryParams queryParams, SelectQuery query) {
        if (queryParams.from().isPresent()) {
            snapshotFilter.addFromDateCondition(query, queryParams.from().get());
        }
        if (queryParams.to().isPresent()) {
            snapshotFilter.addToDateCondition(query, queryParams.to().get());
        }
        if (queryParams.commitId().isPresent()) {
            snapshotFilter.addCommitIdCondition(query, queryParams.commitId().get());
        }
        if (queryParams.version().isPresent()) {
            snapshotFilter.addVersionCondition(query, queryParams.version().get());
        }
        if (queryParams.author().isPresent()) {
            snapshotFilter.addAuthorCondition(query, queryParams.author().get());
        }
        query.limit(queryParams.limit(), queryParams.skip());
    }

    private Optional<Long> selectMaxSnapshotPrimaryKey(long globalIdPk) {
        SelectQuery query = polyJDBC.query()
            .select("MAX(" + SNAPSHOT_PK + ")")
            .from(SNAPSHOT_TABLE_NAME)
            .where(SNAPSHOT_GLOBAL_ID_FK + " = :globalIdPk")
            .withArgument("globalIdPk", globalIdPk);

        Optional<Long> result = queryForOptionalLong(query, polyJDBC);

        if (result.isPresent() && result.get() == 0){
            return Optional.empty();
        }
        return result;
    }
}