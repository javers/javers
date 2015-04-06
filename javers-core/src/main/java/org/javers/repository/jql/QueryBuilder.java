package org.javers.repository.jql;

import org.javers.common.exception.JaversException;
import org.javers.common.exception.JaversExceptionCode;
import org.javers.common.validation.Validate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.javers.repository.jql.InstanceIdDTO.instanceId;
import static org.javers.repository.jql.UnboundedValueObjectIdDTO.unboundedValueObjectId;

/**
 * Created by bartosz.walacik on 2015-03-29.
 */
public class QueryBuilder {
    private int limit = 1000;
    private boolean newObjectChanges;
    private final List<Filter> filters = new ArrayList<>();

    private QueryBuilder(Filter initialFilter) {
        addFilter(initialFilter);
    }

    public static QueryBuilder byClass(Class requiredClass){
        return new QueryBuilder(new ClassFilter(requiredClass));
    }

    public static QueryBuilder byInstanceId(Object localId, Class entityClass){
        Validate.argumentsAreNotNull(localId, entityClass);
        return new QueryBuilder(new IdFilter(instanceId(localId, entityClass)));
    }

    public static QueryBuilder byValueObject(Class ownerEntityClass, String path){
        Validate.argumentsAreNotNull(ownerEntityClass, path);
        return new QueryBuilder(new VoOwnerFilter(ownerEntityClass, path));
    }

    public static QueryBuilder byValueObjectId(Object ownerLocalId, Class ownerEntityClass, String path){
        Validate.argumentsAreNotNull(ownerEntityClass, ownerLocalId, path);
        return new QueryBuilder(new IdFilter(ValueObjectIdDTO.valueObjectId(ownerLocalId, ownerEntityClass, path)));
    }

    /*public static QueryBuilder byUnboundedValueObjectId(Class valueObjectClass){
        Validate.argumentIsNotNull(valueObjectClass);
        return new QueryBuilder(new IdFilter(unboundedValueObjectId(valueObjectClass)));
    }*/

    @Deprecated
    public static QueryBuilder byGlobalIdDTO(GlobalIdDTO globalId){
        Validate.argumentIsNotNull(globalId);
        return new QueryBuilder(new IdFilter(globalId));
    }

    public QueryBuilder andProperty(String propertyName) {
        Validate.argumentIsNotNull(propertyName);
        addFilter(new PropertyFilter(propertyName));
        return this;
    }

    public QueryBuilder withNewObjectChanges(){
        this.newObjectChanges = true;
        return this;
    }

    public QueryBuilder limit(int limit) {
        this.limit = limit;
        return this;
    }

    protected void addFilter(Filter filter) {
        filters.add(filter);
    }

    protected List<Filter> getFilters() {
        return Collections.unmodifiableList(filters);
    }

    protected int getLimit() {
        return limit;
    }

    public JqlQuery build(){
        if (filters.isEmpty()){
            throw new JaversException(JaversExceptionCode.RUNTIME_EXCEPTION, "empty JqlQuery");
        }

        return new JqlQuery(getFilters(), newObjectChanges, getLimit());
    }
}
