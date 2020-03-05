package org.javers.spring.repository

import org.javers.spring.annotation.JaversAuditableAsync
import org.javers.spring.model.DummyObject
import org.springframework.stereotype.Repository

/**
 * @author bartosz walacik
 */
@Repository
class DummyAuditedAsyncRepository {

    @JaversAuditableAsync
    void save(DummyObject obj){
      //... omitted
    }

    @JaversAuditableAsync
    void saveAndFail(DummyObject obj) {
        throw new RuntimeException()
    }

    @JaversAuditableAsync
    void saveTwo(DummyObject obj, obj2){
        //... omitted
    }

    @JaversAuditableAsync
    void saveAll(Iterable<DummyObject> objetcs){
        //... omitted
    }

    DummyObject find(DummyObject query){
        //... omitted
        null
    }

}
