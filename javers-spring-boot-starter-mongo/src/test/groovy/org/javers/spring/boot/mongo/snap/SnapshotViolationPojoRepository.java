package org.javers.spring.boot.mongo.snap;

import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.repository.CrudRepository;

@JaversSpringDataAuditable
public interface SnapshotViolationPojoRepository extends CrudRepository<ExtendedPojo, String> {
}
