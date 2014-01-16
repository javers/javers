package org.javers.core.json

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import org.javers.core.model.DummyPoint

/**
 * @author bartosz walacik
 */
class DummyPointNativeTypeAdapter extends TypeAdapter<DummyPoint> {

    @Override
    void write(final JsonWriter out, final DummyPoint sourceValue) throws IOException {
        out.value(sourceValue.x+","+sourceValue.y)
    }

    @Override
    DummyPoint read(JsonReader reader) throws IOException {
        String serializedValue = reader.nextString()
        String[] vals = serializedValue.split(",")
        new DummyPoint(vals[0].toInteger(), vals[1].toInteger())
    }
}
