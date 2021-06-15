package org.javers.spring.boot.mongo.bug1099;

import java.util.Optional;

import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.javers.spring.boot.mongo.bug1099.model.A;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

@JaversSpringDataAuditable
public interface ARepository extends MongoRepository<A, String>{

//   @Query(value = "{'b.id' : ?0 }")
	public Optional<A> findById(String id);
}

