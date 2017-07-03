package org.javers.spring.boot.sql;

import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * @author f-aubert
 */
@JaversSpringDataAuditable
public interface StudentEntityRepository extends JpaRepository<Student, Integer> {

    @Query ("SELECT s FROM Student s JOIN FETCH s.courses c JOIN FETCH c.course JOIN FETCH c.student WHERE s.id=:id")
    Student findByIdWithCourses(@Param("id") int id);
}
