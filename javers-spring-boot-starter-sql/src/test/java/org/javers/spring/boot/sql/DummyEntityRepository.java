package org.javers.spring.boot.sql;

import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.repository.CrudRepository;

/**
 * @author pawelszymczyk
 */
@JaversSpringDataAuditable
public interface DummyEntityRepository extends CrudRepository<DummyEntity, Integer>{
}
