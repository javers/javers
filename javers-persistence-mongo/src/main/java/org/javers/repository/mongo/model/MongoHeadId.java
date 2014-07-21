package org.javers.repository.mongo.model;

import com.mongodb.BasicDBObject;

/**
 * @author pawel szymczyk
 */
public class MongoHeadId extends BasicDBObject {

    public static final String COLLECTION_NAME = "jv_head_id";
    public static final String KEY = "id";

    public MongoHeadId(String headId) {
        super(KEY, headId);
    }
}
