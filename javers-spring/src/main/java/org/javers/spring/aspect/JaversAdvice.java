package org.javers.spring.aspect;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.javers.core.Javers;

public class JaversAdvice implements MethodInterceptor {

    private final Javers javers;

    public JaversAdvice(Javers javers) {
        this.javers = javers;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {

        Object retVal = invocation.proceed();

        for (Object arg: invocation.getArguments()) {

            //TODO author
            javers.commit("author", arg);
        }

        return retVal;
    }
}
