package org.javers.spring.boot.sql;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * @author f-aubert
 */
public interface StudentEntityNoJaversRepository extends JpaRepository<Student, Integer> {

    @Query("SELECT s FROM Student s JOIN FETCH s.courses WHERE s.id=:id")
    Student findByIdWithCourses(@Param("id") int id);
}
