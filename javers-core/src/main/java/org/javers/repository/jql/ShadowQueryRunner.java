package org.javers.repository.jql;

import org.javers.common.collections.Consumer;
import org.javers.common.exception.JaversException;
import org.javers.common.exception.JaversExceptionCode;
import org.javers.common.validation.Validate;
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
import static org.javers.repository.jql.ShadowScope.COMMIT_DEPTH_PLUS;

/**
 * @author bartosz.walacik
 */
public class ShadowQueryRunner {
    private static final Logger logger = LoggerFactory.getLogger(ShadowQueryRunner.class);

    private final JaversExtendedRepository repository;
    private final ShadowFactory shadowFactory;

    public ShadowQueryRunner(JaversExtendedRepository repository, ShadowFactory shadowFactory) {
        this.repository = repository;
        this.shadowFactory = shadowFactory;
    }

    public List<Shadow> queryForShadows(JqlQuery query, List<CdoSnapshot> coreSnapshots) {
        if (query.getShadowScope() != COMMIT_DEPTH_PLUS && query.getShadowScopeMaxGapsToFill() > 0) {
            throw new JaversException(JaversExceptionCode.MALFORMED_JQL,
                    "maxGapsToFill can be used only in the COMMIT_DEPTH_PLUS query scope");
        }

        final CommitTable commitTable = new CommitTable(coreSnapshots, query.getShadowScopeMaxGapsToFill());

        if (query.getShadowScope().isCommitDepthOrWider()) {
            commitTable.loadFullCommits();
        }

        return commitTable.rootsForQuery(query).stream()
                .map(r -> shadowFactory.createShadow(r.root, r.context, (cm, targetId) -> commitTable.findLatestTo(cm, targetId)))
                .collect(toList());
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
        private final Map<ReferenceKey, CdoSnapshot> filledGaps = new HashMap<>();
        private final Map<CommitMetadata, CommitEntry> commitsMap = new HashMap<>();
        private final List<CommitEntry> commitsList = new ArrayList<>();

        CommitTable(List<CdoSnapshot> coreSnapshots, int maxGapsToFill) {
            this.maxGapsToFill = maxGapsToFill;
            if (coreSnapshots.isEmpty()) {
                return;
            }

            coreSnapshots.forEach(s -> {
                CommitEntry current = commitsMap.get(s.getCommitMetadata());
                if (current == null) {
                    current = appendCommit(s);
                }
                current.append(s);
            });
        }

        List<ShadowRoot> rootsForQuery(JqlQuery query) {
            fillMissingParents();

            return commitsList.stream()
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
                    .commitIds(commitsMap.keySet().stream().map(cm -> cm.getId()).collect(toSet()))
                    .build();
            repository.getSnapshots(params).stream().forEach(s -> commitsMap.get(s.getCommitMetadata()).append(s));
        }

        CdoSnapshot findLatestTo(CommitMetadata rootContext, GlobalId targetId) {

            if (!commitsMap.containsKey(rootContext)) {
                return null;
            }

            final List<CdoSnapshot> found = new ArrayList<>();

            iterateReverseUntil(ce -> {
                if (ce.getAny(targetId) != null) {
                    found.add(ce.getAny(targetId));
                }
            }, rootContext);

            if (found.size() == 0) {
                CdoSnapshot gap = fillGapFromRepository(new ReferenceKey(rootContext, targetId));
                if (gap == null){
                    logger.debug("Object '" + targetId.value() + "' is outside the Shadow query scope" +
                            ", references to this object will be nulled. " +
                            "Use the wider scope to fill gaps in the object graph.");
                }
                return gap;
            } else {
                return found.get(found.size() - 1);
            }
        }

        CdoSnapshot fillGapFromRepository(ReferenceKey referenceKey) {
            if (filledGaps.size() >= maxGapsToFill) {
                return null;
            }

            return filledGaps.computeIfAbsent(referenceKey, key ->
                repository.getHistorical(key.targetId, key.rootContext.getId()).orElse(null)
            );
        }

        void fillMissingParents() {
            Map<GlobalId, CdoSnapshot> movingLatest = new HashMap<>();

            iterateReverse(commitEntry -> {
                commitEntry.getMissingParents().stream()
                        .filter(movingLatest::containsKey)
                        .forEach(voId -> {
                            commitEntry.append(movingLatest.get(voId));
                        });

                //update movingLatest
                commitEntry.getAllStream().forEach(e -> movingLatest.put(e.getGlobalId(), e));
            });
        }

        CommitEntry appendCommit(CdoSnapshot snapshot) {
            CommitEntry entry = new CommitEntry(snapshot.getCommitMetadata());
            commitsMap.put(entry.commitMetadata, entry);
            commitsList.add(entry);
            return entry;
        }

        void iterateReverse(Consumer<CommitEntry> consumer) {
            ListIterator<CommitEntry> it = commitsList.listIterator(commitsList.size());

            while (it.hasPrevious()) {
                consumer.consume(it.previous());
            }
        }

        void iterateReverseUntil(Consumer<CommitEntry> consumer, CommitMetadata bound) {
            ListIterator<CommitEntry> it = commitsList.listIterator(commitsList.size());

            while (it.hasPrevious()) {
                CommitEntry ce = it.previous();
                consumer.consume(ce);
                if (ce.commitMetadata.equals(bound)) {
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
        private final CommitMetadata rootContext;
        private final GlobalId targetId;

        ReferenceKey(CommitMetadata rootContext, GlobalId targetId) {
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
