package org.javers.spring.integration.repositories;

import org.javers.spring.JaversAuditable;
import org.javers.spring.integration.domain.Project;

/**
 * @author Pawel Szymczyk
 */
public class ProjectRepository {

    @JaversAuditable
    public void save(Project project) { }

    public void update(Project project) { }
}
