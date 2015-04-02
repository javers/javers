package org.javers.repository.jql;

import org.javers.common.collections.Lists;
import org.javers.common.validation.Validate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.javers.repository.jql.InstanceIdDTO.instanceId;
import static org.javers.repository.jql.UnboundedValueObjectIdDTO.unboundedValueObjectId;

/**
 * Created by bartosz.walacik on 2015-03-29.
 *
 * @param <S> the "self" type of Builder
 * @param <Q> the Query type of Builder
 */
public abstract class QueryBuilder<S extends QueryBuilder, Q extends Query> {
    private int limit = 1000;
    private final List<Filter> filters = new ArrayList<>();
    protected final S myself;

    QueryBuilder(Class<?> selfType) {
        myself = (S) selfType.cast(this);
    }

    public static ChangeQueryBuilder findChanges() {
        ChangeQueryBuilder builder = new ChangeQueryBuilder();
        return builder;
    }

    public static SnapshotQueryBuilder findSnapshots() {
        SnapshotQueryBuilder builder = new SnapshotQueryBuilder();
        return builder;
    }

    public S byInstanceId(Object localId, Class entityClass){
        Validate.argumentsAreNotNull(localId, entityClass);
        addFilter(new IdFilter(instanceId(localId, entityClass)));
        return myself;
    }

    public S byValueObjectId(Object ownerLocalId, Class ownerEntityClass, String path){
        Validate.argumentsAreNotNull(ownerEntityClass, ownerLocalId, path);
        addFilter(new IdFilter(ValueObjectIdDTO.valueObjectId(ownerLocalId, ownerEntityClass, path)));
        return myself;
    }

    public S byUnboundedValueObjectId(Class valueObjectClass){
        Validate.argumentIsNotNull(valueObjectClass);
        addFilter(new IdFilter(unboundedValueObjectId(valueObjectClass)));
        return myself;
    }

    @Deprecated
    public S byGlobalIdDTO(GlobalIdDTO globalId){
        Validate.argumentIsNotNull(globalId);
        addFilter(new IdFilter(globalId));
        return myself;
    }

    public S andProperty(String propertyName) {
        addFilter(new PropertyFilter(propertyName));
        return myself;
    }

    public S limit(int limit) {
        this.limit = limit;
        return myself;
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

    public abstract Q build();

    public static class ChangeQueryBuilder extends QueryBuilder<ChangeQueryBuilder, ChangeQuery> {
        public ChangeQueryBuilder() {
            super(ChangeQueryBuilder.class);
        }

        @Override
        public ChangeQuery build(){
            return new ChangeQuery(getFilters(), getLimit());
        }
    }

    public static class SnapshotQueryBuilder extends QueryBuilder<SnapshotQueryBuilder, SnapshotQuery> {
        public SnapshotQueryBuilder() {
            super(SnapshotQueryBuilder.class);
        }

        @Override
        public SnapshotQuery build(){
            return new SnapshotQuery(getFilters(), getLimit());
        }
    }

}
