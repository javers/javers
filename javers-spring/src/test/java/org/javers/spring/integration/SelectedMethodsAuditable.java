package org.javers.spring.integration;

import org.javers.spring.JaversAudit;

public class SelectedMethodsAuditable {

    @JaversAudit
    public void auditableMethod() {

    }

    public void nonAuditableMethod() {

    }
}
