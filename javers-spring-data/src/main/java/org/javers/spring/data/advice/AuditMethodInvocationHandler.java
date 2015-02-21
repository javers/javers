package org.javers.spring.data.advice;


public interface AuditMethodInvocationHandler {
    void onAfterMethodInvocation(Object o);
}
