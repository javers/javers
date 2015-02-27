package org.javers.spring.data.integration;

import org.javers.spring.AuthorProvider;

/**
 * Created by gessnerfl on 22.02.15.
 */
public class TestAuthorProvider implements AuthorProvider {
    private String author = "tester";

    public void setAuthor(String author){
        this.author = author;
    }

    @Override
    public String provide() {
        return author;
    }
}
