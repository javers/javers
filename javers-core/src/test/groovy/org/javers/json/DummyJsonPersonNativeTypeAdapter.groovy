package org.javers.json

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter

/**
 * @author bartosz walacik
 */
class DummyJsonPersonNativeTypeAdapter extends TypeAdapter<DummyJsonPerson> {

    @Override
    void write(final JsonWriter out, final DummyJsonPerson value) throws IOException {
        out.value(value.firstName+"@"+value.lastName)
    }

    @Override
    DummyJsonPerson read(JsonReader reader) throws IOException {
        String serializedValue = reader.nextString()
        String[] names = serializedValue.split("@")
        new DummyJsonPerson(names[0],names[1])
    }
}
