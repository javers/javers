package org.javers.core;

import org.javers.common.collections.Optional;
import org.javers.core.changelog.ChangeProcessor;
import org.javers.core.commit.Commit;
import org.javers.core.commit.CommitMetadata;
import org.javers.core.diff.Change;
import org.javers.core.diff.Diff;
import org.javers.core.json.JsonConverter;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.core.metamodel.object.GlobalId;
import org.javers.repository.jql.GlobalIdDTO;
import org.javers.repository.jql.JqlQuery;

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
public interface Javers {

    /**
     * Persists a current state of a given domain object graph
     * in JaVers repository.
     * <br/><br/>
     * 
     * JaVers applies commit() to given object and all objects navigable from it.
     * You can capture a state of an arbitrary complex object graph with a single commit() call.
     *
     * @see <a href="http://javers.org/documentation/repository-examples/">http://javers.org/documentation/repository-examples</a>
     * @param currentVersion standalone object or handle to an object graph
     */
    Commit commit(String author, Object currentVersion);

    /**
     * Marks given object as deleted.
     * <br/><br/>
     * 
     * Unlike {@link Javers#commit(String, Object)}, this method is shallow
     * and affects only given object.
     * <br/><br/>
     *
     * This method doesn't delete anything from JaVers repository.
     * It just persists 'terminal snapshot' of given object.
     *
     * @param deleted object to be marked as deleted
     */
    Commit commitShallowDelete(String author, Object deleted);

    /**
     * The same like {@link #commitShallowDelete(String,Object)}
     * but deleted object is selected using globalId
     */
    Commit commitShallowDeleteById(String author, GlobalIdDTO globalId);

    /**
     * <p>
     * JaVers core function,
     * deeply compares two arbitrary complex object graphs.
     * </p>
     *
     * <p>
     * To calculate a diff, just provide two versions of the
     * same object or handles to two versions of the same object graph.
     * <br/>
     * The handle could be a root of an aggregate, tree root
     * or any node in an object graph from where all other nodes are navigable.
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
    Diff compare(Object oldVersion, Object currentVersion);

    /**
     * Initial diff is a kind of snapshot of given domain object graph.
     * Use it alongside with {@link #compare(Object, Object)}
     */
    Diff initial(Object newDomainObject);

    /**
     * use: <pre>
     * javers.getJsonConverter().toJson(diff);
     * </pre>
     */
    @Deprecated
    String toJson(Diff diff);

    /**
     * Snapshots (historical versions) of given class, object or property,
     * in reverse chronological order.
     *
     * <h3>Querying for Entity snapshots by instance Id</h3>
     *
     * To get last 5 snapshots of "bob" Person, call:
     * <pre>
     * javers.findSnapshots( QueryBuilder.byInstanceId("bob", Person.class).limit(5).build() );
     * </pre>
     *
     * Last snapshots of "bob" Person with a "salary" change:
     * <pre>
     * javers.findSnapshots( QueryBuilder.byInstanceId("bob", Person.class).andProperty("salary").limit(5).build() );
     * </pre>
     *
     *
     * <h3>Querying for ValueObject snapshots</h3>
     *
     * Last snapshots of Address ValueObject owned by "bob" Person:
     * <pre>
     * javers.findSnapshots( QueryBuilder.byValueObjectId("bob", Person.class, "address").build() );
     * </pre>
     *
     * Last snapshots of Address ValueObject owned by any Person:
     * <pre>
     * javers.findSnapshots( QueryBuilder.byValueObject(Person.class, "address").build() );
     * </pre>
     *
     *
     * <h3>Querying for any object snapshots regardless of its type</h3>
     *
     * Last snapshots of any object of MyClass.class:
     * <pre>
     * javers.findSnapshots( QueryBuilder.byClass(MyClass.class).limit(5).build() );
     * </pre>
     *
     * Last snapshots of any object of MyClass.class with a "myProperty" change:
     * <pre>
     * javers.findSnapshots( QueryBuilder.byClass(Person.class).andProperty("myProperty").limit(5).build() );
     * </pre>
     *
     * @return empty List if nothing found
     */
    List<CdoSnapshot> findSnapshots(JqlQuery query);

    /**
     * Changes history (diff sequence) of given class, object or property,
     * in reverse chronological order.
     * <br/><br/>
     *
     * For example, to get change history of last 5 versions of "bob" Person, call:
     * <pre>
     * javers.findChanges( QueryBuilder.byInstanceId("bob", Person.class).limit(5).build() );
     * </pre>
     *
     * For more query examples, see {@link #findSnapshots(JqlQuery)} method.
     * Both methods use Javers Query Language (JQL).
     * So you can use the same query object to get changes and snapshots views.
     *
     * @return empty List if nothing found
     */
    List<Change> findChanges(JqlQuery query);

    /**
     * Latest snapshot of given entity instance
     * or Optional#EMPTY if instance is not versioned.
     * <br/><br/>
     *
     * For example, to get last snapshot of "bob" Person, call:
     * <pre>
     * javers.getLatestSnapshot("bob", Person.class));
     * </pre>
     */
    Optional<CdoSnapshot> getLatestSnapshot(Object localId, Class entityClass);

    /**
     * use {@link #findSnapshots(JqlQuery)}
     */
    @Deprecated
    List<CdoSnapshot> getStateHistory(GlobalIdDTO globalId, int limit);

    /**
     * use {@link #findChanges(JqlQuery)}
     */
    @Deprecated
    List<Change> getChangeHistory(GlobalIdDTO globalId, int limit);

    /**
     * use {@link #getLatestSnapshot(Object, Class)}
     */
    @Deprecated
    Optional<CdoSnapshot> getLatestSnapshot(GlobalIdDTO globalId);

    /**
     * If you are serializing JaVers objects like
     * {@link Commit}, {@link Change}, {@link Diff} or {@link CdoSnapshot} to JSON, use this JsonConverter.
     * For example:
     *
     * <pre>
     * javers.getJsonConverter().toJson(changes);
     * </pre>
     */
    JsonConverter getJsonConverter();

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
     * List&lt;Change&gt; changes = javers.calculateDiffs(...);
     * String changeLog = javers.processChangeList(changes, new SimpleTextChangeLog());
     * System.out.println( changeLog );
     * </pre>
     *
     * @see org.javers.core.changelog.SimpleTextChangeLog
     */
    <T> T processChangeList(List<Change> changes, ChangeProcessor<T> changeProcessor);

    IdBuilder idBuilder();
}
