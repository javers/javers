package org.javers.repository.jql;

import org.javers.common.collections.Consumer;
import org.javers.common.collections.Lists;
import org.javers.common.string.ToStringBuilder;
import org.javers.common.validation.Validate;
import org.javers.core.CommitIdGenerator;
import org.javers.core.CoreConfiguration;
import org.javers.core.commit.CommitId;
import org.javers.core.commit.CommitMetadata;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.core.metamodel.object.GlobalId;
import org.javers.core.metamodel.object.InstanceId;
import org.javers.core.metamodel.object.ValueObjectId;
import org.javers.repository.api.JaversExtendedRepository;
import org.javers.repository.api.QueryParams;
import org.javers.repository.api.QueryParamsBuilder;
import org.javers.shadow.Shadow;
import org.javers.shadow.ShadowFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static org.javers.repository.jql.JqlQuery.JQL_LOGGER_NAME;

/**
 * @author bartosz.walacik
 */
class ShadowQueryRunner {
    private static final Logger logger = LoggerFactory.getLogger(JQL_LOGGER_NAME);

    private final SnapshotQueryRunner snapshotQueryRunner;
    private final JaversExtendedRepository repository;
    private final ShadowFactory shadowFactory;
    private final CoreConfiguration javersCoreConfiguration;

    ShadowQueryRunner(SnapshotQueryRunner snapshotQueryRunner, JaversExtendedRepository repository, ShadowFactory shadowFactory, CoreConfiguration javersCoreConfiguration) {
        this.snapshotQueryRunner = snapshotQueryRunner;
        this.repository = repository;
        this.shadowFactory = shadowFactory;
        this.javersCoreConfiguration = javersCoreConfiguration;
    }

    ShadowQueryResult queryForShadows(JqlQuery query, List<CdoSnapshot> gapsFilledInPreviousQuery) {
        ShadowStats queryStats = new ShadowStats();

        List<CdoSnapshot> coreSnapshots = queryForCoreSnapshots(query, queryStats);

        CommitTable commitTable = new CommitTable(
                coreSnapshots,
                query.getMaxGapsToFill(),
                query,
                queryStats);

        commitTable.appendSnapshots(gapsFilledInPreviousQuery);

        if (query.getShadowScope().isCommitDeep()) {
            commitTable.loadFullCommits();
        }

        List<Shadow> shadows = commitTable.rootsForQuery(query).stream()
                .map(r -> shadowFactory.createShadow(r.root, r.context, (cm, targetId) -> commitTable.referenceResolver(cm, targetId)))
                .collect(toList());

        queryStats.stop();
        ShadowQueryResult result = new ShadowQueryResult(shadows, commitTable.getFilledGapsSnapshots(), queryStats);
        return result;
    }

    private List<CdoSnapshot> queryForCoreSnapshots(JqlQuery query, ShadowStats queryStats) {
        List<CdoSnapshot> snapshots = snapshotQueryRunner.queryForSnapshots(query)
                .stream()
                .filter(s -> !s.isTerminalVO())
                .collect(toList());
        queryStats.logShallowQuery(snapshots);

        return snapshots;
    }

    private static class ShadowRoot {
        final CommitMetadata context;
        final CdoSnapshot root;

        ShadowRoot(CdoSnapshot root) {
            this.context = root.getCommitMetadata();
            this.root = root;
        }

        ShadowRoot(CommitMetadata context, CdoSnapshot root) {
            this.context = context;
            this.root = root;
        }
    }

    private class CommitTable {
        private final int maxGapsToFill;
        private final Map<CommitMetadata, CommitEntry> commitsMap;
        private final JqlQuery query;
        private int filledGapsCount;
        private final List<CdoSnapshot> filledGapsSnapshots = new ArrayList<>();
        private final ShadowStats queryStats;

        CommitTable(List<CdoSnapshot> coreSnapshots, int maxGapsToFill, JqlQuery query, ShadowStats queryStats) {
            this.maxGapsToFill = maxGapsToFill;
            this.query = query;
            this.commitsMap = new TreeMap<>(javersCoreConfiguration.getCommitIdGenerator().getComparator());
            appendSnapshots(coreSnapshots);
            this.queryStats = queryStats;
        }

