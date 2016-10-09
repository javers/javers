package org.javers.repository.sql.finders;

import org.javers.common.collections.Optional;
import org.javers.common.collections.Pair;
import org.javers.core.json.CdoSnapshotSerialized;
import org.javers.core.json.JsonConverter;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.core.metamodel.object.GlobalId;
import org.javers.core.metamodel.type.EntityType;
import org.javers.core.metamodel.type.ManagedType;
import org.javers.repository.api.QueryParams;
import org.javers.repository.api.QueryParamsBuilder;
import org.javers.repository.api.SnapshotIdentifier;
import org.javers.repository.sql.pico.TableNameManager;
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
    private final TableNameManager tableNameManager;

    public CdoSnapshotFinder(PolyJDBC polyJDBC, GlobalIdRepository globalIdRepository, CommitPropertyFinder commitPropertyFinder, TableNameManager tableNameManager) {
        this.polyJDBC = polyJDBC;
        this.globalIdRepository = globalIdRepository;
        this.commitPropertyFinder = commitPropertyFinder;
        this.cdoSnapshotMapper = new CdoSnapshotMapper();
        this.tableNameManager = tableNameManager;
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
        return Optional.of(fetchCdoSnapshots(new SnapshotIdFilter(tableNameManager, maxSnapshot.get()), Optional.of(oneItemLimit)).get(0));
    }

    public List<CdoSnapshot> getSnapshots(QueryParams queryParams) {
        return fetchCdoSnapshots(new AnySnapshotFilter(tableNameManager), Optional.of(queryParams));
    }

    public List<CdoSnapshot> getSnapshots(Collection<SnapshotIdentifier> snapshotIdentifiers) {
        return fetchCdoSnapshots(new SnapshotIdentifiersFilter(tableNameManager, globalIdRepository, snapshotIdentifiers), Optional.<QueryParams>empty());
    }

    public List<CdoSnapshot> getStateHistory(ManagedType managedType, QueryParams queryParams) {
        ManagedClassFilter classFilter = new ManagedClassFilter(tableNameManager, managedType.getName(), queryParams.isAggregate());
        return fetchCdoSnapshots(classFilter, Optional.of(queryParams));
    }

    public List<CdoSnapshot> getVOStateHistory(EntityType ownerEntity, String fragment, QueryParams queryParams) {
        VoOwnerEntityFilter voOwnerFilter = new VoOwnerEntityFilter(tableNameManager, ownerEntity.getName(), fragment);
        return fetchCdoSnapshots(voOwnerFilter, Optional.of(queryParams));
    }

    public List<CdoSnapshot> getStateHistory(GlobalId globalId, QueryParams queryParams) {
        Optional<Long> globalIdPk = globalIdRepository.findGlobalIdPk(globalId);

        if (globalIdPk.isEmpty()){
            return Collections.emptyList();
        }
        return fetchCdoSnapshots(new GlobalIdFilter(tableNameManager, globalIdPk.get(), queryParams.isAggregate()), Optional.of(queryParams));
    }

    private List<CdoSnapshot> fetchCdoSnapshots(SnapshotFilter snapshotFilter, Optional<QueryParams> queryParams) {
        List<Pair<CdoSnapshotSerialized,Long>> serializedSnapshots = queryForCdoSnapshotDTOs(snapshotFilter, queryParams);

        List<CommitPropertyDTO> commitPropertyDTOs =
                commitPropertyFinder.findCommitPropertiesOfSnaphots(Pair.collectRightAsSet(serializedSnapshots));

        cdoSnapshotsEnricher.enrichWithCommitProperties(serializedSnapshots, commitPropertyDTOs);

        List<CdoSnapshot> result = new ArrayList<>();
        for (Pair<CdoSnapshotSerialized,Long> serializedSnapshot : serializedSnapshots) {
            result.add(jsonConverter.fromSerializedSnapshot(serializedSnapshot.left()));
        }

        return result;
    }

    private List<Pair<CdoSnapshotSerialized,Long>> queryForCdoSnapshotDTOs(SnapshotFilter snapshotFilter, Optional<QueryParams> queryParams) {
        SelectQuery query =  polyJDBC.query().select(snapshotFilter.select());
        snapshotFilter.addFrom(query);
        snapshotFilter.addWhere(query);
        if (queryParams.isPresent()) {
            snapshotFilter.applyQueryParams(query, queryParams.get());
        }
        query.orderBy(SNAPSHOT_PK, Order.DESC);
        return polyJDBC.queryRunner().queryList(query, cdoSnapshotMapper);
    }

    private Optional<Long> selectMaxSnapshotPrimaryKey(long globalIdPk) {
        SelectQuery query = polyJDBC.query()
            .select("MAX(" + SNAPSHOT_PK + ")")
            .from(tableNameManager.getSnapshotTableNameWithSchema())
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