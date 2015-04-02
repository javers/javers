package org.javers.repository.jql;

import org.javers.common.collections.Optional;
import org.javers.common.exception.JaversException;
import org.javers.common.exception.JaversExceptionCode;
import org.javers.common.validation.Validate;
import org.javers.core.metamodel.object.GlobalId;

import java.util.List;

/**
 * Created by bartosz.walacik on 2015-03-28.
 */
public abstract class Query<T> {

    private final int limit;
    private final List<Filter> filters;

    Query (List<Filter> filters, int limit) {
        Validate.argumentsAreNotNull(filters);
        this.limit = limit;
        this.filters = filters;

        if (!isIdQuery()){
            throw new JaversException(JaversExceptionCode.MALFORMED_JQL, "no GlobalId, provide object GlobalId using QueryBuilder.byInstanceId()");
        }
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

    GlobalIdDTO getIdFilter() {
        return getFilter(IdFilter.class).get().getGlobalId();
    }

    String getPropertyName(){
        return getFilter(PropertyFilter.class).get().getPropertyName();
    }

    <T extends Filter> Optional<T> getFilter(Class<T> ofType) {
        for (Filter f : filters) {
            if (f.getClass().equals(ofType)) {
                return Optional.of((T)f);
            }
        }
        return Optional.empty();
    }

    boolean isIdOnlyQuery(){
        return hasFilter(IdFilter.class) && !hasFilter(PropertyFilter.class);
    }

    boolean isIdQuery(){
        return hasFilter(IdFilter.class);
    }

    boolean isPropertyQuery(){
        return hasFilter(IdFilter.class) && hasFilter(PropertyFilter.class);
    }
}
