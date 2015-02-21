package org.javers.spring.data;

import org.javers.common.collections.Optional;

import java.lang.reflect.Method;

public enum JaversSpringDataAuditEvent {
    SAVE,
    DELETE;

    public String methodName() {
        return name().toLowerCase();
    }

    public static Optional<JaversSpringDataAuditEvent> byMethodName(String name) {
        for (JaversSpringDataAuditEvent event : values()) {
            if (event.methodName().equals(name)) {
                return Optional.of(event);
            }
        }
        return Optional.empty();
    }

    public static boolean isRelevantMethod(Method method) {
        return isRelevantMethodName(method.getName());
    }

    public static boolean isRelevantMethodName(String name) {
        return byMethodName(name).isPresent();
    }

    public boolean isEventMethod(Method method) {
        return isEventMethodName(method.getName());
    }

    public boolean isEventMethodName(String name) {
        return methodName().equals(name);
    }
}
