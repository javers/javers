package org.javers.repository.jql;

import org.javers.common.exception.JaversException;
import org.javers.common.exception.JaversExceptionCode;
import org.javers.common.validation.Validate;
import org.javers.core.Javers;
import org.javers.core.metamodel.object.CdoSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.javers.repository.jql.InstanceIdDTO.instanceId;

/**
 * Fluent API for building {@link JqlQuery},
 * executed with {@link Javers#findChanges(JqlQuery)} and {@link Javers#findSnapshots(JqlQuery)}
 *
 * @see <a href="http://javers.org/documentation/jql-examples/">http://javers.org/documentation/jql-examples</a>
 * @author bartosz.walacik
 */
public class QueryBuilder {
    private static int DEFAULT_LIMIT = 100;

    private int limit = DEFAULT_LIMIT;
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

    @Deprecated
    public static QueryBuilder byGlobalIdDTO(GlobalIdDTO globalId){
        Validate.argumentIsNotNull(globalId);
        return new QueryBuilder(new IdFilter(globalId));
    }

    /**
     * Filters to snapshots with a given property on changed properties list.
     *
     * @see CdoSnapshot#getChanged()
     */
    public QueryBuilder andProperty(String propertyName) {
        Validate.argumentIsNotNull(propertyName);
        addFilter(new PropertyFilter(propertyName));
        return this;
    }

    /**
     * Affects changes query only.
     * When switched on, additional changes are generated for the initial snapshot
     * (the first commit of a given object). Off by default.
     * <br/>
     * It means one NewObject change for each initial snapshot
     * and the full set of initial PropertyChanges with null on the left side
     * and initial property value on the right.
     */
    public QueryBuilder withNewObjectChanges(boolean newObjectChanges) {
        this.newObjectChanges = newObjectChanges;
        return this;
    }

    /**
     * Limits number of snapshots to be read from JaversRepository, default is 100.
     * <br/>
     * Always choose reasonable limits to improve performance of your queries.
     */
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
