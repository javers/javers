package org.javers.repository.sql.finders;

import org.javers.common.collections.Optional;
import org.javers.core.json.CdoSnapshotSerialized;
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

import java.util.*;

import static org.javers.repository.sql.PolyUtil.queryForOptionalLong;
import static org.javers.repository.sql.schema.FixedSchemaFactory.*;

public class CdoSnapshotFinder {

    private final PolyJDBC polyJDBC;
    private final GlobalIdRepository globalIdRepository;
    private final CommitPropertyFinder commitPropertyFinder;
    private final CdoSnapshotMapper cdoSnapshotMapper;
    private final CdoSnapshotsEnricher cdoSnapshotsEnricher = new CdoSnapshotsEnricher();
    private JsonConverter jsonConverter;

    public CdoSnapshotFinder(PolyJDBC polyJDBC, GlobalIdRepository globalIdRepository, CommitPropertyFinder commitPropertyFinder) {
        this.polyJDBC = polyJDBC;
        this.globalIdRepository = globalIdRepository;
        this.commitPropertyFinder = commitPropertyFinder;
        this.cdoSnapshotMapper = new CdoSnapshotMapper();
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
        return Optional.of(fetchCdoSnapshots(new SnapshotIdFilter(maxSnapshot.get()), Optional.of(oneItemLimit)).get(0));
    }

    public List<CdoSnapshot> getSnapshots(Collection<SnapshotIdentifier> snapshotIdentifiers) {
        return fetchCdoSnapshots(new SnapshotIdentifiersFilter(globalIdRepository, snapshotIdentifiers), Optional.<QueryParams>empty());
    }

    public List<CdoSnapshot> getStateHistory(ManagedType managedType, Optional<String> propertyName, QueryParams queryParams) {
        ManagedClassFilter classFilter = new ManagedClassFilter(managedType.getName(), propertyName);
        return fetchCdoSnapshots(classFilter, Optional.of(queryParams));
    }

    public List<CdoSnapshot> getVOStateHistory(EntityType ownerEntity, String fragment, QueryParams queryParams) {
        VoOwnerEntityFilter voOwnerFilter = new VoOwnerEntityFilter(ownerEntity.getName(), fragment);
        return fetchCdoSnapshots(voOwnerFilter, Optional.of(queryParams));
    }

    public List<CdoSnapshot> getStateHistory(GlobalId globalId, Optional<String> propertyName, QueryParams queryParams) {
        Optional<Long> globalIdPk = globalIdRepository.findGlobalIdPk(globalId);

        if (globalIdPk.isEmpty()){
            return Collections.emptyList();
        }

        return fetchCdoSnapshots(new GlobalIdFilter(globalIdPk.get(), propertyName), Optional.of(queryParams));
    }

    private List<CdoSnapshot> fetchCdoSnapshots(SnapshotFilter snapshotFilter, Optional<QueryParams> queryParams){
        List<CdoSnapshotSerialized> serializedSnapshots = queryForCdoSnapshotDTOs(snapshotFilter, queryParams);

        List<CommitPropertyDTO> commitPropertyDTOs =
                commitPropertyFinder.findCommitPropertiesOfSnaphots(serializedSnapshots);

        cdoSnapshotsEnricher.enrichWithCommitProperties(serializedSnapshots, commitPropertyDTOs);

        List<CdoSnapshot> result = new ArrayList<>();
        for (CdoSnapshotSerialized serializedSnapshot : serializedSnapshots) {
            result.add(jsonConverter.fromSerializedSnapshot(serializedSnapshot));
        }

        return result;
    }

    private List<CdoSnapshotSerialized> queryForCdoSnapshotDTOs(SnapshotFilter snapshotFilter, Optional<QueryParams> queryParams) {
        SelectQuery query =  polyJDBC.query().select(snapshotFilter.select());
        snapshotFilter.addFrom(query);
        snapshotFilter.addWhere(query);
        if (queryParams.isPresent()) {
            applyQueryParams(snapshotFilter, queryParams.get(), query);
        }
        query.orderBy(SNAPSHOT_PK, Order.DESC);
        return polyJDBC.queryRunner().queryList(query, cdoSnapshotMapper);
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
        addCommitPropertyConditions(snapshotFilter, query, queryParams.commitProperties());
        query.limit(queryParams.limit(), queryParams.skip());
    }

    private void addCommitPropertyConditions(SnapshotFilter snapshotFilter, SelectQuery query, Map<String, String> commitProperties) {
        for (Map.Entry<String, String> commitProperty : commitProperties.entrySet()) {
            snapshotFilter.addCommitPropertyCondition(query, commitProperty.getKey(), commitProperty.getValue());
        }
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

    public void setJsonConverter(JsonConverter jsonConverter) {
        this.jsonConverter = jsonConverter;
    }
}