package org.javers.repository.jql;

import org.javers.common.exception.JaversException;
import org.javers.common.exception.JaversExceptionCode;
import org.javers.common.string.ToStringBuilder;
import org.javers.common.validation.Validate;
import org.javers.core.Javers;
import org.javers.core.commit.CommitId;
import org.javers.core.metamodel.object.*;
import org.javers.core.metamodel.type.ManagedType;
import org.javers.core.metamodel.type.TypeMapper;
import org.javers.repository.api.QueryParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
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
    private static final Logger logger = LoggerFactory.getLogger(JQL_LOGGER_NAME);

    private QueryParams queryParams;
    private final FilterDefinition filterDefinition;
    private final ShadowScopeDefinition shadowScopeDef;
    private Filter filter;
    private Stats stats;

    JqlQuery(FilterDefinition filter, QueryParams queryParams, ShadowScopeDefinition shadowScope) {
        Validate.argumentsAreNotNull(filter);
        this.queryParams = queryParams;
        this.filterDefinition = filter;
        this.shadowScopeDef = shadowScope;
    }

    void validate(){
        if (isAggregate()) {
            if (!(isClassQuery() || isInstanceIdQuery())) {
                throw new JaversException(JaversExceptionCode.MALFORMED_JQL,
                        "aggregate filter can be enabled only for byClass and byInstanceId queries");
            }
        }

        if (getShadowScope() != DEEP_PLUS && getMaxGapsToFill() > 0) {
            throw new JaversException(JaversExceptionCode.MALFORMED_JQL,
                    "maxGapsToFill can be used only in the DEEP_PLUS query scope");
        }
    }

    @Override
    public String toString() {
        return "\nJqlQuery {\n" +
                "  "+filter + "\n"+
                "  "+queryParams + "\n" +
                "  "+shadowScopeDef + "\n" +
                "  "+stats+ "\n" +
                "}";
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

    void compile(GlobalIdFactory globalIdFactory, TypeMapper typeMapper, QueryType queryType) {
        this.stats = new Stats();
        this.filter = filterDefinition.compile(globalIdFactory, typeMapper);

        if (queryType == QueryType.SHADOWS &&
                (isInstanceIdQuery() || isClassQuery())) {
            queryParams = QueryParams.forShadowQuery(queryParams);
        }

        validate();
    }

    boolean matches(GlobalId globalId) {
        return filter.matches(globalId);
    }

    boolean isNewObjectChanges() {
        return queryParams.newObjectChanges();
    }

    boolean isAnyDomainObjectQuery() {
        return hasFilter(AnyDomainObjectFilter.class);
    }

    boolean isIdQuery(){
        return hasFilter(IdFilter.class);
    }

    boolean hasChangedPropertyFilter(){
        return queryParams.changedProperty().isPresent();
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
     * Shadow query execution statistics. Can be printed by:
     * <pre>
     * &lt;logger name="org.javers.JQL" level="DEBUG"/&gt;
     * </pre>
     */
    public Stats stats() {
        return stats;
    }

    public static class Stats {
        private long startTimestamp = System.currentTimeMillis();
        private long endTimestamp;
        private int dbQueriesCount;
        private int allSnapshotsCount;
        private int shallowSnapshotsCount;
        private int deepPlusSnapshotsCount;
        private int commitDeepSnapshotsCount;
        private int childVOSnapshotsCount;
        private int deepPlusGapsFilled;
        private int deepPlusGapsLeft;

        void logQueryInChildValueObjectScope(GlobalId reference, CommitId context, int snapshotsLoaded) {
            validateChange();
            logger.debug("CHILD_VALUE_OBJECT query for '{}' at commitId {}, {} snapshot(s) loaded",
                    reference.toString(),
                    context.value(),
                    snapshotsLoaded);

            dbQueriesCount++;
            allSnapshotsCount += snapshotsLoaded;
            childVOSnapshotsCount += snapshotsLoaded;
        }

        void logMaxGapsToFillExceededInfo(GlobalId reference) {
            validateChange();
            deepPlusGapsLeft++;
            logger.debug("warning: object '" + reference.toString() +
                         "' is outside of the DEEP_PLUS+{} scope" +
                         ", references to this object will be nulled. " +
                         "Increase maxGapsToFill and fill all gaps in your object graph.", deepPlusGapsFilled);
        }

        void logQueryInDeepPlusScope(GlobalId reference, CommitId context, int snapshotsLoaded) {
            validateChange();
            dbQueriesCount++;
            allSnapshotsCount += snapshotsLoaded;
            deepPlusSnapshotsCount += snapshotsLoaded;
            deepPlusGapsFilled++;

            logger.debug("DEEP_PLUS query for '{}' at commitId {}, {} snapshot(s) loaded, gaps filled so far: {}",
                    reference.toString(),
                    context.value(),
                    snapshotsLoaded,
                    deepPlusGapsFilled);
        }

        void logShallowQuery(List<CdoSnapshot> snapshots) {
            validateChange();
            logger.debug("SHALLOW query: {} snapshots loaded (entities: {}, valueObjects: {})", snapshots.size(),
                    snapshots.stream().filter(it -> it.getGlobalId() instanceof InstanceId).count(),
                    snapshots.stream().filter(it -> it.getGlobalId() instanceof ValueObjectId).count());
            dbQueriesCount++;
            allSnapshotsCount += snapshots.size();
            shallowSnapshotsCount += snapshots.size();
        }

        void logQueryInCommitDeepScope(List<CdoSnapshot> snapshots) {
            validateChange();
            logger.debug("COMMIT_DEEP query: {} snapshots loaded", snapshots.size());
            dbQueriesCount++;
            allSnapshotsCount += snapshots.size();
            commitDeepSnapshotsCount+=snapshots.size();
        }

        void stop() {
            validateChange();
            endTimestamp = System.currentTimeMillis();
        }

        @Override
        public String toString() {
            if (endTimestamp == 0){
                return ToStringBuilder.toString(this,
                        "executed", "?");
            }
            return ToStringBuilder.toStringBlockStyle(this, "  ",
                    "executed in millis", endTimestamp-startTimestamp,
                    "DB queries", dbQueriesCount,
                    "all snapshots", allSnapshotsCount,
                    "SHALLOW snapshots", shallowSnapshotsCount,
                    "COMMIT_DEEP snapshots", commitDeepSnapshotsCount,
                    "CHILD_VALUE_OBJECT snapshots", childVOSnapshotsCount,
                    "DEEP_PLUS snapshots", deepPlusSnapshotsCount,
                    "gaps filled", deepPlusGapsFilled,
                    "gaps left!", deepPlusGapsLeft
            );
        }

        public long getStartTimestamp() {
            return startTimestamp;
        }

        public long getEndTimestamp() {
            return endTimestamp;
        }

        public int getDbQueriesCount() {
            return dbQueriesCount;
        }

        public int getAllSnapshotsCount() {
            return allSnapshotsCount;
        }

        public int getShallowSnapshotsCount() {
            return shallowSnapshotsCount;
        }

        public int getDeepPlusSnapshotsCount() {
            return deepPlusSnapshotsCount;
        }

        public int getCommitDeepSnapshotsCount() {
            return commitDeepSnapshotsCount;
        }

        public int getChildVOSnapshotsCount() {
            return childVOSnapshotsCount;
        }

        public int getDeepPlusGapsFilled() {
            return deepPlusGapsFilled;
        }

        public int getDeepPlusGapsLeft() {
            return deepPlusGapsLeft;
        }

        private void validateChange() {
            if (endTimestamp > 0) {
                throw new RuntimeException(new IllegalAccessException("executed query can't be changed"));
            }
        }
    }
}
