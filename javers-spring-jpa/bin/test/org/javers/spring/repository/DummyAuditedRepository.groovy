package org.javers.spring.repository

import org.javers.spring.annotation.JaversAuditable
import org.javers.spring.model.DummyObject
import org.springframework.stereotype.Repository

@Repository
class DummyAuditedRepository {

    @JaversAuditable
    void save(DummyObject obj){
      //... omitted
    }
}
