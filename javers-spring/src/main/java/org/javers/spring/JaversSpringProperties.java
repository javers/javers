package org.javers.spring;

import org.javers.core.JaversCoreProperties;

public abstract class JaversSpringProperties extends JaversCoreProperties {
    private boolean auditableAspectEnabled = true;
    private boolean springDataAuditableRepositoryAspectEnabled = true;
    private String objectAccessHook = defaultObjectAccessHook();

    public boolean isAuditableAspectEnabled() {
        return auditableAspectEnabled;
    }

    public boolean isSpringDataAuditableRepositoryAspectEnabled() {
        return springDataAuditableRepositoryAspectEnabled;
    }

    public void setAuditableAspectEnabled(boolean auditableAspectEnabled) {
        this.auditableAspectEnabled = auditableAspectEnabled;
    }

    public void setSpringDataAuditableRepositoryAspectEnabled(boolean springDataAuditableRepositoryAspectEnabled) {
        this.springDataAuditableRepositoryAspectEnabled = springDataAuditableRepositoryAspectEnabled;
    }

    public String getObjectAccessHook() {
        return objectAccessHook;
    }

    public void setObjectAccessHook(String objectAccessHook) {
        this.objectAccessHook = objectAccessHook;
    }

    protected abstract String defaultObjectAccessHook();
}
