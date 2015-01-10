package org.javers.spring.integration;

import org.javers.spring.JaversAuditable;

/**
 * @author Pawel Szymczyk
 */
class ProjectRepository {

    @JaversAuditable
    public void save(Project project) { }

    public void update(Project project) { }
}
