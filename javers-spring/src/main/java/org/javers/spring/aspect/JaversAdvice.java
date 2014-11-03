package org.javers.spring.aspect;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.javers.core.Javers;

public class JaversAdvice implements MethodInterceptor {

    private final Javers javers;
    private final String author;

    public JaversAdvice(Javers javers, String author) {
        this.javers = javers;
        this.author = author;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {

        Object retVal = invocation.proceed();

        for (Object arg: invocation.getArguments()) {
            javers.commit(author, arg);
        }

        return retVal;
    }
}
