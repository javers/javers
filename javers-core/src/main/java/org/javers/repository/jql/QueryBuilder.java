package org.javers.repository.jql;

import org.javers.common.collections.Sets;
import org.javers.common.exception.JaversException;
import org.javers.common.validation.Validate;
import org.javers.core.Javers;
import org.javers.core.commit.CommitId;
import org.javers.core.commit.CommitMetadata;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.core.metamodel.object.SnapshotType;
import org.javers.repository.api.QueryParamsBuilder;
import org.javers.repository.jql.FilterDefinition.*;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.stream.Collectors;

import static org.javers.common.collections.Lists.asList;
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
     * Only snapshots with changes on a given property.
     *
     * @see CdoSnapshot#getChanged()
     */
    public QueryBuilder withChangedProperty(String propertyName) {
        Validate.argumentIsNotNull(propertyName);
        queryParamsBuilder.changedProperties(asList(propertyName));
        return this;
    }

    /**
     * Only snapshots with changes on one or more properties from a given list.
     *
     * @see CdoSnapshot#getChanged()
     */
    public QueryBuilder withChangedPropertyIn(String... propertyNames) {
        Validate.argumentIsNotNull(propertyNames);
        queryParamsBuilder.changedProperties(asList(propertyNames));
        return this;
    }

    /**
     * Since Javers 6.0 this method is <b>deprecated</b> and has no effect.
     * <br/><br/>
     *
     * Since Javers 6.0, the <code>newObjectChanges</code> flag is renamed to <code>initialChanges</code>
     * and can be set only on a Javers instance level,
     * see {@link org.javers.core.JaversBuilder#withInitialChanges(boolean)}.
     */
    @Deprecated
    public QueryBuilder withNewObjectChanges(boolean newObjectChanges) {
        return this;
    }

    /**
     * Since Javers 6.0 this method is <b>deprecated</b> and has no effect.
     * <br/><br/>
     *
     * Since Javers 6.0, the <code>newObjectChanges</code> flag is renamed to <code>initialChanges</code>
     * and can be set only on a Javers instance level,
     * see {@link org.javers.core.JaversBuilder#withInitialChanges(boolean)}.
     */
    @Deprecated
    public QueryBuilder withNewObjectChanges() {
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
     * <b>Should be changed only to improve performance of Shadow queries.</b>
     * Please do not confused it with {@link #limit(int)}.
     * <br/><br/>
     *
     * Works only with {@link Javers#findShadows(JqlQuery)} and {@link Javers#findShadowsAndStream(JqlQuery)}.
     * <br/>
     * Limits the number of Snapshots to be fetched from a JaversRepository in a single DB query
     * <br/>
     * &mdash; 100 by default.
     *
     * @throws JaversException MALFORMED_JQL if used with {@link Javers#findSnapshots(JqlQuery)} or {@link Javers#findChanges(JqlQuery)}
     */
    public QueryBuilder snapshotQueryLimit(Integer snapshotQueryLimit) {
        queryParamsBuilder.snapshotQueryLimit(snapshotQueryLimit);
        return this;
    }

    /**
     * Limits the number of Snapshots or Shadows to be fetched from a JaversRepository.
     * By default, the limit is set to 100.
     * <br/><br/>
     *
     * There are four types of JQL query output: List of Changes,
     * List of Snapshots, Stream of Shadows, and List of Shadows.
     * The limit() filter affects all of them, but in a different way:
     * <br/><br/>
     *
     * <ul>
     *   <li>{@link Javers#findSnapshots(JqlQuery)} &mdash; <code>limit()</code> works intuitively,
     *   it's the maximum size of a returned list.
     *   </li>
     *   <li>{@link Javers#findChanges(JqlQuery)} &mdash;
     *   <code>limit()</code> is applied to
     *   the Snapshots query, which underlies the Changes query.
     *   The size of the returned list can be <b>greater</b> than <code>limit()</code>,
     *   because, typically a difference between any two Snapshots consists of many atomic Changes.
     *   </li>
     *   <li>{@link Javers#findShadows(JqlQuery)} &mdash;
     *   <code>limit()</code> is applied to Shadows,
     *   it limits the size of the returned list.
     *   The underlying Snapshots query uses its own limit &mdash; {@link QueryBuilder#snapshotQueryLimit(Integer)}.
     *   Since one Shadow might be reconstructed from many Snapshots,
     *   when <code>snapshotQueryLimit()</code> is hit, Javers repeats a given Shadow query
     *   to load a next <i>frame</i> of Shadows until required limit is reached.
     *   </li>
     *   <li> {@link Javers#findShadowsAndStream(JqlQuery)} &mdash;
     *   <code>limit()</code> works like in <code>findShadows()</code>, it limits the size of the returned stream.
     *   The main difference is that the stream is lazy loaded and subsequent
     *   <i>frame</i> queries
     *   are executed gradually, during the stream consumption.
     *   </li>
     * </ul>
     *
     * See
     * <a href="https://github.com/javers/javers/blob/master/javers-core/src/test/groovy/org/javers/core/examples/QueryBuilderLimitExamples.groovy">
     * QueryBuilderLimitExamples.groovy
     * </a>.
     */
    public QueryBuilder limit(int limit) {
        queryParamsBuilder.limit(limit);
        return this;
    }

    /**
     * Sets the number of Snapshots or Shadows to skip.<br/>
     * Use skip() and limit() for paging.
     * <br/><br/>
     *
     * See {@link #limit(int)}
     */
    public QueryBuilder skip(int skip) {
        queryParamsBuilder.skip(skip);
        return this;
    }

    /**
     * Limits to snapshots created after this date or exactly at this date.
     *
     * @see CommitMetadata#getCommitDate()
     * @see #fromInstant(Instant)}
     * @see #to(LocalDateTime)
     */
    public QueryBuilder from(LocalDateTime from) {
        queryParamsBuilder.from(from);
        return this;
    }

    /**
     * Limits to snapshots created after this UTC date or exactly at this UTC date.
     * <br/><br/>
     *
     * @see CommitMetadata#getCommitDateInstant()
     * @see #toInstant(Instant)
     * @see #from(LocalDateTime)
     */
    public QueryBuilder fromInstant(Instant fromInstant) {
        queryParamsBuilder.fromInstant(fromInstant);
        return this;
    }

    /**
     * Limits to snapshots created before this date or exactly at this date.
     *
     * @see CommitMetadata#getCommitDate()
     * @see #toInstant(Instant)}
     * @see #from(LocalDateTime)
     */
    public QueryBuilder to(LocalDateTime to) {
        queryParamsBuilder.to(to);
        return this;
    }

    /**
     * Limits to snapshots created before this UTC date or exactly at this UTC date.
     *
     * @see CommitMetadata#getCommitDateInstant()
     * @see #fromInstant(Instant)
     * @see #to(LocalDateTime)
     */
    public QueryBuilder toInstant(Instant toInstant) {
        queryParamsBuilder.toInstant(toInstant);
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
     * Only snapshots with a given commit property partially containing a given value.
     * Equivalent to SQL LIKE clause: WHERE property_value LIKE '%value%'
     * <br/><br/>
     *
     * The matching is case insensitive on MongoDB and on most SQL databases.
     * <br/><br/>
     *
     * If this method is called multiple times,
     * <b>all</b> given values must match with persisted commit properties.
     */
    public QueryBuilder withCommitPropertyLike(String name, String value){
        Validate.argumentsAreNotNull(name, value);
        queryParamsBuilder.commitPropertyLike(name, value);
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
