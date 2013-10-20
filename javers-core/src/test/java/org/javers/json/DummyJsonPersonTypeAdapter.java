package org.javers.json;

/**
 * @author bartosz walacik
 */
public class DummyJsonPersonTypeAdapter extends BasicStringTypeAdapter<DummyJsonPerson> {
    @Override
    public String serialize(DummyJsonPerson sourceValue) {
        return sourceValue.firstName+"@"+sourceValue.lastName;
    }

    @Override
    public DummyJsonPerson deserialize(String serializedValue) {
        String[] names = serializedValue.split("@");
        return new DummyJsonPerson(names[0],names[1]);
    }

    @Override
    public Class<DummyJsonPerson> getType() {
        return DummyJsonPerson.class;
    }
}