        List<ShadowRoot> rootsForQuery(JqlQuery query) {
            fillMissingParents();

            final List<CommitEntry> orderedCommits = new ArrayList<>();
            commitsMap.values().forEach(it -> orderedCommits.add(0,it));

            return orderedCommits.stream()
                    .flatMap(e -> e.getAllStream()
                            .filter(s -> query.matches(s.getGlobalId()))
                            .map(s -> new ShadowRoot(e.commitMetadata, s)))
                    .collect(Collectors.toList());
        }

        void loadFullCommits() {
            if (commitsMap.isEmpty()) {
                return;
            }
            QueryParams params = QueryParamsBuilder
                    .withLimit(Integer.MAX_VALUE)
                    .commitIds(commitsMap.keySet().stream().map(it -> it.getId()).collect(toSet()))
                    .build();
            List<CdoSnapshot> fullCommitsSnapshots = repository.getSnapshots(params);
            queryStats.logQueryInCommitDeepScope(fullCommitsSnapshots);

            appendSnapshots(fullCommitsSnapshots);
        }

        CdoSnapshot referenceResolver(CommitMetadata rootContext, GlobalId targetId) {
            SnapshotReference reference = new SnapshotReference(rootContext, targetId);

            if (!commitsMap.containsKey(rootContext)) {
                return null;
            }

            CdoSnapshot latest = findLatestToInCommitTable(reference);
            if (latest == null) {
                appendSnapshots(fillGapFromRepository(reference, 15));
            }

            latest = findLatestToInCommitTable(reference);
            if (latest == null){
                queryStats.logMaxGapsToFillExceededInfo(targetId);
            }
            return latest;
        }

        List<CdoSnapshot> getFilledGapsSnapshots() {
            return filledGapsSnapshots;
        }

        private CdoSnapshot findLatestToInCommitTable(SnapshotReference reference) {
            final List<CdoSnapshot> found = new ArrayList<>();

            iterateUntil(ce -> {
                if (ce.getAny(reference.targetId()) != null) {
                    found.add(ce.getAny(reference.targetId()));
                }
            }, reference.timepointCommitId());

            if (found.size() == 0) {
                return null;
            } else {
                return found.get(found.size() - 1);
            }
        }

        private boolean isInChildValueObjectScope(SnapshotReference snapshotReference) {
            return query.isAggregate() && snapshotReference.targetId() instanceof ValueObjectId;
        }

        List<CdoSnapshot> fillGapFromRepository(SnapshotReference snapshotReference, int limit) {
            if (filledGapsCount >= maxGapsToFill && !isInChildValueObjectScope(snapshotReference)) {
                return Collections.emptyList();
            }

            List<CdoSnapshot> historicals;
            if (isInChildValueObjectScope(snapshotReference)) {
                historicals = getHistoricals(snapshotReference.targetId(), snapshotReference, false, limit);
                queryStats.logQueryInChildValueObjectScope(snapshotReference.targetId(), snapshotReference.timepointCommitId(), historicals.size());
            }
            else {
                historicals = getHistoricals(snapshotReference.targetId(), snapshotReference, query.isAggregate(), limit);
                queryStats.logQueryInDeepPlusScope(snapshotReference.targetId(), snapshotReference.timepointCommitId(), historicals.size());
            }

            filledGapsCount++;
            filledGapsSnapshots.addAll(historicals);
            return historicals;
        }

        private List<CdoSnapshot> getHistoricals(GlobalId globalId, SnapshotReference timePoint, boolean withChildValueObjects, int limit) {
            if (javersCoreConfiguration.getCommitIdGenerator() == CommitIdGenerator.SYNCHRONIZED_SEQUENCE){
                return repository.getHistoricals(globalId, timePoint.timepointCommitId(), withChildValueObjects, limit);
            }
            return repository.getHistoricals(globalId, timePoint.timepoint().getCommitDate(), withChildValueObjects, limit);
        }

        void fillMissingParents() {
            Map<GlobalId, CdoSnapshot> movingLatest = new HashMap<>();

            commitsMap.values().forEach(commitEntry -> {
                commitEntry.getMissingParents().stream()
                        .filter(movingLatest::containsKey)
                        .forEach(voId -> {
                            commitEntry.append(movingLatest.get(voId));
                        });

                //update movingLatest
                commitEntry.getAllStream().forEach(e -> movingLatest.put(e.getGlobalId(), e));
            });
        }

        void appendSnapshots(Collection<CdoSnapshot> snapshots) {
            snapshots.forEach(it -> appendSnapshot(it));
        }

