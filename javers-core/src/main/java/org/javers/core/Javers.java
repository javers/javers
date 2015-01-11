package org.javers.core;

import org.javers.common.collections.Optional;
import org.javers.core.changelog.ChangeListTraverser;
import org.javers.core.changelog.ChangeProcessor;
import org.javers.core.commit.Commit;
import org.javers.core.commit.CommitFactory;
import org.javers.core.commit.CommitMetadata;
import org.javers.core.diff.Change;
import org.javers.core.diff.Diff;
import org.javers.core.diff.DiffFactory;
import org.javers.core.json.JsonConverter;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.core.metamodel.object.GlobalId;
import org.javers.core.metamodel.object.GlobalIdDTO;
import org.javers.core.metamodel.object.GlobalIdFactory;
import org.javers.core.metamodel.type.JaversType;
import org.javers.core.metamodel.type.TypeMapper;
import org.javers.core.snapshot.GraphSnapshotFacade;
import org.javers.repository.api.JaversExtendedRepository;
import org.javers.repository.api.JaversRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


/**
 * Facade to JaVers instance.<br>
 * Should be constructed by {@link JaversBuilder} provided with your domain model configuration.
 * <br/><br/>
 *
 * For example, to deeply compare two objects
 * or two arbitrary complex graphs of objects, call:
 * <pre>
 * Javers javers = JaversBuilder.javers().build();
 * Diff diff = javers.compare(oldVersion, currentVersion);
 * </pre>
 *
 * @see <a href="http://javers.org/documentation"/>http://javers.org/documentation</a>
 * @author bartosz walacik
 */
public class Javers {
    private static final Logger logger = LoggerFactory.getLogger(Javers.class);

    private final DiffFactory diffFactory;
    private final TypeMapper typeMapper;
    private final JsonConverter jsonConverter;
    private final CommitFactory commitFactory;
    private final JaversExtendedRepository repository;
    private final GraphSnapshotFacade graphSnapshotFacade;

    /**
     * JaVers instance should be constructed by {@link JaversBuilder}
     */
    public Javers(DiffFactory diffFactory, TypeMapper typeMapper, JsonConverter jsonConverter, CommitFactory commitFactory, JaversExtendedRepository repository, GraphSnapshotFacade graphSnapshotFacade) {
        this.diffFactory = diffFactory;
        this.typeMapper = typeMapper;
        this.jsonConverter = jsonConverter;
        this.commitFactory = commitFactory;
        this.repository = repository;
        this.graphSnapshotFacade = graphSnapshotFacade;
    }

    /**
     * Persists current version of a given domain object in JaVers repository.
     * JaVers applies commit() to given object and all objects navigable from it.
     * You can capture a state of an arbitrary complex objects graph with a single commit() call.
     *
     * @param currentVersion Standalone object or handle to an objects graph
     */
    public Commit commit(String author, Object currentVersion) {
        Commit commit = commitFactory.create(author, currentVersion);

        repository.persist(commit);
        logger.info(commit.toString());
        return commit;
    }

    /**
     * Marks given object as deleted.
     *
     * This method doesn't delete anything from JaVers repository.
     * It just persists 'terminal snapshot' of given object.
     *
     * @param deleted object to be marked as deleted
     */
    public Commit commitDelete(String author, Object deleted) {
        Commit commit = commitFactory.createTerminal(author, deleted);

        repository.persist(commit);
        logger.info(commit.toString());
        return commit;
    }

    /**
     * <p>
     * JaVers core function,
     * deeply compares two arbitrary complex objects graphs.
     * </p>
     *
     * <p>
     * To calculate diff, just provide two versions of the
     * same object or handles to two versions of the same objects graph.
     * <br/>
     * The handle could be a root of an aggregate, tree root
     * or any node in an objects graph from where all other nodes are navigable.
     * </p>
     *
     * <p>
     * This function is used for ad-hoc objects comparing.
     * In order to use data auditing feature, call {@link #commit(String, Object)}.
     * </p>
     *
     * <p>
     * Diffs can be converted to JSON with {@link #toJson(Diff)}.
     * </p>
     *
     * @see <a href="http://javers.org/documentation/diff-examples/">http://javers.org/documentation/diff-examples</a>
     */
    public Diff compare(Object oldVersion, Object currentVersion) {
        return diffFactory.compare(oldVersion, currentVersion);
    }

