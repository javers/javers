package org.javers.repository.mongo;

import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import org.javers.core.commit.Commit;
import org.javers.core.json.JsonConverter;
import org.javers.core.metamodel.object.CdoSnapshot;

/**
 * @author pawel szymczyk
 */
public class CommitMapper {

    private JsonConverter jsonConverter;

    public DBObject toDBObject(Commit commit) {
        String commitAsJson = jsonConverter.toJson(commit);
        return (DBObject) JSON.parse(commitAsJson);
    }

    public CdoSnapshot toCdoSnapshot(DBObject dbObject) {
        return null;
    }

    public void setJsonConverter(JsonConverter jsonConverter) {
        this.jsonConverter = jsonConverter;
    }
}
