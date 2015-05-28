package org.javers.repository.mongo.model;

import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.javers.core.commit.CommitId;

import javax.print.Doc;

/**
 * @author pawel szymczyk
 */
public class MongoHeadId {
    public static final String COLLECTION_NAME = "jv_head_id";
    public static final String KEY = "id";

    private final String id;

    public MongoHeadId(Document doc) {
        this.id = doc.getString(KEY);
    }

    public MongoHeadId(CommitId id) {
        this.id = id.value();
    }

    public CommitId toCommitId() {
        return CommitId.valueOf(getId());
    }

    public Document toDocument() {
        return new Document(KEY, id);
    }

    public Document getUpdateCommand(){
        return new Document("$set", toDocument());
    }

    private String getId() {
        return id;
    }
}
