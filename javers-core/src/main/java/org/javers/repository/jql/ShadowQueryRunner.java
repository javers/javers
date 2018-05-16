package org.javers.repository.jql;

import org.javers.common.collections.Consumer;
import org.javers.common.validation.Validate;
import org.javers.core.CommitIdGenerator;
import org.javers.core.JaversCoreConfiguration;
import org.javers.core.commit.CommitId;
import org.javers.core.commit.CommitMetadata;
import org.javers.core.metamodel.object.*;
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

/**
 * @author bartosz.walacik
 */
public class ShadowQueryRunner {
    private static final Logger logger = LoggerFactory.getLogger(JqlQuery.JQL_LOGGER_NAME);

    private final JaversExtendedRepository repository;
    private final ShadowFactory shadowFactory;
    private final JaversCoreConfiguration javersCoreConfiguration;

    public ShadowQueryRunner(JaversExtendedRepository repository, ShadowFactory shadowFactory, JaversCoreConfiguration javersCoreConfiguration) {
        this.repository = repository;
        this.shadowFactory = shadowFactory;
        this.javersCoreConfiguration = javersCoreConfiguration;
    }

    public List<Shadow> queryForShadows(JqlQuery query, List<CdoSnapshot> coreSnapshots) {
        final CommitTable commitTable =
                new CommitTable(coreSnapshots, query.getMaxGapsToFill(), query, javersCoreConfiguration.getCommitIdGenerator());

        if (query.getShadowScope().isShallow()) {
            //TODO load all Child VO
            // List of Instance Id
            // commitId bounds
            // CHILD_ONLY
        }

        if (query.getShadowScope().isCommitDeep()) {
            commitTable.loadFullCommits();
        }

        List<Shadow> shadows = commitTable.rootsForQuery(query).stream()
                .map(r -> shadowFactory.createShadow(r.root, r.context, (cm, targetId) -> commitTable.findLatestTo(cm, targetId)))
                .collect(toList());

        return shadows;
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

    class CommitTable {
        private final Set<GlobalId> rootSnapshotIds;
        private final int maxGapsToFill;
        private int filledGaps;
        private final Map<CommitMetadata, CommitEntry> commitsMap;
        private final JqlQuery query;

        CommitTable(List<CdoSnapshot> rootSnapshots, int maxGapsToFill, JqlQuery query, CommitIdGenerator commitIdGenerator) {
            this.maxGapsToFill = maxGapsToFill;
            this.query = query;
            this.commitsMap = new TreeMap<>(commitIdGenerator.getComparator());
            this.rootSnapshotIds = rootSnapshots.stream().map(it -> it.getGlobalId()).collect(toSet());
            appendSnapshots(rootSnapshots);
        }

        List<ShadowRoot> rootsForQuery(JqlQuery query) {
           // fillMissingParents();

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
            query.stats().logQueryInCommitDeepScope(fullCommitsSnapshots);

            appendSnapshots(fullCommitsSnapshots);
        }

        CdoSnapshot findLatestTo(CommitMetadata rootContext, GlobalId targetId) {

            if (!commitsMap.containsKey(rootContext)) {
                return null;
            }

            CdoSnapshot latest = findLatestToInCommitTable(rootContext.getId(), targetId);
            if (latest == null) {
                appendSnapshots(fillGapFromRepository(new ReferenceKey(rootContext, targetId), 15));
            }

            latest = findLatestToInCommitTable(rootContext.getId(), targetId);
            if (latest == null){
                query.stats().logMaxGapsToFillExceededInfo(targetId);
            }
            return latest;
        }

        private CdoSnapshot findLatestToInCommitTable(CommitId rootContext, GlobalId targetId) {
            final List<CdoSnapshot> found = new ArrayList<>();

            iterateUntil(ce -> {
                if (ce.getAny(targetId) != null) {
                    found.add(ce.getAny(targetId));
                }
            }, rootContext);

            if (found.size() == 0) {
                return null;
            } else {
                return found.get(found.size() - 1);
            }
        }

        //TODO remove
        private boolean isInChildValueObjectScope(ReferenceKey referenceKey) {
            if (!(referenceKey.targetId instanceof ValueObjectId)) {
                return false;
            }

            ValueObjectId valueObjectId = (ValueObjectId) referenceKey.targetId;

            return rootSnapshotIds.contains(valueObjectId.getOwnerId());
        }

        List<CdoSnapshot> fillGapFromRepository(ReferenceKey referenceKey, int limit) {

            List<CdoSnapshot> historicals;
// TODO remove
//            if (isInChildValueObjectScope(referenceKey)) {
//                historicals = getHistoricals(referenceKey.targetId, referenceKey, false, limit);
//                query.stats().logQueryInChildValueObjectScope(referenceKey.targetId, referenceKey.commit.getId(), historicals.size());
//           }
//            else {
                if (filledGaps >= maxGapsToFill) {
                    return Collections.emptyList();
                }
                else {
                    historicals = getHistoricals(referenceKey.targetId, referenceKey, true, limit);
                    query.stats().logQueryInDeepPlusScope(referenceKey.targetId, referenceKey.commit.getId(), historicals.size());
                }
//            }

            filledGaps++;
            return historicals;
        }

        private List<CdoSnapshot> getHistoricals(GlobalId globalId, ReferenceKey timePoint, boolean withChildValueObjects, int limit) {
            if (javersCoreConfiguration.getCommitIdGenerator() == CommitIdGenerator.SYNCHRONIZED_SEQUENCE){
                return repository.getHistoricals(globalId, timePoint.commit.getId(), withChildValueObjects, limit);
            }
            return repository.getHistoricals(globalId, timePoint.commit.getCommitDate(), withChildValueObjects, limit);
        }

        /*
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
        }*/

        void appendSnapshots(List<CdoSnapshot> snapshots) {
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

        /*
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
        }*/
    }

    final static class ReferenceKey {
        private final CommitMetadata commit;
        private final GlobalId targetId;

        ReferenceKey(CommitMetadata rootContext, GlobalId targetId) {
            Validate.argumentsAreNotNull(rootContext, targetId);
            this.commit = rootContext;
            this.targetId = targetId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ReferenceKey that = (ReferenceKey) o;
            return Objects.equals(commit.getId(), that.commit.getId()) &&
                   Objects.equals(targetId, that.targetId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(commit.getId(), targetId);
        }
    }
}
