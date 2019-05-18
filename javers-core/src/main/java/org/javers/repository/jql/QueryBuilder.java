package org.javers.repository.jql;

import org.javers.common.collections.Sets;
import org.javers.common.exception.JaversException;
import org.javers.common.exception.JaversExceptionCode;
import org.javers.common.validation.Validate;
import org.javers.core.Javers;
import org.javers.core.commit.CommitId;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.core.metamodel.object.SnapshotType;
import org.javers.repository.api.QueryParamsBuilder;
import org.javers.repository.jql.FilterDefinition.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.javers.repository.jql.InstanceIdDTO.instanceId;
import static java.time.LocalTime.MIDNIGHT;
import static org.javers.repository.jql.ShadowScope.*;

/**
 * Fluent API for building {@link JqlQuery},
 * executed with {@link Javers#findChanges(JqlQuery)} and {@link Javers#findSnapshots(JqlQuery)}
 *
 * @see <a href="http://javers.org/documentation/jql-examples/">http://javers.org/documentation/jql-examples</a>
 * @author bartosz.walacik
 */
public class QueryBuilder {
    private static final int DEFAULT_LIMIT = 100;
    private static final int DEFAULT_SKIP = 0;
    private static final int DEFAULT_GAPS_TO_FILL_LIMIT = 10;
    private static final ShadowScope DEFAULT_SHADOW_SCOPE = SHALLOW;

    private final FilterDefinition filter;
    private final QueryParamsBuilder queryParamsBuilder;
    private ShadowScope shadowScope = DEFAULT_SHADOW_SCOPE;
    private int maxGapsToFill;

    private QueryBuilder(FilterDefinition filter) {
        Validate.argumentIsNotNull(filter);
        this.filter = filter;
        queryParamsBuilder = QueryParamsBuilder
                .withLimit(DEFAULT_LIMIT)
                .skip(DEFAULT_SKIP);
    }

    /**
     * Query for selecting changes (or snapshots) made on any object.
     * <br/><br/>
     *
     * For example, last changes committed on any object can be fetched with:
     * <pre>
     * javers.findChanges( QueryBuilder.anyDomainObject().build() );
     * </pre>
     * @since 2.0
     */
    public static QueryBuilder anyDomainObject(){
        return new QueryBuilder(new AnyDomainObjectFilterDefinition());
    }

    /**
     * Query for selecting changes (or snapshots) made on
     * any object (Entity or ValueObject) of given classes.
     * <br/><br/>
     *
     * For example, last changes on any object of MyClass.class:
     * <pre>
     * javers.findChanges( QueryBuilder.byClass(MyClass.class).build() );
     * </pre>
     */
    public static QueryBuilder byClass(Class... requiredClasses){
        return new QueryBuilder(new ClassFilterDefinition(Sets.asSet(requiredClasses)));
    }

    /**
     * Query for selecting Changes, Snapshots or Shadows for a given Entity instance.
     * <br/><br/>
     *
     * For example, last Changes on "bob" Person:
     * <pre>
     * javers.findChanges( QueryBuilder.byInstanceId("bob", Person.class).build() );
     * </pre>
     *
     * @param localId Value of an Id-property. When an Entity has Composite-Id (more than one Id-property) &mdash;
     *                <code>localId</code> should be <code>Map&lt;String, Object&gt;</code> with
     *                Id-property name to value pairs.
     * @see <a href="https://github.com/javers/javers/blob/master/javers-core/src/test/groovy/org/javers/core/examples/CompositeIdExample.groovy">CompositeIdExample.groovy</a>
     */
    public static QueryBuilder byInstanceId(Object localId, Class entityClass){
        Validate.argumentsAreNotNull(localId, entityClass);
        return new QueryBuilder(new IdFilterDefinition(instanceId(localId, entityClass)));
    }


    /**
     * Query for selecting Changes, Snapshots or Shadows for a given Entity instance, identified by its type name.
     * <br/><br/>
     *
     * For example, last Changes on "bob" Person:
     * <pre>
     * javers.findChanges( QueryBuilder.byInstanceId("bob", "Person").build() );
     * </pre>
     *
     * @param localId Value of an Id-property. When an Entity has Composite-Id (more than one Id-property) &mdash;
     *                <code>localId</code> should be <code>Map&lt;String, Object&gt;</code> with
     *                Id-property name to value pairs.
     * @see <a href="https://github.com/javers/javers/blob/master/javers-core/src/test/groovy/org/javers/core/examples/CompositeIdExample.groovy">CompositeIdExample.groovy</a>
     */
    public static QueryBuilder byInstanceId(Object localId, String typeName){
        Validate.argumentsAreNotNull(localId, typeName);
        return new QueryBuilder(new IdAndTypeNameFilterDefinition(localId, typeName));
    }

