package org.javers.spring.boot.sql;


import javax.persistence.*;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author f-aubert
 */
@Entity
public class Course {

    private int id;
    private String name;

    private Set<LinkStudentCourse> students = new LinkedHashSet<>();

    public Course() {
    }

    public Course(int id, String name) {
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

    @OneToMany(mappedBy = "student", fetch = FetchType.LAZY)
    public Set<LinkStudentCourse> getStudents() {
        return students;
    }

    public void setStudents(Set<LinkStudentCourse> students) {
        this.students = students;
    }
}
