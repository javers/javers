package org.javers.spring.boot.limit.repository;

import java.util.UUID;

import org.javers.spring.boot.limit.domain.LimitRangeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface LimitRangeRepository extends JpaRepository<LimitRangeEntity, UUID> {

}
