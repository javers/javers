package org.javers.repository.sql.finders;

import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

import org.javers.common.collections.Lists;
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
import org.javers.repository.sql.finders.SnapshotQuery.SnapshotDbIdentifier;
import org.javers.repository.sql.repositories.GlobalIdRepository;
import org.javers.repository.sql.schema.DBNameProvider;
import org.javers.repository.sql.session.Session;

public class CdoSnapshotFinder {

    private final GlobalIdRepository globalIdRepository;
    private final CommitPropertyFinder commitPropertyFinder;
    private final CdoSnapshotsEnricher cdoSnapshotsEnricher = new CdoSnapshotsEnricher();
    private JsonConverter jsonConverter;
    private final DBNameProvider dbNameProvider;

    public CdoSnapshotFinder(GlobalIdRepository globalIdRepository, CommitPropertyFinder commitPropertyFinder, DBNameProvider dbNameProvider) {
        this.globalIdRepository = globalIdRepository;
        this.commitPropertyFinder = commitPropertyFinder;
        this.dbNameProvider = dbNameProvider;
    }

    public Optional<CdoSnapshot> getLatest(GlobalId globalId, Session session, boolean loadCommitProps) {
        Optional<Long> globalIdPk = globalIdRepository.findGlobalIdPk(globalId, session);
        if (!globalIdPk.isPresent()){
            return Optional.empty();
        }

        return selectMaxSnapshotPrimaryKey(globalIdPk.get(), session).map(maxSnapshotId -> {
            QueryParams oneItemLimit = QueryParamsBuilder
                    .withLimit(1)
                    .withCommitProps(loadCommitProps)
                    .build();
            return fetchCdoSnapshots(q -> q.addSnapshotPkFilter(maxSnapshotId), oneItemLimit, session).get(0);
        });
    }

    public List<CdoSnapshot> getSnapshots(QueryParams queryParams, Session session) {
        return fetchCdoSnapshots(q -> {}, queryParams, session);
    }

    public List<CdoSnapshot> getSnapshots(Collection<SnapshotIdentifier> snapshotIdentifiers, Session session) {

        List<SnapshotDbIdentifier> snapshotIdentifiersWithPk = snapshotIdentifiers.stream()
                .map(si -> globalIdRepository.findGlobalIdPk(si.getGlobalId(), session)
                                             .map(id -> new SnapshotDbIdentifier(si, id)))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(toList());

        QueryParams queryParams = QueryParamsBuilder.withLimit(Integer.MAX_VALUE).build();
        return fetchCdoSnapshots(q -> q.addSnapshotIdentifiersFilter(snapshotIdentifiersWithPk), queryParams, session);
    }

    public List<CdoSnapshot> getStateHistory(Set<ManagedType> managedTypes, QueryParams queryParams, Session session) {
        Set<String> managedTypeNames = Sets.transform(managedTypes, managedType -> managedType.getName());
        return fetchCdoSnapshots(q -> q.addManagedTypesFilter(managedTypeNames), queryParams, session);
    }

    public List<CdoSnapshot> getVOStateHistory(EntityType ownerEntity, String fragment, QueryParams queryParams, Session session) {
        return fetchCdoSnapshots(q -> q.addVoOwnerEntityFilter(ownerEntity.getName(), fragment), queryParams, session);
    }

    public List<CdoSnapshot> getStateHistory(GlobalId globalId, QueryParams queryParams, Session session) {
        Optional<Long> globalIdPk = globalIdRepository.findGlobalIdPk(globalId, session);

        return globalIdPk.map(idPk -> fetchCdoSnapshots(q -> q.addGlobalIdFilter(idPk), queryParams, session))
                         .orElse(Collections.emptyList());
    }

    private List<CdoSnapshot> fetchCdoSnapshots(Consumer<SnapshotQuery> additionalFilter,
                                                QueryParams queryParams, Session session) {
        SnapshotQuery query = new SnapshotQuery(dbNameProvider, queryParams, session);
        additionalFilter.accept(query);
        List<CdoSnapshotSerialized> serializedSnapshots = query.run();

        if (queryParams.isLoadCommitProps()) {
            List<CommitPropertyDTO> commitPropertyDTOs = commitPropertyFinder.findCommitPropertiesOfSnaphots(
                    serializedSnapshots.stream().map(it -> it.getCommitPk()).collect(toList()),session);
            cdoSnapshotsEnricher.enrichWithCommitProperties(serializedSnapshots, commitPropertyDTOs);
        }

        return Lists.transform(serializedSnapshots,
                serializedSnapshot -> jsonConverter.fromSerializedSnapshot(serializedSnapshot));
    }

    private Optional<Long> selectMaxSnapshotPrimaryKey(long globalIdPk, Session session) {

        Optional<Long> maxPrimaryKey =  session
                .select("MAX(" + dbNameProvider.getSnapshotPKColumnName() + ")")
                .from(dbNameProvider.getSnapshotTableNameWithSchema())
                .and(dbNameProvider.getSnapshotGlobalIdFKColumnName(), globalIdPk)
                .queryName("select max snapshot's PK")
                .queryForOptionalLong();

        if (maxPrimaryKey.isPresent() && maxPrimaryKey.get() == 0){
            return Optional.empty();
        }
        return maxPrimaryKey;
    }

    public void setJsonConverter(JsonConverter jsonConverter) {
        this.jsonConverter = jsonConverter;
    }

}