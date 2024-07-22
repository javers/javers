package org.javers.spring.boot.redis.repository;

import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.javers.spring.boot.redis.domain.Device;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;

@JaversSpringDataAuditable
public interface DeviceRepository extends CrudRepository<Device, String>, QueryByExampleExecutor<Device> {

}
