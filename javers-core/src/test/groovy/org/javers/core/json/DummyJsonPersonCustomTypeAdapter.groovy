package org.javers.core.json
/**
 * @author bartosz walacik
 */
class DummyJsonPersonCustomTypeAdapter extends BasicStringTypeAdapter<DummyJsonPerson> {
    @Override
    String serialize(DummyJsonPerson sourceValue) {
        sourceValue.firstName+"@"+sourceValue.lastName
    }

    @Override
    DummyJsonPerson deserialize(String serializedValue) {
        String[] names = serializedValue.split("@")
        new DummyJsonPerson(names[0],names[1])
    }

    @Override
    Class<DummyJsonPerson> getType() {
        DummyJsonPerson
    }
}