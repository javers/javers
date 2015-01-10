package org.javers.spring.integration.repositories;

import org.javers.spring.JaversAuditable;
import org.javers.spring.integration.domain.User;

/**
 * @author Pawel Szymczyk
 */
@JaversAuditable
public class UserRepository {

    public void save(User user) { }

    public void update(User user) { }
}
