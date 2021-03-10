package org.javers.repository.jql;

import org.javers.common.exception.JaversException;
import org.javers.common.exception.JaversExceptionCode;
import org.javers.common.validation.Validate;
import org.javers.core.CommitIdGenerator;
import org.javers.core.Javers;
import org.javers.core.metamodel.object.GlobalId;
import org.javers.core.metamodel.object.GlobalIdFactory;
import org.javers.core.metamodel.type.ManagedType;
import org.javers.core.metamodel.type.TypeMapper;
import org.javers.repository.api.QueryParams;
import org.javers.repository.api.QueryParamsBuilder;
import org.javers.repository.jql.ShadowQueryRunner.ShadowStats;
import org.javers.repository.jql.ShadowStreamQueryRunner.ShadowStreamStats;

import java.util.Optional;
import java.util.Set;

import static org.javers.repository.jql.ShadowScope.DEEP_PLUS;

/**
 * JaversRepository query.
 * Use it to query for object snapshots and object change history.
 * <br/><br/>
 *
 * Queries should be created by {@link QueryBuilder}
 * and executed with {@link Javers#findChanges(JqlQuery)} and {@link Javers#findSnapshots(JqlQuery)}
 *
 * @author bartosz.walacik
 */
public class JqlQuery {
    public static final String JQL_LOGGER_NAME = "org.javers.JQL";

    private QueryParams queryParams;
    private final FilterDefinition filterDefinition;
    private final ShadowScopeDefinition shadowScopeDef;
    private Filter filter;
    private ShadowStreamStats shadowStats;

    JqlQuery(FilterDefinition filter, QueryParams queryParams, ShadowScopeDefinition shadowScope) {
        Validate.argumentsAreNotNull(filter);
        this.queryParams = queryParams;
        this.filterDefinition = filter;
        this.shadowScopeDef = shadowScope;
    }

    JqlQuery nextQueryForStream() {
        return new JqlQuery(filterDefinition, queryParams.nextPage(), shadowScopeDef);
    }

    void validate(CommitIdGenerator commitIdGenerator){
        if (queryParams.toCommitId().isPresent() &&
            commitIdGenerator != CommitIdGenerator.SYNCHRONIZED_SEQUENCE)
        {
            throw new JaversException(JaversExceptionCode.MALFORMED_JQL,
                    "toCommitId() filter can be used only with CommitIdGenerator.SYNCHRONIZED_SEQUENCE");
        }

        if (isAggregate()) {
            if (!(isClassQuery() || isInstanceIdQuery())) {
                throw new JaversException(JaversExceptionCode.MALFORMED_JQL,
                        "aggregate filter can be enabled only when querying for Entities, in byClass() and byInstanceId() queries");
            }
        }

        if (getShadowScope() != DEEP_PLUS && getMaxGapsToFill() > 0) {
            throw new JaversException(JaversExceptionCode.MALFORMED_JQL,
                    "maxGapsToFill can be used only in the DEEP_PLUS query scope");
        }
    }

    @Override
    public String toString() {
        return "JqlQuery {\n" +
                "  "+filterDefinition + "\n"+
                "  "+queryParams + "\n" +
                "  shadowScope: "+shadowScopeDef.getScope() + "\n" +
                streamStats().map(it -> "  "+ it + "\n").orElse("") +
                "}";
    }

    FilterDefinition getFilterDefinition() {
        return filterDefinition;
    }

    QueryParams getQueryParams() {
        return queryParams;
    }

    boolean hasFilter(Class<? extends Filter> ofType){
        return getFilter(ofType).isPresent();
    }

    Set<ManagedType> getClassFilter(){
        return getFilter(ClassFilter.class).get().getManagedTypes();
    }

    GlobalId getIdFilter() {
        return getFilter(IdFilter.class).get().getGlobalId();
    }

    VoOwnerFilter getVoOwnerFilter() {
        return getFilter(VoOwnerFilter.class).get();
    }

