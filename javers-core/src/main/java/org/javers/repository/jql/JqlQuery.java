package org.javers.repository.jql;

import org.javers.common.collections.Optional;
import org.javers.common.exception.JaversException;
import org.javers.common.exception.JaversExceptionCode;
import org.javers.common.validation.Validate;
import org.javers.core.Javers;
import org.javers.repository.api.QueryParams;

import java.util.List;

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
    private final List<Filter> filters;

    JqlQuery(List<Filter> filters, QueryParams queryParams) {
        Validate.argumentsAreNotNull(filters);
        this.queryParams = queryParams;
        this.filters = filters;
        validate();
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
                ", filters=" + filters +
                '}';
    }

    QueryParams getQueryParams() {
        return queryParams;
    }

    boolean hasFilter(Class<? extends Filter> ofType){
        return getFilter(ofType).isPresent();
    }

    Class getClassFilter(){
        return getFilter(ClassFilter.class).get().getRequiredClass();
    }

    GlobalIdDTO getIdFilter() {
        return getFilter(IdFilter.class).get().getGlobalId();
    }

    String getChangedProperty(){
        return queryParams.changedProperty().get();
    }

    VoOwnerFilter getVoOwnerFilter() {
        return getFilter(VoOwnerFilter.class).get();
    }

    <T extends Filter> Optional<T> getFilter(Class<T> ofType) {
        for (Filter f : filters) {
            if (f.getClass().equals(ofType)) {
                return Optional.of((T)f);
            }
        }
        return Optional.empty();
    }

    public boolean isNewObjectChanges() {
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
        return hasFilter(IdFilter.class)  && getIdFilter() instanceof InstanceIdDTO;
    }

    boolean isVoOwnerQuery(){
        return hasFilter(VoOwnerFilter.class);
    }
}
