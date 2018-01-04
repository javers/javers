package org.javers.core;

import org.javers.core.changelog.ChangeProcessor;
import org.javers.core.commit.Commit;
import org.javers.core.commit.CommitMetadata;
import org.javers.core.diff.Change;
import org.javers.core.diff.Diff;
import org.javers.core.diff.changetype.PropertyChange;
import org.javers.core.json.JsonConverter;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.core.metamodel.object.GlobalId;
import org.javers.core.metamodel.property.Property;
import org.javers.core.metamodel.type.JaversType;
import org.javers.repository.jql.GlobalIdDTO;
import org.javers.repository.jql.JqlQuery;
import org.javers.repository.jql.QueryBuilder;
import org.javers.repository.jql.ShadowScope;
import org.javers.shadow.Shadow;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;


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
     * @param author current user
     * @param currentVersion standalone object or handle to an object graph
     */
    Commit commit(String author, Object currentVersion);

    /**
     * Variant of {@link #commit(String, Object)} with commitProperties.
     * <br/>
     * You can pass arbitrary commit properties and
     * use them in JQL to search for snapshots or changes.
     *
     * @see QueryBuilder#withCommitProperty(String, String)
     * @param commitProperties for example ["channel":"web", "locale":"pl-PL"]
     */
    Commit commit(String author, Object currentVersion, Map<String, String> commitProperties);

    /**
     * Marks given object as deleted.
     * <br/><br/>
     *
     * Unlike {@link Javers#commit(String, Object)}, this method is shallow
     * and affects only given object.
     * <br/><br/>
     *
     * This method doesn't delete anything from JaVers repository.
     * It just persists 'terminal snapshot' of a given object.
     *
     * @param deleted object to be marked as deleted
     */
    Commit commitShallowDelete(String author, Object deleted);

    /**
     * Variant of {@link #commitShallowDelete(String, Object)} with commitProperties.
     * <br/>
     *
     * See {@link #commit(String, Object, Map)} for commitProperties description.
     */
    Commit commitShallowDelete(String author, Object deleted, Map<String, String> commitProperties);

    /**
     * The same like {@link #commitShallowDelete(String,Object)}
     * but deleted object is selected using globalId
     */
    Commit commitShallowDeleteById(String author, GlobalIdDTO globalId);

    /**
     * Variant of {@link #commitShallowDeleteById(String, GlobalIdDTO)} with commitProperties.
     * <br/>
     *
     * See {@link #commit(String, Object, Map)} for commitProperties description.
     */
    Commit commitShallowDeleteById(String author, GlobalIdDTO globalId, Map<String, String> commitProperties);

    /**
     * <h2>Deep compare</h2>
     *
     * JaVers core function,
     * deeply compares two arbitrary complex object graphs.
     *
     * <p>
     * To calculate a diff, just provide two versions of the
     * same Domain Object (Entity or ValueObject)
     * or handles to two versions of the same object graph.
     * <br/>
     * The handle could be a root of an Aggregate, tree root
     * or any node in an Domain Object graph from where all other nodes are navigable.
     * </p>
     *
     * <h2>Flat collection compare</h2>
     * You can also pass object collections here (List, Sets or Maps),
     * but in this case, JaVers calculates flat collection diff only.
     * Because it's impossible to determine type of raw collection items, JaVers maps them as Values
     * and compares using {@link Object#equals(Object)}. <br/>
     * So if you need to deep compare, wrap collections in some Value Objects.
     *
     * <h2>Misc</h2>
     * <code>compare()</code> function is used for ad-hoc objects comparing.
     * In order to use data auditing feature, call {@link #commit(String, Object)}.
     *
     * <p>
     * Diffs can be converted to JSON with {@link JsonConverter#toJson(Object)} or pretty-printed with {@link Diff#prettyPrint()}
     * </p>
     *
     * @see <a href="http://javers.org/documentation/diff-examples/">
     *     http://javers.org/documentation/diff-examples</a>
     */
    Diff compare(Object oldVersion, Object currentVersion);

    /**
     * Deeply compares two top-level collections.
     * <br/><br/>
     *
     * Introduced due to the lack of possibility to statically
     * determine type of collection items when two top-level collections are passed as references to
     * {@link #compare(Object, Object)}.
     * <br/><br/>
     *
     * Usage example:
     * <pre>
     * List&lt;Person&gt; oldList = ...
     * List&lt;Person&gt; newList = ...
     * Diff diff = javers.compareCollections(oldList, newList, Person.class);
     * </pre>
     *
     * @see <a href="http://javers.org/documentation/diff-examples/#compare-collections">
     *     Compare top-level collections example</a>
     */
    <T> Diff compareCollections(Collection<T> oldVersion, Collection<T> currentVersion, Class<T> itemClass);

    /**
     * Initial diff is a kind of snapshot of a given object graph.
     */
    Diff initial(Object newDomainObject);

    /**
     * Queries JaversRepository for object Shadows. <br/>
     * Shadow is a historical version of a domain object restored from a snapshot.
     * <br/><br/>
     *
     * For example, to get latest Shadows of "bob" Person, call:
     * <pre>
     * List<Shadow> shadows = javers.findShadows( QueryBuilder.byInstanceId("bob", Person.class)
     *      .limit(5).build() );
     * </pre>
     *
     * Since Shadows are instances of your domain classes,
     * you can use them directly in your application:
     *
     * <pre>
     * assert shadows.get(0).get() instanceof Person.class;
     * </pre>
     *
     * <h2>Query scopes</h2>
     *
     * <b>By default</b>, queries are run in the Shallow scope,
     * which is the fastest one.<br/>
     * To eagerly load all referenced objects use one of the wider scopes,
     * Commit-deep or Deep+ :
     *
     * <ul>
     *  <li/> {@link QueryBuilder#withScopeCommitDeep()}
     *  <li/> {@link QueryBuilder#withScopeDeepPlus(int)}
     * </ul>
     *
     * We recommend the Deep+ scope as a good start.
     *
     * <h2>Query scopes example</h2>
     *
     * To understand Shadow query scopes, you need to understand how JaVers commit works.<br/>
     * Remember that JaVers reuses existing snapshots and creates a fresh one
     * only if a given object is changed.<br/>
     * The way how objects are committed affects shadow query results.
     *
     * <h3>For example, we have four Entities in the object graph, joined by references:</h3>
     *
     * <pre>
     * // E1 -> E2 -> E3 -> E4
     * def e4 = new Entity(id:4)
     * def e3 = new Entity(id:3, ref:e4)
     * def e2 = new Entity(id:2, ref:e3)
     * def e1 = new Entity(id:1, ref:e2)
     * </pre>
     *
     * <h3>In the first scenario, our four entities are committed in three commits:</h3>
     *
     * In Shallow scope, referenced entities are not loaded.
     * But they all can be loaded using Deep+3 scope.
     *
     *<pre>
     *given:
     *  javers.commit("author", e4) // commit 1.0 with e4 snapshot
     *  javers.commit("author", e3) // commit 2.0 with e3 snapshot
     *  javers.commit("author", e1) // commit 3.0 with snapshots of e1 and e2
     *
     *when: 'shallow scope query'
     *  def shadows = javers.findShadows(QueryBuilder.byInstanceId(1, Entity)
     *                      .build())
     *  def shadowE1 = shadows.get(0).get()
     *
     *then: 'only e1 is loaded'
     *  shadowE1 instanceof Entity
     *  shadowE1.id == 1
     *  shadowE1.ref == null
     *
     *when: 'commit-deep scope query'
     *  shadows = javers.findShadows(QueryBuilder.byInstanceId(1, Entity)
     *                  .withScopeCommitDeep().build())
     *  shadowE1 = shadows.get(0).get()
     *
     *then: 'only e1 and e2 are loaded, both was committed in commit 3.0'
     *  shadowE1.id == 1
     *  shadowE1.ref.id == 2
     *  shadowE1.ref.ref == null
     *
     *when: 'deep+1 scope query'
     *  shadows = javers.findShadows(QueryBuilder.byInstanceId(1, Entity)
     *                  .withScopeDeepPlus(1).build())
     *  shadowE1 = shadows.get(0).get()
     *
     *then: 'only e1 + e2 are loaded'
     *  shadowE1.id == 1
     *  shadowE1.ref.id == 2
     *  shadowE1.ref.ref == null
     *
     *when: 'deep+3 scope query'
     *  shadows = javers.findShadows(QueryBuilder.byInstanceId(1, Entity)
     *                  .withScopeDeepPlus(3).build())
     *  shadowE1 = shadows.get(0).get()
     *
     *then: 'all object are loaded'
     *  shadowE1.id == 1
     *  shadowE1.ref.id == 2
     *  shadowE1.ref.ref.id == 3
     *  shadowE1.ref.ref.ref.id == 4
     *</pre>
     *
     * <h3>In the second scenario, our four entities are committed in the single commit:</h3>
     *
     * Shallow scope works in the same way.
     * Commit-deep scope is enough to load the full graph.
     *
     *<pre>
     *given:
     *  javers.commit("author", e1) //commit 1.0 with snapshots of e1, e2, e3 and e4
     *
     *when: 'commit-deep scope query'
     *  shadows = javers.findShadows(QueryBuilder.byInstanceId(1, Entity)
     *                  .withScopeCommitDeep().build())
     *  shadowE1 = shadows.get(0).get()
     *
     *then: 'all object are loaded'
     *  shadowE1.id == 1
     *  shadowE1.ref.id == 2
     *  shadowE1.ref.ref.id == 3
     *  shadowE1.ref.ref.ref.id == 4
     *</pre>
     *
     * <h2>Performance & Profiling</h2>
     * Each Shadow query executes one or more Snapshot queries (depending on the scope)
     * and complexity of an object graph.
     * If you are having
     * performance issues, check with debug mode how your Shadow query is executed
     * and then, try to reduce the scope.
     *
     *<pre>
     *&lt;logger name="org.javers.JQL" level="DEBUG"/&gt;
     *</pre>
     *
     * Example debug for a Shadow query execution:
     *
     *<pre>
     *DEBUG org.javers.JQL - SHALLOW query: 1 snapshots loaded (entities: 1, valueObjects: 0)
     *DEBUG org.javers.JQL - DEEP_PLUS query for '...SnapshotEntity/2' at commitId 3.0, 1 snapshot(s) loaded, gaps filled so far: 1
     *DEBUG org.javers.JQL - warning: object '...SnapshotEntity/3' is outside of the DEEP_PLUS+1 scope, references to this object will be nulled. Increase maxGapsToFill and fill all gaps in your object graph.
     *DEBUG org.javers.JQL - queryForShadows executed:
     *JqlQuery {
     *  IdFilter{ globalId: ...SnapshotEntity/1 }
     *  QueryParams{ aggregate: true, limit: 100 }
     *  ShadowScopeDefinition{ shadowScope: DEEP_PLUS, maxGapsToFill: 1 }
     *  Stats{
     *    executed in millis: 12
     *    DB queries: 2
     *    all snapshots: 2
     *    SHALLOW snapshots: 1
     *    DEEP_PLUS snapshots: 1
     *    gaps filled: 1
     *    gaps left!: 1
     *  }
     *}
     *</pre>
     *
     * Execution stats are also available in {@link JqlQuery#stats()}.
     *
     * @return A list ordered in reverse chronological order. Empty if nothing found.
     * @param <T> type of a domain object
     * @see ShadowScope
     * @see <a href="http://javers.org/documentation/jql-examples/">http://javers.org/documentation/jql-examples</a>
     * @since 3.2
     */
    <T> List<Shadow<T>> findShadows(JqlQuery query);

    /**
     * Queries JaversRepository for changes history (diff sequence) of a given class, object or property.<br/>
     * There are various types of changes. See {@link Change} class hierarchy.
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
     * javers.findChanges( QueryBuilder.byInstanceId("bob", Person.class).withChangedProperty("salary").build() );
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
     * Last changes on nested ValueObject
     * (when Address is a ValueObject nested in PersonDetails ValueObject):
     * <pre>
     * javers.findChanges( QueryBuilder.byValueObject(Person.class, "personDetails/address").build() );
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
     * javers.findChanges( QueryBuilder.byClass(Person.class).withChangedProperty("myProperty").build() );
     * </pre>
     *
     * @return A list ordered in reverse chronological order. Empty if nothing found.
     * @see <a href="http://javers.org/documentation/jql-examples/">http://javers.org/documentation/jql-examples</a>
     */
    List<Change> findChanges(JqlQuery query);

    /**
     * Queries JaversRepository for object Snapshots. <br/>
     * Snapshot is a historical state of a domain object captured as the property->value Map.
     * <br/><br/>
     *
     * For example, to get latest Snapshots of "bob" Person, call:
     * <pre>
     * javers.findSnapshots( QueryBuilder.byInstanceId("bob", Person.class).limit(5).build() );
     * </pre>
     *
     * For more query examples, see {@link #findChanges(JqlQuery)} method.
     * <br/>
     * Use the same JqlQuery to get changes, snapshots and shadows views.
     *
     * @return A list ordered in reverse chronological order. Empty if nothing found.
     * @see <a href="http://javers.org/documentation/jql-examples/">http://javers.org/documentation/jql-examples</a>
     */
    List<CdoSnapshot> findSnapshots(JqlQuery query);

    /**
     * Latest snapshot of a given Entity instance.
     * <br/><br/>
     *
     * For example, to get the current state of <code>Bob</code>, call:
     * <pre>
     * javers.getLatestSnapshot("bob", Person.class);
     * </pre>
     *
     * Returns Optional#EMPTY if an instance is not versioned.
     */
    Optional<CdoSnapshot> getLatestSnapshot(Object localId, Class entity);

    /**
     * Historical snapshot of a given Entity instance.
     * <br/><br/>
     *
     * For example, to get the historical state of <code>Bob</code> at 2017-01-01, call:
     * <pre>
     * javers.getHistoricalSnapshot("bob", Person.class, LocalDateTime.of(2017,01,01));
     * </pre>
     *
     * Returns Optional#EMPTY if an instance is not versioned.
     *
     * @since 3.4
     */
    Optional<CdoSnapshot> getHistoricalSnapshot(Object localId, Class entity, LocalDateTime effectiveDate);

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
     * for (Property p : jType.getPropertyNames()){
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
    <T extends JaversType> T getTypeMapping(Type userType);

    /**
     * Returns {@link Property} which underlies given {@link PropertyChange}
     *
     * @since 1.4.1
     */
    Property getProperty(PropertyChange propertyChange);
}
