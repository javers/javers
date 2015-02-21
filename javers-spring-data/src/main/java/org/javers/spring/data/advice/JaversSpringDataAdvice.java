package org.javers.spring.data.advice;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.javers.common.collections.Arrays;
import org.javers.common.collections.Lists;

import java.util.Collections;

public class JaversSpringDataAdvice implements MethodInterceptor {

    private final AuditMethodInvocationHandlerFactory handlerFactory;

    public JaversSpringDataAdvice(AuditMethodInvocationHandlerFactory handlerFactory) {
        this.handlerFactory = handlerFactory;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Object retVal = invocation.proceed();
        writeAuditLog(invocation);
        return retVal;
    }

    private void writeAuditLog(MethodInvocation invocation) {
        Iterable<Object> changedObjects = getChangeObjectsFromMethodArgument(invocation);
        for (Object changedObject : changedObjects) {
            writeAuditLog(invocation, changedObject);
        }
    }

    private void writeAuditLog(MethodInvocation invocation, Object changedObject) {
        AuditMethodInvocationHandler handler = handlerFactory.createFor(invocation);
        handler.onAfterMethodInvocation(changedObject);
    }

    private Iterable<Object> getChangeObjectsFromMethodArgument(MethodInvocation invocation) {
        if (hasExpectedNumberOfArguments(invocation)) {
            Object arg = invocation.getArguments()[0];
            if (arg instanceof Iterable) {
                return (Iterable<Object>) arg;
            } else {
                return Lists.asList(arg);
            }
        }
        return Collections.EMPTY_LIST;
    }

    private boolean hasExpectedNumberOfArguments(MethodInvocation invocation) {
        return invocation.getArguments() != null && invocation.getArguments().length == 1;
    }

}
