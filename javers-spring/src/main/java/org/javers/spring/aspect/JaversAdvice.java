package org.javers.spring.aspect;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

public class JaversAdvice implements MethodInterceptor {
    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {

        Object retVal = invocation.proceed();
        System.out.println(">> Done");
        return retVal;
    }
}
