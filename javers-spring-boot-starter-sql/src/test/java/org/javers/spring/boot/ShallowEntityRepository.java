package org.javers.spring.boot;

import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;

@JaversSpringDataAuditable
public interface ShallowEntityRepository extends JpaRepository<ShallowEntity, Integer> {
}
