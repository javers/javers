package org.javers.spring.integration;

import org.javers.spring.JaversAuditable;

public class SelectedMethodsAuditable {

    @JaversAuditable
    public void auditableMethod(Object arg) {

    }

    public void nonAuditableMethod(Object arg) {

    }
}
