package org.javers.repository.jql;

import org.javers.common.collections.Consumer;
import org.javers.common.exception.JaversException;
import org.javers.common.exception.JaversExceptionCode;
import org.javers.common.validation.Validate;
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

    public ShadowQueryRunner(JaversExtendedRepository repository, ShadowFactory shadowFactory) {
        this.repository = repository;
        this.shadowFactory = shadowFactory;
    }

    public List<Shadow> queryForShadows(JqlQuery query, List<CdoSnapshot> coreSnapshots) {
        final CommitTable commitTable = new CommitTable(coreSnapshots, query.getMaxGapsToFill(),
                query);

        if (query.getShadowScope().isCommitDeep()) {
            commitTable.loadFullCommits();
        }

        List<Shadow> shadows = commitTable.rootsForQuery(query).stream()
                .map(r -> shadowFactory.createShadow(r.root, r.context, (cm, targetId) -> commitTable.findLatestTo(cm.getId(), targetId)))
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
        private final int maxGapsToFill;
        private int filledGaps;
        private final Map<CommitId, CommitEntry> commitsMap = new TreeMap<>();
        private final JqlQuery query;

        CommitTable(List<CdoSnapshot> coreSnapshots, int maxGapsToFill, JqlQuery query) {
            this.maxGapsToFill = maxGapsToFill;
            this.query = query;

            appendSnapshots(coreSnapshots);
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
                    .commitIds(commitsMap.keySet().stream().collect(toSet()))
                    .build();
            List<CdoSnapshot> fullCommitsSnapshots = repository.getSnapshots(params);
            query.stats().logQueryInCommitDeepScope(fullCommitsSnapshots);

            appendSnapshots(fullCommitsSnapshots);
        }

        CdoSnapshot findLatestTo(CommitId rootContext, GlobalId targetId) {

            if (!commitsMap.containsKey(rootContext)) {
                return null;
            }

            CdoSnapshot latest = findLatestToInCommitTable(rootContext, targetId);
            if (latest == null) {
                appendSnapshots(fillGapFromRepository(new ReferenceKey(rootContext, targetId), 15));
            }

            latest = findLatestToInCommitTable(rootContext, targetId);
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

        private boolean isInChildValueObjectScope(ReferenceKey referenceKey) {
            //TODO
            return query.isAggregate() && referenceKey.targetId instanceof ValueObjectId;
        }

        List<CdoSnapshot> fillGapFromRepository(ReferenceKey referenceKey, int limit) {
            if (filledGaps >= maxGapsToFill && !isInChildValueObjectScope(referenceKey)) {
                return Collections.emptyList();
            }

            List<CdoSnapshot> historicals;
            if (isInChildValueObjectScope(referenceKey)) {
                historicals = repository.getHistoricals(referenceKey.targetId, referenceKey.rootContext, false, limit);
                query.stats().logQueryInChildValueObjectScope(referenceKey.targetId, referenceKey.rootContext, historicals.size());
            }
            else {
                historicals = repository.getHistoricals(referenceKey.targetId, referenceKey.rootContext, query.isAggregate(), limit);
                query.stats().logQueryInDeepPlusScope(referenceKey.targetId, referenceKey.rootContext, historicals.size());
            }

            filledGaps++;
            return historicals;
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

        void appendSnapshots(List<CdoSnapshot> snapshots) {
            snapshots.forEach(it -> appendSnapshot(it));
        }

        CommitEntry appendSnapshot(CdoSnapshot snapshot) {
            CommitEntry entry = commitsMap.get(snapshot.getCommitMetadata().getId());
            if (entry == null) {
                entry = new CommitEntry(snapshot.getCommitMetadata());
                commitsMap.put(snapshot.getCommitId(), entry);
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

    final static class ReferenceKey {
        private final CommitId rootContext;
        private final GlobalId targetId;

        ReferenceKey(CommitId rootContext, GlobalId targetId) {
            Validate.argumentsAreNotNull(rootContext, targetId);
            this.rootContext = rootContext;
            this.targetId = targetId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ReferenceKey that = (ReferenceKey) o;
            return Objects.equals(rootContext, that.rootContext) &&
                    Objects.equals(targetId, that.targetId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(rootContext, targetId);
        }
    }
}
