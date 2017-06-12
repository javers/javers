package org.javers.repository.jql;

import org.javers.common.collections.Consumer;
import org.javers.core.commit.CommitMetadata;
import org.javers.core.metamodel.object.*;
import org.javers.repository.api.JaversExtendedRepository;
import org.javers.repository.api.QueryParams;
import org.javers.repository.api.QueryParamsBuilder;
import org.javers.shadow.Shadow;
import org.javers.shadow.ShadowFactory;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

/**
 * @author bartosz.walacik
 */
public class ShadowQueryRunner {
    private final JaversExtendedRepository repository;
    private final ShadowFactory shadowFactory;

    public ShadowQueryRunner(JaversExtendedRepository repository, ShadowFactory shadowFactory) {
        this.repository = repository;
        this.shadowFactory = shadowFactory;
    }

    public List<Shadow> queryForShadows(JqlQuery query, List<CdoSnapshot> coreSnapshots) {

        final CommitTable commitTable = new CommitTable(coreSnapshots);

        if (query.getShadowScope() == ShadowScope.COMMIT_DEPTH) {
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
        private final Map<CommitMetadata, CommitEntry> commitsMap = new HashMap<>();
        private final List<CommitEntry> commitsList = new ArrayList<>();
        private final List<CdoSnapshot> coreSnapshots;

        CommitTable(List<CdoSnapshot> coreSnapshots) {
            this.coreSnapshots = coreSnapshots;
            if (coreSnapshots.isEmpty()) {
                return;
            }

            coreSnapshots.forEach(s -> {
                CommitEntry current = commitsMap.get(s.getCommitMetadata());
                if (current == null) {
                    current = nextCommit(s);
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
                return null;
            }

            return found.get(found.size() - 1);
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

        CommitEntry nextCommit(CdoSnapshot snapshot) {
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
}
