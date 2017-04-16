package org.javers.repository.jql;

import org.javers.common.collections.Sets;
import org.javers.common.validation.Validate;
import org.javers.core.Javers;
import org.javers.core.commit.CommitId;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.repository.api.QueryParamsBuilder;
import org.javers.repository.jql.FilterDefinition.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.stream.Collectors;

import static org.javers.repository.jql.InstanceIdDTO.instanceId;
import static java.time.LocalTime.MIDNIGHT;

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

    private final FilterDefinition filter;
    private final QueryParamsBuilder queryParamsBuilder;
    private ShadowScope shadowScope = ShadowScope.SHALLOW;

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
     * Query for selecting changes (or snapshots) made on a concrete Entity instance.
     * <br/><br/>
     *
     * For example, last changes on "bob" Person:
     * <pre>
     * javers.findChanges( QueryBuilder.byInstanceId("bob", Person.class).build() );
     * </pre>
     */
    public static QueryBuilder byInstanceId(Object localId, Class entityClass){
        Validate.argumentsAreNotNull(localId, entityClass);
        return new QueryBuilder(new IdFilterDefinition(instanceId(localId, entityClass)));
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

    public static QueryBuilder byGlobalId(GlobalIdDTO globalId){
        Validate.argumentIsNotNull(globalId);
        return new QueryBuilder(new IdFilterDefinition(globalId));
    }

    /**
     * Filters to snapshots (or changes) with a given property on changed properties list.
     *
     * @see CdoSnapshot#getChanged()
     */
    public QueryBuilder andProperty(String propertyName) {
        Validate.argumentIsNotNull(propertyName);
        queryParamsBuilder.changedProperty(propertyName);
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
    public QueryBuilder withNewObjectChanges(boolean newObjectChanges) {
        queryParamsBuilder.newObjectChanges(newObjectChanges);
        return this;
    }

    /**
     * Alias to {@link #withNewObjectChanges(boolean)} with true
     */
    public QueryBuilder withNewObjectChanges() {
        queryParamsBuilder.newObjectChanges(true);
        return this;
    }

    /**
     * Optional filter for Entity queries ({@link #byInstanceId(Object, Class)} and {@link #byClass(Class...)}).
     * Can be used with both changes and snapshots queries.
     * <br/><br/>
     *
     * When enabled, all child ValueObjects owned by selected Entities are included in a query scope.
     *
     * @since 2.1
     */
    public QueryBuilder withChildValueObjects(boolean aggregate) {
        queryParamsBuilder.withChildValueObjects(aggregate);
        return this;
    }

    /**
     * Alias to {@link #withChildValueObjects(boolean)} with true
     *
     * @since 2.1
     */
    public QueryBuilder withChildValueObjects() {
        queryParamsBuilder.withChildValueObjects(true);
        return this;
    }

    /**
     * Limits number of snapshots to be fetched from JaversRepository, default is 100.
     * <br/>
     * Always choose reasonable limits to improve performance of your queries,
     * production database could contain more records than you expect.
     */
    public QueryBuilder limit(int limit) {
        queryParamsBuilder.limit(limit);
        return this;
    }

    /**
     * Sets the number of snapshots to skip.
     * Use skip() and limit() for for paging.
     */
    public QueryBuilder skip(int skip) {
        queryParamsBuilder.skip(skip);
        return this;
    }

    /**
     * Limits snapshots (or changes) to be fetched from JaversRepository
     * to those created after (>=) given util.
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
     * delegates to {@link #from(LocalDateTime)} with MIDNIGHT
     */
    public QueryBuilder from(LocalDate fromDate) {
        return from(fromDate.atTime(MIDNIGHT));
    }

    /**
     * Limits snapshots (or changes) to be fetched from JaversRepository
     * to those created before (<=) given util.
     */
    public QueryBuilder to(LocalDateTime to) {
        queryParamsBuilder.to(to);
        return this;
    }

    /**
     * delegates to {@link #to(LocalDateTime)} with MIDNIGHT
     */
    public QueryBuilder to(LocalDate toDate) {
        return to(toDate.atTime(MIDNIGHT));
    }

    /**
     * Limits snapshots (or changes) to be fetched from JaversRepository
     * to those with a given commitId.
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
     * Limits snapshots (or changes) to be fetched from JaversRepository
     * to those with given commit ids.
     */
    public QueryBuilder withCommitIds(Collection<BigDecimal> commitIds) {
        Validate.argumentIsNotNull(commitIds);
        queryParamsBuilder.commitIds(commitIds.stream().map(CommitId::valueOf).collect(Collectors.toSet()));
        return this;
    }

    /**
     * Limits snapshots (or changes) to be fetched from JaversRepository
     * to those with a given commit property.
     * <br/>
     * If this method is called multiple times,
     * all given properties must match with persisted commit properties.
     * @since 2.0
     */
    public QueryBuilder withCommitProperty(String name, String value) {
        Validate.argumentsAreNotNull(name, value);
        queryParamsBuilder.commitProperty(name, value);
        return this;
    }

    /**
     * Limits snapshots (or changes) to be fetched from JaversRepository
     * to those with a given snapshot version.
     */
    public QueryBuilder withVersion(long version) {
        Validate.argumentCheck(version > 0, "Version is not a positive number.");
        queryParamsBuilder.version(version);
        return this;
    }

    /**
     * Choose between shallow or deep shadows.
     * <br/>
     *
     * Default is {@link ShadowScope#SHALLOW}
     * <br/>
     *
     * Makes sense only for Shadow queries.
     *
     * @see ShadowScope
     * @since 3.2
     */
    public QueryBuilder withShadowScope(ShadowScope shadowScope){
        Validate.argumentIsNotNull(shadowScope);
        this.shadowScope = shadowScope;
        return this;
    }

    /**
     * Alias to <code>withShadowScope(ShadowScope.COMMIT_DEPTH)</code>
     *
     * @see #withShadowScope(ShadowScope)
     * @see ShadowScope
     * @since 3.2
     */
    public QueryBuilder withShadowScopeDeep() {
        return withShadowScope(ShadowScope.COMMIT_DEPTH);
    }

    /**
     * Limits Snapshots to be fetched from JaversRepository
     * to those with a given commit author.
     * @since 2.0
     */
    public QueryBuilder byAuthor(String author) {
        Validate.argumentIsNotNull(author);
        queryParamsBuilder.author(author);
        return this;
    }

    public JqlQuery build(){
        return new JqlQuery(filter, queryParamsBuilder.build(), shadowScope);
    }
}
