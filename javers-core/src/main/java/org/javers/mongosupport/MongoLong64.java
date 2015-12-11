package org.javers.mongosupport;

import com.google.gson.annotations.SerializedName;

public class MongoLong64 {
    @SerializedName("$numberLong")
    private Long value;

    public Long getValue() {
        return value;
    }
}
