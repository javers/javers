package org.javers.repository.jql;

import org.javers.common.collections.Optional;
import org.javers.common.exception.JaversException;
import org.javers.common.exception.JaversExceptionCode;
import org.javers.common.validation.Validate;

import java.util.List;

/**
 * Created by bartosz.walacik on 2015-03-28.
 */
public class JqlQuery<T> {

    private final int limit;
    private final boolean newObjectChanges;
    private final List<Filter> filters;

    JqlQuery(List<Filter> filters, boolean newObjectChanges, int limit) {
        Validate.argumentsAreNotNull(filters);
        this.limit = limit;
        this.filters = filters;
        this.newObjectChanges = newObjectChanges;
    }

    @Override
    public String toString() {
        return "JqlQuery{" +
                "limit=" + limit +
                ", filters=" + filters +
                ", newObjectChanges="+ newObjectChanges +
                '}';
    }

    /**
     * choose reasonable limit (number of objects to fetch),
     * production database could contain more records than you expect
     */
    int getLimit() {
        return limit;
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
