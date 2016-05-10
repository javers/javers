package org.javers.repository.jql;

import org.javers.common.collections.Optional;
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
    private final boolean newObjectChanges;
    private final List<Filter> filters;

    JqlQuery(List<Filter> filters, boolean newObjectChanges, QueryParams queryParams) {
        Validate.argumentsAreNotNull(filters);
        this.queryParams = queryParams;
        this.filters = filters;
        this.newObjectChanges = newObjectChanges;
    }

    @Override
    public String toString() {
        return "JqlQuery{" +
                "queryParams=" + queryParams +
                ", filters=" + filters +
                ", newObjectChanges="+ newObjectChanges +
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

    String getPropertyName(){
        return getFilter(PropertyFilter.class).get().getPropertyName();
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
        return newObjectChanges;
    }

    boolean isAnyDomainObjectOnlyQuery() {
        return hasFilter(AnyDomainObjectFilter.class) && filters.size() == 1;
    }

    boolean isIdOnlyQuery(){
        return hasFilter(IdFilter.class) && filters.size() == 1;
    }

    boolean isIdAndPropertyQuery(){
        return hasFilter(IdFilter.class) && hasFilter(PropertyFilter.class);
    }

    boolean isClassOnlyQuery() {
        return hasFilter(ClassFilter.class) && filters.size() == 1;
    }

    boolean isClassAndPropertyQuery(){
        return hasFilter(ClassFilter.class) && hasFilter(PropertyFilter.class);
    }

    boolean isVoOwnerOnlyQuery(){
        return hasFilter(VoOwnerFilter.class) && filters.size() == 1;
    }
}
