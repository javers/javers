package org.javers.spring.repository

import org.javers.spring.annotation.JaversAuditable
import org.javers.spring.annotation.JaversAuditableDelete
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
    void saveAndFail(DummyObject obj) {
        throw new RuntimeException()
    }

    @JaversAuditable
    void saveTwo(DummyObject obj, obj2){
        //... omitted
    }

    @JaversAuditable
    void saveAll(Iterable<DummyObject> objetcs){
        //... omitted
    }

    DummyObject find(DummyObject query) {
        //... omitted
        null
    }

    @JaversAuditableDelete
    void delete(DummyObject obj) {
        //... omitted
    }

    @JaversAuditableDelete(entity = DummyObject)
    void deleteById(String id) {
    }

    @JaversAuditableDelete(entity = DummyObject.class)
    void deleteAllById(Iterable<String> ids) {
    }

    @JaversAuditableDelete
    void deleteByIdNoClass(String id) {
    }


    @JaversAuditableDelete
    void deleteTwo(DummyObject obj, obj2) {
        //... omitted
    }

    @JaversAuditableDelete
    void deleteAll(Iterable<DummyObject> objetcs) {
        //... omitted
    }

}
