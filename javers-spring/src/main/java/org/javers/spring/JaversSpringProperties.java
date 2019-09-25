package org.javers.spring;

import org.javers.core.JaversCoreProperties;

public abstract class JaversSpringProperties extends JaversCoreProperties {
    private boolean auditableAspectEnabled = true;
    private boolean springDataAuditableRepositoryAspectEnabled = true;
    private boolean auditableAspectAsyncEnabled = true;

    public boolean isAuditableAspectEnabled() {
        return auditableAspectEnabled;
    }

    public boolean isSpringDataAuditableRepositoryAspectEnabled() {
        return springDataAuditableRepositoryAspectEnabled;
    }

    public boolean auditableAspectAsyncEnabled() {
        return auditableAspectAsyncEnabled;
    }

    public void setAuditableAspectEnabled(boolean auditableAspectEnabled) {
        this.auditableAspectEnabled = auditableAspectEnabled;
    }

    public void setSpringDataAuditableRepositoryAspectEnabled(boolean springDataAuditableRepositoryAspectEnabled) {
        this.springDataAuditableRepositoryAspectEnabled = springDataAuditableRepositoryAspectEnabled;
    }

    public void setAuditableAspectAsyncEnabled(boolean auditableAspectAsyncEnabled) {
        this.auditableAspectAsyncEnabled = auditableAspectAsyncEnabled;
    }
}
