package org.javers.repository.mongo;

import com.mongodb.DBObject;
import org.javers.core.json.JsonConverter;
import org.javers.core.metamodel.object.CdoSnapshot;

/**
 * @author pawel szymczyk
 */
public class ModelMapper {

    private final JsonConverter jsonConverter;

    public ModelMapper(JsonConverter jsonConverter) {
        this.jsonConverter = jsonConverter;
    }

    public CdoSnapshot toCdoSnapshot(DBObject mongoSnapshot) {
        return jsonConverter.fromJson(mongoSnapshot.toString(), CdoSnapshot.class);
    }
}
