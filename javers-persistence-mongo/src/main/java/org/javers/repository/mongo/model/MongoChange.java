package org.javers.repository.mongo.model;

import com.mongodb.BasicDBObject;
import org.javers.core.diff.Change;
import org.javers.core.metamodel.object.GlobalCdoId;

import java.util.List;

public class MongoChange extends BasicDBObject {

    public static final String COLLECTION_NAME = "Changes";

    private GlobalCdoId globalCdoId;
    private List<Change> changes;
}
