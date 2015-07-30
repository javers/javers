package org.javers.spring.boot;

import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author bartosz.walacik
 */
@JaversSpringDataAuditable
public interface DummyEntityRepository extends JpaRepository<DummyEntity, Integer> {
}
