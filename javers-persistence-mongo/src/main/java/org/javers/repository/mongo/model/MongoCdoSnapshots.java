package org.javers.repository.mongo.model;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.javers.common.validation.Validate;

import java.util.List;

public class MongoCdoSnapshots extends BasicDBObject {

    public static final String COLLECTION_NAME = "Snapshots";

    public static final String GLOBAL_CDO_ID = "globalCdoId";
    private static final String SNAPSHOTS = "snapshots";

    public String getGlobalCdoId() {
        return get(GLOBAL_CDO_ID).toString();
    }

    public MongoSnapshot getLatest() {
        BasicDBList snapshots = getSnapshots();

        return new MongoSnapshot((DBObject) snapshots.get(snapshots.size() - 1));
    }

    public void addSnapshot(MongoSnapshot snapshot) {
        getSnapshots().add(snapshot);
    }

    private BasicDBList getSnapshots() {
        return (BasicDBList) get(SNAPSHOTS);
    }

    /**
    * cdoSnapshot - object from database
    */
    public MongoCdoSnapshots(DBObject cdoSnapshot) {
        Validate.argumentIsNotNull(cdoSnapshot);

        append(GLOBAL_CDO_ID, cdoSnapshot.get(GLOBAL_CDO_ID));
        append(SNAPSHOTS, cdoSnapshot.get(SNAPSHOTS));
    }

    public MongoCdoSnapshots(DBObject globalCdoId, List<MongoSnapshot> snapshots) {
        append(GLOBAL_CDO_ID, globalCdoId);
        append(SNAPSHOTS, new BasicDBList());

        for (MongoSnapshot snapshot: snapshots) {
            addSnapshot(snapshot);
        }
    }
}
