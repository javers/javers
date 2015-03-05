package org.javers.spring.integration

import org.javers.spring.annotation.JaversAuditable

/**
 * @author bartosz walacik
 */
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