        CommitEntry appendSnapshot(CdoSnapshot snapshot) {
            CommitEntry entry = commitsMap.get(snapshot.getCommitMetadata());
            if (entry == null) {
                entry = new CommitEntry(snapshot.getCommitMetadata());
                commitsMap.put(snapshot.getCommitMetadata(), entry);
            }
            entry.append(snapshot);
            return entry;
        }

        void iterateUntil(Consumer<CommitEntry> consumer, CommitId bound) {
            for(CommitEntry ce : commitsMap.values()) {
                consumer.consume(ce);
                if (ce.commitMetadata.getId().equals(bound)) {
                    break;
                }
            }
        }
    }

    private static class CommitEntry {
        private final CommitMetadata commitMetadata;
        private final Map<GlobalId, CdoSnapshot> entities = new HashMap<>();
        private final Map<ValueObjectId, CdoSnapshot> valueObjects = new HashMap<>();

        CommitEntry(CommitMetadata commitMetadata) {
            this.commitMetadata = commitMetadata;
        }

        void append(CdoSnapshot snapshot){
            if (snapshot.getGlobalId() instanceof InstanceId) {
                entities.put(snapshot.getGlobalId(), snapshot);
            }

            if (snapshot.getGlobalId() instanceof ValueObjectId) {
                valueObjects.put((ValueObjectId)snapshot.getGlobalId(), snapshot);;
            }
        }

        CdoSnapshot getAny(GlobalId globalId) {
            if (entities.containsKey(globalId)) {
                return entities.get(globalId);
            }
            return valueObjects.get(globalId);
        }

        Collection<CdoSnapshot> getEntities() {
            return entities.values();
        }

        Stream<CdoSnapshot> getAllStream() {
            return Stream.concat(valueObjects.values().stream(), entities.values().stream());
        }

        Set<GlobalId> getMissingParents() {
            Set<GlobalId> result = valueObjects.keySet().stream()
                    .map(voId -> voId.getOwnerId())
                    .filter(instanceId -> !entities.containsKey(instanceId))
                    .collect(toSet());

            result.addAll(valueObjects.keySet().stream()
                    .flatMap(voId -> voId.getParentValueObjectIds().stream())
                    .filter(voId -> ! valueObjects.containsKey(voId))
                    .collect(toSet()));

            return result;
        }
    }

    static class SnapshotReference {
        private final CommitMetadata timepoint;
        private final GlobalId targetId;

        SnapshotReference(CommitMetadata rootContext, GlobalId targetId) {
            Validate.argumentsAreNotNull(rootContext, targetId);
            this.timepoint = rootContext;
            this.targetId = targetId;
        }

        CommitMetadata timepoint() {
            return timepoint;
        }

        GlobalId targetId() {
            return targetId;
        }

        CommitId timepointCommitId() {
            return timepoint.getId();
        }
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            SnapshotReference that = (SnapshotReference) o;
            return Objects.equals(timepointCommitId(), timepointCommitId()) &&
                   Objects.equals(targetId, that.targetId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(timepointCommitId(), targetId);
        }
    }

    static class ShadowQueryResult {
        private final List<Shadow> shadows;
        private final List<CdoSnapshot> filledGapsSnapshots;
        private final ShadowStats queryStats;

        ShadowQueryResult(List<Shadow> shadows, List<CdoSnapshot> filledGapsSnapshots, ShadowStats queryStats) {
            this.shadows = shadows;
            this.filledGapsSnapshots = filledGapsSnapshots;
            this.queryStats = queryStats;
        }

        List<Shadow> getShadows() {
            return shadows;
        }

        List<CdoSnapshot> getFilledGapsSnapshots() {
            return filledGapsSnapshots;
        }

        ShadowStats getQueryStats() {
            return queryStats;
        }
    }

    static class ShadowStats {
        private long startTimestamp = System.currentTimeMillis();
        private long endTimestamp;
        private int dbQueriesCount;
        private int allSnapshotsCount;
        private int shallowSnapshotsCount;
        private int deepPlusSnapshotsCount;
        private int commitDeepSnapshotsCount;
        private int childVOSnapshotsCount;
        private int deepPlusGapsFilled;
        private int deepPlusGapsLeft;

