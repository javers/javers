package org.javers.core.json

import org.javers.core.model.DummyPoint

/**
 * @author bartosz walacik
 */
class DummyPointJsonTypeAdapter extends BasicStringTypeAdapter<DummyPoint> {
    @Override
    String serialize(DummyPoint sourceValue) {
        sourceValue.x+","+sourceValue.y
    }

    @Override
    DummyPoint deserialize(String serializedValue) {
        String[] vals = serializedValue.split(",")
        new DummyPoint(vals[0].toInteger(), vals[1].toInteger())
    }

    @Override
    Class<DummyPoint> getValueType() {
        DummyPoint
    }
}