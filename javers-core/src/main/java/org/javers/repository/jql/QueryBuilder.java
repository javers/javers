package org.javers.repository.jql;

import org.javers.common.collections.Lists;
import org.javers.common.validation.Validate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.javers.repository.jql.InstanceIdDTO.instanceId;

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

    public static SnapshotQuery getLatestSnapshotQuery(GlobalIdDTO globalId) {
        Validate.argumentIsNotNull(globalId);
        return new SnapshotQuery((List) Lists.asList(new IdFilter(globalId)),1);
    }

    public static ChangeQueryBuilder findChanges() {
        ChangeQueryBuilder builder = new ChangeQueryBuilder();
        return builder;
    }

    public static SnapshotQueryBuilder findSnapshots() {
        SnapshotQueryBuilder builder = new SnapshotQueryBuilder();
        return builder;
    }

    public S byInstanceId(Object localId, Class javaClass){
        Validate.argumentsAreNotNull(localId, javaClass);
        addFilter(new IdFilter(instanceId(localId, javaClass)));
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