        void logQueryInChildValueObjectScope(GlobalId reference, CommitId context, int snapshotsLoaded) {
            validateChange();
            logger.debug("CHILD_VALUE_OBJECT query for '{}' at timepointCommitId {}, {} snapshot(s) loaded",
                    reference.toString(),
                    context.value(),
                    snapshotsLoaded);

            dbQueriesCount++;
            allSnapshotsCount += snapshotsLoaded;
            childVOSnapshotsCount += snapshotsLoaded;
        }

        void logMaxGapsToFillExceededInfo(GlobalId reference) {
            validateChange();
            deepPlusGapsLeft++;
            logger.debug("warning: object '" + reference.toString() +
                    "' is outside of the DEEP_PLUS+{} scope" +
                    ", references to this object will be nulled. " +
                    "Increase maxGapsToFill and fill all gaps in your object graph.", deepPlusGapsFilled);
        }

        void logQueryInDeepPlusScope(GlobalId reference, CommitId context, int snapshotsLoaded) {
            validateChange();
            dbQueriesCount++;
            allSnapshotsCount += snapshotsLoaded;
            deepPlusSnapshotsCount += snapshotsLoaded;
            deepPlusGapsFilled++;

            logger.debug("DEEP_PLUS query for '{}' at timepointCommitId {}, {} snapshot(s) loaded, gaps filled so far: {}",
                    reference.toString(),
                    context.value(),
                    snapshotsLoaded,
                    deepPlusGapsFilled);
        }

        void logShallowQuery(List<CdoSnapshot> snapshots) {
            validateChange();
            logger.debug("SHALLOW query (core snapshots): {} snapshots loaded (entities: {}, valueObjects: {})", snapshots.size(),
                    snapshots.stream().filter(it -> it.getGlobalId() instanceof InstanceId).count(),
                    snapshots.stream().filter(it -> it.getGlobalId() instanceof ValueObjectId).count());
            dbQueriesCount++;
            allSnapshotsCount += snapshots.size();
            shallowSnapshotsCount += snapshots.size();
        }

        void logQueryInCommitDeepScope(List<CdoSnapshot> snapshots) {
            validateChange();
            logger.debug("COMMIT_DEEP query: {} snapshots loaded", snapshots.size());
            dbQueriesCount++;
            allSnapshotsCount += snapshots.size();
            commitDeepSnapshotsCount+=snapshots.size();
        }

        void stop() {
            validateChange();
            endTimestamp = System.currentTimeMillis();
        }

        @Override
        public String toString() {
            if (getEndTimestamp() == 0){
                return ToStringBuilder.toString(this,
                        "still running", "?");
            }
            return ToStringBuilder.toStringBlockStyle(this, "  ", toStringProps().toArray());
        }

        List<Object> toStringProps() {
            return Lists.asList(
                    "executed in millis", getEndTimestamp()-getStartTimestamp(),
                    "DB queries", getDbQueriesCount(),
                    "snapshots loaded", getAllSnapshotsCount(),
                    "SHALLOW snapshots", getShallowSnapshotsCount(),
                    "COMMIT_DEEP snapshots", getCommitDeepSnapshotsCount(),
                    "CHILD_VALUE_OBJECT snapshots", getChildVOSnapshotsCount(),
                    "DEEP_PLUS snapshots", getDeepPlusSnapshotsCount(),
                    "gaps filled", getDeepPlusGapsFilled(),
                    "gaps left!", getDeepPlusGapsLeft()
            );
        }

        public int getDbQueriesCount() {
            return dbQueriesCount;
        }

        /**
         * number of all snapshots loaded from a JaversRepository
         */
        public int getAllSnapshotsCount() {
            return allSnapshotsCount;
        }

        public long getStartTimestamp() {
            return startTimestamp;
        }

        public long getEndTimestamp() {
            return endTimestamp;
        }

        public int getShallowSnapshotsCount() {
            return shallowSnapshotsCount;
        }

        public int getDeepPlusSnapshotsCount() {
            return deepPlusSnapshotsCount;
        }

        public int getCommitDeepSnapshotsCount() {
            return commitDeepSnapshotsCount;
        }

        public int getChildVOSnapshotsCount() {
            return childVOSnapshotsCount;
        }

        public int getDeepPlusGapsFilled() {
            return deepPlusGapsFilled;
        }

        public int getDeepPlusGapsLeft() {
            return deepPlusGapsLeft;
        }

        private void validateChange() {
            if (endTimestamp > 0) {
                throw new RuntimeException(new IllegalAccessException("executed query can't be changed"));
            }
        }
    }
}
