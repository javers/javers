package org.javers.spring.integration;

import org.javers.spring.JaversAuditable;

/**
 * @author Pawel Szymczyk
 */
@JaversAuditable
class UserRepository {

    public void save(User user) { }

    public void update(User user) { }
}
