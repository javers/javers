package org.javers.repository.mongo;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import org.javers.core.commit.Commit;
import org.javers.core.commit.CommitId;
import org.javers.core.diff.Change;
import org.javers.core.diff.Diff;
import org.javers.core.json.JsonConverter;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.core.metamodel.object.GlobalCdoId;
import org.javers.core.metamodel.object.InstanceId;
import org.joda.time.LocalDateTime;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author pawel szymczyk
 */
public class ModelMapper {

    public static final String GLOBAL_CDO_ID = "globalCdoId";
    public static final String SNAPSHOTS = "snapshots";
    public static final String COMMIT_ID = "commitId";
    public static final String CDO_ID = "cdoId";
    public static final String ENTITY = "entity";
    public static final String AUTHOR = "author";
    public static final String DATE = "date";
    public static final String DIFF = "diff";

    private JsonConverter jsonConverter;

    public ModelMapper(JsonConverter jsonConverter) {
        this.jsonConverter = jsonConverter;
    }

    /**
    * to DBObject
    */
    public DBObject toDBObject(Commit commit) {

        GlobalCdoId globalCdoId = commit.getGlobalCdoId();

        return BasicDBObjectBuilder.start()
                .add(COMMIT_ID, toDBObject(commit.getId()))
                .add(GLOBAL_CDO_ID, toDBObject(globalCdoId))
                .add(AUTHOR, commit.getAuthor())
                .add(DATE, toDBObject(commit.getCommitDate()))
                .add(SNAPSHOTS, toDBList(commit.getSnapshots()))
                .add(DIFF, toDBList(commit.getDiff())).get();
    }

    private Object toDBList(Diff diff) {
        BasicDBList basicDBList = new BasicDBList();

        for (Change change : diff.getChanges()) {
            basicDBList.add(JSON.parse(jsonConverter.toJson(change)));
        }

        return basicDBList;
    }

    private BasicDBList toDBList(List<CdoSnapshot> snapshots) {
        BasicDBList basicDBList = new BasicDBList();

        for (CdoSnapshot cdoSnapshot : snapshots) {
            basicDBList.add(JSON.parse(jsonConverter.toJson(cdoSnapshot)));
        }

        return basicDBList;
    }

    public DBObject toDBObject(InstanceId.InstanceIdDTO dtoId) {

        return BasicDBObjectBuilder.start()
                .add(CDO_ID, dtoId.getCdoId())
                .add(ENTITY, dtoId.getEntity().getName())
                .get();
    }

    public DBObject toDBObject(GlobalCdoId globalId) {
        return (DBObject) JSON.parse(jsonConverter.toJson(globalId));
    };

    public String toDBObject(CommitId commitId) {
        return jsonConverter.toJsonElement(commitId).getAsString();
    }

    private String toDBObject(LocalDateTime commitDate) {
        return jsonConverter.toJsonElement(commitDate).getAsString();
    }

    /**
     * from DBObject
     */
    public List<CdoSnapshot> toCdoSnapshots(BasicDBList dbObject) {
        List<CdoSnapshot> snapshots = new ArrayList<>();

        Iterator<Object> iterator = dbObject.iterator();

        while (iterator.hasNext()) {
            snapshots.add(toCdoSnapshot((DBObject) iterator.next()));
        }

        return snapshots;
    }

    public CdoSnapshot toCdoSnapshot(DBObject snapshot) {
        return jsonConverter.fromJson(snapshot.toString(), CdoSnapshot.class);
    }

    public void setJsonConverter(JsonConverter jsonConverter) {
        this.jsonConverter = jsonConverter;
    }

    public CommitId toCommitId(String commitId) {
        return jsonConverter.fromJson(commitId, CommitId.class);
    }

}
