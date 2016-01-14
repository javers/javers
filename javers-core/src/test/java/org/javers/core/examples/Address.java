package org.javers.core.examples;

abstract class Address {
    private boolean verified;

    Address(boolean verified) {
        this.verified = verified;
    }
}
