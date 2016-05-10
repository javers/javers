package org.javers.repository.jql;

import org.javers.common.exception.JaversException;
import org.javers.common.exception.JaversExceptionCode;
import org.javers.common.validation.Validate;
import org.javers.core.Javers;
import org.javers.core.commit.CommitId;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.repository.api.QueryParams;
import org.javers.repository.api.QueryParamsBuilder;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.javers.repository.jql.InstanceIdDTO.instanceId;
import static org.joda.time.LocalTime.MIDNIGHT;

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


    private int limit = DEFAULT_LIMIT;
    private int skip = DEFAULT_SKIP;
    private LocalDateTime from;
    private LocalDateTime to;
    private boolean newObjectChanges;
    private CommitId commitId;
    private Long version;
    private String author;
    private final List<Filter> filters = new ArrayList<>();

    private QueryBuilder(Filter initialFilter) {
        addFilter(initialFilter);
    }

    /**
     * Query for selecting changes (or snapshots) made on any object.
     * <br/><br/>
     *
     * For example, last changes committed on any object can be fetched with:
     * <pre>
     * javers.findChanges( QueryBuilder.anyDomainObject().build() );
     * </pre>
     */
    public static QueryBuilder anyDomainObject(){
        return new QueryBuilder(new AnyDomainObjectFilter());
    }

    /**
     * Query for selecting changes (or snapshots) made on
     * any object (Entity or ValueObject) of given class.
     * <br/><br/>
     *
     * For example, last changes on any object of MyClass.class:
     * <pre>
     * javers.findChanges( QueryBuilder.byClass(MyClass.class).build() );
     * </pre>
     */
    public static QueryBuilder byClass(Class requiredClass){
        return new QueryBuilder(new ClassFilter(requiredClass));
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
        return new QueryBuilder(new IdFilter(instanceId(localId, entityClass)));
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
        return new QueryBuilder(new VoOwnerFilter(ownerEntityClass, path));
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
        return new QueryBuilder(new IdFilter(ValueObjectIdDTO.valueObjectId(ownerLocalId, ownerEntityClass, path)));
    }

    @Deprecated
    public static QueryBuilder byGlobalIdDTO(GlobalIdDTO globalId){
        Validate.argumentIsNotNull(globalId);
        return new QueryBuilder(new IdFilter(globalId));
    }

    /**
     * Filters to snapshots with a given property on changed properties list.
     *
     * @see CdoSnapshot#getChanged()
     */
    public QueryBuilder andProperty(String propertyName) {
        Validate.argumentIsNotNull(propertyName);
        addFilter(new PropertyFilter(propertyName));
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
        this.newObjectChanges = newObjectChanges;
        return this;
    }

    /**
     * Alias to {@link #withNewObjectChanges(boolean)} with true
     */
    public QueryBuilder withNewObjectChanges() {
        this.newObjectChanges = true;
        return this;
    }

    /**
     * Limits number of Snapshots to be fetched from JaversRepository, default is 100.
     * <br/>
     * Always choose reasonable limits to improve performance of your queries,
     * production database could contain more records than you expect.
     */
    public QueryBuilder limit(int limit) {
        this.limit = limit;
        return this;
    }

    /**
     * Sets the number of Snapshots to skip.
     * Use skip() and limit() for for paging.
     */
    public QueryBuilder skip(int skip) {
        this.skip = skip;
        return this;
    }

    /**
     * Limits Snapshots to be fetched from JaversRepository
     * to those created after (>=) given date.
     * <br/><br/>
     *
     * <h2>Important for Changes query</h2>
     * When querying for Changes done
     * after given point in time, results will lack
     * in <b>first</b> set of changes after that point.
     * <br/>
     *
     * For example. Consider three commits of some object done in every two days:
     *
     * <pre>
     *     javers.commit(someObject); //Monday
     *     javers.commit(someObject); //Wednesday
     *     javers.commit(someObject); //Friday
     * </pre>
     *
     * Query for Snapshots from Wednesday gives you 2 results as expected:
     * Wednesday's and Friday's Snapshots.
     * <br/>
     * But query for Changes from Wednesday gives you only
     * changes done on Friday.
     * That's because Changes Query is backed by Snapshots Query
     * and Changes are calculated as a diff between subsequent Snapshots.
     *
     * <h2>CommitDate is local datetime</h2>
     * Please remember that commitDate is persisted as LocalDateTime
     * (without information about time zone and daylight saving time).
     * <br/
     * It may affects your query results. For example,
     * once a year when DST ends,
     * one hour is repeated (clock goes back from 3 am to 2 am).
     * Looking just on the commitDate we
     * can't distinct in which <i>iteration</i> of the hour, given commit was made.
     *
     * @see #to(LocalDateTime)
     */
    public QueryBuilder from(LocalDateTime from) {
        this.from = from;
        return this;
    }

    /**
     * delegates to {@link #from(LocalDateTime)} with MIDNIGHT
     */
    public QueryBuilder from(LocalDate fromDate) {
        return from(fromDate.toLocalDateTime(MIDNIGHT));
    }

    /**
     * Limits Snapshots to be fetched from JaversRepository
     * to those created before (<=) given date.
     */
    public QueryBuilder to(LocalDateTime to) {
        this.to = to;
        return this;
    }

    /**
     * delegates to {@link #to(LocalDateTime)} with MIDNIGHT
     */
    public QueryBuilder to(LocalDate toDate) {
        return to(toDate.toLocalDateTime(MIDNIGHT));
    }

    /**
     * Limits Snapshots to be fetched from JaversRepository
     * to those with a given commitId.
     * <br/><br/>
     *
     * <b>Warning!</b> Using withCommitId filter when querying
     * for Changes makes no sense because the result will
     * always be empty.
     */
    public QueryBuilder withCommitId(CommitId commitId) {
        Validate.argumentIsNotNull(commitId);
        this.commitId = commitId;
        return this;
    }

    /**
     * delegates to {@link #withCommitId(CommitId)}
     */
    public QueryBuilder withCommitId(BigDecimal commitId) {
        Validate.argumentIsNotNull(commitId);
        return withCommitId(CommitId.valueOf(commitId));
    }

    /**
     * Limits Snapshots to be fetched from JaversRepository
     * to those with a given version.
     * <br/><br/>
     *
     * <b>Warning!</b> Using withVersion filter when querying
     * for Changes makes no sense because the result will
     * always be empty.
     */
    public QueryBuilder withVersion(long version) {
        Validate.argumentCheck(version > 0, "Version is not a positive number.");
        this.version = version;
        return this;
    }

    /**
     * Limits Snapshots to be fetched from JaversRepository
     * to those with a given commit author.
     */
    public QueryBuilder byAuthor(String author) {
        Validate.argumentIsNotNull(author);
        this.author = author;
        return this;
    }

    protected void addFilter(Filter filter) {
        filters.add(filter);
    }

    protected List<Filter> getFilters() {
        return Collections.unmodifiableList(filters);
    }

    protected QueryParams getQueryParams() {
        return QueryParamsBuilder
            .withLimit(limit)
            .skip(skip)
            .from(from).to(to)
            .commitId(commitId)
            .version(version)
            .author(author)
            .build();
    }

    public JqlQuery build(){
        if (filters.isEmpty()){
            throw new JaversException(JaversExceptionCode.RUNTIME_EXCEPTION, "empty JqlQuery");
        }
        return new JqlQuery(getFilters(), newObjectChanges, getQueryParams());
    }
}
