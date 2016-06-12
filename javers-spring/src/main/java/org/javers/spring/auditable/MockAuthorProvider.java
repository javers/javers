package org.javers.spring.auditable;

public class MockAuthorProvider implements AuthorProvider {
    @Override
    public String provide() {
        return "unknown";
    }
}
