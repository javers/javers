package org.javers.repository.jql;

import org.javers.common.collections.Lists;
import org.javers.common.validation.Validate;

import java.util.List;

/**
 * Created by bartosz.walacik on 2015-03-29.
 */
public class QueryBuilder {

    public static ChangeQuery findChangesByGlobalId(GlobalIdDTO globalId, int limit) {
        Validate.argumentIsNotNull(globalId);
        return new ChangeQuery((List)Lists.asList(new IdFilter(globalId)), limit);
    }

    public static ChangeQuery findChangesByIdAndProperty(GlobalIdDTO globalId, String propertyName, int limit) {
        Validate.argumentsAreNotNull(globalId, propertyName);
        return new ChangeQuery((List)Lists.asList(new IdFilter(globalId), new PropertyFilter(propertyName)), limit);
    }

    public static SnapshotQuery findSnapshotsByGlobalId(GlobalIdDTO globalId, int limit) {
        Validate.argumentIsNotNull(globalId);
        return new SnapshotQuery((List)Lists.asList(new IdFilter(globalId)), limit);
    }

    public static SnapshotQuery findSnapshotsByIdAndProperty(GlobalIdDTO globalId, String propertyName, int limit) {
        Validate.argumentsAreNotNull(globalId, propertyName);
        return new SnapshotQuery((List)Lists.asList(new IdFilter(globalId), new PropertyFilter(propertyName)), limit);
    }

    public static SnapshotQuery getLatestSnapshotQuery(GlobalIdDTO globalId) {
        Validate.argumentIsNotNull(globalId);
        return new SnapshotQuery((List)Lists.asList(new IdFilter(globalId)),1);
    }
}
