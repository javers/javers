package org.javers.spring.boot.mongo;

import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.repository.CrudRepository;

@JaversSpringDataAuditable
public interface DummyEntityRepository extends CrudRepository<DummyEntity, Integer>{
}
