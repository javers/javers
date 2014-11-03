package org.javers.spring;

public class DefaultAuthorProvider implements AuthorProvider {
    @Override
    public String provide() {
        return "author";
    }
}