    /**
     * Query for selecting changes (or snapshots) made on a concrete Entity instance.
     * <br/><br/>
     *
     * For example, last changes on "bob" Person:
     * <pre>
     * javers.findChanges( QueryBuilder.byInstanceId(new Person("bob")).build() );
     * </pre>
     * @Since 2.8.0
     */
    public static QueryBuilder byInstance(Object instance){
        Validate.argumentsAreNotNull(instance);
        return new QueryBuilder(new InstanceFilterDefinition(instance));
    }

    /**
     * Query for selecting changes (or snapshots)
     * made on all ValueObjects at given path, owned by any instance of given Entity.
     * <br/><br/>
     *
     * See <b>path</b> parameter hints in {@link #byValueObjectId(Object, Class, String)}.
     */
    public static QueryBuilder byValueObject(Class ownerEntityClass, String path){
        Validate.argumentsAreNotNull(ownerEntityClass, path);
        return new QueryBuilder(new VoOwnerFilterDefinition(ownerEntityClass, path));
    }

    /**
     * Query for selecting changes (or snapshots) made on a concrete ValueObject
     * (so a ValueObject owned by a concrete Entity instance).
     * <br/><br/>
     *
     * <b>Path parameter</b> is a relative path from owning Entity instance to ValueObject that you are looking for.
     * <br/><br/>
     *
     * When ValueObject is just <b>a property</b>, use propertyName. For example:
     * <pre>
     * class Employee {
     *     &#64;Id String name;
     *     Address primaryAddress;
     * }
     * ...
     * javers.findChanges( QueryBuilder.byValueObjectId("bob", Employee.class, "primaryAddress").build() );
     * </pre>
     *
     * When ValueObject is stored in <b>a List</b>, use propertyName and list index separated by "/", for example:
     * <pre>
     * class Employee {
     *     &#64;Id String name;
     *     List&lt;Address&gt; addresses;
     * }
     * ...
     * javers.findChanges( QueryBuilder.byValueObjectId("bob", Employee.class, "addresses/0").build() );
     * </pre>
     *
     * When ValueObject is stored as <b>a Map value</b>, use propertyName and map key separated by "/", for example:
     * <pre>
     * class Employee {
     *     &#64;Id String name;
     *     Map&lt;String,Address&gt; addressMap;
     * }
     * ...
     * javers.findChanges( QueryBuilder.byValueObjectId("bob", Employee.class, "addressMap/HOME").build() );
     * </pre>
     */
    public static QueryBuilder byValueObjectId(Object ownerLocalId, Class ownerEntityClass, String path){
        Validate.argumentsAreNotNull(ownerEntityClass, ownerLocalId, path);
        return new QueryBuilder(new IdFilterDefinition(ValueObjectIdDTO.valueObjectId(ownerLocalId, ownerEntityClass, path)));
    }

    @Deprecated
    public static QueryBuilder byGlobalId(GlobalIdDTO globalId){
        Validate.argumentIsNotNull(globalId);
        return new QueryBuilder(new IdFilterDefinition(globalId));
    }

    /**
     * Only snapshots which changed a given property.
     *
     * @see CdoSnapshot#getChanged()
     */
    public QueryBuilder withChangedProperty(String propertyName) {
        Validate.argumentIsNotNull(propertyName);
        queryParamsBuilder.changedProperty(propertyName);
        return this;
    }

    /**
     * See javadoc in {@link #withNewObjectChanges()}
     */
    public QueryBuilder withNewObjectChanges(boolean newObjectChanges) {
        queryParamsBuilder.newObjectChanges(newObjectChanges);
        return this;
    }

    /**
     * Affects changes query only.
     * When switched on, additional changes are generated for the initial snapshot
     * (the first commit of a given object). Off by default.
     * <br/>
     * It means one NewObject change for each initial snapshot
     * and the full set of initial PropertyChanges with null on the left side
     * and initial property value on the right.
     */
    public QueryBuilder withNewObjectChanges() {
        queryParamsBuilder.newObjectChanges(true);
        return this;
    }

    /**
     * Selects only snapshots with a given type: initial, update or terminal.
     * <br/><br/>
     *
     * Typical use case:
     *
     * <pre>
     * javers.findSnapshots(QueryBuilder.byClass(SnapshotEntity)
     *       .withChangedProperty("someProperty")
     *       .withSnapshotTypeUpdate().build())
     * </pre>
     */
    public QueryBuilder withSnapshotType(SnapshotType snapshotType) {
        queryParamsBuilder.withSnapshotType(snapshotType);
        return this;
    }

