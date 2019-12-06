package org.javers.spring.repository

import org.javers.spring.annotation.JaversAuditable
import org.javers.spring.annotation.JaversAuditableAsync
import org.javers.spring.annotation.JaversAuditableDelete
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

    @JaversAuditable
    void saveAll(Iterable<DummyObject> objetcs){
        //... omitted
    }

    DummyObject find(DummyObject query){
        //... omitted
        null
    }

    @JaversAuditableDelete
    void delete(DummyObject obj) {
        //... omitted
    }

    @JaversAuditableDelete
    void deleteTwo(DummyObject obj, obj2){
        //... omitted
    }

    @JaversAuditableDelete
    void deleteAll(Iterable<DummyObject> objetcs){
        //... omitted
    }

}
