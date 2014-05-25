package org.javers.repository.mongo.model;

import com.mongodb.BasicDBObject;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.core.metamodel.object.GlobalCdoId;

import java.util.List;

public class MongoSnapshot extends BasicDBObject {

    public static final String COLLECTION_NAME = "Snapshots";

    private GlobalCdoId globalCdoId;
    private List<CdoSnapshot> snapshots;

}