    /**
     * Selects only updating snapshots (without initial ones).
     */
    public QueryBuilder withSnapshotTypeUpdate() {
        queryParamsBuilder.withSnapshotType(SnapshotType.UPDATE);
        return this;
    }

    /**
     * Only for Snapshot and Changes queries, see {@link #withChildValueObjects()}
     *
     * @since 2.1
     */
    public QueryBuilder withChildValueObjects(boolean aggregate) {
        queryParamsBuilder.withChildValueObjects(aggregate);
        return this;
    }

    /**
     * Only for Snapshot and Changes queries.
     * When enabled, selects all child ValueObjects owned by selected Entities.
     * <br/><br/>
     *
     * This switch <b>has no effect on Shadow queries</b> because Shadows
     * are always loaded together with their child ValueObjects
     * (see  {@link ShadowScope#CHILD_VALUE_OBJECT}).
     *
     * @since 2.1
     * @see <a href="http://javers.org/documentation/jql-examples/">http://javers.org/documentation/jql-examples</a>
     */
    public QueryBuilder withChildValueObjects() {
        queryParamsBuilder.withChildValueObjects(true);
        return this;
    }

    /**
     * Limits number of Snapshots to be fetched from JaversRepository in a single query,
     * default is 100.
     * <br/><br/>
     *
     * Always choose reasonable limits to improve performance of your queries.
     */
    public QueryBuilder limit(int limit) {
        queryParamsBuilder.limit(limit);
        return this;
    }

    /**
     * Sets the number of Snapshots to skip.
     * Use skip() and limit() for paging Snapshots and Changes.
     * <br/><br/>
     *
     * For paging Shadows use {@link Javers#findShadowsAndStream(JqlQuery)}
     * with {@link Stream#skip(long)} and {@link Stream#limit(long)}.
     *  <br/><br/>
     */
    public QueryBuilder skip(int skip) {
        queryParamsBuilder.skip(skip);
        return this;
    }

    /**
     * Limits to snapshots created after this date or exactly at this date.
     * <br/><br/>
     *
     * <h2>CommitDate is local datetime</h2>
     * Please remember that commitDate is persisted as LocalDateTime
     * (without information about time zone and daylight saving time).
     * <br/><br/>
     *
     * It may affects your query results. For example,
     * once a year when DST ends,
     * one hour is repeated (clock goes back from 3 am to 2 am).
     * Looking just on the commitDate we
     * can't distinct in which <i>iteration</i> of the hour, given commit was made.
     *
     * @see #to(LocalDateTime)
     */
    public QueryBuilder from(LocalDateTime from) {
        queryParamsBuilder.from(from);
        return this;
    }

    /**
     * Limits to snapshots created before this date or exactly at this date.
     */
    public QueryBuilder to(LocalDateTime to) {
        queryParamsBuilder.to(to);
        return this;
    }

    /**
     * Only snapshots created in a given commit.
     */
    public QueryBuilder withCommitId(CommitId commitId) {
        Validate.argumentIsNotNull(commitId);
        queryParamsBuilder.commitId(commitId);
        return this;
    }

    /**
     * Delegates to {@link #withCommitId(CommitId)}
     */
    public QueryBuilder withCommitId(BigDecimal commitId) {
        Validate.argumentIsNotNull(commitId);
        queryParamsBuilder.commitId(CommitId.valueOf(commitId));
        return this;
    }

    /**
     * Only snapshots created in given commits.
     */
    public QueryBuilder withCommitIds(Collection<BigDecimal> commitIds) {
        Validate.argumentIsNotNull(commitIds);
        queryParamsBuilder.commitIds(commitIds.stream().map(CommitId::valueOf).collect(Collectors.toSet()));
        return this;
    }

    /**
     * Only snapshots created before this commit or exactly in this commit.
     */
    public QueryBuilder toCommitId(CommitId commitId) {
        Validate.argumentIsNotNull(commitId);
        queryParamsBuilder.toCommitId(commitId);
        return this;
    }

    /**
     * delegates to {@link #from(LocalDateTime)} with MIDNIGHT
     */
    public QueryBuilder from(LocalDate fromDate) {
        return from(fromDate.atTime(MIDNIGHT));
    }

    /**
     * delegates to {@link #to(LocalDateTime)} with MIDNIGHT
     */
    public QueryBuilder to(LocalDate toDate) {
        return to(toDate.atTime(MIDNIGHT));
    }

    /**
     * Only snapshots with a given commit property.
     * <br/><br/
     *
     * If this method is called multiple times,
     * <b>all</b> given properties must match with persisted commit properties.
     * @since 2.0
     */
    public QueryBuilder withCommitProperty(String name, String value) {
        Validate.argumentsAreNotNull(name, value);
        queryParamsBuilder.commitProperty(name, value);
        return this;
    }

