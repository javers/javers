package org.javers.core.examples;

class EmailAddress extends Address {
    private String email;

    EmailAddress(String email, boolean verified) {
        super(verified);
        this.email = email;
    }
}
