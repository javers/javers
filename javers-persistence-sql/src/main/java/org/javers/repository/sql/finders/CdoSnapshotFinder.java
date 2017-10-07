package org.javers.repository.sql.finders;

import java.util.Optional;

import org.javers.common.collections.Lists;
import org.javers.common.collections.Pair;
import org.javers.common.collections.Sets;
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
import org.javers.repository.sql.schema.TableNameProvider;
import org.polyjdbc.core.PolyJDBC;
import org.polyjdbc.core.query.Order;
import org.polyjdbc.core.query.SelectQuery;

import java.util.*;

import static org.javers.repository.sql.poly.PolyUtil.queryForOptionalLong;
import static org.javers.repository.sql.schema.FixedSchemaFactory.*;

public class CdoSnapshotFinder {

    private final PolyJDBC polyJDBC;
    private final GlobalIdRepository globalIdRepository;
    private final CommitPropertyFinder commitPropertyFinder;
    private final CdoSnapshotMapper cdoSnapshotMapper;
    private final CdoSnapshotsEnricher cdoSnapshotsEnricher = new CdoSnapshotsEnricher();
    private JsonConverter jsonConverter;
    private final TableNameProvider tableNameProvider;

    public CdoSnapshotFinder(PolyJDBC polyJDBC, GlobalIdRepository globalIdRepository, CommitPropertyFinder commitPropertyFinder, TableNameProvider tableNameProvider) {
        this.polyJDBC = polyJDBC;
        this.globalIdRepository = globalIdRepository;
        this.commitPropertyFinder = commitPropertyFinder;
        this.cdoSnapshotMapper = new CdoSnapshotMapper();
        this.tableNameProvider = tableNameProvider;
    }

    public Optional<CdoSnapshot> getLatest(GlobalId globalId) {
        Optional<Long> globalIdPk = globalIdRepository.findGlobalIdPk(globalId);
        if (!globalIdPk.isPresent()){
            return Optional.empty();
        }

        return selectMaxSnapshotPrimaryKey(globalIdPk.get()).map(maxSnapshot -> {
            QueryParams oneItemLimit = QueryParamsBuilder.withLimit(1).build();
            return fetchCdoSnapshots(new SnapshotIdFilter(tableNameProvider, maxSnapshot), Optional.of(oneItemLimit)).get(0);
        });
    }

    public List<CdoSnapshot> getSnapshots(QueryParams queryParams) {
        return fetchCdoSnapshots(new AnySnapshotFilter(tableNameProvider), Optional.of(queryParams));
    }

    public List<CdoSnapshot> getSnapshots(Collection<SnapshotIdentifier> snapshotIdentifiers) {
        return fetchCdoSnapshots(new SnapshotIdentifiersFilter(tableNameProvider, globalIdRepository, snapshotIdentifiers), Optional.<QueryParams>empty());
    }

    public List<CdoSnapshot> getStateHistory(Set<ManagedType> managedTypes, QueryParams queryParams) {
        Set<String> managedTypeNames = Sets.transform(managedTypes, managedType -> managedType.getName());
        ManagedClassFilter classFilter = new ManagedClassFilter(tableNameProvider, managedTypeNames, queryParams.isAggregate());
        return fetchCdoSnapshots(classFilter, Optional.of(queryParams));
    }

    public List<CdoSnapshot> getVOStateHistory(EntityType ownerEntity, String fragment, QueryParams queryParams) {
        VoOwnerEntityFilter voOwnerFilter = new VoOwnerEntityFilter(tableNameProvider, ownerEntity.getName(), fragment);
        return fetchCdoSnapshots(voOwnerFilter, Optional.of(queryParams));
    }

    public List<CdoSnapshot> getStateHistory(GlobalId globalId, QueryParams queryParams) {
        Optional<Long> globalIdPk = globalIdRepository.findGlobalIdPk(globalId);

        return globalIdPk.map(id ->
                fetchCdoSnapshots(new GlobalIdFilter(tableNameProvider, id, queryParams.isAggregate()), Optional.of(queryParams)))
                .orElse(Collections.emptyList());
    }

    private List<CdoSnapshot> fetchCdoSnapshots(SnapshotFilter snapshotFilter, Optional<QueryParams> queryParams) {
        List<Pair<CdoSnapshotSerialized,Long>> serializedSnapshots = queryForCdoSnapshotDTOs(snapshotFilter, queryParams);

        List<CommitPropertyDTO> commitPropertyDTOs =
                commitPropertyFinder.findCommitPropertiesOfSnaphots(Pair.collectRightAsSet(serializedSnapshots));

        cdoSnapshotsEnricher.enrichWithCommitProperties(serializedSnapshots, commitPropertyDTOs);

        return Lists.transform(serializedSnapshots,
                serializedSnapshot -> jsonConverter.fromSerializedSnapshot(serializedSnapshot.left()));
    }

    private List<Pair<CdoSnapshotSerialized,Long>> queryForCdoSnapshotDTOs(SnapshotFilter snapshotFilter, Optional<QueryParams> queryParams) {
        SelectQuery query =  polyJDBC.query().select(snapshotFilter.select());
        snapshotFilter.addFrom(query);
        snapshotFilter.addWhere(query);
        if (queryParams.isPresent()) {
            snapshotFilter.applyQueryParams(query, queryParams.get());
        }
        query.orderBy(SNAPSHOT_PK, Order.DESC);
        //TODO HOT SPOT!!!
        return polyJDBC.queryRunner().queryList(query, cdoSnapshotMapper);
    }

    private Optional<Long> selectMaxSnapshotPrimaryKey(long globalIdPk) {
        SelectQuery query = polyJDBC.query()
            .select("MAX(" + SNAPSHOT_PK + ")")
            .from(tableNameProvider.getSnapshotTableNameWithSchema())
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