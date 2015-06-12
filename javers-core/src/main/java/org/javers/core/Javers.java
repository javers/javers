package org.javers.core;

import org.javers.common.collections.Optional;
import org.javers.core.changelog.ChangeProcessor;
import org.javers.core.commit.Commit;
import org.javers.core.commit.CommitMetadata;
import org.javers.core.diff.Change;
import org.javers.core.diff.Diff;
import org.javers.core.diff.changetype.NewObject;
import org.javers.core.diff.changetype.ReferenceChange;
import org.javers.core.diff.changetype.ValueChange;
import org.javers.core.diff.changetype.container.ListChange;
import org.javers.core.json.JsonConverter;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.core.metamodel.object.GlobalId;
import org.javers.core.metamodel.property.Property;
import org.javers.core.metamodel.type.JaversType;
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
     * Queries JaversRepository for changes history (diff sequence) of given class, object or property.<br/>
     * There are various types of changes: {@link ValueChange}, {@link ReferenceChange}, {@link ListChange}, {@link NewObject} and so on. <br/>
     * See {@link Change} class hierarchy.
     * <br/><br/>
     *
     * Resulting List is ordered in reverse chronological order.
     * <br/><br/>
     *
     * <b>Querying for Entity changes by instance Id</b><br/><br/>
     *
     * For example, to get change history of last 5 versions of "bob" Person, call:
     * <pre>
     * javers.findChanges( QueryBuilder.byInstanceId("bob", Person.class).limit(5).build() );
     * </pre>
     *
     * Last "salary" changes of "bob" Person:
     * <pre>
     * javers.findChanges( QueryBuilder.byInstanceId("bob", Person.class).andProperty("salary").build() );
     * </pre>
     *
     * <b>Querying for ValueObject changes</b><br/><br/>
     *
     * Last changes on Address ValueObject owned by "bob" Person:
     * <pre>
     * javers.findChanges( QueryBuilder.byValueObjectId("bob", Person.class, "address").build() );
     * </pre>
     *
     * Last changes on Address ValueObject owned by any Person:
     * <pre>
     * javers.findChanges( QueryBuilder.byValueObject(Person.class, "address").build() );
     * </pre>
     *
     * <b>Querying for any object changes by its class</b><br/><br/>
     *
     * Last changes on any object of MyClass.class:
     * <pre>
     * javers.findChanges( QueryBuilder.byClass(MyClass.class).build() );
     * </pre>
     *
     * Last "myProperty" changes on any object of MyClass.class:
     * <pre>
     * javers.findChanges( QueryBuilder.byClass(Person.class).andProperty("myProperty").build() );
     * </pre>
     *
     * @return empty List if nothing found
     * @see <a href="http://javers.org/documentation/jql-examples/">http://javers.org/documentation/jql-examples</a>
     */
    List<Change> findChanges(JqlQuery query);

    /**
     * Queries JaversRepository for object Snapshots (historical versions). <br/>
     * Snapshot is a simple Map (property -> value) representation of your domain object.
     * <br/><br/>
     *
     * Resulting List is ordered in reverse chronological order.
     * <br/><br/>
     *
     * For example, to get last 5 snapshots versions of "bob" Person, call:
     * <pre>
     * javers.findSnapshots( QueryBuilder.byInstanceId("bob", Person.class).limit(5).build() );
     * </pre>
     *
     * For more query examples, see {@link #findChanges(JqlQuery)} method.
     * Both methods use Javers Query Language (JQL).
     * So you can use the same query object to get changes and snapshots views.
     *
     * @return empty List if nothing found
     * @see <a href="http://javers.org/documentation/jql-examples/">http://javers.org/documentation/jql-examples</a>
     */
    List<CdoSnapshot> findSnapshots(JqlQuery query);

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
     * If you are serializing JaVers objects like
     * {@link Commit}, {@link Change}, {@link Diff} or {@link CdoSnapshot} to JSON, use this JsonConverter.
     * <br/><br/>
     *
     * For example:
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

    /**
     * use: <pre>
     * javers.getJsonConverter().toJson(diff);
     * </pre>
     */
    @Deprecated
    String toJson(Diff diff);

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
     * Use JaversTypes, if you want to: <br/>
     * - describe your class in the context of JaVers domain model mapping, <br/>
     * - use JaVers Reflection API to conveniently access your object properties
     *  (instead of awkward java.lang.reflect API).
     *
     * <br/><br/>
     *
     * <b>Class describe example</b>.
     * You can pretty-print JaversType of your class and
     * check if mapping is correct.
     * <pre>
     * class Person {
     *     &#64;Id int id;
     *     &#64;Transient String notImportantField;
     *     String name;
     * }
     * </pre>
     *
     * Calling
     * <pre>
     * System.out.println( javers.getTypeMapping(Person.class).prettyPrint() );
     * </pre>
     *
     * prints:
     * <pre>
     * EntityType{
     *   baseType: org.javers.core.examples.Person
     *   managedProperties:
     *      Field int id; //declared in: Person
     *      Field String name; //declared in: Person
     *   idProperty: login
     * }
     * </pre>
     *
     * <b>Property access example</b>.
     * You can list object property values using {@link Property} abstraction.
     * <pre>
     * Javers javers = JaversBuilder.javers().build();
     * ManagedType jType = javers.getTypeMapping(Person.class);
     * Person person = new Person("bob", "Uncle Bob");
     *
     * System.out.println("Bob's properties:");
     * for (Property p : jType.getManagedClass().getProperties()){
     *     Object value = p.get(person);
     *     System.out.println( "property:" + p.getName() + ", value:" + value );
     * }
     * </pre>
     *
     * prints:
     * <pre>
     * Bob's properties:
     * property:login, value:bob
     * property:name, value:Uncle Bob
     * </pre>
     */
    <T extends JaversType> T getTypeMapping(Class<?> clientsClass);

    IdBuilder idBuilder();

}