    /**
     * Initial diff is a kind of snapshot of given domain objects graph.
     * Use it alongside with {@link #compare(Object, Object)}
     */
    public Diff initial(Object newDomainObject) {
        return diffFactory.initial(newDomainObject);
    }

    /**
     * Diff serialized to pretty JSON, useful if you are not using {@link JaversRepository}
     */
    public String toJson(Diff diff) {
        return jsonConverter.toJson(diff);
    }

    public IdBuilder idBuilder() {
        return new IdBuilder(new GlobalIdFactory(typeMapper));
    }

    /**
     * Snapshots (historical versions) of given object,
     * in reverse chronological order.
     * <br/><br/>
     *
     * For example, to list 5 last snapshots of "bob" Person, call:
     * <pre>
     * javers.getStateHistory(InstanceIdDTO.instanceId("bob", Person.class), 5);
     * </pre>
     *
     * @param globalId given object ID
     * @param limit choose reasonable limit
     * @return empty List if object is not versioned
     */
    public List<CdoSnapshot> getStateHistory(GlobalIdDTO globalId, int limit){
        return repository.getStateHistory(globalId, limit);
    }

    /**
     * Latest snapshot of given object
     * or Optional#EMPTY if object is not versioned.
     * <br/><br/>
     *
     * For example, to get last snapshot of "bob" Person, call:
     * <pre>
     * javers.getStateHistory(InstanceIdDTO.instanceId("bob", Person.class), 5);
     * </pre>
     */
    public Optional<CdoSnapshot> getLatestSnapshot(GlobalIdDTO globalId){
        return repository.getLatest(globalId);
    }

    /**
     * Changes history (diff sequence) of given object,
     * in reverse chronological order.
     *
     * For example, to get change history of "bob" Person, call:
     * <pre>
     * javers.getChangeHistory(InstanceIdDTO.instanceId("bob", Person.class), 5);
     * </pre>
     *
     * @param globalId given object ID
     * @param limit choose reasonable limit
     * @return empty List, if object is not versioned or was committed only once
     */
    public List<Change> getChangeHistory(GlobalIdDTO globalId, int limit) {
        return graphSnapshotFacade.getChangeHistory(globalId, limit);
    }

    JaversType getForClass(Class<?> clazz) {
        return typeMapper.getJaversType(clazz);
    }

    public JsonConverter getJsonConverter() {
        return jsonConverter;
    }

    /**
     * Generic purpose method for processing a changes list.
     * After iterating over given list, returns data computed by
     * {@link org.javers.core.changelog.ChangeProcessor#result()}.
     * <br/>
     * It's more convenient than iterating over changes on your own.
     * ChangeProcessor frees you from <tt>if + inctanceof</tt> boilerplate.
     *
     * <br/><br/>
     * Additional features: <br/>
     *  - when several changes in a row refers to the same Commit, {@link ChangeProcessor#onCommit(CommitMetadata)}
     *  is called only for first occurrence <br/>
     *  - similarly, when several changes in a row affects the same object, {@link ChangeProcessor#onAffectedObject(GlobalId)}
     *  is called only for first occurrence
     *
     * <br/><br/>
     * For example, to get pretty change log, call:
     * <pre>
     * List&lt;Change&gt; changes = javers.getChangeHistory(...);
     * String changeLog = javers.processChangeList(changes, new SimpleTextChangeLog());
     * System.out.println( changeLog );
     * </pre>
     *
     * @see org.javers.core.changelog.SimpleTextChangeLog
     */
    public <T> T processChangeList(List<Change> changes, ChangeProcessor<T> changeProcessor){
        ChangeListTraverser.traverse(changes, changeProcessor);
        return changeProcessor.result();
    }
}
