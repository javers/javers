package org.javers.core.json.typeadapter.util;

import org.javers.core.json.BasicStringSuperTypeAdapter;
import java.nio.file.Path;

public class PathTypeAdapter extends BasicStringSuperTypeAdapter<Path> {

    @Override
    public String serialize(Path sourceValue) {
        return sourceValue.toString();
    }

    @Override
    public Path deserialize(String serializedValue) {
        return Path.of(serializedValue);
    }

    @Override
    public Class<Path> getTypeSuperclass() {
        return Path.class;
    }
}
