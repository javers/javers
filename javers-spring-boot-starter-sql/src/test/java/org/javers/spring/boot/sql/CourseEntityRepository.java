package org.javers.spring.boot.sql;

import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author f-aubert
 */
@JaversSpringDataAuditable
public interface CourseEntityRepository extends JpaRepository<Course, Integer> {
}
