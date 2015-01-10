package org.javers.spring;

/**
 * @author Pawel Szymczyk
 */
public class DefaultAuthorProvider implements AuthorProvider {
    @Override
    public String provide() {
        return "author";
    }
}
