package org.javers.repository.sql.finders;

/**
* @author bartosz walacik
*/
class SnapshotPropertyDTO {
    private final String name;
    private final String value;

    SnapshotPropertyDTO(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }
}
