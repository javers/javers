package org.javers.repository.jql;

import java.util.Optional;
import org.javers.common.exception.JaversException;
import org.javers.common.exception.JaversExceptionCode;
import org.javers.common.validation.Validate;
import org.javers.core.Javers;
import org.javers.core.metamodel.object.GlobalId;
import org.javers.core.metamodel.object.GlobalIdFactory;
import org.javers.core.metamodel.type.ManagedType;
import org.javers.core.metamodel.type.TypeMapper;
import org.javers.repository.api.QueryParams;

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

    private final QueryParams queryParams;
    private final FilterDefinition filterDefinition;
    private Filter filter;
    private final ShadowScope shadowScope;

    JqlQuery(FilterDefinition filter, QueryParams queryParams, ShadowScope shadowScope) {
        Validate.argumentsAreNotNull(filter);
        this.queryParams = queryParams;
        this.filterDefinition = filter;
        this.shadowScope = shadowScope;
    }

    private void validate(){
        if (queryParams.isAggregate()) {
            if (!(isClassQuery() || isInstanceIdQuery())) {
                throw new JaversException(JaversExceptionCode.MALFORMED_JQL,
                        "aggregate filter can be enabled only for byClass and byInstanceId queries");
            }
        }
    }

    @Override
    public String toString() {
        return "JqlQuery{" +
                "queryParams=" + queryParams +
                ", filter=" + filter +
                '}';
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
        return shadowScope;
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
}
