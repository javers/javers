package org.javers.repository.mongo;

import com.google.gson.Gson;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import org.javers.core.commit.Commit;

/**
 * @author pawel szymczyk
 */
public class CommitMapper {

    private Gson gson;

    public CommitMapper() {
        this.gson = new Gson();
    }

    public DBObject map(Commit commit) {
        String commitAsJson = gson.toJson(commit);
        return (DBObject) JSON.parse(commitAsJson);
    }
}
