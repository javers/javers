package org.javers.core.cases;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Property;
import org.mongodb.morphia.annotations.Entity;

@Entity
public class MongoStoredEntity {
    @Id
    private ObjectId _id;
    @Property("algorithm")
    private String _algorithm;
    @Property("version")
    private String _version;
    @Property("name")
    private String _name;
    @Property("description")
    private String _description;

    public MongoStoredEntity() {
    }

    public MongoStoredEntity(ObjectId id, String algorithm, String version, String name) {
        _id = id;
        _algorithm = algorithm;
        _version = version;
        _name = name;
    }

    public ObjectId get_id() {
        return _id;
    }

    public void set_id(ObjectId _id) {
        this._id = _id;
    }

    public String getAlgorithm() {
        return _algorithm;
    }

    public void setAlgorithm(String _algorithm) {
        this._algorithm = _algorithm;
    }

    public String getVersion() {
        return _version;
    }

    public void setVersion(String _version) {
        this._version = _version;
    }

    public String getName() {
        return _name;
    }

    public void setName(String _name) {
        this._name = _name;
    }

    public String getDescription() {
        return _description;
    }

    public void setDescription(String _description) {
        this._description = _description;
    }
}
