package org.javers.repository.redis.domain;

/**
 * POJO
 */
public class Firmware {

    private String name;

    private String version;

    public Firmware(String name, String version) {
        super();
        this.name = name;
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return String.format("Firmware [name=%s, version=%s]", name, version);
    }

}
