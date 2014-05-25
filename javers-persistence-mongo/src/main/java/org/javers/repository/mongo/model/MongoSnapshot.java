package org.javers.repository.mongo.model;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class MongoSnapshot extends BasicDBObject {

    private static final String COMMIT_ID = "commitId";
    private static final String STATE = "state";

    public MongoSnapshot(DBObject dbObject) {
        append(COMMIT_ID, dbObject.get(COMMIT_ID));
        append(STATE, dbObject.get(STATE));
    }

    public MongoSnapshot(String commitId, DBObject state) {
        append(COMMIT_ID, commitId);
        append(STATE, state);
    }
}
