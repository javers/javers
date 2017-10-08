package org.javers.spring.repository

import org.javers.spring.annotation.JaversAuditable
import org.javers.spring.model.DummyObject
import org.springframework.stereotype.Repository

/**
 * @author bartosz walacik
 */
@Repository
class DummyAuditedRepository {

    @JaversAuditable
    void save(DummyObject obj){
      //... omitted
    }

    @JaversAuditable
    void saveTwo(DummyObject obj, obj2){
        //... omitted
    }

    @JaversAuditable
    void saveAll(Iterable<DummyObject> objetcs){
        //... omitted
    }

    DummyObject find(DummyObject query){
        //... omitted
        null
    }
}
