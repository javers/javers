package org.javers.spring.boot.limit.repository;

import java.util.UUID;

import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.javers.spring.boot.limit.domain.LimitEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
@JaversSpringDataAuditable
public interface LimitRepository extends JpaRepository<LimitEntity, UUID> {

}
