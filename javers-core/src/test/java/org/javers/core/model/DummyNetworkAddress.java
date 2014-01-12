package org.javers.core.model;

/**
 * Embedded Value Object
 *
 * @author pawel szymczyk
 */
public class DummyNetworkAddress {

    private enum Version {
        IPv4,
        IPv6;
    };

    private String addres;
    private Version version;

    public String getAddres() {
        return addres;
    }

    public void setAddres(String addres) {
        this.addres = addres;
    }

    public Version getVersion() {
        return version;
    }

    public void setVersion(Version version) {
        this.version = version;
    }
}
