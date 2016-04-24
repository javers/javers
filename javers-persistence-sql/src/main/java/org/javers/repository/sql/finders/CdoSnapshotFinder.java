package org.javers.repository.sql.finders;

import com.google.common.collect.ImmutableMap;
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
import org.polyjdbc.core.dialect.Dialect;
import org.polyjdbc.core.dialect.MysqlDialect;
import org.polyjdbc.core.query.Order;
import org.polyjdbc.core.query.SelectQuery;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.javers.repository.sql.PolyUtil.queryForOptionalLong;
import static org.javers.repository.sql.schema.FixedSchemaFactory.*;

public class CdoSnapshotFinder {

    private final PolyJDBC polyJDBC;
    private final Dialect dialect;
    private JsonConverter jsonConverter;
    private GlobalIdRepository globalIdRepository;

    public CdoSnapshotFinder(GlobalIdRepository globalIdRepository, PolyJDBC polyJDBC, Dialect dialect) {
        this.globalIdRepository = globalIdRepository;
        this.polyJDBC = polyJDBC;
        this.dialect = dialect;
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

    public List<CdoSnapshot> getSnapshots(Collection<SnapshotIdentifier> snapshotIdentifiers) {
        return queryForCdoSnapshots(new SnapshotIdentifiersFilter(globalIdRepository, snapshotIdentifiers), Optional.<GlobalId>empty(), Optional.<QueryParams>empty());
    }

    public List<CdoSnapshot> getStateHistory(ManagedType managedType, Optional<String> propertyName, QueryParams queryParams) {
        ManagedClassFilter classFilter = new ManagedClassFilter(managedType.getName(), propertyName);
        return queryForCdoSnapshots(classFilter, Optional.<GlobalId>empty(), Optional.of(queryParams));
    }

    public List<CdoSnapshot> getVOStateHistory(EntityType ownerEntity, String fragment, QueryParams queryParams) {
        VoOwnerEntityFilter voOwnerFilter = new VoOwnerEntityFilter(ownerEntity.getName(), fragment);
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
        if (queryParams.hasCommitProperties()) {
            addCommitPropertyConditions(snapshotFilter, query, queryParams.commitProperties().get());
        }
        query.limit(queryParams.limit(), queryParams.skip());
    }

    private void addCommitPropertyConditions(SnapshotFilter snapshotFilter, SelectQuery query, Map<String, String> commitProperties) {
        for (Map.Entry<String, String> commitProperty : commitProperties.entrySet()) {
            String serializedProperty = prepareSerializedCommitProperty(commitProperty.getKey(), commitProperty.getValue());
            snapshotFilter.addCommitPropertyCondition(query, serializedProperty);
        }
    }

    private String prepareSerializedCommitProperty(String propertyName, String propertyValue) {
        String serializedProperty = jsonConverter.toJson(ImmutableMap.of(propertyName, propertyValue));
        serializedProperty = removeOuterCurlyBraces(serializedProperty);
        serializedProperty = escapeBackslashes(serializedProperty);
        return serializedProperty;
    }

    private String removeOuterCurlyBraces(String serializedProperty) {
        if (serializedProperty.startsWith("{") && serializedProperty.endsWith("}")) {
            return serializedProperty.substring(1, serializedProperty.length() - 1).trim();
        } else {
            return serializedProperty;
        }
    }

    private String escapeBackslashes(String serializedProperty) {
        if (dialect instanceof MysqlDialect) {
            serializedProperty = serializedProperty.replace("\\", "\\\\\\\\");
        } else {
            serializedProperty = serializedProperty.replace("\\", "\\\\");
        }
        return serializedProperty;
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