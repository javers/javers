package org.javers.spring.boot.sql;

import org.javers.spring.annotation.JaversAuditable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author pawelszymczyk
 */
@Component
public class DummyEntityService {

    @Autowired
    private DummyEntityRepository dummyEntityRepository;

    @Transactional
    @JaversAuditable
    public void save(DummyEntity dummyEntity) {
        dummyEntityRepository.save(dummyEntity);
    }
}
