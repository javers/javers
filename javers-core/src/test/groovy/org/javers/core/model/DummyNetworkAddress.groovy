package org.javers.core.model

/**
 * Embedded Value Object
 *
 * @author pawel szymczyk
 */
class DummyNetworkAddress {

    private enum Version {
        IPv4,
        IPv6
    }

    String address
    Version version
}
