package org.javers.spring;

import org.javers.common.exception.JaversException;
import org.javers.common.exception.JaversExceptionCode;
import org.javers.common.reflection.ReflectionUtil;
import org.javers.core.JaversCoreProperties;
import org.javers.core.graph.ObjectAccessHook;

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

    public ObjectAccessHook createObjectAccessHookInstance() {
        Class<?> clazz = ReflectionUtil.classForName(objectAccessHook);
        if (!ObjectAccessHook.class.isAssignableFrom(clazz)) {
            throw new JaversException(JaversExceptionCode.CLASS_IS_NOT_INSTANCE_OF, objectAccessHook, ObjectAccessHook.class.getName());
        }
        return (ObjectAccessHook)ReflectionUtil.newInstance(clazz);
    }
}


