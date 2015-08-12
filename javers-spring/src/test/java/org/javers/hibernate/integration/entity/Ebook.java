package org.javers.hibernate.integration.entity;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "ebook")
public class Ebook {

    private String id;
    private String title;
    private Author author;
    private List<String> comments;

    public Ebook() {
    }

    public Ebook(String id, String title, Author author, List<String> comments) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.comments = comments;
    }

    @Id
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author authors) {
        this.author = authors;
    }

    @ElementCollection
    public List<String> getComments() {
        return comments;
    }

    public void setComments(List<String> comments) {
        this.comments = comments;
    }
}
