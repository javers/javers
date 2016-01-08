package org.javers.spring.boot.sql;

import org.javers.spring.boot.sql.DummyEntity;
import org.springframework.data.repository.CrudRepository;

/**
 * @author pawelszymczyk
 */
public interface DummyEntityRepository extends CrudRepository<DummyEntity, Integer>{
}
