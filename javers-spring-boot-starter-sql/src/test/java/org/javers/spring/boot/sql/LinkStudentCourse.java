package org.javers.spring.boot.sql;


import org.javers.core.metamodel.annotation.ShallowReference;

import javax.persistence.*;

/**
 * @author f-aubert
 */
@Entity
public class LinkStudentCourse {

    private int id;
    private String type;

    private Student student;
    private Course course;

    public LinkStudentCourse() {
    }

    public LinkStudentCourse(int id, String type, Student student, Course course) {
        this.id = id;
        this.type = type;
        this.student = student;
        this.course = course;
    }

    @Id
    @GeneratedValue
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @ShallowReference
    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @ShallowReference
    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }
}