    <T extends Filter> Optional<T> getFilter(Class<T> ofType) {
        Validate.conditionFulfilled(filter != null, "jqlQuery is not compiled");
        if (filter.getClass().equals(ofType)) {
            return Optional.of((T)filter);
        }
        return Optional.empty();
    }

    JqlQuery changeLimit(int newLimit, int newSkip) {
        return new JqlQuery(
                filterDefinition,
                QueryParamsBuilder.copy(queryParams).limit(newLimit).skip(newSkip).build(),
                shadowScopeDef);
    }

    void changeToAggregatedIfEntityQuery() {
        if (isInstanceIdQuery() || isClassQuery()) {
            queryParams = queryParams.changeAggregate(true);
        }
    }

    void compile(GlobalIdFactory globalIdFactory, TypeMapper typeMapper, CommitIdGenerator commitIdGenerator) {
        filter = filterDefinition.compile(globalIdFactory, typeMapper);
        validate(commitIdGenerator);
    }

    boolean matches(GlobalId globalId) {
        return filter.matches(globalId);
    }

    boolean isAnyDomainObjectQuery() {
        return hasFilter(AnyDomainObjectFilter.class);
    }

    boolean isIdQuery(){
        return hasFilter(IdFilter.class);
    }

    boolean isClassQuery() {
        return hasFilter(ClassFilter.class);
    }

    boolean isInstanceIdQuery() {
        Optional<IdFilter> idFilter = getFilter(IdFilter.class);
        return idFilter.isPresent() && idFilter.get().isInstanceIdFilter();
    }

    boolean isVoOwnerQuery(){
        return hasFilter(VoOwnerFilter.class);
    }

    public boolean isAggregate() {
        return queryParams.isAggregate();
    }

    public int getMaxGapsToFill() {
        return shadowScopeDef.getMaxGapsToFill();
    }

    public ShadowScope getShadowScope() {
        return shadowScopeDef.getScope();
    }

    /**
     * Full statistics from Shadow query execution.
     * Contains joined stats from all frames.<br/>
     * If only one frame was needed
     * (if {@link QueryBuilder#snapshotQueryLimit(Integer)} wasn't hit)
     * &mdash; it's equiv to {@link #firstFrameStats()}.
     * <br/><br/>
     *
     * Available only for {@link Javers#findShadows(JqlQuery)} and {@link Javers#findShadowsAndStream(JqlQuery)}.
     *
     * <br/><br/>
     *
     * Usage:<br/>
     * <code>System.out.println(query))</code><br/>
     * or<br/>
     * <code>System.out.println(query.streamStats().get())</code>
     * <br/><br/>
     *
     * Detailed log from can printed by the org.javers.JQL logger:
     * <pre>&lt;logger name="org.javers.JQL" level="DEBUG"/&gt;
     * </pre>
     * @see #firstFrameStats()
     */
    public Optional<ShadowStreamStats> streamStats() {
        return Optional.ofNullable(shadowStats);
    }

    /**
     * Statistics from the first (frame) Shadow query
     * executed
     * by {@link Javers#findShadows(JqlQuery)} or {@link Javers#findShadowsAndStream(JqlQuery)}.
     * <br/>
     *
     * If only one frame was needed
     * (if {@link QueryBuilder#snapshotQueryLimit(Integer)} wasn't hit)
     * &mdash; it's equiv to {@link #streamStats()}.
     * <br/><br/>
     *
     * Available only for {@link Javers#findShadows(JqlQuery)} and {@link Javers#findShadowsAndStream(JqlQuery)}.
     * <br/><br/>
     *
     * Usage:<br/>
     * <code>System.out.println(query.firstFrameStats().get())</code>
     *e
     * @see #streamStats()
     */
    public Optional<ShadowStats> firstFrameStats() {
        return streamStats().map(it -> it.getFirstFrameStats());
    }

    void setShadowQueryRunnerStats(ShadowStreamStats stats) {
        this.shadowStats = stats;
    }
}
