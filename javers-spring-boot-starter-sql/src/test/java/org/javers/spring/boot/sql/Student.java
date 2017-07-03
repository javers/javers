package org.javers.spring.boot.sql;


import javax.persistence.*;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author f-aubert
 */
@Entity
public class Student {

    private int id;
    private String name;

    private Set<LinkStudentCourse> courses = new LinkedHashSet<>();

    public Student() {
    }

    public Student(int id, String name) {
        this.id = id;
        this.name = name;
    }

    @Id
    @GeneratedValue
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @OneToMany(mappedBy = "course", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    public Set<LinkStudentCourse> getCourses() {
        return courses;
    }

    public void setCourses(Set<LinkStudentCourse> courses) {
        this.courses = courses;
    }
}
