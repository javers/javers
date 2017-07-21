package org.javers.spring.sql

import org.hibernate.Hibernate
import org.javers.core.Javers
import org.javers.repository.jql.QueryBuilder
import org.javers.spring.boot.sql.*
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
/**
 * @author f-aubert
 */
@RunWith(SpringJUnit4ClassRunner)
@SpringBootTest(classes = [Issue558Application.class])
@ActiveProfiles("issue558")
public class Issue558Test {

    @Autowired
    Javers javers

    @Autowired
    StudentEntityRepository studentRepository;

    @Autowired
    StudentEntityNoJaversRepository studentNoJaversRepository;

    @Autowired
    CourseEntityRepository courseRepository;

    @Autowired
    CourseEntityNoJaversRepository courseNoJaversRepository;

    @Autowired
    LinkStudentCourseEntityRepository linkStudentCourseRepository;

    @Test
    void "should not save any commits for John"() {
        //given
        def john = new Student(1, "john")
        def maths = new Course(1, "maths")

        //when
        john = studentNoJaversRepository.save(john) // no commit
        maths = courseRepository.save(maths)

        Course mathsRef = courseNoJaversRepository.getOne(1); // TestCase Note: get an Hibernate Proxy!!! It never gets selected in DB.
        def enrolledJohnInMaths = new LinkStudentCourse(1, "enrolled", john, mathsRef);
        john.getCourses().add(enrolledJohnInMaths);
        john = studentNoJaversRepository.save(john) // no commit

        //then
        def snapshots = javers.findSnapshots(QueryBuilder.byClass(Student).build())
        assert snapshots.size() == 0
    }

    @Test
    void "should save 2 commits for John"() {
        //given
        def john = new Student(1, "john")
        def maths = new Course(1, "maths")

        //when
        john = studentRepository.save(john) // 1st commit
        maths = courseRepository.save(maths)

        Course maths2 = courseRepository.findOne(1); // TestCase Note: get a real Entity!!! It gets selected in DB.
        def enrolledJohnInMaths = new LinkStudentCourse(1, "enrolled", john, maths2);
        john.getCourses().add(enrolledJohnInMaths);
        john = studentRepository.save(john) // 2nd commit

        //then
        def snapshots = javers.findSnapshots(QueryBuilder.byClass(Student).build())
        assert snapshots.size() == 2
    }

    @Test
    void "should save 2 commits for John and not try to unproxy mathsRef to enroll John in Maths"() {
        //given
        def john = new Student(1, "john")
        def maths = new Course(1, "maths")

        //when
        john = studentRepository.save(john)
        maths = courseRepository.save(maths) // 1st commit

        Course mathsRef = courseRepository.getOne(1); // TestCase Note: get an Hibernate Proxy!!! It never gets selected in DB.

        println "proxy.isInitialized: " + Hibernate.isInitialized(mathsRef)
        println "proxy.class: " + mathsRef.getClass()
        println "proxy.id: " + mathsRef.getId()
        println "proxy.persistenClass: "+ mathsRef.getHibernateLazyInitializer().getPersistentClass()
        println "proxy.isInitialized: " + Hibernate.isInitialized(mathsRef)
        println 'I am happy :)'

        def enrolledJohnInMaths = new LinkStudentCourse(1, "enrolled", john, mathsRef);
        john.getCourses().add(enrolledJohnInMaths);
        john = studentRepository.save(john) // 2nd commit

        //then
        def snapshots = javers.findSnapshots(QueryBuilder.byClass(Student).build())
        assert snapshots.size() == 2
    }

    @Test
    void "should save 3 logical commits for John"() {
        //given
        def john = new Student(1, "john")
        def maths = new Course(1, "maths")

        //when
        john = studentRepository.save(john) // 1st commit
        maths = courseRepository.save(maths)

        Course maths2 = courseRepository.findOne(1); // TestCase Note: get a real Entity!!! It gets selected in DB.
        def enrolledJohnInMaths = new LinkStudentCourse(1, "enrolled", john, maths2);
        john.getCourses().add(enrolledJohnInMaths);
        john = studentRepository.save(john) // 2nd commit

        Student john2 = studentRepository.findByIdWithCourses(1); // TestCase Note: get a real Entity!!! It gets selected in DB.
        john2.getCourses().getAt(0).setType("auditor");
        john2 = studentRepository.save(john2) // 3rd commit

        //then
        def linkSnapshots = javers.findSnapshots(QueryBuilder.byClass(LinkStudentCourse).build())
        assert linkSnapshots.size() == 2

        // TestCase Note: Should from a global/logical perspective be '3', as if we conceptually define the link to be owned by a Student
        // and any update to the link is a change as well in the graph representing a Student. I could settle with two Javers Queries in
        // order to keep the query language simple, but how I'm to retrieve all commits byClass LinkStudentCourse relevant to entities with
        // property'course' having the value "org.javers.spring.boot.sql.Course/1". Hence my proposal for a query andPropertyEquals('course',
        // 'org.javers.spring.boot.sql.Course/1)
        def snapshots = javers.findSnapshots(QueryBuilder.byClass(Student).build())
        assert snapshots.size() == 3
    }

}