    /**
     * Only snapshots with a given version.
     */
    public QueryBuilder withVersion(long version) {
        Validate.argumentCheck(version > 0, "Version is not a positive number.");
        queryParamsBuilder.version(version);
        return this;
    }

    /**
     * Choose between <i>shallow</i>, <i>child-value-object</i>, </i><i>commit-deep</i> or <i>deep+</i> query scopes.
     * <br/>
     * The wider the scope, the more object shadows are loaded to the resulting graph.
     * <br/><br/>
     *
     * Default scope is {@link ShadowScope#SHALLOW}.
     * <br/><br/>
     *
     * Read more about query scopes in {@link Javers#findShadows(JqlQuery)} javadoc.
     * <br/><br/>
     *
     * Only for Shadow queries.
     *
     * @see <a href="http://javers.org/documentation/jql-examples/">http://javers.org/documentation/jql-examples</a>
     * @since 3.2
     */
    public QueryBuilder withShadowScope(ShadowScope shadowScope){
        Validate.argumentIsNotNull(shadowScope);
        this.shadowScope = shadowScope;
        if (shadowScope == DEEP_PLUS && maxGapsToFill == 0) {
            this.maxGapsToFill = DEFAULT_GAPS_TO_FILL_LIMIT;
        }

        return this;
    }

    /**
     * Selects {@link ShadowScope#COMMIT_DEEP} for Shadow queries.
     * <br/><br/>
     *
     * Read about query scopes in {@link Javers#findShadows(JqlQuery)} javadoc.
     *
     * @see <a href="http://javers.org/documentation/jql-examples/">http://javers.org/documentation/jql-examples</a>
     * @since 3.5
     */
    public QueryBuilder withScopeCommitDeep() {
        return withShadowScope(COMMIT_DEEP);
    }

    /**
     * Selects {@link ShadowScope#DEEP_PLUS}
     * with <code></cpce>maxGapsToFill</code> defaulted to <b>10</b>.
     * <br/><br/>
     *
     * Read more about query scopes in {@link Javers#findShadows(JqlQuery)} javadoc.
     * <br/><br/>
     *
     * Only for Shadow queries.
     *
     * @see <a href="http://javers.org/documentation/jql-examples/">http://javers.org/documentation/jql-examples</a>
     * @since 3.5
     */
    public QueryBuilder withScopeDeepPlus() {
        return withShadowScope(DEEP_PLUS);
    }

    /**
     * Selects {@link ShadowScope#DEEP_PLUS} with given <code>maxGapsToFill</code>.
     * <br/><br/>
     *
     * Read more about Shadow query <b>scopes, profiling, and runtime statistics</b>
     * in {@link Javers#findShadows(JqlQuery)} javadoc.
     * <br/><br/>
     *
     * Only for Shadow queries.
     *
     * @param maxGapsToFill Limits the number of referenced entity Shadows to be eagerly loaded.
     *                      The limit is global for a query. When it is exceeded,
     *                      references to other entities are nulled. Collections of entities may not be fully loaded.
     * @see <a href="http://javers.org/documentation/jql-examples/">http://javers.org/documentation/jql-examples</a>
     * @since 3.5
     */
    public QueryBuilder withScopeDeepPlus(int maxGapsToFill) {
        this.maxGapsToFill = maxGapsToFill;
        return withShadowScope(DEEP_PLUS);
    }

    /**
     * @deprecated renamed to {@link #withScopeDeepPlus()} ()}
     */
    @Deprecated
    public QueryBuilder withScopeCommitDeepPlus() {
        return withScopeDeepPlus();
    }

    /**
     * @deprecated renamed to {@link #withScopeDeepPlus(int)} ()}
     */
    @Deprecated
    public QueryBuilder withScopeCommitDeepPlus(int maxGapsToFill) {
        return withScopeDeepPlus(maxGapsToFill);
    }

    /**
     * Only snapshots committed by a given author.
     * @since 2.0
     */
    public QueryBuilder byAuthor(String author) {
        Validate.argumentIsNotNull(author);
        queryParamsBuilder.author(author);
        return this;
    }

    public JqlQuery build() {
        return new JqlQuery(filter, queryParamsBuilder.build(), new ShadowScopeDefinition(shadowScope, maxGapsToFill));
    }

    /**
     * renamed to {@link #withScopeCommitDeep()}
     * @deprecated
     */
    @Deprecated
    public QueryBuilder withShadowScopeDeep() {
        return withShadowScope(COMMIT_DEEP);
    }

    /**
     * renamed to {@link #withChangedProperty(String)}
     * @deprecated
     */
    @Deprecated
    public QueryBuilder andProperty(String propertyName) {
        return withChangedProperty(propertyName);
    }
}
