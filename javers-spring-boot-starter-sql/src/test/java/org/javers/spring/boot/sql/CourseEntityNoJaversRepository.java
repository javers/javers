package org.javers.spring.boot.sql;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author f-aubert
 */
public interface CourseEntityNoJaversRepository extends JpaRepository<Course, Integer> {
}
