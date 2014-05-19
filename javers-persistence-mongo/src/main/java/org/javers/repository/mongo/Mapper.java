package org.javers.repository.mongo;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import org.javers.common.collections.Function;
import org.javers.common.collections.Lists;
import org.javers.core.commit.Commit;
import org.javers.core.json.JsonConverter;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.core.metamodel.object.GlobalCdoId;
import org.javers.core.metamodel.object.InstanceId;

import java.util.ArrayList;
import java.util.List;

/**
 * @author pawel szymczyk
 */
public class Mapper {

    public static final String GLOBAL_CDO_ID = "globalCdoId";
    public static final String SNAPSHOTS = "snapshots";
    private JsonConverter jsonConverter;

    public Mapper(JsonConverter jsonConverter) {
        this.jsonConverter = jsonConverter;
    }

    public DBObject toDBObject(Commit commit) {

        GlobalCdoId globalCdoId = commit.getGlobalCdoId();

        return BasicDBObjectBuilder.start()
                .add(GLOBAL_CDO_ID, toDBObject(globalCdoId))
                .add(SNAPSHOTS, snapshotsToJson(commit.getSnapshots())).get();
    }

    private BasicDBList snapshotsToJson(List<CdoSnapshot> snapshots) {
        BasicDBList basicDBList = new BasicDBList();

        for (CdoSnapshot cdoSnapshot : snapshots) {
            basicDBList.add(jsonConverter.toJson(cdoSnapshot));
        }

        return basicDBList;
    }

    public List<CdoSnapshot> toCdoSnapshots(DBCursor dbCursor) {
        List<CdoSnapshot> result = new ArrayList<>();

        while (dbCursor.hasNext()) {
            result.add(toCdoSnapshot(dbCursor.next()));
        }

        return result;
    }

    public CdoSnapshot toCdoSnapshot(DBObject _snapshot) {
        return jsonConverter.fromJson(_snapshot.toString(), CdoSnapshot.class);
    }

    public void setJsonConverter(JsonConverter jsonConverter) {
        this.jsonConverter = jsonConverter;
    }

    public DBObject toDBObject(InstanceId.InstanceIdDTO dtoId) {

        return new BasicDBObject("globalCdoId", BasicDBObjectBuilder.start()
                .add("cdoId", dtoId.getCdoId())
                .add("entity", dtoId.getEntity().getName())
                .get());
    }

    public DBObject toDBObject(GlobalCdoId globalId) {
        return (DBObject) JSON.parse(jsonConverter.toJson(globalId));
    };
}
