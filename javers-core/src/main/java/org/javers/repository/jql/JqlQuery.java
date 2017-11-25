package org.javers.repository.jql;

import java.util.List;
import java.util.Optional;
import org.javers.common.exception.JaversException;
import org.javers.common.exception.JaversExceptionCode;
import org.javers.common.string.ToStringBuilder;
import org.javers.common.validation.Validate;
import org.javers.core.Javers;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.core.metamodel.object.GlobalId;
import org.javers.core.metamodel.object.GlobalIdFactory;
import org.javers.core.metamodel.type.ManagedType;
import org.javers.core.metamodel.type.TypeMapper;
import org.javers.repository.api.QueryParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

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
    private static final String JQL_LOGGER_NAME = "org.javers.JQL";
    private static final Logger logger = LoggerFactory.getLogger(JQL_LOGGER_NAME);

    private final QueryParams queryParams;
    private final FilterDefinition filterDefinition;
    private Filter filter;
    private final ShadowScopeDefinition shadowScopeDef;
    private final Stats stats = new Stats();

    JqlQuery(FilterDefinition filter, QueryParams queryParams, ShadowScopeDefinition shadowScope) {
        Validate.argumentsAreNotNull(filter);
        this.queryParams = queryParams;
        this.filterDefinition = filter;
        this.shadowScopeDef = shadowScope;
    }

    private void validate(){
        if (isAggregate()) {
            if (!(isClassQuery() || isInstanceIdQuery())) {
                throw new JaversException(JaversExceptionCode.MALFORMED_JQL,
                        "aggregate filter can be enabled only for byClass and byInstanceId queries");
            }
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

    ShadowScope getShadowScope() {
        return shadowScopeDef.getShadowScope();
    }

    int getShadowScopeMaxGapsToFill() {
        return shadowScopeDef.getMaxGapsToFill();
    }

    String getChangedProperty(){
        return queryParams.changedProperty().get();
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

    void compile(GlobalIdFactory globalIdFactory, TypeMapper typeMapper) {
        this.filter = filterDefinition.compile(globalIdFactory, typeMapper);
        validate();
        this.stats.startTimestamp = System.currentTimeMillis();
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

    public Stats stats() {
        return stats;
    }

    public static class Stats {
        private long startTimestamp;
        private long endTimestamp;
        private int dbQueriesCount;
        private int allSnapshotsCount;
        private int shallowSnapshotsCount;
        private int deepPlusSnapshotsCount;
        private int commitDeepSnapshotsCount;
        private int childVOSnapshotsCount;

        void logQueryInChildValueObjectScope(List<CdoSnapshot> snapshots) {
            logger.debug("CHILD_VALUE_OBJECT query: {} snapshots loaded", snapshots.size());

            dbQueriesCount++;
            allSnapshotsCount += snapshots.size();
            childVOSnapshotsCount += snapshots.size();
        }

        void logQueryInDeepPlusScope(List<CdoSnapshot> snapshots) {
            logger.debug("DEEP_PLUS query: {} snapshots loaded", snapshots.size());

            dbQueriesCount++;
            allSnapshotsCount += snapshots.size();
            deepPlusSnapshotsCount = snapshots.size();
        }

        void logShallowQuery(List<CdoSnapshot> snapshots) {
            logger.debug("SHALLOW query: {} snapshots loaded", snapshots.size());
            dbQueriesCount++;
            allSnapshotsCount += snapshots.size();
            shallowSnapshotsCount += snapshots.size();
        }

        void logQueryInCommitDeepScope(List<CdoSnapshot> snapshots) {
            logger.debug("COMMIT_DEEP query: {} snapshots loaded", snapshots.size());
            dbQueriesCount++;
            allSnapshotsCount += snapshots.size();
            commitDeepSnapshotsCount+=snapshots.size();
        }

        void stop() {
            endTimestamp = System.currentTimeMillis();
        }

        @Override
        public String toString() {
            if (endTimestamp == 0){
                return ToStringBuilder.toString(this,
                        "executed", "?");
            }
            return ToStringBuilder.toString(this,
                    "executed in (ms)", endTimestamp-startTimestamp,
                    "DB queries", dbQueriesCount,
                    "all snapshots", allSnapshotsCount,
                    "SHALLOW snapshots", shallowSnapshotsCount,
                    "COMMIT_DEEP snapshots", commitDeepSnapshotsCount,
                    "CHILD_VALUE_OBJECT snapshots", childVOSnapshotsCount,
                    "DEEP_PLUS snapshots", deepPlusSnapshotsCount
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
    }
}
